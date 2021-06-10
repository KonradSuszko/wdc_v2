package Server.HttpHandlers;

import Server.*;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.TextCodec;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.Date;
import java.util.List;

public class GetImagesHandler implements HttpHandler {
    ResourcesManager resourcesManager;
    boolean rolesMode;
    DatabaseManager dm;
    private static final String SECRET = "secret";
    int policyRequired = 1;

    public GetImagesHandler(ResourcesManager manager , DatabaseManager dataBaseManager, boolean rolesMode) {
        this.resourcesManager = manager;
        this.rolesMode = rolesMode;
        this.dm = dataBaseManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        resourcesManager.updateList();
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
        //System.out.println("policy: " + user.getPolicy()) ;
        }
        if(new Date().after(result.getBody().get("exp", Date.class))){
            // expired
            ResponsesManager.TokenExpiredResponse(user, dm, exchange);
        }
        else if(rolesMode || (!rolesMode && (user.getPolicy() % (policyRequired*2) >= policyRequired))) {
            List<File> files = resourcesManager.getFileList();
            Integer n = files.size();
            byte[] bytes = ByteBuffer.allocate(4).putInt(n).array();
            OutputStream outputStream = exchange.getResponseBody();
            exchange.sendResponseHeaders(200, bytes.length);
            outputStream.write(bytes);
            outputStream.close();
        }
        else{
            ResponsesManager.AccessDeniedResponse(exchange);
        }

    }
}
