package tilesgui;

import javafx.application.Application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;


/**
 * Main class that creates animation rectangle and sets up the FXML and CSS files with the scene.
 */
public class Main extends Application {

    /**
     * Loads FXML and CSS, sets up scene and initializes animation rectangle.
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        BorderPane root = FXMLLoader.load(getClass().getResource("8tilesgui.fxml"));
        Rectangle rect = new Rectangle(50, 50);
        rect.setStroke(Color.LIGHTGRAY);
        rect.setFill(Color.LIGHTGRAY);
        rect.relocate(100, 80);
        rect.setId("animationRect");
        rect.setVisible(false);
        root.getChildren().add(rect);

        primaryStage.setTitle("8 Tiles Puzzle");
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("stylesheet.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Launches app
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }
}
