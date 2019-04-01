package sample;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;

public class Main extends Application {

    public static Stage myStage;
    public static Parent root;
    public static Scene scene;

    public static Image getImage(String filename) {
        int width = 352, height = 288;

        byte[] fileContent = {0, 0};

        File fi = new File(filename);
        try {
            fileContent = Files.readAllBytes(fi.toPath());
        }catch(Exception e) {
            System.out.println("Oops, read error.");
        }

        BufferedImage bImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int count = 0;
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                int r = fileContent[count] & 0xff;
                int g = fileContent[count + (width * height)] & 0xff;
                int b = fileContent[count + (2 * width * height)] & 0xff;

                count++;

                int pix = 0xff000000 | (r << 16) | (g << 8) | b;
                bImage.setRGB(x, y, pix);
            }
        }

        Image image = SwingFXUtils.toFXImage(bImage, null);

        return image;
    }

    @Override
    public void start(Stage primaryStage) throws Exception{

        myStage = primaryStage;
        myStage.setTitle("Author HyperVideo");

        sceneStart("sample.fxml");

//        sceneStart("display.fxml");




    }

    public static void sceneStart(String sceneName) throws IOException {
        root = FXMLLoader.load(Main.class.getResource(sceneName));
        scene = new Scene(root);
        scene.getStylesheets().add(Main.class.getResource(sceneName.substring(0, sceneName.length() - 5) + "CSS.css").toExternalForm());
        myStage.setScene(scene);
        myStage.setTitle("HyperVideo Player");
        myStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
