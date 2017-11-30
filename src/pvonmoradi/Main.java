/*
 * Automatic File Renamer
 * A simple JavaFX based application to batch-rename files which have a string pre-attached to their names
 * It could be used to remove text water-marks from files
 * Licence: GNU GPLv3 
 * pvonmoradi, 1/12/2017
 *  
 */

package pvonmoradi;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {
    private String versionNumber = "1.0.0";
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AutomaticFileRenamer.fxml"));
            Parent root = (Parent) loader.load();
            AutomaticFileRenamerController controller =
                    (AutomaticFileRenamerController) loader.getController();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            controller.setScene(scene);
            controller.setHostServices(getHostServices());
            primaryStage.setScene(scene);
            primaryStage.setTitle("Automatic File Renamer (version " + versionNumber + " )");
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
