package Server;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

@NoArgsConstructor
@Getter
@Setter
public class ResourcesManager {
    private static final File FOLDER = new File("src/main/resources/pics");
    private List<File> fileList = new ArrayList<>(Arrays.asList(FOLDER.listFiles()));
    private List<BufferedImage> images = new ArrayList<>();

    public void updateList(){
        images.clear();
        for(File f : FOLDER.listFiles()){
            BufferedImage image = bufferedImageFromPath(f);
            images.add(image);
        }
    }
    public void deleteFile(int index){
        File f = fileList.get(index);
        f.delete();
        fileList.remove(index);
    }

    public void saveFile(byte[] data){
        try{
            String newName = getRandomString(10);
            newName += ".jpg";
            //System.out.println(FOLDER.getAbsolutePath() + "\\" + newName);
            File file = new File(FOLDER.getAbsolutePath() + "\\" + newName);
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data);
            fos.close();
            updateList();
        } catch (IOException ex){
            System.err.println(ex);
        }
    }

    private BufferedImage bufferedImageFromPath(File file) {
        try {
            return ImageIO.read(file);
        } catch (IOException e) {
            return null;
        }
    }

    private String getRandomString(int n){
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder(n);
        for(int i = 0; i < n; i++){
            int index = (int) (AlphaNumericString.length() * Math.random());
            sb.append(AlphaNumericString.charAt(index));
        }
        return sb.toString();
    }
}
