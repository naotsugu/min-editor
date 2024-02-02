/*
 * Copyright 2023-2024 the original author or authors.
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
package com.mammb.code.editor.ui.pane;

import com.mammb.code.editor.ui.model.EditorModel;
import com.mammb.code.editor.ui.model.ImeRun;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.InputMethodRequests;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.Objects;

/**
 * ImeAction.
 * @author Naotsugu Kobayashi
 */
public class ImeAction {

    /** logger. */
    private static final System.Logger log = System.getLogger(ImeAction.class.getName());

    /** The graphics context. */
    private final GraphicsContext gc;

    /** The editor model. */
    private final EditorModel model;


    /**
     * Constructor.
     * @param gc the graphics context
     * @param model the editor model
     */
    private ImeAction(GraphicsContext gc, EditorModel model) {
        this.gc = Objects.requireNonNull(gc);
        this.model = Objects.requireNonNull(model);
    }


    /**
     * Create a new ImeBehavior.
     * @param gc the graphics context
     * @param model the editor model
     * @return ImeBehavior
     */
    public static ImeAction of(GraphicsContext gc, EditorModel model) {
        return new ImeAction(gc, model);
    }


    /**
     * Input method handler
     * @param e the input method event
     */
    public void handle(InputMethodEvent e) {

        if (!e.getCommitted().isEmpty()) {
            model.imeCommitted(e.getCommitted());

        } else if (!e.getComposed().isEmpty()) {

            if (!model.isImeOn()) {
                model.imeOn(gc);
            }

            var runs = new ArrayList<ImeRun>();
            int offset = 0;
            for (var run : e.getComposed()) {
                var r = new ImeRun(
                    offset,
                    run.getText(),
                    ImeRun.RunType.valueOf(run.getHighlight().name()));
                runs.add(r);
                offset += r.length();
            }

            model.imeComposed(runs);

        } else {
            model.imeOff();
        }

        model.draw(gc);

    }


    /**
     * Create input method request.
     * @return the InputMethodRequests
     */
    public InputMethodRequests createRequest(Pane pane) {

        return new InputMethodRequests() {

            @Override
            public Point2D getTextLocation(int offset) {
                var rect = model.imeOn(gc);
                return pane.localToScreen(rect.x(), rect.y() + rect.height() + 5);
            }

            @Override
            public void cancelLatestCommittedText() {
                model.imeOff();
            }

            @Override
            public int getLocationOffset(int x, int y) {
                return 0;
            }

            @Override
            public String getSelectedText() {
                return "";
            }

        };

    }

}
