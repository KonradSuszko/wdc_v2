package Server.HttpHandlers;

import Server.*;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.TextCodec;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

public class SendImageHandler implements HttpHandler {
    ResourcesManager resourcesManager;
    DatabaseManager dm;
    boolean rolesMode;
    private static final String SECRET = "siema";
    int policyRequired = 2;

    public SendImageHandler(ResourcesManager manager, DatabaseManager dm, boolean rolesMode) {
        resourcesManager = manager;
        this.dm = dm;
        this.rolesMode = rolesMode;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        //resourcesManager.updateList();
        System.out.println("saving new file");
        InputStream inputStream = exchange.getRequestBody();
        Headers headers = exchange.getRequestHeaders();
        String token = headers.getFirst("Authorization").split(" ")[1];
        Jws<Claims> result = Jwts.parser()
                .setSigningKey(TextCodec.BASE64.encode(SECRET))
                .parseClaimsJws(token);
        List<User> users = dm.findAll();
        User user = users.get(0);
        for (User u : users){
            if(u.getLastToken() == null){
                continue;
            }
            if (u.getLastToken().equals(token)) {
                user = u;
                break;
            }
        }
        if(new Date().after(result.getBody().get("exp", Date.class))){
            // expired
            ResponsesManager.TokenExpiredResponse(user, dm, exchange);
        }
        else if((rolesMode && (user.getRole() != Role.USER))  ||
                (!rolesMode && (user.getPolicy() % (policyRequired*2) >= policyRequired))) {
            byte[] fileInBytes = inputStream.readAllBytes();
            resourcesManager.saveFile(fileInBytes);
            resourcesManager.updateList();
            ResponsesManager.OkResponse(exchange);
        }
        else{
            ResponsesManager.AccessDeniedResponse(exchange);
        }
    }
}
