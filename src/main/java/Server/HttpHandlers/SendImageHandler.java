package Server.HttpHandlers;

import Server.ResourcesManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class SendImageHandler implements HttpHandler {
    ResourcesManager resourcesManager;

    public SendImageHandler(ResourcesManager manager) {
        resourcesManager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("saving new file");
        InputStream inputStream = exchange.getRequestBody();
        byte[] fileInBytes = inputStream.readAllBytes();
        resourcesManager.saveFile(fileInBytes);
        String response = "ok";
        exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes(StandardCharsets.UTF_8));
        os.close();
    }
}
