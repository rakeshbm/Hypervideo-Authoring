package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;

public class DisplayController implements Initializable {

    @FXML
    private ImageView displayView1;
    @FXML
    private ImageView displayView2;
    @FXML
    private Slider displaySeek1;
    @FXML
    private Slider displaySeek2;
    @FXML
    private Button disPlayButton;
    @FXML
    private Button disPauseButton;
    @FXML
    private Canvas displayCanvas;

    private List<String> videoData;
    public int[] currentFrame;
    private Image[] image;
    private boolean[] isPlaying;
    public String videoOne;
    public String videoTwo;
    private Timeline[] timeline;
    private Main main;
    private MediaPlayer[] mediaPlayer;
    private Box[] boxes;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        isPlaying = new boolean[2];
        currentFrame = new int[2];
        image = new Image[2];
        timeline = new Timeline[2];
        mediaPlayer = new MediaPlayer[2];
        isPlaying[0] = false;
        isPlaying[1] = false;

        try {
            videoData = Files.readAllLines(Paths.get("C:\\Users\\abhin\\Desktop\\USC Stuff\\CSCI 576 Multimedia Systems\\Final Project\\Data\\metadata.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        boxes = new Box[videoData.size() - 1];

        String sourcePrefix = videoData.get(0);

        for (int i = 1; i < videoData.size(); i++) {
            String[] data = videoData.get(i).split(":");
            boxes[i-1] = new Box(Integer.parseInt(data[1]), Integer.parseInt(data[2]), Integer.parseInt(data[3]), Integer.parseInt(data[4]), Integer.parseInt(data[5]), Integer.parseInt(data[6]), Integer.parseInt(data[7]), data[8] + ":" +data[9]);
        }

        displaySeek1.valueProperty().addListener((observable, oldValue, newValue) -> {
//            double position = Math.floor((double) newValue) * 33.33;
            timeline[0].pause();
            currentFrame[0] = (int) Math.floor((Double) newValue);
            if (currentFrame[0] > 8999) {
                currentFrame[0] = 8999;
                mediaPlayer[0].pause();
                isPlaying[0] = false;
            }
            timeline[0].play();
            mediaPlayer[0].seek(Duration.millis(currentFrame[0]*33.33));
            mediaPlayer[0].play();
            isPlaying[0] = true;
        });

        displaySeek2.valueProperty().addListener((observable, oldValue, newValue) -> {

            timeline[1].pause();
            currentFrame[1] = (int) Math.floor((Double) newValue);
            if (currentFrame[1] > 8999) {
                currentFrame[1] = 8999;
                mediaPlayer[1].pause();
                isPlaying[1] = false;
            }
            timeline[1].play();
            mediaPlayer[1].seek(Duration.millis(currentFrame[1]*33.33));
            mediaPlayer[1].play();
            isPlaying[1] = true;
        });

        displayCanvas.addEventHandler(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent t) {
                        int posX = (int) Math.floor(t.getX());
                        int posY = (int) Math.floor(t.getY());

                        for (Box b : boxes) {
                            if (currentFrame[0] >= b.startFrame && currentFrame[0] <= b.endFrame && posX > (b.boxX - b.width) && posX < (b.boxX + b.width) && posY > (b.boxY - b.height) && posY < (b.boxY + b.height)) {
                                try {
                                    getTargetVideo(b.target, b.linkFrame);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                });

        try {
            getSourceVideo(sourcePrefix);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // To do: slider listeners, canvas listener
    }

    public void videoLoop(String videoPrefix, int videoNumber, int startFrame) throws InterruptedException {
        currentFrame[videoNumber] = startFrame;

        String audioFile = videoPrefix + ".wav";
        Media media = new Media(new File(audioFile).toURI().toString());
        mediaPlayer[videoNumber] = new MediaPlayer(media);
        mediaPlayer[videoNumber].setAutoPlay(true);
        mediaPlayer[videoNumber].seek(Duration.millis(startFrame * 33.33));


        timeline[videoNumber] = new Timeline(new KeyFrame(Duration.millis(33.33333), new EventHandler<ActionEvent>() {

            private int i = 1;
            String imageName = "";

            GraphicsContext gc = displayCanvas.getGraphicsContext2D();

            @Override
            public void handle(ActionEvent event) {
                imageName = videoPrefix + String.format("%04d", currentFrame[videoNumber]) + ".rgb";
//                System.out.println(imageName);
                image[videoNumber] = Main.getImage(imageName);
                if (videoNumber == 0)
                    displayView1.setImage(image[videoNumber]);
                else
                    displayView2.setImage(image[videoNumber]);
                currentFrame[videoNumber]++;
                if (currentFrame[videoNumber] >= 8999)
                    timeline[videoNumber].stop();
                else {
                    // all canvas operations in here

                    gc.clearRect(0, 0, displayCanvas.getWidth(), displayCanvas.getHeight());
                    gc.setGlobalAlpha(0.5);
                    gc.setFill(Color.valueOf("#c0c0c0"));
                    for (Box b : boxes) {
                        if (currentFrame[videoNumber] >= b.startFrame && currentFrame[videoNumber] <= b.endFrame) {
                            gc.fillRect(b.boxX - b.width, b.boxY - b.height, b.width*2, b.height*2);
                        }
                    }
                }
                i++;
            }
        }));
        isPlaying[videoNumber] = true;
        timeline[videoNumber].setCycleCount(9000);
//        timeline.play();
    }

    public void getSourceVideo(String source) throws InterruptedException {
        if (timeline != null && timeline[0] != null)
            timeline[0].stop();

        videoLoop(source, 0, 1);

        mediaPlayer[0].setOnReady(new Runnable() {
            @Override
            public void run() {
                mediaPlayer[0].play();
                timeline[0].play();
                isPlaying[0] = true;
            }
        });
    }

    public void getTargetVideo(String source, int startFrame) throws InterruptedException {
        if (timeline != null && timeline[1] != null)
            timeline[1].stop();

        timeline[0].pause();
        mediaPlayer[0].pause();
        isPlaying[0] = false;

        videoLoop(source, 1, startFrame);

        mediaPlayer[1].setOnReady(new Runnable() {
            @Override
            public void run() {
                mediaPlayer[1].play();
                timeline[1].play();
                isPlaying[1] = true;
            }
        });
    }

    @FXML
    public void disPlayToggle1() {
        if (isPlaying[0]) {
            timeline[0].pause();
            isPlaying[0] = false;
            mediaPlayer[0].pause();
            disPlayButton.setText("\u25b6");
        }
        else {
            timeline[0].play();
            isPlaying[0] = true;
            mediaPlayer[0].seek(Duration.millis(currentFrame[0]*33.33));
            mediaPlayer[0].play();
            disPlayButton.setText("\u23f8");
        }
    }

    @FXML
    public void disPlayToggle2() {
        if (isPlaying[1]) {
            timeline[1].pause();
            mediaPlayer[1].pause();
            isPlaying[1] = false;
            disPauseButton.setText("\u25b6");
        }
        else {
            timeline[1].play();
            mediaPlayer[1].seek(Duration.millis(currentFrame[1]*33.33));
            mediaPlayer[1].play();
            isPlaying[1] = true;
            disPauseButton.setText("\u23f8");
        }
    }
}
