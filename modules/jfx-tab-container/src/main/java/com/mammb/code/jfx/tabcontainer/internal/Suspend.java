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
package com.mammb.code.jfx.tabcontainer.internal;

import com.mammb.code.jfx.tabcontainer.ContentPane;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The Suspend.
 * @author Naotsugu Kobayashi
 */
public class Suspend {

    /** The logger. */
    private static final System.Logger log = System.getLogger(Suspend.class.getName());

    /** The store path. */
    private final Path path;

    public Suspend(Path path) {
        this.path = Objects.requireNonNull(path);
    }


    public void save(Stage stage) {
        try {
            Files.write(path, asString(stage));
        } catch (IOException e) {
            log.log(System.Logger.Level.ERROR, "failed to write resume file", e);
        }
    }

    private static List<String> asString(Stage stage) {

        Scene scene = stage.getScene();

        String loc = String.join(",",
            Double.toString(stage.getX()),
            Double.toString(stage.getY()),
            Double.toString(scene.getWidth()),
            Double.toString(scene.getHeight()));

        Node node = scene.lookup("." + BranchNode.STYLE_CLASS);
        String str = (node instanceof BranchNode branchNode)
            ? asStringRecursive(branchNode.root())
            : "";

        return List.of(loc, str);
    }


    private static String asStringRecursive(ParentOf<?> parentOf) {

        return switch (parentOf) {

            case BranchNode branchNode -> "{" + String.join(",",
                // orientation
                branchNode.orientation().toString().substring(0, 1),
                // dividerPositions
                Arrays.stream(branchNode.dividerPositions())
                    .mapToObj(String::valueOf).findFirst().orElse("0.5"),
                // children
                branchNode.children().stream()
                    .filter(ParentOf.class::isInstance)
                    .map(e -> (ParentOf<?>) e)
                    .map(Suspend::asStringRecursive)
                    .collect(Collectors.joining(","))
            ) + "}";

            case LeafNode leafNode -> leafNode.children().stream()
                .map(Tab::content)
                .map(ContentPane::asString)
                .map(Suspend::escape)
                .collect(Collectors.joining(",", "[", "]"));

            default -> "";
        };
    }

    static final String[][] ESCAPES = { {"%", "%25"}, {"[", "%5B"}, {"]", "%5D"}, {"{", "%7B"}, {"}", "%7D"}, {"\"", "%22"}, {",", "%2C"} };

    private static String escape(String str) {
        if (str == null) return null;
        if (str.isBlank()) return "";
        for (String[] rule : ESCAPES) {
            str = str.replace(rule[0], rule[1]);
        }
        return str;
    }


}
