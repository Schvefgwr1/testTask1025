package com.fileservice.router;

import com.fileservice.core.http.Router;
import com.fileservice.core.http.StandardRouter;
import com.fileservice.handler.AuthHandler;
import com.fileservice.handler.FileHandler;
import com.fileservice.middleware.AuthenticationMiddleware;
import com.fileservice.middleware.CorsMiddleware;
import com.fileservice.middleware.ExceptionHandlerMiddleware;

import java.util.Map;

/**
 * Конфигурация маршрутов приложения
 */
public class RouterConfig {

    public static Router configure(
        AuthHandler authHandler,
        FileHandler fileHandler,
        CorsMiddleware corsMiddleware,
        AuthenticationMiddleware authMiddleware,
        ExceptionHandlerMiddleware exceptionHandler
    ) {
        StandardRouter router = new StandardRouter();

        router.use(corsMiddleware);
        router.exceptionHandler(exceptionHandler);
        
        router.post("/api/register", authHandler::handleRegister);
        router.post("/api/login", authHandler::handleLogin);

        router.post("/api/files/upload", fileHandler::handleUpload, authMiddleware);
        router.get("/api/files/{uuid}", exchange -> {
            @SuppressWarnings("unchecked")
            Map<String, String> pathParams = (Map<String, String>) exchange.getAttribute("pathParams");
            String uuid = pathParams.get("uuid");
            fileHandler.handleDownload(exchange, uuid);
        }, authMiddleware);
        router.get("/api/stats/files/", fileHandler::handleStats, authMiddleware);

        return router;
    }
}

