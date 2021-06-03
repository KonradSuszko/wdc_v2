package Server.HttpHandlers;

import Server.DatabaseManager;
import Server.ResourcesManager;
import Server.Role;
import Server.User;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.TextCodec;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.lang.Object.*;

public class DeleteImageHandler implements HttpHandler {
    ResourcesManager resourcesManager;
    DatabaseManager dm;
    private static final String SECRET = "siema";
    public DeleteImageHandler(ResourcesManager manager, DatabaseManager dm) {
        this.resourcesManager = manager;
        this.dm = dm;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        resourcesManager.updateList();
        List<File> files = resourcesManager.getFileList();
        String query = exchange.getRequestURI().getQuery();
        InputStream inputStream = exchange.getRequestBody();
        Headers headers = exchange.getRequestHeaders();
        //String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        String token = headers.getFirst("Token");
        System.out.println("request: " + token);
        Jws<Claims> result = Jwts.parser()
                .setSigningKey(TextCodec.BASE64.encode(SECRET))
                .parseClaimsJws(token);
        System.out.println("result: " + result);
        System.out.println(result.getBody().get("exp", Date.class));
        System.out.println(new Date());

        List<User> users = dm.findAll();
        User user = users.get(0);
        for (User u : users){
            if (u.getLastToken().equals(token)) {
                user = u;
                break;
            }
        }
        if(new Date().after(result.getBody().get("exp", Date.class))){
            // expired
            //User user = dm.findByToken(token);
            int id = user.getId();
            System.out.println(id);
            dm.nullToken(id);
            System.out.println("token nullified");
            String response = "Access denied";
            exchange.sendResponseHeaders(403, response.getBytes(StandardCharsets.UTF_8).length);
            exchange.getResponseBody().write(response.getBytes(StandardCharsets.UTF_8));
            exchange.getResponseBody().close();
        }
        else if(user.getRole() != Role.USER) {
            String[] e = query.split("=");
            Integer index = Integer.parseInt(e[1]);
            System.out.println(index);
            resourcesManager.deleteFile(index);
            String response = "ok";
            exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
            exchange.getResponseBody().write(response.getBytes(StandardCharsets.UTF_8));
            exchange.getResponseBody().close();
        }
        else{
            String response = "Access denied";
            exchange.sendResponseHeaders(403, response.getBytes(StandardCharsets.UTF_8).length);
            exchange.getResponseBody().write(response.getBytes(StandardCharsets.UTF_8));
            exchange.getResponseBody().close();
        }
    }
}
