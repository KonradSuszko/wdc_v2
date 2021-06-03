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
        System.out.println("Handling getUsers");
        List<User> users= dm.findAll();
        System.out.println("Got users from database");
        Integer n = users.size();
        byte[] bytes = ByteBuffer.allocate(4).putInt(n).array();
        List<byte[]> toSend = new ArrayList<>();
        long len = bytes.length;
        toSend.add(bytes);
        for (User u : users){
            int id = u.getId();
            len += ByteBuffer.allocate(4).putInt(id).array().length;
            toSend.add(ByteBuffer.allocate(4).putInt(id).array());
        }
        byte[] bytesToSend = new byte[4 * toSend.size()];
        for(int i = 0; i < toSend.size(); i++){
            bytesToSend[4*i] = toSend.get(i)[0];
            bytesToSend[4*i + 1] = toSend.get(i)[1];
            bytesToSend[4*i + 2] = toSend.get(i)[2];
            bytesToSend[4*i + 3] = toSend.get(i)[3];
        }
        OutputStream os = exchange.getResponseBody();
        exchange.sendResponseHeaders(200, bytesToSend.length);
        os.write(bytesToSend);
        System.out.println("Bytes sent");
        os.close();
    }
}
