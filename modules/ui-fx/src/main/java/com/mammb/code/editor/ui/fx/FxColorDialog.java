package com.mammb.code.editor.ui.fx;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import java.util.Locale;
import java.util.Objects;

public class FxColorDialog extends HBox {

    private final Stage dialog = new Stage();
    private ColorRectPane colorRectPane;
    private ControlsPane controlsPane;
    private final Scene customScene;

    private final ObjectProperty<Color> currentColorProperty = new SimpleObjectProperty<>(Color.WHITE);
    private final ObjectProperty<Color> customColorProperty = new SimpleObjectProperty<>(Color.TRANSPARENT);
    private Runnable onSelect;
    private Runnable onCancel;

    public FxColorDialog(Window owner) {
        getStyleClass().add("custom-color-dialog");
        Objects.requireNonNull(owner);
        dialog.initOwner(owner);
        dialog.setTitle("Color Pick");
        dialog.initModality(Modality.APPLICATION_MODAL);
        //dialog.initStyle(StageStyle.UNDECORATED);
        dialog.initStyle(StageStyle.UTILITY);
        dialog.setResizable(false);
        dialog.addEventHandler(KeyEvent.ANY, keyEventListener);

        customScene = new Scene(this);
        final Scene ownerScene = owner.getScene();
        if (ownerScene != null) {
            if (ownerScene.getUserAgentStylesheet() != null) {
                customScene.setUserAgentStylesheet(ownerScene.getUserAgentStylesheet());
            }
            customScene.getStylesheets().addAll(ownerScene.getStylesheets());
        }

        buildUI();

        dialog.setScene(customScene);
    }

    private void buildUI() {
        colorRectPane = new ColorRectPane();
        controlsPane = new ControlsPane();
        setHgrow(controlsPane, Priority.ALWAYS);
        getChildren().setAll(colorRectPane, controlsPane);
    }

    private final EventHandler<KeyEvent> keyEventListener = e -> {
        switch (e.getCode()) {
            case ESCAPE -> {
                dialog.setScene(null);
                dialog.close();
            }
            default -> {
            }
        }
    };

    public void setColor(Color color) {
        if (color == null) {
            color = Color.BLACK;
        }
        setCurrentColor(color);
        setCustomColor(color);
    }

    public void setCurrentColor(Color currentColor) {
        this.currentColorProperty.set(currentColor == null ? Color.WHITE : currentColor);
    }

    public final Color getCurrentColor() {
        return currentColorProperty.get();
    }

    public final String getSelectedWebColor() {
        return formatHexString(getCustomColor());
    }

    private static String formatHexString(Color c) {
        if (c != null) {
            return String.format((Locale) null, "#%02x%02x%02x",
                Math.round(c.getRed() * 255),
                Math.round(c.getGreen() * 255),
                Math.round(c.getBlue() * 255));
        } else {
            return null;
        }
    }


    public final ObjectProperty<Color> customColorProperty() {
        return customColorProperty;
    }

    public final void setCustomColor(Color color) {
        customColorProperty.set(color);
    }

    public final Color getCustomColor() {
        return customColorProperty.get();
    }

    public void setOnSelect(Runnable onSelect) {
        this.onSelect = onSelect;
    }

    public void show() {
        if (dialog.getOwner() != null) {
            dialog.widthProperty().addListener(positionAdjuster);
            dialog.heightProperty().addListener(positionAdjuster);
            positionAdjuster.invalidated(null);
        }
        if (dialog.getScene() == null) dialog.setScene(customScene);
        // colorRectPane.updateValues();
        dialog.show();
    }

    public void hide() {
        if (dialog.getOwner() != null) {
            dialog.hide();
        }
    }

    private final InvalidationListener positionAdjuster = new InvalidationListener() {

        @Override
        public void invalidated(Observable ignored) {
            if (Double.isNaN(dialog.getWidth()) || Double.isNaN(dialog.getHeight())) {
                return;
            }
            dialog.widthProperty().removeListener(positionAdjuster);
            dialog.heightProperty().removeListener(positionAdjuster);
            fixPosition();
        }

    };

    private void fixPosition() {
        Window w = dialog.getOwner();
        double wx = w.getX() + (w.getWidth() / 2);
        double wy = w.getY() + (w.getHeight() / 2);
        double x = wx - (dialog.getWidth() / 2);
        double y = wy - (dialog.getHeight() / 2);
        dialog.setX(x);
        dialog.setY(y);
    }

    @Override
    public void layoutChildren() {
        super.layoutChildren();
        if (dialog.getMinWidth() > 0 && dialog.getMinHeight() > 0) {
            // don't recalculate min size once it's set
            return;
        }

        // Math.max(0, ...) added for JDK-8095542 to ensure the dialog is at least 0 x 0
        double minWidth = Math.max(0, computeMinWidth(getHeight()) + (dialog.getWidth() - customScene.getWidth()));
        double minHeight = Math.max(0, computeMinHeight(getWidth()) + (dialog.getHeight() - customScene.getHeight()));
        dialog.setMinWidth(minWidth);
        dialog.setMinHeight(minHeight);
    }

    private class ColorRectPane extends HBox {
        private final Pane colorRect;
        private final Pane colorBar;
        private final Pane colorRectOverlayOne;
        private final Pane colorRectOverlayTwo;
        private final Region colorRectIndicator;
        private final Region colorBarIndicator;
        private boolean changeIsLocal = false;
        private final DoubleProperty hue = new SimpleDoubleProperty(-1) {
            @Override
            protected void invalidated() {
                if (!changeIsLocal) {
                    changeIsLocal = true;
                    updateHSBColor();
                    changeIsLocal = false;
                }
            }
        };
        private final DoubleProperty sat = new SimpleDoubleProperty(-1) {
            @Override
            protected void invalidated() {
                if (!changeIsLocal) {
                    changeIsLocal = true;
                    updateHSBColor();
                    changeIsLocal = false;
                }
            }
        };
        private final DoubleProperty bright = new SimpleDoubleProperty(-1) {
            @Override
            protected void invalidated() {
                if (!changeIsLocal) {
                    changeIsLocal = true;
                    updateHSBColor();
                    changeIsLocal = false;
                }
            }
        };
        private final IntegerProperty red = new SimpleIntegerProperty(-1) {
            @Override
            protected void invalidated() {
                if (!changeIsLocal) {
                    changeIsLocal = true;
                    updateRGBColor();
                    changeIsLocal = false;
                }
            }
        };

        private final IntegerProperty green = new SimpleIntegerProperty(-1) {
            @Override
            protected void invalidated() {
                if (!changeIsLocal) {
                    changeIsLocal = true;
                    updateRGBColor();
                    changeIsLocal = false;
                }
            }
        };

        private final IntegerProperty blue = new SimpleIntegerProperty(-1) {
            @Override
            protected void invalidated() {
                if (!changeIsLocal) {
                    changeIsLocal = true;
                    updateRGBColor();
                    changeIsLocal = false;
                }
            }
        };

        private final DoubleProperty alpha = new SimpleDoubleProperty(100) {
            @Override
            protected void invalidated() {
                if (!changeIsLocal) {
                    changeIsLocal = true;
                    setCustomColor(new Color(
                        getCustomColor().getRed(),
                        getCustomColor().getGreen(),
                        getCustomColor().getBlue(),
                        Math.clamp(alpha.get() / 100, 0, 1)));
                    changeIsLocal = false;
                }
            }
        };
        private void updateRGBColor() {
            Color newColor = Color.rgb(red.get(), green.get(), blue.get(),
                Math.clamp(alpha.get() / 100, 0, 1));
            hue.set(newColor.getHue());
            sat.set(newColor.getSaturation() * 100);
            bright.set(newColor.getBrightness() * 100);
            setCustomColor(newColor);
        }

        private void updateHSBColor() {
            Color newColor = Color.hsb(hue.get(),
                Math.clamp(sat.get() / 100, 0, 1),
                Math.clamp(bright.get() / 100, 0, 1),
                Math.clamp(alpha.get() / 100, 0, 1));
            red.set(doubleToInt(newColor.getRed()));
            green.set(doubleToInt(newColor.getGreen()));
            blue.set(doubleToInt(newColor.getBlue()));
            setCustomColor(newColor);
        }

        private void colorChanged() {
            if (!changeIsLocal) {
                changeIsLocal = true;
                hue.set(getCustomColor().getHue());
                sat.set(getCustomColor().getSaturation() * 100);
                bright.set(getCustomColor().getBrightness() * 100);
                red.set(doubleToInt(getCustomColor().getRed()));
                green.set(doubleToInt(getCustomColor().getGreen()));
                blue.set(doubleToInt(getCustomColor().getBlue()));
                changeIsLocal = false;
            }
        }
        public ColorRectPane() {

            getStyleClass().add("color-rect-pane");

            customColorProperty().addListener((_, _, _) -> colorChanged());

            colorRectIndicator = new Region();
            colorRectIndicator.setId("color-rect-indicator");
            colorRectIndicator.setManaged(false);
            colorRectIndicator.setMouseTransparent(true);
            colorRectIndicator.setCache(true);

            final Pane colorRectOpacityContainer = new StackPane();

            colorRect = new StackPane() {
                // This is an implementation of square control that chooses its
                // size to fill the available height
                @Override
                public Orientation getContentBias() {
                    return Orientation.VERTICAL;
                }

                @Override
                protected double computePrefWidth(double height) {
                    return height;
                }

                @Override
                protected double computeMaxWidth(double height) {
                    return height;
                }
            };
            colorRect.getStyleClass().addAll("color-rect", "transparent-pattern");

            Pane colorRectHue = new Pane();
            colorRectHue.backgroundProperty().bind(new ObjectBinding<>() {
                {
                    bind(hue);
                }
                @Override
                protected Background computeValue() {
                    return new Background(new BackgroundFill(
                        Color.hsb(hue.getValue(), 1.0, 1.0),
                        CornerRadii.EMPTY, Insets.EMPTY));
                }
            });

            colorRectOverlayOne = new Pane();
            colorRectOverlayOne.getStyleClass().add("color-rect");
            colorRectOverlayOne.setBackground(new Background(new BackgroundFill(
                new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                    new Stop(0, Color.rgb(255, 255, 255, 1)),
                    new Stop(1, Color.rgb(255, 255, 255, 0))),
                CornerRadii.EMPTY, Insets.EMPTY)));

            EventHandler<MouseEvent> rectMouseHandler = event -> {
                final double x = event.getX();
                final double y = event.getY();
                sat.set(Math.clamp(x / colorRect.getWidth(), 0, 1) * 100);
                bright.set(100 - (Math.clamp(y / colorRect.getHeight(), 0, 1) * 100));
            };

            colorRectOverlayTwo = new Pane();
            colorRectOverlayTwo.getStyleClass().addAll("color-rect");
            colorRectOverlayTwo.setBackground(new Background(new BackgroundFill(
                new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                    new Stop(0, Color.rgb(0, 0, 0, 0)), new Stop(1, Color.rgb(0, 0, 0, 1))),
                CornerRadii.EMPTY, Insets.EMPTY)));
            colorRectOverlayTwo.setOnMouseDragged(rectMouseHandler);
            colorRectOverlayTwo.setOnMousePressed(rectMouseHandler);

            Pane colorRectBlackBorder = new Pane();
            colorRectBlackBorder.setMouseTransparent(true);
            colorRectBlackBorder.getStyleClass().addAll("color-rect", "color-rect-border");

            colorBar = new Pane();
            colorBar.getStyleClass().add("color-bar");
            colorBar.setBackground(new Background(new BackgroundFill(createHueGradient(),
                CornerRadii.EMPTY, Insets.EMPTY)));

            colorBarIndicator = new Region();
            colorBarIndicator.setId("color-bar-indicator");
            colorBarIndicator.setMouseTransparent(true);
            colorBarIndicator.setCache(true);

            colorRectIndicator.layoutXProperty().bind(sat.divide(100).multiply(colorRect.widthProperty()));
            colorRectIndicator.layoutYProperty().bind(Bindings.subtract(1, bright.divide(100)).multiply(colorRect.heightProperty()));
            colorBarIndicator.layoutYProperty().bind(hue.divide(360).multiply(colorBar.heightProperty()));
            colorRectOpacityContainer.opacityProperty().bind(alpha.divide(100));

            EventHandler<MouseEvent> barMouseHandler = event -> {
                final double y = event.getY();
                hue.set(Math.clamp(y / colorRect.getHeight(), 0, 1) * 360);
            };

            colorBar.setOnMouseDragged(barMouseHandler);
            colorBar.setOnMousePressed(barMouseHandler);

            colorBar.getChildren().setAll(colorBarIndicator);
            colorRectOpacityContainer.getChildren().setAll(colorRectHue, colorRectOverlayOne, colorRectOverlayTwo);
            colorRect.getChildren().setAll(colorRectOpacityContainer, colorRectBlackBorder, colorRectIndicator);
            HBox.setHgrow(colorRect, Priority.SOMETIMES);
            getChildren().addAll(colorRect, colorBar);
        }

        private void updateValues() {
            if (getCurrentColor() == null) {
                setCurrentColor(Color.TRANSPARENT);
            }
            changeIsLocal = true;
            //Initialize hue, sat, bright, color, red, green and blue
            hue.set(getCurrentColor().getHue());
            sat.set(getCurrentColor().getSaturation() * 100);
            bright.set(getCurrentColor().getBrightness() * 100);
            alpha.set(getCurrentColor().getOpacity() * 100);
            setCustomColor(Color.hsb(hue.get(),
                Math.clamp(sat.get() / 100, 0, 1),
                Math.clamp(bright.get() / 100, 0, 1),
                Math.clamp(alpha.get() / 100, 0, 1)));
            red.set(doubleToInt(getCustomColor().getRed()));
            green.set(doubleToInt(getCustomColor().getGreen()));
            blue.set(doubleToInt(getCustomColor().getBlue()));
            changeIsLocal = false;
        }

        @Override
        protected void layoutChildren() {
            super.layoutChildren();
            // to maintain the default size
            colorRectIndicator.autosize();
            // to maintain square size
            double size = Math.min(colorRect.getWidth(), colorRect.getHeight());
            colorRect.resize(size, size);
            colorBar.resize(colorBar.getWidth(), size);
        }

    }

    private class ControlsPane extends VBox {
        private final Region currentColorRect;
        private final Region newColorRect;
        private final Region currentTransparent; // for opacity
        private final GridPane currentAndNewColor;
        private final Region currentNewColorBorder;
        private final ToggleButton hsbButton;
        private final ToggleButton rgbButton;
        private final HBox hBox;

        private final Label[] labels = new Label[4];
        private final Slider[] sliders = new Slider[4];
        private final IntegerField[] fields = new IntegerField[4];
        private final Label[] units = new Label[4];
        private final HBox buttonBox;
        private final Region whiteBox;

        private final GridPane settingsPane = new GridPane();

        public ControlsPane() {
            getStyleClass().add("controls-pane");

            currentNewColorBorder = new Region();
            currentNewColorBorder.setId("current-new-color-border");

            currentTransparent = new Region();
            currentTransparent.getStyleClass().addAll("transparent-pattern");

            currentColorRect = new Region();
            currentColorRect.getStyleClass().add("color-rect");
            currentColorRect.setId("current-color");
            currentColorRect.backgroundProperty().bind(new ObjectBinding<Background>() {
                {
                    bind(currentColorProperty);
                }

                @Override
                protected Background computeValue() {
                    return new Background(new BackgroundFill(currentColorProperty.get(), CornerRadii.EMPTY, Insets.EMPTY));
                }
            });

            newColorRect = new Region();
            newColorRect.getStyleClass().add("color-rect");
            newColorRect.setId("new-color");
            newColorRect.backgroundProperty().bind(new ObjectBinding<Background>() {
                {
                    bind(customColorProperty);
                }

                @Override
                protected Background computeValue() {
                    return new Background(new BackgroundFill(customColorProperty.get(), CornerRadii.EMPTY, Insets.EMPTY));
                }
            });

            whiteBox = new Region();
            whiteBox.getStyleClass().add("customcolor-controls-background");

            hsbButton = new ToggleButton("HSB");
            hsbButton.getStyleClass().add("left-pill");
            rgbButton = new ToggleButton("RGB");
            rgbButton.getStyleClass().add("center-pill");
            final ToggleGroup group = new ToggleGroup();

            hBox = new HBox();
            hBox.setAlignment(Pos.CENTER);
            hBox.getChildren().addAll(hsbButton, rgbButton);

            Region spacer1 = new Region();
            spacer1.setId("spacer1");
            Region spacer2 = new Region();
            spacer2.setId("spacer2");
            Region leftSpacer = new Region();
            leftSpacer.setId("spacer-side");
            Region rightSpacer = new Region();
            rightSpacer.setId("spacer-side");
            Region bottomSpacer = new Region();
            bottomSpacer.setId("spacer-bottom");

            currentAndNewColor = new GridPane();
            currentAndNewColor.getColumnConstraints().addAll(new ColumnConstraints(), new ColumnConstraints());
            currentAndNewColor.getColumnConstraints().get(0).setHgrow(Priority.ALWAYS);
            currentAndNewColor.getColumnConstraints().get(1).setHgrow(Priority.ALWAYS);
            currentAndNewColor.getRowConstraints().addAll(new RowConstraints(), new RowConstraints(), new RowConstraints());
            currentAndNewColor.getRowConstraints().get(2).setVgrow(Priority.ALWAYS);
            VBox.setVgrow(currentAndNewColor, Priority.ALWAYS);

            currentAndNewColor.getStyleClass().add("current-new-color-grid");
            currentAndNewColor.add(new Label("Current Color"), 0, 0);
            currentAndNewColor.add(new Label("New Color"), 1, 0);
            currentAndNewColor.add(spacer1, 0, 1, 2, 1);
            currentAndNewColor.add(currentTransparent, 0, 2, 2, 1);
            currentAndNewColor.add(currentColorRect, 0, 2);
            currentAndNewColor.add(newColorRect, 1, 2);
            currentAndNewColor.add(currentNewColorBorder, 0, 2, 2, 1);
            currentAndNewColor.add(spacer2, 0, 3, 2, 1);

            settingsPane.setId("settings-pane");
            settingsPane.getColumnConstraints().addAll(new ColumnConstraints(),
                new ColumnConstraints(), new ColumnConstraints(),
                new ColumnConstraints(), new ColumnConstraints(),
                new ColumnConstraints());
            settingsPane.getColumnConstraints().get(0).setHgrow(Priority.NEVER);
            settingsPane.getColumnConstraints().get(2).setHgrow(Priority.ALWAYS);
            settingsPane.getColumnConstraints().get(3).setHgrow(Priority.NEVER);
            settingsPane.getColumnConstraints().get(4).setHgrow(Priority.NEVER);
            settingsPane.getColumnConstraints().get(5).setHgrow(Priority.NEVER);
            settingsPane.add(whiteBox, 0, 0, 6, 5);
            settingsPane.add(hBox, 0, 0, 6, 1);
            settingsPane.add(leftSpacer, 0, 0);
            settingsPane.add(rightSpacer, 5, 0);
            settingsPane.add(bottomSpacer, 0, 4);

            // Color settings Grid Pane
            for (int i = 0; i < 4; i++) {
                labels[i] = new Label();
                labels[i].getStyleClass().add("settings-label");

                sliders[i] = new Slider();

                fields[i] = new IntegerField();
                fields[i].getStyleClass().add("color-input-field");

                units[i] = new Label(i == 0 ? "\u00B0" : "%");
                units[i].getStyleClass().add("settings-unit");

                if (i < 3) {
                    units[i].visibleProperty().bind(group.selectedToggleProperty().isEqualTo(hsbButton));
                }
                int row = 1 + i;
                if (i == 3) {
                    // opacity row is shifted one gridPane row down
                    row++;
                }

                settingsPane.add(labels[i], 1, row);
                settingsPane.add(sliders[i], 2, row);
                settingsPane.add(fields[i], 3, row);
                settingsPane.add(units[i], 4, row);
            }

            set(3, "Opacity", 100, colorRectPane.alpha);

            hsbButton.setToggleGroup(group);
            rgbButton.setToggleGroup(group);
            group.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == null) {
                    group.selectToggle(oldValue);
                } else {
                    if (newValue == hsbButton) {
                        showHSBSettings();
                    } else if (newValue == rgbButton) {
                        showRGBSettings();
                    }
                }
            });
            group.selectToggle(hsbButton);

            buttonBox = new HBox();
            buttonBox.setId("buttons-hbox");

            Button okButton = new Button("Ok");
            okButton.setOnAction(_ -> {
                if (onSelect != null) {
                    onSelect.run();
                }
                dialog.hide();
            });

            Button cancelButton = new Button("Cancel");
            cancelButton.setCancelButton(true);
            cancelButton.setOnAction(e -> {
                customColorProperty.set(getCurrentColor());
                if (onCancel != null) {
                    onCancel.run();
                }
                dialog.hide();
            });

            buttonBox.getChildren().addAll(okButton, cancelButton);

            getChildren().addAll(currentAndNewColor, settingsPane, buttonBox);
        }

        private void showHSBSettings() {
            set(0, "Hue:", 360, colorRectPane.hue);
            set(1, "Saturation:", 100, colorRectPane.sat);
            set(2, "Brightness:", 100, colorRectPane.bright);
        }

        private void showRGBSettings() {
            set(0, "Red:", 255, colorRectPane.red);
            set(1, "Green:", 255, colorRectPane.green);
            set(2, "Blue:", 255, colorRectPane.blue);
        }

        private final Property<Number>[] bindedProperties = new Property[4];

        private void set(int row, String caption, int maxValue, Property<Number> prop) {
            labels[row].setText(caption);
            if (bindedProperties[row] != null) {
                sliders[row].valueProperty().unbindBidirectional(bindedProperties[row]);
                fields[row].valueProperty().unbindBidirectional(bindedProperties[row]);
            }
            sliders[row].setMax(maxValue);
            sliders[row].valueProperty().bindBidirectional(prop);
            labels[row].setLabelFor(sliders[row]);
            fields[row].setMaxValue(maxValue);
            fields[row].valueProperty().bindBidirectional(prop);
            bindedProperties[row] = prop;
        }

    }

    private static LinearGradient createHueGradient() {
        double offset;
        Stop[] stops = new Stop[255];
        for (int y = 0; y < 255; y++) {
            offset = 1 - (1.0 / 255) * y;
            int h = (int) ((y / 255.0) * 360);
            stops[y] = new Stop(offset, Color.hsb(h, 1.0, 1.0));
        }
        return new LinearGradient(0f, 1f, 0f, 0f, true, CycleMethod.NO_CYCLE, stops);
    }

    private static int doubleToInt(double value) {
        return (int) (value * 255 + 0.5); // Adding 0.5 for rounding only
    }

}
