package crow;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;

import crow.ui.DialogBox;
import javafx.application.Application;
import javafx.scene.layout.HBox;

class MainTest {
    @Test
    void main_isJavaFxApplication() {
        assertTrue(Application.class.isAssignableFrom(Main.class));
    }

    @Test
    void launcher_hasPublicStaticMainMethod() throws NoSuchMethodException {
        Method mainMethod = Launcher.class.getMethod("main", String[].class);

        assertTrue(Modifier.isPublic(mainMethod.getModifiers()));
        assertTrue(Modifier.isStatic(mainMethod.getModifiers()));
    }

    @Test
    void dialogBox_isReusableHBoxControl() {
        assertTrue(HBox.class.isAssignableFrom(DialogBox.class));
    }

    @Test
    void dialogBox_hasUserAndCrowFactoryMethods() throws NoSuchMethodException {
        Method userFactory = DialogBox.class.getMethod(
                "getUserDialog", String.class, javafx.scene.image.Image.class);
        Method crowFactory = DialogBox.class.getMethod(
                "getCrowDialog", String.class, javafx.scene.image.Image.class);

        assertTrue(Modifier.isStatic(userFactory.getModifiers()));
        assertTrue(Modifier.isStatic(crowFactory.getModifiers()));
    }

    @Test
    void main_hasUserInputHandler() throws NoSuchMethodException {
        assertNotNull(Main.class.getDeclaredMethod("handleUserInput"));
    }

    @Test
    void avatarImages_areAvailableOnClasspath() {
        assertNotNull(Main.class.getResource("/images/DaUser.png"));
        assertNotNull(Main.class.getResource("/images/DaCrow.png"));
    }
}
