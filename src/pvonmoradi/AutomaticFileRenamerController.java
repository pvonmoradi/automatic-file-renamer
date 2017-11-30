package pvonmoradi;

// TODO
// - Add JUnit tests (should have done it before impl. though)

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class AutomaticFileRenamerController {
    private Scene scene;
    @FXML
    Button chooseDirectoryButton;
    @FXML
    Button startRenamingButton;
    @FXML
    Button exportLogButton;
    @FXML
    Button clearLogButton;
    @FXML
    TextField directoryTextField;
    @FXML
    TextField stringToRemoveTextField;
    @FXML
    TextArea logTextArea;
    @FXML
    ProgressBar progressBar;
    @FXML
    MenuItem aboutMenuItem;
    @FXML
    MenuItem closeMenuItem;
    @FXML
    RadioMenuItem modenaMenuItem;
    @FXML
    RadioMenuItem caspianMenuItem;
    
    private HostServices hostServices;
    
    String stringToRemove;

    File targetFile;
    File logDestinationFile;
    StringBuilder sb;

    Task<Void> renameWorker;

    public void initialize() {
        createGUI();
    }

    private void createGUI() {
        DirectoryChooser dc = new DirectoryChooser();
        dc.setTitle("Choose target directory...");
        chooseDirectoryButton.setOnAction(e -> {
            targetFile = dc.showDialog(new Stage());
            if (targetFile != null) {
                directoryTextField.setText(targetFile.getAbsolutePath());
                updateLog("directory is changed to \"" + targetFile.getAbsolutePath() + "\"");
            }

        });

        directoryTextField.setOnAction(e -> {
            targetFile = new File(directoryTextField.getText());
            directoryTextField.setText(targetFile.getAbsolutePath());
            updateLog("directory is changed to \"" + targetFile.getAbsolutePath() + "\"");
        });
        directoryTextField.textProperty().addListener((observable, oldVal, newVal) -> {
            updateLog("directory is changed to \"" + targetFile.getAbsolutePath() + "\"");
        });

        clearLogButton.setOnAction(e -> {
            logTextArea.clear();
        });
        stringToRemoveTextField.setOnAction(e -> {
            stringToRemove = stringToRemoveTextField.getText();
        });
        startRenamingButton.setOnAction(e -> {
            if (directoryTextField.getText().equals("")) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error in input");
                alert.setContentText("You have not chosen the directory yet!");
                alert.showAndWait();
            }
            if (!stringToRemoveTextField.getText().equals("")) {
                renameWorker = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        sb = new StringBuilder();
                        int renameCount = 0;
                        int failedCount = 0;
                        File[] fileList = targetFile.listFiles();
                        int totalCount = fileList.length;
                        stringToRemove = stringToRemoveTextField.getText();
                        for (File f : fileList) {
                            String oldName = f.getName();
                            if (oldName.startsWith(stringToRemove)) {
                                String newPath = targetFile.getAbsolutePath() + "\\"
                                        + oldName.substring(stringToRemove.length());
                                if (f.renameTo(new File(newPath))) {
                                    renameCount++;
                                    updateProgress(renameCount, totalCount);

                                    sb.append("\t" + "\"" + oldName + "\"" + " was renamed to "
                                            + "\"" + oldName.substring(stringToRemove.length())
                                            + "\"" + "\n");

                                } else {
                                    failedCount++;
                                    sb.append("\t" + "failed attempt at renaming \"" + oldName
                                            + "\"" + "\n");
                                }
                            }
                        }
                        updateProgress(totalCount, totalCount);
                        updateLog("\n" + sb.toString());
                        updateLog(
                                "total rename operations : " + renameCount + " out of " + totalCount
                                        + " files with " + failedCount + " failed attempts" + "\n");
                        return null;
                    }
                };
                progressBar.progressProperty().unbind();
                progressBar.progressProperty().bind(renameWorker.progressProperty());
                new Thread(renameWorker).start();
            } else {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error in input");
                alert.setContentText("\"String To Remove\" text box is empty!");
                alert.showAndWait();
            }

        });

        exportLogButton.setOnAction(e -> {
            FileChooser fclog = new FileChooser();
            fclog.setTitle("Save directory of log ...");
            fclog.getExtensionFilters().add(new ExtensionFilter("TXT files", "*.txt"));
            File logDestinationFile = fclog.showSaveDialog(new Stage());
            if (logDestinationFile == null) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error in output");
                alert.setContentText("Problem in selection of log output file. Log was NOT saved!");
                alert.showAndWait();
            } else {
                try {
                    FileWriter fw = new FileWriter(logDestinationFile);
                    fw.write(logTextArea.getText());
                    fw.close();
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Information");
                    alert.setHeaderText("Logging successful");
                    alert.setContentText(
                            "Log text data was saved to " + logDestinationFile.getAbsolutePath());
                    alert.showAndWait();
                } catch (Exception e1) {
                    e1.printStackTrace();
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Error in output");
                    alert.setContentText("--Unexpected Error:--" + "\n" + e1.toString());
                    alert.showAndWait();
                }

            }
        });
        aboutMenuItem.setOnAction(e -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText("About");
            Text text1 = new Text("Automatic File Renamer (version 1.0.0)\n");
            Text text2 = new Text("Developed by Pooya Moradi\n");
            Text text3 = new Text("Email: pvonmoradi@gmail.com\n");
            Text text4 = new Text("GitHub:");
            Hyperlink hp = new Hyperlink("https://github.com/pvonmoradi");
            hp.setOnAction(e2 -> {
                hostServices.showDocument(hp.getText());
            });
            TextFlow tf = new TextFlow(text1, text2, text3, text4, hp);
            alert.getDialogPane().setContent(tf);
            alert.showAndWait();
        });
        modenaMenuItem.setOnAction(e -> {
            caspianMenuItem.setSelected(false);
            Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
        });
        caspianMenuItem.setOnAction(e -> {
            modenaMenuItem.setSelected(false);
            Application.setUserAgentStylesheet(Application.STYLESHEET_CASPIAN);
        });
        closeMenuItem.setOnAction(e -> {
            Platform.exit();
        });
    }



    private void updateLog(String message) {
        LocalDateTime now = LocalDateTime.now();
        String timeString = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:SSS"));
        logTextArea.appendText(timeString + " : " + message + "\r\n");
    }

    public void setScene(Scene scene) {
        this.scene = scene;
        scene.setOnDragOver((DragEvent event) -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                event.acceptTransferModes(TransferMode.LINK);
            } else {
                event.consume();
            }
        });
        scene.setOnDragDropped((DragEvent event) -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                db.getFiles().forEach(file -> {
                    targetFile = file.getParentFile();
                    directoryTextField.setText(targetFile.getAbsolutePath());
                });
            }
            event.setDropCompleted(true);
            event.consume();
        });
    }

    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }
}
