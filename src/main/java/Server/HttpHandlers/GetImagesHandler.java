package Server.HttpHandlers;

import Server.ResourcesManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.List;

public class GetImagesHandler implements HttpHandler {
    ResourcesManager resourcesManager;

    public GetImagesHandler(ResourcesManager manager) {
        this.resourcesManager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        resourcesManager.updateList();
        List<File> files = resourcesManager.getFileList();
        Integer n = files.size();
        byte[] bytes = ByteBuffer.allocate(4).putInt(n).array();
        OutputStream outputStream = exchange.getResponseBody();
        exchange.sendResponseHeaders(200, bytes.length);
        outputStream.write(bytes);
        outputStream.close();
    }
}
