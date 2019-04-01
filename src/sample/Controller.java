package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

public class Controller implements Initializable {

    @FXML
    private ImageView view1;
    @FXML
    private Slider seek1;
    @FXML
    private ImageView view2;
    @FXML
    private Slider seek2;
    @FXML
    private Canvas canvas1;
    @FXML
    private Button playpauseButton1;
    @FXML
    private Button playPauseButton2;
    @FXML
    private Button startLinkButton;
    @FXML
    private Button endLinkButton;
    @FXML
    private TextField startFrame;
    @FXML
    private TextField endFrame;
    @FXML
    private ListView linkList;
    @FXML
    private TextField linkName;
    @FXML
    private Button linkButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button submitButton;
    @FXML
    private TextField linkStartFrame;
    @FXML
    private Slider boxWidth;
    @FXML
    private Slider boxHeight;

    private MediaPlayer[] mediaPlayer;
    private Main main;
    private Image[] image;
    private boolean[] isPlaying;
    private Timeline[] timeline;
    public int[] currentFrame;
    public String videoOne;
    public String videoTwo;
    private ObservableList<String> items;
    public String boxCoordinates;
    private int widthOffset = 40;
    private int heightOffset = 40;

    private String prefix = "C:\\Users\\abhin\\Desktop\\USC Stuff\\CSCI 576 Multimedia Systems\\Final Project\\Data\\USC\\USC\\USCOne\\";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        isPlaying = new boolean[2];
        currentFrame = new int[2];
        image = new Image[2];
        timeline = new Timeline[2];
        mediaPlayer = new MediaPlayer[2];
        isPlaying[0] = false;
        isPlaying[1] = false;
        // defaults
        boxCoordinates = "176:144";
        videoOne = "C:\\Users\\abhin\\Desktop\\USC Stuff\\CSCI 576 Multimedia Systems\\Final Project\\Data\\USC\\USC\\USCOne\\USCOne0001.rgb";
        videoTwo = "C:\\Users\\abhin\\Desktop\\USC Stuff\\CSCI 576 Multimedia Systems\\Final Project\\Data\\USC\\USC\\USCTwo\\USCTwo0001.rgb";

        items = FXCollections.observableArrayList();
        linkList.setItems(items);


        seek1.valueProperty().addListener((observable, oldValue, newValue) -> {
//            double position = Math.floor((double) newValue) * 33.33;
            timeline[0].pause();
            isPlaying[0] = false;
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

        seek2.valueProperty().addListener((observable, oldValue, newValue) -> {
//            double position = Math.floor((double) newValue) * 33.33;
            timeline[1].pause();
            isPlaying[1] = false;
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

        canvas1.addEventHandler(MouseEvent.MOUSE_CLICKED,
            new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent t) {
                    timeline[0].pause();
                    mediaPlayer[0].pause();
                    isPlaying[0] = false;
                    boxCoordinates = (int) Math.floor(t.getX()) + ":" + (int) Math.floor(t.getY());

                    GraphicsContext gc = canvas1.getGraphicsContext2D();
                    gc.clearRect(0, 0, canvas1.getWidth(), canvas1.getHeight());
                    gc.setGlobalAlpha(0.5);
                    gc.setFill(Color.valueOf("#c0c0c0"));
                    gc.fillRect(t.getX() - widthOffset, t.getY() - heightOffset, widthOffset*2, heightOffset*2);
                }
            });

        boxWidth.valueProperty().addListener((observable, oldValue, newValue) -> {
            int curPosX = (int)Math.floor(Double.parseDouble(boxCoordinates.split(":")[0])), curPosY = (int)Math.floor(Double.parseDouble(boxCoordinates.split(":")[1]));
            widthOffset = (int) Math.floor((Double) newValue);

            GraphicsContext gc = canvas1.getGraphicsContext2D();
            gc.clearRect(0, 0, canvas1.getWidth(), canvas1.getHeight());
            gc.setGlobalAlpha(0.5);
            gc.setFill(Color.valueOf("#c0c0c0"));
            gc.fillRect(curPosX - widthOffset, curPosY - heightOffset, widthOffset*2, heightOffset*2);
        });

        boxHeight.valueProperty().addListener((observable, oldValue, newValue) -> {
            int curPosX = (int)Math.floor(Double.parseDouble(boxCoordinates.split(":")[0])), curPosY = (int)Math.floor(Double.parseDouble(boxCoordinates.split(":")[1]));
            heightOffset = (int) Math.floor((Double) newValue);

            GraphicsContext gc = canvas1.getGraphicsContext2D();
            gc.clearRect(0, 0, canvas1.getWidth(), canvas1.getHeight());
            gc.setGlobalAlpha(0.5);
            gc.setFill(Color.valueOf("#c0c0c0"));
            gc.fillRect(curPosX - widthOffset, curPosY - heightOffset, widthOffset*2, heightOffset*2);
        });

    }

    public void videoLoop(String videoPrefix, int videoNumber, int startFrame) throws InterruptedException {
        currentFrame[videoNumber] = startFrame;

        String audioFile = videoPrefix + ".wav";
        Media media = new Media(new File(audioFile).toURI().toString());
        mediaPlayer[videoNumber] = new MediaPlayer(media);
        mediaPlayer[videoNumber].setAutoPlay(true);
//        mediaPlayer.setOnReady(new Runnable() {
//            @Override
//            public void run() {
//                mediaPlayer.play();
//            }
//        });


        timeline[videoNumber] = new Timeline(new KeyFrame(Duration.millis(33.33333), new EventHandler<ActionEvent>() {

            private int i = 1;
            String imageName = "";

            @Override
            public void handle(ActionEvent event) {
                imageName = videoPrefix + String.format("%04d", currentFrame[videoNumber]) + ".rgb";
                image[videoNumber] = Main.getImage(imageName);
                if (videoNumber == 0)
                    view1.setImage(image[videoNumber]);
                else
                    view2.setImage(image[videoNumber]);
                currentFrame[videoNumber]++;
                if (currentFrame[videoNumber] >= 8999)
                    timeline[videoNumber].stop();
                i++;
            }
        }));
        isPlaying[videoNumber] = true;
        timeline[videoNumber].setCycleCount(9000);
//        timeline.play();
    }

    @FXML
    public void playPauseToggle1() {
        if (isPlaying[0]) {
            timeline[0].pause();
            isPlaying[0] = false;
            mediaPlayer[0].pause();
            playpauseButton1.setText("\u25b6");
        }
        else {
            timeline[0].play();
            isPlaying[0] = true;
            mediaPlayer[0].seek(Duration.millis(currentFrame[0]*33.33));
            mediaPlayer[0].play();
            playpauseButton1.setText("\u23f8");
        }
    }

    @FXML
    public void playPauseToggle2() {
        if (isPlaying[1]) {
            timeline[1].pause();
            mediaPlayer[1].pause();
            isPlaying[1] = false;
            playPauseButton2.setText("\u25b6");
        }
        else {
            timeline[1].play();
            mediaPlayer[1].seek(Duration.millis(currentFrame[1]*33.33));
            mediaPlayer[1].play();
            isPlaying[1] = true;
            playPauseButton2.setText("\u23f8");
        }
    }

    @FXML
    public void getSourceVideo() throws InterruptedException {
        if (timeline != null && timeline[0] != null) {
            timeline[0].stop();
            mediaPlayer[0].stop();
        }
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open File");
        chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("RGB Files", "*.rgb"));

        File file = chooser.showOpenDialog(new Stage());
        videoOne = file.getAbsolutePath();
        String fileprefix = videoOne.substring(0, videoOne.length() - 8);
        videoLoop(fileprefix, 0, 1);

        mediaPlayer[0].setOnReady(new Runnable() {
            @Override
            public void run() {
                mediaPlayer[0].play();
                timeline[0].play();
                isPlaying[0] = true;
            }
        });
    }

    @FXML
    public void getTargetVideo() throws InterruptedException {
        if (timeline != null && timeline[1] != null) {
            timeline[1].stop();
            mediaPlayer[1].stop();
        }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open File");
        chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("RGB Files", "*.rgb"));

        File file = chooser.showOpenDialog(new Stage());
        videoTwo = file.getAbsolutePath();
        String fileprefix = videoTwo.substring(0, videoTwo.length() - 8);
        videoLoop(fileprefix, 1, 1);

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
    public void startLinkProcess() {
        startFrame.setText(String.valueOf(currentFrame[0]));
        timeline[0].play();
        mediaPlayer[0].play();
        isPlaying[0] = true;
    }

    @FXML
    public void endLinkProcess() {
        timeline[0].pause();
        mediaPlayer[0].pause();
        isPlaying[0] = false;
        endFrame.setText(String.valueOf(currentFrame[0]));
    }

    @FXML
    public void setLink() {
        timeline[1].pause();
        mediaPlayer[1].pause();
        isPlaying[1] = false;
        linkStartFrame.setText(String.valueOf(currentFrame[1]));
    }

    @FXML
    public void makeLink() {

        // defaults
        String dataLinkName = "Link";
        String dataStartFrame = "0";
        String dataEndFrame = "100";
        String dataLinkStartFrame = "0";

        /*
        format: linkName : startFrame : endFrame : linkStartFrame : boxX : boxY : boxW : boxH : videoTwo
         */
        String filePrefix = videoTwo.substring(0, videoTwo.length() - 8);
        String boxOffsets = String.valueOf(widthOffset) + ":" + String.valueOf(heightOffset);
        dataLinkName = linkName.getText();
        if (dataLinkName.length() < 1 || dataLinkName == "") {
            dataLinkName = "Link";
        }
        dataStartFrame = startFrame.getText();
        if (dataStartFrame.length() < 1 || dataStartFrame == "") {
            dataStartFrame = "0";
        }
        dataEndFrame = endFrame.getText();
        if (dataEndFrame.length() < 1 || dataEndFrame == "") {
            dataEndFrame = "100";
        }
        if (Integer.parseInt(dataEndFrame) < Integer.parseInt(dataStartFrame))
            dataEndFrame = dataStartFrame;

        dataLinkStartFrame = linkStartFrame.getText();
        if (dataLinkStartFrame.length() < 1 || dataLinkStartFrame == "") {
            dataLinkStartFrame = "0";
        }
        String linkEntry = dataLinkName + ":" + dataStartFrame + ":" + dataEndFrame + ":" + dataLinkStartFrame + ":" + boxCoordinates + ":" + boxOffsets + ":" + filePrefix;
        items.add(linkEntry);
    }

    @FXML
    public void deleteLink() {
        int selectedIdx = linkList.getSelectionModel().getSelectedIndex();
        if (selectedIdx >= 0) {
            linkList.getItems().remove(selectedIdx);
        }
    }

    @FXML
    public void submitHyperVideo() throws IOException {
        if (timeline != null) {
            if (timeline[0] != null) {
                timeline[0].stop();
                mediaPlayer[0].stop();
            }
            if (timeline[1] != null) {
                timeline[1].stop();
                mediaPlayer[1].stop();
            }
        }


        // default
        String listData = "Link:0:100:0:176:144:40:40:" + videoTwo.substring(0, videoTwo.length() - 8);
        System.out.println(listData);

        listData = String.join("\n", linkList.getItems());
        BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\abhin\\Desktop\\USC Stuff\\CSCI 576 Multimedia Systems\\Final Project\\Data\\metadata.txt"));
        String filePrefix = videoOne.substring(0, videoOne.length() - 8);
        writer.write(filePrefix + "\n");
        writer.write(listData);
        writer.close();
        Main.sceneStart("display.fxml");
    }
}
