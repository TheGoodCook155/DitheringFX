package com.dither.demo;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainController {

    @FXML
    private GridPane pane;

    @FXML
    private Button open;

    @FXML
    private Button save;

    @FXML
    private Label label;

    @FXML
    private ProgressBar progressBar;

    private ScheduledExecutorService timerThread = Executors.newSingleThreadScheduledExecutor();

    public static final int DURATION_SECONDS = 5;

    private List<File> allFiles = new ArrayList<>();

    private File dir;

    public void initialize() {
        progressBar.setVisible(false);
        label.setVisible(false);
    }


    @FXML
    private void loadFiles() {
//        System.out.println("test");
        allFiles.clear();
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Please choose file to convert");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("png", "*.png"),
                new FileChooser.ExtensionFilter("jpg", "*.jpg"),
                new FileChooser.ExtensionFilter("jpeg", "*.jpeg")

        );
        File file;
        List<File> files = chooser.showOpenMultipleDialog(pane.getScene().getWindow());
        if (files != null) {
            files.stream().forEach(e -> {
//                System.out.println(e);
                allFiles.add(e);
//                System.out.println("============================");

            });
        } else if (files == null) {
            System.out.println("Canceled");
        }

//        System.out.println("In all files ");
//        allFiles.stream().forEach(e -> System.out.println(e));
    }

    @FXML
    public void saveFiles() throws IOException {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Please select folder to save file/s");
        dir = chooser.showDialog(pane.getScene().getWindow());
//        System.out.println(dir);
        //save(BufferedImage image, File dir,File savedFile)
        String path = returnSavePath();

        Task task = new Color(allFiles, path);
        Thread thread = new Thread(task);
        progressBar.visibleProperty().bind(task.runningProperty());
        progressBar.progressProperty().bind(task.progressProperty());
        task.setOnRunning(e -> {
            label.setVisible(true);
            label.setText("Converting...");
        });
        task.setOnSucceeded(e -> {
            label.setText("Done!");
            timerThread.schedule(()->{
                label.setVisible(false);
            } ,DURATION_SECONDS, TimeUnit.SECONDS);
            timerThread.shutdown();
        });

        thread.start();

    }



    public String returnSavePath() {
        String toReturn = dir.getPath() + "\\";
        return toReturn;
    }


}