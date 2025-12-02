package com.academy.academymanager;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class.
 * Loads .env file BEFORE Spring Boot initialization to ensure
 * environment variables are available when reading application.properties.
 */
@SpringBootApplication
public class AcademymanagerApplication {

	public static void main(String[] args) {
		// Load .env file BEFORE Spring Boot starts
		// This ensures variables are available when Spring reads application.properties
		loadDotenv();
		
		SpringApplication.run(AcademymanagerApplication.class, args);
	}
	
	/**
	 * Loads .env file and sets system properties.
	 * This runs BEFORE Spring Boot initialization.
	 */
	private static void loadDotenv() {
		try {
			// Check if production profile is set
			final String activeProfile = System.getProperty("spring.profiles.active", 
					System.getenv("SPRING_PROFILES_ACTIVE"));
			
			// Skip .env in production
			if ("prod".equals(activeProfile) || "production".equals(activeProfile)) {
				System.out.println("Production profile - skipping .env file");
				return;
			}
			
			// Load .env file from project root
			final Dotenv dotenv = Dotenv.configure()
					.directory("./")
					.ignoreIfMissing()
					.load();
			
			// Set system properties from .env
			dotenv.entries().forEach(entry -> {
				final String key = entry.getKey();
				final String value = entry.getValue();
				// Only set if not already set (env vars take precedence)
				if (System.getProperty(key) == null && System.getenv(key) == null) {
					System.setProperty(key, value);
				}
			});
			
			System.out.println("✓ .env file loaded successfully");
		} catch (Exception e) {
			// .env is optional - don't fail if it doesn't exist
			System.out.println("ℹ .env file not found - using defaults or application-dev.properties");
		}
	}
}
