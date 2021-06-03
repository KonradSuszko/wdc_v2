package Server.HttpHandlers;

import Server.DatabaseManager;
import Server.User;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


@AllArgsConstructor
public class DeleteUserHandler implements HttpHandler {
    DatabaseManager dm;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        String[] e = query.split("=");
        Integer id = Integer.parseInt(e[1]);

        User user = dm.find(id);
        dm.delete(user);

        String response = "ok";
        exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
        exchange.getResponseBody().write(response.getBytes(StandardCharsets.UTF_8));
        exchange.getResponseBody().close();

    }
}
