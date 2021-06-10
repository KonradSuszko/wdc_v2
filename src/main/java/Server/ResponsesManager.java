package Server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.TextCodec;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public final class ResponsesManager {
    private static final String SECRET = "secret";
    public static void TokenExpiredResponse(User user, DatabaseManager dm, HttpExchange exchange) throws IOException {
        int id = user.getId();
        //System.out.println(id);
        dm.nullToken(id);
        System.out.println("token nullified");
        AccessDeniedResponse(exchange);
    }
    public static void OkResponse(HttpExchange exchange) throws IOException{
        String response = "ok";
        exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
        exchange.getResponseBody().write(response.getBytes(StandardCharsets.UTF_8));
        exchange.getResponseBody().close();
    }
    public static void AccessDeniedResponse(HttpExchange exchange) throws IOException{
        String response = "Access denied";
        exchange.sendResponseHeaders(403, response.getBytes(StandardCharsets.UTF_8).length);
        exchange.getResponseBody().write(response.getBytes(StandardCharsets.UTF_8));
        exchange.getResponseBody().close();
    }
    public static void OkResponse(HttpExchange exchange, User user) throws IOException{
        String response = "ok";
        Date now = new Date();
        String jws = Jwts.builder()
                .setId(user.getUsername())
                .setIssuedAt(now)
                .setExpiration(Date.from(Instant.ofEpochMilli(now.getTime() + TimeUnit.HOURS.toMillis(2))))
                .signWith(SignatureAlgorithm.HS256, TextCodec.BASE64.encode(SECRET))
                .compact();
        Headers headers = exchange.getResponseHeaders();
        headers.add("Token", jws);
        user.setLastToken(jws);
        exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
        exchange.getResponseBody().write(response.getBytes(StandardCharsets.UTF_8));
        exchange.getResponseBody().close();
    }

}
