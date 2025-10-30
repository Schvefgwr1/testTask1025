package com.meteoservice.router;

import com.meteoservice.core.http.Router;
import com.meteoservice.core.http.StandardRouter;
import com.meteoservice.handler.WeatherHandler;
import com.meteoservice.middleware.CorsMiddleware;
import com.meteoservice.middleware.ExceptionHandlerMiddleware;

/**
 * Конфигурация маршрутов приложения
 */
public class RouterConfig {

    public static Router configure(
        WeatherHandler weatherHandler,
        CorsMiddleware corsMiddleware,
        ExceptionHandlerMiddleware exceptionHandlerMiddleware
    ) {
        StandardRouter router = new StandardRouter();

        router.exceptionHandler(exceptionHandlerMiddleware);

        router.use(corsMiddleware);

        router.get("/weather", weatherHandler::handleGetWeather);

        System.out.println("Router configured");
        return router;
    }
}

