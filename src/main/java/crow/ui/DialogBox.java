package crow.ui;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * Displays a chat message beside its sender's profile image.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialogLabel;
    @FXML
    private ImageView avatarImageView;

    /**
     * Loads the dialog layout and fills it with a message and profile image.
     */
    private DialogBox(String message, Image avatarImage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load a dialog box.", e);
        }

        dialogLabel.setText(message);
        avatarImageView.setImage(avatarImage);
    }

    /**
     * Creates a right-aligned dialog for a message sent by the user.
     */
    public static DialogBox createUserDialog(String message, Image avatarImage) {
        return new DialogBox(message, avatarImage);
    }

    /**
     * Creates a left-aligned dialog for a response sent by Crow.
     */
    public static DialogBox createCrowDialog(String message, Image avatarImage) {
        DialogBox dialogBox = new DialogBox(message, avatarImage);
        dialogBox.flipAlignment();
        return dialogBox;
    }

    /**
     * Places the profile image on the left and the message on the right.
     */
    private void flipAlignment() {
        ObservableList<Node> dialogElements = FXCollections.observableArrayList(getChildren());
        Collections.reverse(dialogElements);
        getChildren().setAll(dialogElements);
        setAlignment(Pos.TOP_LEFT);
    }
}
