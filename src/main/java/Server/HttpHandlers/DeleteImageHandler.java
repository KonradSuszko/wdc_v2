package Server.HttpHandlers;

import Server.ResourcesManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class DeleteImageHandler implements HttpHandler {
    ResourcesManager resourcesManager;

    public DeleteImageHandler(ResourcesManager manager) {
        this.resourcesManager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        resourcesManager.updateList();
        List<File> files = resourcesManager.getFileList();
        String query = exchange.getRequestURI().getQuery();
        String[] e = query.split("=");
        Integer index = Integer.parseInt(e[1]);
        System.out.println(index);
        resourcesManager.deleteFile(index);
        String response = "ok";
        exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
        exchange.getResponseBody().write(response.getBytes(StandardCharsets.UTF_8));
        exchange.getResponseBody().close();
    }
}
