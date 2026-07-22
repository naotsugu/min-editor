/*
 * Copyright 2026- the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mammb.code.jfx.multitab;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.StackPane;
import java.nio.file.Path;

public class ContentPane extends StackPane {

    private final SimpleObjectProperty<String> shortNameProperty = new SimpleObjectProperty<>("");
    private final SimpleObjectProperty<String> fullNameProperty = new SimpleObjectProperty<>("");

    private ContentPane(String shortName, String fullName) {
        shortNameProperty.set(shortName);
        fullNameProperty.set(fullName);
    }

    public ContentPane(Path path) {
        this(path.getFileName().toString(), path.toString());
    }

    public ContentPane(String string) {
        if (string == null || string.isBlank()) {
            shortNameProperty.set("Untitled");
        } else {
            String[] strip = string.split(System.getProperty("path.separator", ";"), 2);
            shortNameProperty.set(strip[0]);
            if (strip.length > 1) {
                fullNameProperty.set(strip[1]);
            }
        }
    }

    public String asString() {
        return shortNameProperty.get() + System.getProperty("path.separator", ";") + fullNameProperty.get();
    }

    public boolean canClose() {
        return true;
    }

    public boolean closeRequest() {
        return true;
    }

    public ReadOnlyObjectProperty<String> shortNameProperty() {
        return shortNameProperty;
    }

    public ReadOnlyObjectProperty<String> fullNameProperty() {
        return fullNameProperty;
    }

}
