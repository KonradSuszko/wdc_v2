package Server.HttpHandlers;

import Server.DatabaseManager;
import Server.User;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class LogoutHandler implements HttpHandler {
    DatabaseManager databaseManager;

    public LogoutHandler(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        Headers headers = exchange.getRequestHeaders();
        String token = headers.getFirst("Token");
        String response = null;
        try{
            User user = databaseManager.findByToken(token);
            databaseManager.nullToken(user.getId());
            response = "ok";
        } catch (NullPointerException ex){
            System.err.println(ex);
            response = "not good";
        }
        exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes(StandardCharsets.UTF_8));
        os.close();
    }
}
