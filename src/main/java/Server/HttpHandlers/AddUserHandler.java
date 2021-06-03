package Server.HttpHandlers;

import Server.DatabaseManager;
import Server.Policy;
import Server.Role;
import Server.User;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.AllArgsConstructor;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


@AllArgsConstructor
public class AddUserHandler implements HttpHandler {
    DatabaseManager dm;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String tmp = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        try{
            JSONTokener tokener = new JSONTokener(tmp);
            JSONObject json = new JSONObject(tokener);
            String username = json.getString("username");
            String password = json.getString("password");
            List<Role> roles = generateListOfRoles(json);
            List<Policy> policies = generateListOfPolicies(json);

            User user = new User(username, password, roles, policies);
            System.out.println(user);
            dm.add(user);
            String response = "ok"; //ew obsluga tego ze jest juz taki uzytkownik
            exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes(StandardCharsets.UTF_8));
            os.close();
            inputStream.close();

        } catch (JSONException ex){
            System.err.println(ex);
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
    private List<Policy> generateListOfPolicies(JSONObject json) throws JSONException {
        List<Policy> result = new ArrayList<>();

        if(json.getBoolean("policy1")){
            result.add(Policy.AccessLevel1);
        }
        if(json.getBoolean("policy2")){
            result.add(Policy.AccessLevel2);
        }
        if(json.getBoolean("policy3")){
            result.add(Policy.AccessLevel3);
        }
        if(json.getBoolean("policy4")){
            result.add(Policy.AccessLevel4);
        }
        if(json.getBoolean("policy5")){
            result.add(Policy.AccessLevel5);
        }
        return result;
    }
}
