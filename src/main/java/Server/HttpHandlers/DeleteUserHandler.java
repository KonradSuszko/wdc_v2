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
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;


@AllArgsConstructor
public class DeleteUserHandler implements HttpHandler {
    DatabaseManager dm;
    private static final String SECRET = "siema";
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
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
        else if(user.getRole() == Role.ADMIN) {
            String query = exchange.getRequestURI().getQuery();
            String[] e = query.split("=");
            Integer id = Integer.parseInt(e[1]);
            user = dm.find(id);
            dm.delete(user);
            ResponsesManager.OkResponse(exchange);
        }
        else{
            ResponsesManager.AccessDeniedResponse(exchange);
        }
    }
}
