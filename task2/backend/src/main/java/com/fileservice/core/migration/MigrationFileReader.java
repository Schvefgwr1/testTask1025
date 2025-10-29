package com.fileservice.core.migration;

import com.fileservice.exception.DatabaseException;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Читает SQL файлы миграций из resources на основе конфигурации migrations.yaml
 */
public class MigrationFileReader implements IMigrationFileReader {
    private static final String MIGRATIONS_PATH = "/migrations/";
    private static final String MIGRATIONS_CONFIG = "/migrations.yaml";
    private static final Pattern MIGRATION_FILE_PATTERN = Pattern.compile("V(\\d+)__(.+)\\.sql");

    /**
     * Загружает все доступные файлы миграций из migrations.yaml
     */
    public List<MigrationFile> loadMigrationFiles() {
        List<String> migrationFilenames = loadMigrationConfig();
        List<MigrationFile> files = new ArrayList<>();
        
        for (String filename : migrationFilenames) {
            try {
                MigrationFile file = loadMigrationFile(filename);
                files.add(file);
            } catch (Exception e) {
                throw new DatabaseException("Failed to load migration file: " + filename, e);
            }
        }
        
        if (files.isEmpty()) {
            throw new DatabaseException("No migration files found in migrations.yaml");
        }
        
        System.out.println("Found " + files.size() + " migration file(s)");
        return files;
    }
    
    /**
     * Загружает список миграций из migrations.yaml
     */
    @SuppressWarnings("unchecked")
    private List<String> loadMigrationConfig() {
        try (InputStream is = getClass().getResourceAsStream(MIGRATIONS_CONFIG)) {
            if (is == null) {
                throw new DatabaseException("Migration config file not found: " + MIGRATIONS_CONFIG);
            }
            
            Yaml yaml = new Yaml();
            Map<String, Object> config = yaml.load(is);
            
            if (config == null || !config.containsKey("migrations")) {
                throw new DatabaseException("Invalid migrations.yaml: 'migrations' key not found");
            }
            
            Object migrationsObj = config.get("migrations");
            if (!(migrationsObj instanceof List)) {
                throw new DatabaseException("Invalid migrations.yaml: 'migrations' must be a list");
            }
            
            List<String> migrations = (List<String>) migrationsObj;
            
            if (migrations.isEmpty()) {
                throw new DatabaseException("No migrations defined in migrations.yaml");
            }
            
            return migrations;
            
        } catch (IOException e) {
            throw new DatabaseException("Failed to read migrations.yaml", e);
        }
    }

    /**
     * Загружает конкретный файл миграции
     */
    private MigrationFile loadMigrationFile(String filename) {
        Matcher matcher = MIGRATION_FILE_PATTERN.matcher(filename);
        if (!matcher.matches()) {
            throw new DatabaseException("Invalid migration filename format: " + filename);
        }
        
        String version = "V" + matcher.group(1);
        String description = matcher.group(2).replace("_", " ");
        
        String content = readFileContent(MIGRATIONS_PATH + filename);
        String checksum = calculateChecksum(content);
        
        return MigrationFile.builder()
            .version(version)
            .description(description)
            .filename(filename)
            .content(content)
            .checksum(checksum)
            .build();
    }

    /**
     * Читает содержимое файла из resources
     */
    private String readFileContent(String resourcePath) {
        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new DatabaseException("Migration file not found: " + resourcePath);
            }
            
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8)
            );
            
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            
            return content.toString();
        } catch (IOException e) {
            throw new DatabaseException("Failed to read migration file: " + resourcePath, e);
        }
    }

    /**
     * Вычисляет MD5 checksum для SQL скрипта
     */
    private String calculateChecksum(String content) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(content.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new DatabaseException("Failed to calculate checksum", e);
        }
    }
}

