package Server;

import Server.HttpHandlers.*;
import com.sun.net.httpserver.HttpServer;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.IOException;
import java.net.InetSocketAddress;

public class ServerApp {
    public static void main(String[] args) throws Exception{
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
            DatabaseManager dm = new DatabaseManager(Persistence.createEntityManagerFactory("testPU"), User.class);
            ResourcesManager rm = new ResourcesManager();
            dm.generateSampleData();
            server.createContext("/login", new LoginHandler(dm));
            server.createContext("/getImages", new GetImagesHandler(rm));
            server.createContext("/getSingleImage", new GetSingleImageHandler(rm));
            server.createContext("/deleteImage", new DeleteImageHandler(rm, dm));
            server.createContext("/sendImage", new SendImageHandler(rm));
            server.createContext("/getUsers", new GetUsersHandler(dm));
            server.createContext("/getSingleUser", new GetSingleUserHandler(dm));
            server.createContext("/deleteUser", new DeleteUserHandler(dm));
            server.createContext("/addUser", new AddUserHandler(dm));
            server.setExecutor(null);
            System.out.println("Starting...");
            server.start();
        } catch (IOException ex){
            System.out.println("io exception");
        }
    }
}
