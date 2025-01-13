package ClientProgram;

import config.Config;

import org.openapitools.client.ApiException;
import org.openapitools.client.model.ServerInp;
import org.openapitools.client.api.DefaultApi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientProgram {

    public static Map<String, String> filterDataFromFile(String filePath, String[] regex) {
        Map<String, String> filteredData = new HashMap<>();
        if (regex == null) return filteredData;

        BufferedReader br = null;

        try {
            File file = new File(filePath);
            br = new BufferedReader(new FileReader(file));

            String line = null;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("=");
                if (parts.length == 0)
                    continue;

                if (parts.length <= 1) {
                    continue;
                }
                String key = parts[0].trim();
                for (String regex_ : regex)
                {
                    Pattern pattern_ = Pattern.compile(regex_);
                    Matcher matcher = pattern_.matcher(key);
                    if(matcher.find()) {
                        String value = parts[1].trim();
                        filteredData.put(key, value);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (br != null) {
                try {
                    br.close();
                }
                catch (Exception e) {
                };
            }
        }

        return filteredData;
    }

    public static void main(String[] args) {
        Config config = Config.GetSingleton();
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--config") && i + 1 < args.length) {
                String configPath = args[i + 1]; // Check if the file exists
                config.Load(configPath);

                // skip loading the next arg
                i++;
                break;
            }
        }

        DefaultApi serverApi = new DefaultApi();

        String serverAddress = config.serverAddress;
        if (!serverAddress.isEmpty())
        {
            serverApi.setCustomBaseUrl(serverAddress);
        }

        try {
            // Specify the directory which supposed to be watched
            Path directoryPath = Paths.get(config.dirPath);

            // Create a WatchService
            WatchService watchService = FileSystems.getDefault().newWatchService();

            // Register the directory for specific events
            directoryPath.register(watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY);

            System.out.println("Watching directory: " + directoryPath);

            // Infinite loop to continuously watch for events
            while (true) {
                WatchKey key = watchService.take();

                for (WatchEvent<?> event : key.pollEvents())
                {
                    // Handle the specific event
                    if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE)
                    {
                        System.out.println("File created: " + event.context());

                        String filename = event.context().toString();
                        System.out.println(filename);
                        String filePath = directoryPath.toString() + "/" + event.context().toString();

                        Map<String, String> filteredData = filterDataFromFile(filePath, config.regex);
                        System.out.println(filteredData);
                        System.out.println("Calling server...");

                        ServerInp request = new ServerInp();
                        request.setFileName(filename);
                        request.setFilteredItems(filteredData);

                        try
                        {
                            serverApi.callServer(request);
                        }
                        catch (ApiException e)
                        {
                            e.printStackTrace();
                        }

                        File file = new File(filePath);
                        if (file.delete()) {
                            System.out.println("Deleting file " + filename + " from directory: {} " + filePath);
                        } else {
                            System.out.println("Failed to delete file " + filename + " from directory " + filePath);
                        }


                    }
                    else if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE)
                    {
                        System.out.println("File deleted: " + event.context());
                    }
                    else if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY)
                    {
                        System.out.println("File modified: " + event.context());
                    }
                }

                // To receive further events, reset the key
                key.reset();
            }

        }
        catch (IOException | InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}