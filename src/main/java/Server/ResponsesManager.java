package Server;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public final class ResponsesManager {
    public static void TokenExpiredResponse(User user, DatabaseManager dm, HttpExchange exchange) throws IOException {
        int id = user.getId();
        System.out.println(id);
        dm.nullToken(id);
        System.out.println("token nullified");
        AccessDeniedResponse(exchange);
    }
    public static void OkResponse(HttpExchange exchange) throws IOException{
        String response = "ok";
        exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
        exchange.getResponseBody().write(response.getBytes(StandardCharsets.UTF_8));
        exchange.getResponseBody().close();
    }
    public static void AccessDeniedResponse(HttpExchange exchange) throws IOException{
        String response = "Access denied";
        exchange.sendResponseHeaders(403, response.getBytes(StandardCharsets.UTF_8).length);
        exchange.getResponseBody().write(response.getBytes(StandardCharsets.UTF_8));
        exchange.getResponseBody().close();
    }
}
