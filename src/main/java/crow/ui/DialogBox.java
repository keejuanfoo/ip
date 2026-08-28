package crow.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    private final Label text;
    private final ImageView displayPicture;

    /**
     * Creates a dialog box containing a message and profile image.
     *
     * @param message Message to display.
     * @param image Sender's profile image.
     */
    public DialogBox(String message, Image image) {
        text = new Label(message);
        displayPicture = new ImageView(image);

        text.setWrapText(true);
        displayPicture.setFitWidth(100.0);
        displayPicture.setFitHeight(100.0);
        this.setAlignment(Pos.TOP_RIGHT);

        this.getChildren().addAll(text, displayPicture);
    }

    /**
     * Creates a right-aligned dialog for a message sent by the user.
     *
     * @param message Message to display.
     * @param image User's profile image.
     * @return User dialog box.
     */
    public static DialogBox getUserDialog(String message, Image image) {
        return new DialogBox(message, image);
    }

    /**
     * Creates a left-aligned dialog for a response sent by Crow.
     *
     * @param message Message to display.
     * @param image Crow's profile image.
     * @return Crow dialog box.
     */
    public static DialogBox getCrowDialog(String message, Image image) {
        DialogBox dialogBox = new DialogBox(message, image);
        dialogBox.flip();
        return dialogBox;
    }

    /**
     * Places the profile image on the left and the message on the right.
     */
    private void flip() {
        this.setAlignment(Pos.TOP_LEFT);
        ObservableList<Node> children = FXCollections.observableArrayList(this.getChildren());
        FXCollections.reverse(children);
        this.getChildren().setAll(children);
    }
}
