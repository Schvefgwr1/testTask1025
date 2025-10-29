package com.fileservice.core.http;

import com.fileservice.exception.NotFoundException;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Стандартная реализация роутера с поддержкой:
 * - HTTP методов (GET, POST, PUT, PATCH, DELETE)
 * - Path parameters (/api/files/{uuid})
 * - Глобальных middleware
 * - Специфичных для маршрута middleware
 */
public class StandardRouter implements Router {
    private final List<Middleware> globalMiddlewares = new ArrayList<>();
    private final List<Route> routes = new ArrayList<>();
    private Middleware exceptionHandler;

    /**
     * Добавляет глобальный middleware (применяется ко всем маршрутам)
     * 
     * @param middleware middleware для добавления
     * @return this для цепочки вызовов
     */
    public StandardRouter use(Middleware middleware) {
        globalMiddlewares.add(middleware);
        return this;
    }

    /**
     * Устанавливает обработчик исключений (оборачивает всю цепочку middleware)
     * 
     * @param handler обработчик исключений
     * @return this для цепочки вызовов
     */
    public StandardRouter exceptionHandler(Middleware handler) {
        this.exceptionHandler = handler;
        return this;
    }

    /**
     * Регистрирует GET маршрут
     * 
     * @param path путь маршрута (поддерживает параметры: /api/files/{uuid})
     * @param handler терминальный обработчик
     * @param middlewares middleware специфичные для этого маршрута
     * @return this для цепочки вызовов
     */
    public StandardRouter get(String path, MiddlewareChain.TerminalHandler handler, Middleware... middlewares) {
        return addRoute("GET", path, handler, middlewares);
    }

    /**
     * Регистрирует POST маршрут
     */
    public StandardRouter post(String path, MiddlewareChain.TerminalHandler handler, Middleware... middlewares) {
        return addRoute("POST", path, handler, middlewares);
    }

    /**
     * Регистрирует PUT маршрут
     */
    public StandardRouter put(String path, MiddlewareChain.TerminalHandler handler, Middleware... middlewares) {
        return addRoute("PUT", path, handler, middlewares);
    }

    /**
     * Регистрирует PATCH маршрут
     */
    public StandardRouter patch(String path, MiddlewareChain.TerminalHandler handler, Middleware... middlewares) {
        return addRoute("PATCH", path, handler, middlewares);
    }

    /**
     * Регистрирует DELETE маршрут
     */
    public StandardRouter delete(String path, MiddlewareChain.TerminalHandler handler, Middleware... middlewares) {
        return addRoute("DELETE", path, handler, middlewares);
    }

    /**
     * Внутренний метод для добавления маршрута
     */
    private StandardRouter addRoute(String method, String path, MiddlewareChain.TerminalHandler handler, Middleware... middlewares) {
        routes.add(new Route(method, path, handler, middlewares));
        return this;
    }

    @Override
    public void route(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        // Ищем подходящий маршрут
        Route matchedRoute = findRoute(method, path, exchange);

        if (matchedRoute == null) {
            matchedRoute = createNotFoundRoute(path);
        }

        // Строим цепочку middleware: глобальные + специфичные для маршрута
        List<Middleware> middlewareList = new ArrayList<>(globalMiddlewares);
        middlewareList.addAll(Arrays.asList(matchedRoute.middlewares));

        MiddlewareChain middlewareChain = new MiddlewareChain(middlewareList, matchedRoute.handler);

        // Оборачиваем в exception handler (если установлен)
        if (exceptionHandler != null) {
            exceptionHandler.handle(exchange, middlewareChain);
        } else {
            middlewareChain.proceed(exchange);
        }
    }

    /**
     * Ищет маршрут, соответствующий методу и пути
     */
    private Route findRoute(String method, String path, HttpExchange exchange) {
        for (Route route : routes) {
            if (route.matches(method, path)) {
                // Извлекаем path parameters и сохраняем в exchange attributes
                Map<String, String> pathParams = route.extractPathParams(path);
                exchange.setAttribute("pathParams", pathParams);
                return route;
            }
        }
        return null;
    }

    /**
     * Создаёт маршрут для 404 Not Found
     */
    private Route createNotFoundRoute(String path) {
        return new Route("*", "*", (ex) -> {
            throw new NotFoundException("Endpoint not found: " + path);
        });
    }

    /**
     * Внутренний класс, представляющий маршрут
     */
    private static class Route {
        private final String method;
        private final String path;
        private final MiddlewareChain.TerminalHandler handler;
        private final Middleware[] middlewares;
        
        // Для поддержки path parameters
        private final Pattern pathPattern;
        private final List<String> paramNames;

        public Route(String method, String path, MiddlewareChain.TerminalHandler handler, Middleware... middlewares) {
            this.method = method;
            this.path = path;
            this.handler = handler;
            this.middlewares = middlewares;
            
            // Компилируем паттерн для path parameters
            PathPatternResult result = compilePathPattern(path);
            this.pathPattern = result.pattern;
            this.paramNames = result.paramNames;
        }

        /**
         * Проверяет, соответствует ли маршрут методу и пути
         */
        public boolean matches(String method, String path) {
            if (!this.method.equals(method)) {
                return false;
            }
            return pathPattern.matcher(path).matches();
        }

        /**
         * Извлекает значения path parameters из пути
         */
        public Map<String, String> extractPathParams(String path) {
            Map<String, String> params = new HashMap<>();
            Matcher matcher = pathPattern.matcher(path);
            
            if (matcher.matches()) {
                for (int i = 0; i < paramNames.size(); i++) {
                    params.put(paramNames.get(i), matcher.group(i + 1));
                }
            }
            
            return params;
        }

        /**
         * Компилирует путь в регулярное выражение
         */
        private static PathPatternResult compilePathPattern(String path) {
            List<String> paramNames = new ArrayList<>();
            
            // Ищем все {param} и заменяем на capturing groups
            String regex = path;
            Pattern paramPattern = Pattern.compile("\\{([^/]+)\\}");
            Matcher matcher = paramPattern.matcher(path);
            
            while (matcher.find()) {
                String paramName = matcher.group(1);
                paramNames.add(paramName);
                regex = regex.replace("{" + paramName + "}", "([^/]+)");
            }
            
            // Экранируем слэши и компилируем
            Pattern compiled = Pattern.compile("^" + regex + "$");
            
            return new PathPatternResult(compiled, paramNames);
        }

        private static class PathPatternResult {
            final Pattern pattern;
            final List<String> paramNames;

            PathPatternResult(Pattern pattern, List<String> paramNames) {
                this.pattern = pattern;
                this.paramNames = paramNames;
            }
        }
    }
}

