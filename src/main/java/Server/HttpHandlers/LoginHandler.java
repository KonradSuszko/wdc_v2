package Server.HttpHandlers;

import Server.DatabaseManager;
import Server.User;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.TextCodec;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class LoginHandler implements HttpHandler {
    public LoginHandler(DatabaseManager manager) {
        this.manager = manager;
    }

    DatabaseManager manager;

    private static final String SECRET = "siema";
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("New login request");
        InputStream inputStream = exchange.getRequestBody();
        String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        try {
            JSONTokener tokener = new JSONTokener(requestBody);
            JSONObject loginData = new JSONObject(tokener);
            String username = loginData.getString("username");
            String hashedPassword = loginData.getString("password");
            String response;
            User user;
            try {
                user = manager.findByUsername(username);
            } catch (NullPointerException e){
                user = null;
            }
            if(user == null){
                response = "user not found";
            }
            else if(user.getHashedPassword().equals(hashedPassword)){
                Date now = new Date();
                String jws = Jwts.builder()
                        .setId(username)
                        .setIssuedAt(now)
                        .setExpiration(Date.from(Instant.ofEpochMilli(now.getTime() + TimeUnit.HOURS.toMillis(2))))
                        .signWith(SignatureAlgorithm.HS256, TextCodec.BASE64.encode(SECRET)) //decode?
                        .compact();
                System.out.println(jws);
                manager.updateToken(user.getId(), jws);
                response = jws;
            }
            else{
                response = "bad password";
            }
            exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
            System.out.println(response);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes(StandardCharsets.UTF_8));
            os.close();
            inputStream.close();
        } catch (JSONException ex){
            System.out.println(ex.getMessage());
        }
    }
}
