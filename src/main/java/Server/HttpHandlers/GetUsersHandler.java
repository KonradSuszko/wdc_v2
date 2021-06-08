package Server.HttpHandlers;

import Server.DatabaseManager;
import Server.ResponsesManager;
import Server.Role;
import Server.User;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.TextCodec;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GetUsersHandler implements HttpHandler {
    DatabaseManager dm;
    boolean rolesMode;
    private static final String SECRET = "siema";
    int policyRequired = 8;

    public GetUsersHandler(DatabaseManager databaseManager, boolean rolesMode){
        this.rolesMode = rolesMode;
        dm = databaseManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Headers headers = exchange.getRequestHeaders();
        String token = headers.getFirst("Authorization").split(" ")[1];
        Jws<Claims> result = Jwts.parser()
                .setSigningKey(TextCodec.BASE64.encode(SECRET))
                .parseClaimsJws(token);
        List<User> users = dm.findAll();
        User userFound = users.get(0);
        for (User u : users){
            if(u.getLastToken() == null){
                continue;
            }
            if (u.getLastToken().equals(token)) {
                userFound = u;
                break;
            }
        }
        if(new Date().after(result.getBody().get("exp", Date.class))){
            // expired
            ResponsesManager.TokenExpiredResponse(userFound, dm, exchange);
        }
        else if((rolesMode && (userFound.getRole() == Role.ADMIN))  ||
                (!rolesMode && (userFound.getPolicy() % (policyRequired*2) >= policyRequired))) {
            System.out.println("Handling getUsers");
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
        else{
            ResponsesManager.AccessDeniedResponse(exchange);
        }

    }
}
