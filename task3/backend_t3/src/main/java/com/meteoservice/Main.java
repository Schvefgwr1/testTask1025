package com.meteoservice;

import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.meteoservice.client.*;
import com.common.core.cache.ICache;
import com.common.core.cache.RedisCache;
import com.common.core.cache.RedisConnection;
import com.common.core.config.Config;
import com.common.core.http.OurHttpServer;
import com.common.core.http.ResponseHelper;
import com.common.core.http.Router;
import com.meteoservice.handler.WeatherHandler;
import com.meteoservice.middleware.CorsMiddleware;
import com.meteoservice.middleware.ExceptionHandlerMiddleware;
import com.meteoservice.router.RouterConfig;
import com.meteoservice.service.IWeatherService;
import com.meteoservice.service.WeatherService;

/**
 * Главный класс приложения Weather Service
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("=== Starting Weather Service Application ===\n");
        
        try {
            // ============================================
            // 1. Создаём основные компоненты (Config, Gson)
            // ============================================
            Config config = new Config();
            Gson gson = Converters.registerAll(new GsonBuilder())
                .setPrettyPrinting()
                .create();
            
            System.out.println("Configuration loaded");
            System.out.println("  - Server port: " + config.getServerPort());
            System.out.println("  - Redis: " + config.getRedisHost() + ":" + config.getRedisPort());
            System.out.println("  - Cache TTL: " + config.getCacheTimeoutSeconds() + " seconds");
            
            // ============================================
            // 2. Инициализируем подключение к Redis
            // ============================================
            RedisConnection redisConnection = new RedisConnection(config);
            ICache<String> cache = new RedisCache(redisConnection.getJedisPool(), gson);
            
            System.out.println("Redis cache initialized");
            
            // ============================================
            // 3. Создаём HTTP клиенты для внешних API
            // ============================================
            IHttpClient httpClient = new HttpClientImpl(config.getApiTimeoutSeconds());
            
            IGeocodingClient geocodingClient = new GeocodingClient(
                httpClient,
                config.getGeocodingApiUrl(),
                gson
            );
            
            IWeatherApiClient weatherApiClient = new WeatherApiClient(
                httpClient,
                config.getWeatherApiUrl(),
                gson
            );
            
            System.out.println("External API clients initialized");
            System.out.println("  - Geocoding API: " + config.getGeocodingApiUrl());
            System.out.println("  - Weather API: " + config.getWeatherApiUrl());
            
            // ============================================
            // 4. Создаём сервисы (бизнес-логика)
            // ============================================
            IWeatherService weatherService = new WeatherService(
                geocodingClient,
                weatherApiClient,
                cache,
                config.getCacheTimeoutSeconds()
            );
            
            System.out.println("Weather service initialized");
            
            // ============================================
            // 5. Создаём утилиты
            // ============================================
            ResponseHelper responseHelper = new ResponseHelper(gson);
            
            // ============================================
            // 6. Создаём Handlers
            // ============================================
            WeatherHandler weatherHandler = new WeatherHandler(
                weatherService,
                gson,
                responseHelper
            );
            
            System.out.println("HTTP handlers created");
            
            // ============================================
            // 7. Создаём Middleware
            // ============================================
            CorsMiddleware corsMiddleware = new CorsMiddleware();
            ExceptionHandlerMiddleware exceptionHandlerMiddleware = 
                new ExceptionHandlerMiddleware(responseHelper);
            
            System.out.println("Middleware configured");
            
            // ============================================
            // 8. Конфигурируем Router
            // ============================================
            Router router = RouterConfig.configure(
                weatherHandler,
                corsMiddleware,
                exceptionHandlerMiddleware
            );
            
            System.out.println("Router configured");
            
            // ============================================
            // 9. Создаём и запускаем HTTP Server
            // ============================================
            int port = config.getServerPort();
            OurHttpServer server = new OurHttpServer(port, router);
            server.start();
            
            System.out.println("\nHTTP Server started successfully!");
            System.out.println("URL: http://localhost:" + port);
            System.out.println("Endpoint: GET /weather?city={city}");
            System.out.println("\nPress Ctrl+C to stop the server\n");
            
            // ============================================
            // 10. Graceful shutdown hook
            // ============================================
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\n\n=== Shutting down Weather Service ===");
                server.stop();
                redisConnection.close();
                System.out.println("Server stopped");
                System.out.println("Redis connection closed");
            }));
            
        } catch (Exception e) {
            System.err.println("\nFailed to start Weather Service application");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}

