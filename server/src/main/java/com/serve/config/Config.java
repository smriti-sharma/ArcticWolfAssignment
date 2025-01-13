package com.serve.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Config {
    public String outputDir = "./";
    public Integer portNumber = 8080;

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
                if(key.equals("outputDir") && parts.length>1 && !parts[1].trim().isEmpty()) {
                    this.outputDir = parts[1].trim();
                }
                else if(key.equals("portNumber") && parts.length>1 && !parts[1].trim().isEmpty()) {
                    try {
                        Integer portNum = Integer.parseInt(parts[1].trim());
                        this.portNumber = portNum;
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
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
    }
}
