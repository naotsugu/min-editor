package com.mammb.code.editor.ui.app;

import javafx.geometry.Insets;
import javafx.scene.control.Labeled;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

public record ThemeColor(
    Color background,
    Color foreground,
    Color backgroundActive,
    Color foregroundActive) {

    public Background backgroundFill() {
        return new Background(new BackgroundFill(background, CornerRadii.EMPTY, Insets.EMPTY));
    }

    public Background backgroundActiveFill() {
        return new Background(new BackgroundFill(backgroundActive, CornerRadii.EMPTY, Insets.EMPTY));
    }

    public String foregroundColorString() {
        return toRGBCode(foreground);
    }

    private static String toRGBCode(Color color) {
        return "#%02X%02X%02X".formatted(
            (int)(color.getRed() * 255),
            (int)(color.getGreen() * 255),
            (int)(color.getBlue() * 255));
    }

    public void apply(Region region) {
        region.setBackground(backgroundFill());
        region.setBorder(Border.EMPTY);
        if (region instanceof Labeled labeled) {
            labeled.setTextFill(foreground);
        }
    }

    public void applyHover(Region region) {
        region.setOnMouseEntered(e -> region.setBackground(backgroundActiveFill()));
        region.setOnMouseExited(e -> region.setBackground(backgroundFill()));
    }

    public static ThemeColor darkDefault() {
        return new ThemeColor(
            Color.web("#26252D"), Color.web("#FAFAFE"),
            Color.web("#42424A"), Color.web("#FAFAFE"));
    }

    public static ThemeColor lightDefault() {
        return new ThemeColor(
            Color.web("#FFFFFF"), Color.web("#616A71"),
            Color.web("#F7F8F9"), Color.web("#616A71"));
    }

}
