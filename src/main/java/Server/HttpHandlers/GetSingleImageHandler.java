package Server.HttpHandlers;

import Server.ResourcesManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.AllArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.List;


@AllArgsConstructor
public class GetSingleImageHandler implements HttpHandler {
    ResourcesManager resourcesManager;
    boolean rolesMode;
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        resourcesManager.updateList();
        List<File> files = resourcesManager.getFileList();
        String query = exchange.getRequestURI().getQuery();
        String[] e = query.split("=");
        Integer i = Integer.parseInt(e[1]);
        System.out.println(i);
        File target = files.get(i);
        exchange.sendResponseHeaders(200, target.length());
        OutputStream out = exchange.getResponseBody();
        Files.copy(target.toPath(), out);
        out.close();
    }
}
