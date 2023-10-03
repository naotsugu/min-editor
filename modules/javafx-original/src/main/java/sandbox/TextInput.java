package sandbox;

import javafx.beans.property.ObjectProperty;
import javafx.scene.text.Font;

public class TextInput {

    private final TextContent content;

    private ObjectProperty<Font> font;


    protected TextInput(final TextContent content) {
        this.content = content;
    }


    /**
     * Returns the text input's content model.
     * @return the text input's content model
     */
    protected final TextContent getContent() {
        return content;
    }
}
