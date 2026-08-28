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
    void avatarImages_areAvailableOnClasspath() {
        assertNotNull(Main.class.getResource("/images/DaUser.png"));
        assertNotNull(Main.class.getResource("/images/DaCrow.png"));
    }
}
