/*
 * Copyright 2023-2025 the original author or authors.
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
package com.mammb.code.editor.fx;

import com.mammb.code.editor.core.Name;
import com.mammb.code.editor.core.Session;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * The WebPane.
 * @author Naotsugu Kobayashi
 */
public class WebPane extends ContentPane {

    private final WebView webView;
    private final SimpleObjectProperty<Name> pageNameProperty = new SimpleObjectProperty<>(Name.EMPTY);


    private WebPane(WebView webView) {
        this.webView = webView;
        var engine = webView.getEngine();
        engine.getLoadWorker().stateProperty().addListener((_, _, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                pageNameProperty.setValue(new NameRecord(
                    engine.getLocation(),
                    engine.getTitle()));
            }
        });
        webView.setZoom(0.8);
        engine.setUserAgent("""
            Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.44 (KHTML, like Gecko) JavaFX/8.0 Safari/537.44
            x-devcat-category: smartphone""");
        getChildren().add(webView);
    }

    public WebPane() {
        this(new WebView());
    }

    public WebPane search(String query) {
        webView.getEngine().load(url(query));
        return this;
    }

    private static String url(String query) {
        return "https://www.google.com/search?q=" + URLEncoder.encode(query, StandardCharsets.UTF_8);
    }

    @Override
    void focus() {

    }

    @Override
    boolean needsCloseConfirmation() {
        return false;
    }

    @Override
    boolean canClose() {
        return true;
    }

    @Override
    Optional<Session> close() {
        return Optional.empty();
    }

    @Override
    void setCloseListener(Consumer<ContentPane> closeListener) {

    }

    @Override
    ReadOnlyObjectProperty<Name> nameProperty() {
        return pageNameProperty;
    }

    record NameRecord(String canonical, String plain) implements Name {
        @Override public String contextual() { return plain; }
    }

}
