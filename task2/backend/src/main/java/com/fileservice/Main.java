package com.fileservice;

import com.fileservice.router.RouterConfig;
import com.common.core.config.Config;
import com.common.core.db.DatabaseConnection;
import com.fatboyindustrial.gsonjavatime.Converters;
import com.common.core.http.MultipartParser;
import com.common.core.http.OurHttpServer;
import com.common.core.http.ResponseHelper;
import com.common.core.http.Router;
import com.common.core.migration.*;
import com.common.core.transaction.TransactionalProxy;
import com.fileservice.handler.AuthHandler;
import com.fileservice.handler.FileHandler;
import com.fileservice.mapper.*;
import com.fileservice.middleware.AuthenticationMiddleware;
import com.fileservice.middleware.CorsMiddleware;
import com.fileservice.middleware.ExceptionHandlerMiddleware;
import com.fileservice.repository.*;
import com.fileservice.service.*;
import com.fileservice.util.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.sql.Connection;

/**
 * Главный класс приложения
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("=== Starting File Service Application ===\n");
        
        try {
            // ============================================
            // 1. Создаем основные компоненты (Config, Gson)
            // ============================================
            Config config = new Config();
            Gson gson = Converters.registerAll(new GsonBuilder())
                .setPrettyPrinting()
                .create();
            
            System.out.println("Configuration loaded");
            
            // ============================================
            // 2. Инициализируем подключение к БД
            // ============================================
            DatabaseConnection databaseConnection = new DatabaseConnection(config);
            Connection connection = databaseConnection.getConnection();
            
            // ============================================
            // 3. Запускаем миграции
            // ============================================
            IMigrationRepository migrationRepository = new MigrationRepository(connection);
            IMigrationFileReader migrationFileReader = new MigrationFileReader();
            MigrationService migrationService = new MigrationService(connection, migrationRepository, migrationFileReader);
            migrationService.runMigrations();
            
            // ============================================
            // 4. Создаем Mappers
            // ============================================
            IMapper<com.fileservice.model.User> userMapper = new UserMapper();
            IMapper<com.fileservice.model.Session> sessionMapper = new SessionMapper();
            IMapper<com.fileservice.model.FileInfo> fileInfoMapper = new FileInfoMapper();
            
            // ============================================
            // 5. Создаем Repositories (зависят от Connection + Mappers)
            // ============================================
            IUserRepository userRepository = new UserRepository(connection, userMapper);
            ISessionRepository sessionRepository = new SessionRepository(connection, sessionMapper);
            IFileRepository fileRepository = new FileRepository(connection, fileInfoMapper);
            
            // ============================================
            // 6. Создаем Utilities
            // ============================================
            ITokenService tokenService = new JwtUtil(config);
            IPasswordHasher passwordHasher = new PasswordUtil();
            ResponseHelper responseHelper = new ResponseHelper(gson);
            MultipartParser multipartParser = new MultipartParser();
            
            // ============================================
            // 7. Создаем Services (зависят от Repositories + Utilities)
            // ============================================
            
            // 7.1. Создаем реализации сервисов
            AuthService authServiceImpl = new AuthService(
                userRepository,
                sessionRepository,
                tokenService,
                passwordHasher
            );
            
            FileService fileServiceImpl = new FileService(
                fileRepository,
                config
            );
            
            // 7.2. Оборачиваем в транзакционные прокси
            IAuthService authService = TransactionalProxy.wrap(
                IAuthService.class,
                authServiceImpl,
                connection
            );
            
            IFileService fileService = TransactionalProxy.wrap(
                IFileService.class,
                fileServiceImpl,
                connection
            );
            
            System.out.println("Services wrapped with transactional proxy");
            
            // ============================================
            // 8. Создаем Middleware
            // ============================================
            CorsMiddleware corsMiddleware = new CorsMiddleware();
            
            AuthenticationMiddleware authenticationMiddleware = new AuthenticationMiddleware(
                sessionRepository,
                tokenService,
                passwordHasher
            );
            
            ExceptionHandlerMiddleware exceptionHandlerMiddleware = new ExceptionHandlerMiddleware(responseHelper);
            
            // ============================================
            // 9. Создаем Handlers
            // ============================================
            AuthHandler authHandler = new AuthHandler(
                authService,
                gson,
                responseHelper,
                multipartParser
            );
            
            FileHandler fileHandler = new FileHandler(
                fileService,
                gson,
                responseHelper,
                multipartParser
            );
            
            // ============================================
            // 10. Конфигурируем Router через RouterConfig
            // ============================================
            Router router = RouterConfig.configure(
                authHandler,
                fileHandler,
                corsMiddleware,
                authenticationMiddleware,
                exceptionHandlerMiddleware
            );
            
            // ============================================
            // 11. Создаем и запускаем HTTP Server
            // ============================================
            int port = config.getServerPort();
            OurHttpServer server = new OurHttpServer(port, router);
            server.start();
            
            System.out.println("HTTP Server started on port " + port);
            System.out.println("API available at: http://localhost:" + port);
            
            // ============================================
            // 12. Запускаем фоновую задачу очистки файлов
            // ============================================
            FileCleanupService fileCleanupService = new FileCleanupService(
                fileRepository,
                config
            );
            fileCleanupService.start();
            
            System.out.println("\n=== Application started successfully ===");
            System.out.println("Press Ctrl+C to stop the server");
            
            // ============================================
            // 13. Graceful shutdown
            // ============================================
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\n=== Shutting down ===");
                server.stop();
                fileCleanupService.stop();
                databaseConnection.closeConnection();
                System.out.println("Application stopped");
            }));
            
        } catch (Exception e) {
            System.err.println("Failed to start application: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}

