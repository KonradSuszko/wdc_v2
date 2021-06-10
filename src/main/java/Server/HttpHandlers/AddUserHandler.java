package Server.HttpHandlers;

import Server.*;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.TextCodec;
import lombok.AllArgsConstructor;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class AddUserHandler implements HttpHandler {
    DatabaseManager dm;
    private static final String SECRET = "secret";
    boolean rolesMode;
    int policyRequired = 16;

    public AddUserHandler(DatabaseManager dm, boolean rolesMode){
        this.dm = dm;
        this.rolesMode = rolesMode;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String tmp = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Headers headers = exchange.getRequestHeaders();
        String token = headers.getFirst("Authorization").split(" ")[1];
        Jws<Claims> result = Jwts.parser()
                .setSigningKey(TextCodec.BASE64.encode(SECRET))
                .parseClaimsJws(token);
        List<User> usersAll = dm.findAll();
        User foundUser = usersAll.get(0);
        for (User u : usersAll){
            if(u.getLastToken() == null){
                continue;
            }
            if (u.getLastToken().equals(token)) {
                foundUser = u;
                break;
            }
        }
        if(new Date().after(result.getBody().get("exp", Date.class))){
            // expired
            ResponsesManager.TokenExpiredResponse(foundUser, dm, exchange);
        }
        else if((rolesMode && (foundUser.getRole() == Role.ADMIN))  ||
                (!rolesMode && (foundUser.getPolicy() % (policyRequired*2) >= policyRequired))) {
            try{
                JSONTokener tokener = new JSONTokener(tmp);
                JSONObject json = new JSONObject(tokener);
                String username = json.getString("username");
                String password = json.getString("password");
                List<Role> roles = generateListOfRoles(json);
                int policy = generatePolicy(json);
                Role role = highestRole(json);
                User user = new User(username, password, role, policy);
                //System.out.println(user);
                //User existent = dm.findByUsername(user.getUsername());
                List<User> users = dm.findAll();
                boolean exists = false;
                for (User u : users){
                    if (u.getUsername().equals(user.getUsername())){
                        exists = true;
                        break;
                    }
                }
                String response = "ok"; //ew obsluga tego ze jest juz taki uzytkownik
                if (exists)
                    response = "user already exists";
                else
                    dm.add(user);
                //String response = "ok";
                ResponsesManager.OkResponse(exchange, user);
                inputStream.close();

            } catch (JSONException ex){
                System.err.println(ex);
            }
        }
        else{
            ResponsesManager.AccessDeniedResponse(exchange);
        }

    }

    private List<Role> generateListOfRoles(JSONObject json) throws JSONException {
        List<Role> result = new ArrayList<>();
        if(json.getBoolean("adminRole")){
            result.add(Role.ADMIN);
        }
        if(json.getBoolean("stuffRole")){
            result.add(Role.STUFF);
        }
        if(json.getBoolean("userRole")){
            result.add(Role.USER);
        }
        return result;
    }
    private Role highestRole(JSONObject json) throws JSONException {
        if(json.getBoolean("adminRole"))
            return Role.ADMIN;
        if(json.getBoolean("stuffRole"))
            return Role.STUFF;
        if(json.getBoolean("userRole"))
            return Role.USER;
        return null;
    }
    private Policy highestPolicy(JSONObject json) throws JSONException{
        if(json.getBoolean("policy5"))
            return Policy.AccessLevel5;
        if(json.getBoolean("policy4"))
            return Policy.AccessLevel4;
        if(json.getBoolean("policy3"))
            return Policy.AccessLevel3;
        if(json.getBoolean("policy2"))
            return Policy.AccessLevel2;
        if(json.getBoolean("policy1"))
            return Policy.AccessLevel1;
        return null;
    }
    private int generatePolicy(JSONObject json) throws JSONException {
        //List<Policy> result = new ArrayList<>();
        int result = 0;
        if(json.getBoolean("policy1")){
            //result.add(Policy.AccessLevel1);
            result += 1;
        }
        if(json.getBoolean("policy2")){
            //result.add(Policy.AccessLevel2);
            result += 2;
        }
        if(json.getBoolean("policy3")){
            //result.add(Policy.AccessLevel3);
            result += 4;
        }
        if(json.getBoolean("policy4")){
            //result.add(Policy.AccessLevel4);
            result += 8;
        }
        if(json.getBoolean("policy5")){
            //result.add(Policy.AccessLevel5);
            result += 16;
        }
        return result;
    }
}
