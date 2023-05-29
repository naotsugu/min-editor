package javafx.scene.text;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.scene.text.TextLayout;
import com.sun.javafx.tk.Toolkit;

public class TextLayoutShim {

    /** The delegated text layout. */
    private final TextLayout textLayout;


    public TextLayoutShim() {
        this.textLayout = Toolkit.getToolkit().getTextLayoutFactory().createLayout();
    }


    /**
     * Sets the content for the TextLayout. Shorthand for single span text
     * (no rich text).
     * @return returns true is the call modifies the layout internal state.
     */
    public boolean setContent(String string, Object font) {
        return textLayout.setContent(string, font);
    }


    /**
     * Sets the alignment for the TextLayout.
     * @return returns true is the call modifies the layout internal state.
     */
    public boolean setAlignment(TextAlignment alignment) {
        return textLayout.setAlignment(alignment.ordinal());
    }


    /**
     * Sets the wrap width for the TextLayout.
     * @return returns true is the call modifies the layout internal state.
     */
    public boolean setWrapWidth(float wrapWidth) {
        return textLayout.setWrapWidth(wrapWidth);
    }


    /**
     * Sets the line spacing for the TextLayout.
     * @return returns true is the call modifies the layout internal state.
     */
    public boolean setLineSpacing(float spacing) {
        return textLayout.setLineSpacing(spacing);
    }

    /**
     * Sets the tab size for the TextLayout.
     *
     * @param spaces the number of spaces represented by a tab. Default is 8.
     * Minimum is 1, lower values will be clamped to 1.
     * @return returns true if the call modifies the layout internal state.
     */
    public boolean setTabSize(int spaces) {
        return textLayout.setTabSize(spaces);
    }

    /**
     * Returns the (logical) bounds of the layout minX is always zero
     * minY is the ascent of the first line (negative)
     * width the width of the widest line
     * height the sum of all lines height
     * Note that this width is different the wrapping width!
     * @return the layout bounds
     */
    public Bounds getBounds() {
        BaseBounds b = textLayout.getBounds();
        return switch (b.getBoundsType()) {
            case BOX -> new BoundsBox(b.getMinX(), b.getMaxX(), b.getMinY(), b.getMaxY(), b.getMinZ(), b.getMaxZ());
            case RECTANGLE -> new BoundsRect(b.getMinX(), b.getMaxX(), b.getMinY(), b.getMaxY());
        };
    }

    public HitRecord getHitInfo(float x, float y) {
        TextLayout.Hit hit = textLayout.getHitInfo(x, y);
        return new HitRecord(
            hit.getCharIndex(),
            hit.getInsertionIndex(),
            hit.isLeading());
    }

    public enum TextAlignment { ALIGN_LEFT, ALIGN_CENTER, ALIGN_RIGHT, ALIGN_JUSTIFY; }

    public record HitRecord(int charIndex, int insertionIndex, boolean leading) { }

    public interface Bounds {
        float minX();
        float minY();
        default float minZ()  { return 0.0f; }
        float maxX();
        float maxY();
        default float maxZ() {
            return 0.0f;
        }
        default float width() {
            return maxX() - minX();
        }
        default float height() {
            return maxY() - minY();
        }
        default  float depth() {
            return maxZ() - minZ();
        }
    }

    public record BoundsRect(float minX, float maxX, float minY, float maxY) implements Bounds {}

    public record BoundsBox(float minX, float maxX, float minY, float maxY, float minZ, float maxZ) implements Bounds {}

}
