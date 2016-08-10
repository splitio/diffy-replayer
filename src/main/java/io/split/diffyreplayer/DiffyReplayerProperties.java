package io.split.diffyreplayer;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * Simple Singleton wrapper around a properties file.
 *
 * It loads the property file based on the environment variable "ENVIRONMENT"
 * The file should be in the resources file.
 * If no environment variable is defined, by default it will load the properties diffyreplayer.properties.dev
 */
public enum DiffyReplayerProperties {
    INSTANCE;

    private final Logger LOG = LoggerFactory.getLogger(DiffyReplayerProperties.class);

    public static final String LOCAL_ENVIRONMENT = "dev";

    // The file should be in the resources folder.
    // Based on the environment variable "ENVIRONMENT
    private static final String DIFFY_FILE = "/diffyreplayer.properties.%s";
    private static final String ENVIRONMENT = "ENVIRONMENT";

    // At most how many request will be processed by Diffy.
    public static final String DIFFY_THREAD_POOL = "DIFFY_THREAD_POOL";
    // Where Diffy Server is residing.
    public static final String DIFFY_URL = "DIFFY_URL";
    // Percentage between 0 and 1 for Queries that the frequency is LOW, 0.01 by Default.
    public static final String DIFFY_LOW_RATE = "DIFFY_LOW_RATE";
    // Percentage between 0 and 1 for Queries that the frequency is LOW, 0.2 by Default.
    public static final String DIFFY_MEDIUM_RATE = "DIFFY_MEDIUM_RATE";
    // Percentage between 0 and 1 for Queries that the frequency is LOW, 0.5 by Default.
    public static final String DIFFY_HIGH_RATE = "DIFFY_HIGH_RATE";
    // Where the patterns for parsing URIS are defined. Empty by Default.
    private static final String PATTERN_FILE = "PATTERN_FILE";

    private final Properties properties = new Properties();
    private String environment;
    private final Map<Pattern, String> patterns = Maps.newLinkedHashMap();

    /**
     * Default Constructor that loads the properties file.
     */
    DiffyReplayerProperties() {
        try {
            environment = Optional.ofNullable(System.getenv(ENVIRONMENT)).orElse(LOCAL_ENVIRONMENT);
            String envFile = String.format(DIFFY_FILE, environment);
            LOG.info("diffyreplayer.properties file to be load: " + envFile);
            InputStream diffyProperties = ClassLoader.class.getResourceAsStream(envFile);
            if (diffyProperties != null) {
                properties.load(diffyProperties);
                LOG.info("Loaded Diffy Replayer properties: " + properties.entrySet());
                populatePatterns();
            } else {
                LOG.warn("Could not find diffyreplayer.properties file");
            }
        } catch (IOException e) {
            LOG.error("Failed to load properties file for diffy replayer", e);
        }
    }

    /**
     * Percentage between 0 and 1 for the queries with low rate.
     * 0.01 If not set
     */
    public double getLowRate() {
        return Double.valueOf(properties.getProperty(DIFFY_LOW_RATE, "0.01"));
    }

    /**
     * Percentage between 0 and 1 for the queries with medium rate.
     * 0.2 If not set
     */
    public double getMediumRate() {
        return Double.valueOf(properties.getProperty(DIFFY_MEDIUM_RATE, "0.2"));
    }

    /**
     * Percentage between 0 and 1 for the queries with high rate.
     * 0.5 If not set
     */
    public double getHighRate() {
        return Double.valueOf(properties.getProperty(DIFFY_HIGH_RATE, "0.5"));
    }

    /**
     * Where the Diffy Server resides.
     * Empty String if not set, meaning a request will never be replayed.
     */
    public String getDiffyUrl() {
        return properties.getProperty(DIFFY_URL, "");
    }

    /**
     * To cap how many diffy request can be made simultaneously.
     * 10 by default.
     */
    public int getDiffyThreadPool() {
        return Integer.valueOf(properties.getProperty(DIFFY_THREAD_POOL, "10"));
    }

    /**
     * Gets the environment running.
     *
     * In case you need to define a DiffyReplayerCondition that can return a different % depending the environment.
     */
    public String getEnvironment() {
        return !Strings.isNullOrEmpty(environment) ? environment : LOCAL_ENVIRONMENT;
    }

    /**
     * Patterns for parsing the URIs
     */
    public Map<Pattern, String> getPatterns() {
        return patterns;
    }
    
    private void populatePatterns() {
        String file = properties.getProperty(PATTERN_FILE, "");
        if (Strings.isNullOrEmpty(file)) {
            return;
        }
        try {
            file = file.startsWith("/") ? file : "/" + file;
            InputStream resource = ClassLoader.class.getResourceAsStream(file);
            if (resource != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(resource));
                String line;
                while ((line = reader.readLine()) != null) {
                    //Skip comments '#'
                    if (!line.startsWith("#") && !line.isEmpty()) {
                        String[] splitted = line.split(",");
                        if (splitted.length != 2) {
                            throw new IllegalArgumentException(String.format("Line %s is not well formatted", line));
                        }
                        Pattern compile = Pattern.compile(splitted[0]);
                        patterns.put(compile, splitted[1]);
                    }
                }
            } else {
                LOG.warn("Could not find diffyreplayer patterns file " + file);
            }
        } catch (IOException e) {
            LOG.warn("Could not load pattern file " + file, e);
        } catch (IllegalArgumentException e) {
            LOG.warn("Could not parse pattern file " + file, e);
        }
    }
}
