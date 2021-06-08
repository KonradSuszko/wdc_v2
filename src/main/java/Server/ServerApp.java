package Server;

import Server.HttpHandlers.*;
import com.sun.net.httpserver.HttpServer;

import javax.persistence.Persistence;
import java.io.IOException;
import java.net.InetSocketAddress;

public class ServerApp {
    public static void main(String[] args){
        try {
            boolean rolesMode = false;
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
            DatabaseManager dm = new DatabaseManager(Persistence.createEntityManagerFactory("testPU"), User.class);
            ResourcesManager rm = new ResourcesManager();
            dm.generateSampleData();
            server.createContext("/login", new LoginHandler(dm, rolesMode));
            server.createContext("/getImages", new GetImagesHandler(rm, dm, rolesMode));
            server.createContext("/getSingleImage", new GetSingleImageHandler(rm, rolesMode));
            server.createContext("/deleteImage", new DeleteImageHandler(rm, dm, rolesMode));
            server.createContext("/sendImage", new SendImageHandler(rm, dm, rolesMode));
            server.createContext("/getUsers", new GetUsersHandler(dm, rolesMode));
            server.createContext("/getSingleUser", new GetSingleUserHandler(dm, rolesMode));
            server.createContext("/deleteUser", new DeleteUserHandler(dm, rolesMode));
            server.createContext("/addUser", new AddUserHandler(dm, rolesMode));
            server.setExecutor(null);
            System.out.println("Starting...");
            server.start();
        } catch (IOException ex){
            System.out.println("io exception");
        }
    }
}
