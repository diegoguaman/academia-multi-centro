package com.academy.academymanager.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Enterprise-grade configuration for loading environment variables from .env file.
 * 
 * This is the professional approach used in enterprise environments:
 * - Development: Reads from .env file (gitignored, secure)
 * - Production: Uses system environment variables (set by DevOps/Platform team)
 * - CI/CD: Uses secrets from GitHub Actions / GitLab CI / Jenkins
 * 
 * Priority order (highest to lowest):
 * 1. System environment variables (production, CI/CD)
 * 2. .env file (development local)
 * 3. application.properties defaults
 * 
 * Security:
 * - .env file is in .gitignore (never committed)
 * - Production uses external secret managers (HashiCorp Vault, AWS Secrets Manager)
 * - Secrets are encrypted at rest and in transit
 */
@Configuration
public class DotenvConfig {
    private static final Logger logger = LoggerFactory.getLogger(DotenvConfig.class);
    private final Environment environment;
    /**
     * Constructor with Spring Environment injection.
     * @param environment Spring environment for property resolution
     */
    public DotenvConfig(final Environment environment) {
        this.environment = environment;
    }
    /**
     * Loads .env file if it exists and sets system properties.
     * This runs as a static initializer BEFORE Spring Boot reads application.properties.
     * 
     * Enterprise pattern:
     * - Only loads .env in development (not in production)
     * - Production uses environment variables set by orchestration (K8s, Docker, etc.)
     * - CI/CD injects secrets as environment variables
     */
    static {
        loadDotenvStatic();
    }
    /**
     * Static method to load .env before Spring Boot initialization.
     * This ensures variables are available when Spring reads application.properties.
     */
    private static void loadDotenvStatic() {
        try {
            // Check if production profile is set via system property
            final String activeProfile = System.getProperty("spring.profiles.active", 
                    System.getenv("SPRING_PROFILES_ACTIVE"));
            // Only load .env in development/local profiles
            // Production should NEVER use .env files
            if ("prod".equals(activeProfile) || "production".equals(activeProfile)) {
                System.out.println("Production profile detected - skipping .env file loading");
                return;
            }
            // Load .env file from project root
            final Dotenv dotenv = Dotenv.configure()
                    .directory("./")  // Project root
                    .ignoreIfMissing()  // Don't fail if .env doesn't exist
                    .load();
            // Set system properties from .env (Spring Boot will pick them up)
            dotenv.entries().forEach(entry -> {
                final String key = entry.getKey();
                final String value = entry.getValue();
                // Only set if not already set as system property (env vars take precedence)
                if (System.getProperty(key) == null && System.getenv(key) == null) {
                    System.setProperty(key, value);
                }
            });
            System.out.println("✓ .env file loaded successfully (development mode)");
        } catch (Exception e) {
            // .env file is optional - don't fail if it doesn't exist
            System.out.println("ℹ .env file not found - using defaults or application-dev.properties");
        }
    }
    /**
     * PostConstruct method for logging (runs after Spring context is ready).
     */
    @PostConstruct
    public void logDotenvStatus() {
        final String activeProfile = environment.getProperty("spring.profiles.active", "default");
        if (!"prod".equals(activeProfile) && !"production".equals(activeProfile)) {
            logger.info("Development mode - .env file support enabled");
        }
    }
}

