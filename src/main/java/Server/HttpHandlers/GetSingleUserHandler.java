package Server.HttpHandlers;

import Server.DatabaseManager;
import Server.User;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.AllArgsConstructor;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

@AllArgsConstructor
public class GetSingleUserHandler implements HttpHandler {
    DatabaseManager dm;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        String[] e = query.split("=");
        Integer id = Integer.parseInt(e[1]);

        User user = dm.find(id);
        try {
            String response = buildJSON(user).toString();
            OutputStream os = exchange.getResponseBody();
            exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
            os.write(response.getBytes(StandardCharsets.UTF_8));
            os.close();
        } catch (JSONException ex){
            System.err.println(ex);
        }

    }

    private JSONObject buildJSON(User user) throws JSONException {
        return new JSONObject()
                .put("id", user.getId().toString())
                .put("username", user.getUsername())
                .put("highestRole", user.getRole())
                .put("highestPolicy", user.getPolicy());
    }
}
