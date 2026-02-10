package com.common.core.http;

import com.common.core.exception.NotFoundException;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Стандартная реализация роутера с поддержкой HTTP методов, path parameters и middleware
 */
public class StandardRouter implements Router {
    private final List<Middleware> globalMiddlewares = new ArrayList<>();
    private final List<Route> routes = new ArrayList<>();
    private Middleware exceptionHandler;

    public StandardRouter use(Middleware middleware) {
        globalMiddlewares.add(middleware);
        return this;
    }

    public StandardRouter exceptionHandler(Middleware handler) {
        this.exceptionHandler = handler;
        return this;
    }

    public StandardRouter get(String path, MiddlewareChain.TerminalHandler handler, Middleware... middlewares) {
        return addRoute("GET", path, handler, middlewares);
    }

    public StandardRouter post(String path, MiddlewareChain.TerminalHandler handler, Middleware... middlewares) {
        return addRoute("POST", path, handler, middlewares);
    }

    public StandardRouter put(String path, MiddlewareChain.TerminalHandler handler, Middleware... middlewares) {
        return addRoute("PUT", path, handler, middlewares);
    }

    public StandardRouter patch(String path, MiddlewareChain.TerminalHandler handler, Middleware... middlewares) {
        return addRoute("PATCH", path, handler, middlewares);
    }

    public StandardRouter delete(String path, MiddlewareChain.TerminalHandler handler, Middleware... middlewares) {
        return addRoute("DELETE", path, handler, middlewares);
    }

    private StandardRouter addRoute(String method, String path, MiddlewareChain.TerminalHandler handler, Middleware... middlewares) {
        routes.add(new Route(method, path, handler, middlewares));
        return this;
    }

    @Override
    public void route(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        Route matchedRoute = findRoute(method, path, exchange);

        if (matchedRoute == null) {
            matchedRoute = createNotFoundRoute(path);
        }

        List<Middleware> middlewareList = new ArrayList<>(globalMiddlewares);
        middlewareList.addAll(Arrays.asList(matchedRoute.middlewares));

        MiddlewareChain middlewareChain = new MiddlewareChain(middlewareList, matchedRoute.handler);

        if (exceptionHandler != null) {
            exceptionHandler.handle(exchange, middlewareChain);
        } else {
            middlewareChain.proceed(exchange);
        }
    }

    private Route findRoute(String method, String path, HttpExchange exchange) {
        for (Route route : routes) {
            if (route.matches(method, path)) {
                Map<String, String> pathParams = route.extractPathParams(path);
                exchange.setAttribute("pathParams", pathParams);
                return route;
            }
        }
        return null;
    }

    private Route createNotFoundRoute(String path) {
        return new Route("*", "*", (ex) -> {
            throw new NotFoundException("Endpoint not found: " + path);
        });
    }

    private static class Route {
        private final String method;
        private final String path;
        private final MiddlewareChain.TerminalHandler handler;
        private final Middleware[] middlewares;
        private final Pattern pathPattern;
        private final List<String> paramNames;

        public Route(String method, String path, MiddlewareChain.TerminalHandler handler, Middleware... middlewares) {
            this.method = method;
            this.path = path;
            this.handler = handler;
            this.middlewares = middlewares;

            PathPatternResult result = compilePathPattern(path);
            this.pathPattern = result.pattern;
            this.paramNames = result.paramNames;
        }

        public boolean matches(String method, String path) {
            if (!this.method.equals(method)) {
                return false;
            }
            return pathPattern.matcher(path).matches();
        }

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

        private static PathPatternResult compilePathPattern(String path) {
            List<String> paramNames = new ArrayList<>();
            String regex = path;
            Pattern paramPattern = Pattern.compile("\\{([^/]+)\\}");
            Matcher matcher = paramPattern.matcher(path);

            while (matcher.find()) {
                String paramName = matcher.group(1);
                paramNames.add(paramName);
                regex = regex.replace("{" + paramName + "}", "([^/]+)");
            }

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
