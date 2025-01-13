package config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class Config {
    public String dirPath = "./";
    public String serverAddress = "http://127.0.0.1:8080";
    public String[] regex;

    Config() {}

    private static Config _config = new Config();
    public static Config GetSingleton()
    {
        return _config;
    }

    public void Load(String filePath)
    {
        BufferedReader br = null;
        try {
            File file = new File(filePath);
            br = new BufferedReader(new FileReader(file));

            String line = null;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("=");
                String key = parts[0].trim();
                if(key.equals("dirPath") && parts.length>1 && !parts[1].trim().isEmpty()) {
                    this.dirPath = parts[1].trim();
                }
                else if(key.equals("regex") && parts.length>1 && !parts[1].trim().isEmpty()) {
                    this.regex = parts[1].trim().split(",");
                }
                else if(key.equals("serverAddress") && parts.length>1 && !parts[1].trim().isEmpty()) {
                    this.serverAddress = parts[1].trim();
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
    }
}

