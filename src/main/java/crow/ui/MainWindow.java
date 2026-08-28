package crow.ui;

import crow.Crow;
import crow.parser.CommandType;
import crow.parser.Parser;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;

/**
 * Controls Crow's main JavaFX window.
 */
public class MainWindow {
    private final Image userImage = new Image(
            this.getClass().getResourceAsStream("/images/DaUser.png"));
    private final Image crowImage = new Image(
            this.getClass().getResourceAsStream("/images/DaCrow.png"));

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Crow crow;

    /**
     * Configures behavior that depends on controls injected from FXML.
     */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener(
                observable -> scrollPane.setVvalue(1.0));
    }

    /**
     * Supplies the chatbot used to generate responses.
     *
     * @param crow Crow chatbot instance.
     */
    public void setCrow(Crow crow) {
        this.crow = crow;
    }

    /**
     * Adds the user's message and Crow's response, then clears the input field.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = crow.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getCrowDialog(response, crowImage));
        userInput.clear();

        if (isExitCommand(input)) {
            Platform.exit();
        }
    }

    /**
     * Checks whether a command should close the JavaFX application.
     *
     * @param input User command.
     * @return {@code true} when the command is {@code bye}.
     */
    private static boolean isExitCommand(String input) {
        return Parser.parseCommandType(input) == CommandType.BYE;
    }
}
