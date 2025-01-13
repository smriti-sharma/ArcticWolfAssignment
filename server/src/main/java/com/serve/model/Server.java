package com.serve.model;

import com.serve.config.Config;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/server")
public class Server {

    @PostMapping("/")
        public String callServer(@RequestBody ServerInp inp) {
        var filename = inp.fileName;
        var filteredItems = inp.filteredItems;

        String filepath = Config.GetSingleton().outputDir + "/" + filename;
        try {
            File outFile = new File(filepath);
            boolean exists = outFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(filepath);
            for (Map.Entry<String,String> entry : filteredItems.entrySet())
            {
                String dataItem = entry.getKey() + "=" + entry.getValue() + "\n";
                fos.write(dataItem.getBytes());
                fos.flush();
            }
            System.out.println("Written all filetred values in file with name: " + filename);
            fos.close();
            System.out.println("Created file with name:" + filename + " in path:" + filepath);
            return "Success";
        } catch (FileNotFoundException e) {
            System.out.println("File with name " + filename + " not found on the path" + filepath);
            return e.toString();
        } catch (IOException e) {
            System.out.println("File with name " + filename + " not found on the path" + filepath);
            return e.toString();
        }
    }
}
