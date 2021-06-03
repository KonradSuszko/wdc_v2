package Server.HttpHandlers;

import Server.DatabaseManager;
import Server.User;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class GetUsersHandler implements HttpHandler {
    DatabaseManager dm;
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        List<User> users= dm.findAll();
        Integer n = users.size();
        byte[] bytes = ByteBuffer.allocate(4).putInt(n).array();
        OutputStream os = exchange.getResponseBody();
        List<byte[]> ids = new ArrayList<>();
        for (User u : users){
            int id = u.getId();
            ids.add(ByteBuffer.allocate(4).putInt(id).array());
        }
        exchange.sendResponseHeaders(200, bytes.length + ids.size());
        os.write(bytes);
        for(int i = 0; i < n; i++){
            os.write(ids.get(i));
        }
        os.close();
    }
}
