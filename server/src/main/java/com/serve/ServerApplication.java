package com.serve;

import com.serve.config.Config;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;

@SpringBootApplication
public class ServerApplication {

	public static void main(String[] args) {
		ArrayList<String> filteredArgs = new ArrayList<>();

		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("--config") && i + 1 < args.length) {
				String configPath = args[i + 1]; // Check if the file exists
				Config.GetSingleton().Load(configPath);

				// skip loading the next arg
				i++;
				continue;
			}

			filteredArgs.add(args[i]);
		}

		String[] filteredArgsArray = new String[filteredArgs.size()];
		filteredArgs.toArray(filteredArgsArray);
		SpringApplication.run(ServerApplication.class, filteredArgsArray);
	}

	@Bean
	public TomcatServletWebServerFactory servletContainer() {
		TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
		factory.setPort(Config.GetSingleton().portNumber); // Set your desired port here
		return factory;
	}
}
