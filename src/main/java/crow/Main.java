package crow;

import crow.ui.MainWindow;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Displays the FXML-based JavaFX user interface for Crow.
 */
public class Main extends Application {
    private final Crow crow = new Crow();

    /**
     * Loads and displays the application's primary window.
     *
     * @param stage Primary stage provided by JavaFX.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    Main.class.getResource("/view/MainWindow.fxml"));
            BorderPane root = fxmlLoader.load();
            fxmlLoader.<MainWindow>getController().setCrow(crow);

            stage.setScene(new Scene(root));
            stage.setTitle("Crow");
            stage.setMinHeight(600.0);
            stage.setMinWidth(400.0);
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load the main window.", e);
        }
    }
}
