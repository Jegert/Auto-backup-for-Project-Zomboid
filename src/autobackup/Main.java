package autobackup;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("controller.fxml")));
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("favicon.png"))));
        primaryStage.setTitle("PZ Auto Backup");
        primaryStage.setScene(new Scene(root, 300, 315));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
