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
package com.mammb.code.jfx.tabcontainer;

import com.mammb.code.jfx.tabcontainer.internal.BranchNode;
import com.mammb.code.jfx.tabcontainer.internal.Context;
import com.mammb.code.jfx.tabcontainer.internal.LeafNode;
import com.mammb.code.jfx.tabcontainer.internal.ParentOf;
import com.mammb.code.jfx.tabcontainer.internal.Tab;
import com.mammb.code.jfx.tabcontainer.internal.TreeNode;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * The TabContainers.
 * @author Naotsugu Kobayashi
 */
public interface TabContainers {

    /** The logger. */
    System.Logger log = System.getLogger(TabContainers.class.getName());

    static SceneBuilder builder() {
        return new SceneBuilder();
    }

    record SceneBuilder(
            Stage stage,
            Function<Object, ? extends ContentPane> toContent,
            BiFunction<Stage, Pane, Scene> toScene,
            BiPredicate<Object, Container> onOpenRequest,
            Path resumePath,
            Function<String, ? extends ContentPane> resumeToContent) {
        public SceneBuilder() {
            this(null, null, null, null, null, null);
        }
        public SceneBuilder stage(Stage stage) {
            return new SceneBuilder(Objects.requireNonNull(stage), toContent, toScene, onOpenRequest, resumePath, resumeToContent);
        }
        public SceneBuilder toContent(Function<Object, ? extends ContentPane> toContent) {
            return new SceneBuilder(stage, Objects.requireNonNull(toContent), toScene, onOpenRequest, resumePath, resumeToContent);
        }
        public SceneBuilder toScene(BiFunction<Stage, Pane, Scene> toScene) {
            return new SceneBuilder(stage, toContent, Objects.requireNonNull(toScene), onOpenRequest, resumePath, resumeToContent);
        }
        public SceneBuilder onOpenRequest(BiPredicate<Object, Container> onOpenRequest) {
            return new SceneBuilder(stage, Objects.requireNonNull(toContent), toScene, onOpenRequest, resumePath, resumeToContent);
        }
        public SceneBuilder resume(Path resumePath, Function<String, ? extends ContentPane> resumeToContent) {
            return new SceneBuilder(stage, toContent, Objects.requireNonNull(toScene), onOpenRequest, resumePath, resumeToContent);
        }
        public Scene build() {
            var ctx = context();
            var st = (stage == null) ? new Stage() : stage;
            var pane = buildNode(st, ctx, resumePath, resumeToContent);
            return ctx.toScene(st, pane);
        }
        private Context context() {
            return new Context(
                Objects.requireNonNull(toContent),
                wrappedToScene(toScene, resumePath),
                (onOpenRequest == null) ? (_, _) -> false : onOpenRequest);
        }
    }

    private static BranchNode buildNode(Stage stage, Context ctx, Path resumePath,
            Function<String, ? extends ContentPane> resumeToContent) {
        if (resumePath != null && resumeToContent != null &&
            Files.exists(resumePath) && Files.isRegularFile(resumePath) &&
            Files.isReadable(resumePath)) {
            try {
                var lines = Files.readAllLines(resumePath);
                String[] split = lines.getFirst().split(",");

                double x = Double.parseDouble(split[0]);
                double y = Double.parseDouble(split[1]);
                if (Screen.getScreens().stream().anyMatch(screen ->
                    screen.getVisualBounds().contains(x, y))) {
                    stage.setX(x);
                    stage.setY(y);
                }

                double w = Math.max(Double.parseDouble(split[2]), 90);
                double h = Math.max(Double.parseDouble(split[3]), 30);
                stage.setWidth(w);
                stage.setHeight(h);
                Pane pane = fromString(ctx, lines.get(1), resumeToContent);
                if (pane instanceof BranchNode branchNode) {
                    return branchNode;
                }
            } catch (Exception e) {
                log.log(System.Logger.Level.ERROR, "Failed to read resume file", e);
            }
        }
        stage.setWidth(600);
        stage.setHeight(400);
        return new BranchNode(ctx, ctx.createEmptyContent());
    }


    private static BiFunction<Stage, Pane, Scene> wrappedToScene(
            BiFunction<Stage, Pane, Scene> toScene, Path resumePath) {
        BiFunction<Stage, Pane, Scene> toSceneFun = (toScene != null)
            ? toScene
            : (stage, pane) -> new Scene(pane);
        if (resumePath != null) {
            return (stage, pane) -> {
                stage.setOnCloseRequest(TabContainers::handleStageCloseRequest);
                stage.setOnHiding(e -> handleStageHiding(e, resumePath));
                return toSceneFun.apply(stage, pane);
            };
        } else {
            return toSceneFun;
        }
    }

    private static void handleStageCloseRequest(WindowEvent event) {
        if (event.getTarget() instanceof Stage stage) {
            Scene scene = stage.getScene();
            if (scene == null) {
                return;
            }

            if (scene.getRoot().lookup("." + BranchNode.STYLE_CLASS) instanceof BranchNode branchNode) {

                Predicate<ContentPane> predicate = (Stage.getWindows().stream().filter(Window::isShowing).count() > 1)
                    ? ContentPane::canCloseQuiet
                    : ContentPane::canExitQuiet;

                List<ContentPane> contentPanes = branchNode.leaves().stream()
                    .map(LeafNode::children)
                    .flatMap(Collection::stream)
                    .map(Tab::content)
                    .filter(Predicate.not(predicate))
                    .toList();

                for (ContentPane contentPane : contentPanes) {
                    if (!contentPane.closeRequest()) {
                        event.consume();
                        return;
                    }
                }
            }
        }
    }


    private static void handleStageHiding(WindowEvent event, Path resumePath) {
        if (Stage.getWindows().stream().noneMatch(Window::isShowing)) {
            if (event.getTarget() instanceof Stage stage) {
                Scene scene = stage.getScene();
                String loc = String.join(",",
                    Double.toString(stage.getX()),
                    Double.toString(stage.getY()),
                    Double.toString(scene.getWidth()),
                    Double.toString(scene.getHeight()));
                String string = asString(scene.getRoot());
                try {
                    Files.write(resumePath, List.of(loc, string));
                } catch (IOException e) {
                    log.log(System.Logger.Level.ERROR, "Failed to write resume file", e);
                }
            }
        }
    }

    private static String asString(Parent parent) {
        Node node = parent.lookup("." + BranchNode.STYLE_CLASS);
        if (node instanceof BranchNode branchNode) {
            return asStringRecursive(branchNode.root());
        }
        return "";
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
                    .map(TabContainers::asStringRecursive)
                    .collect(Collectors.joining(","))
            ) + "}";

            case LeafNode leafNode -> leafNode.children().stream()
                .map(Tab::content)
                .map(ContentPane::asString)
                .map(TabContainers::escape)
                .collect(Collectors.joining(",", "[", "]"));

            default -> "";
        };
    }

    private static Pane fromString(Context ctx, String str,
            Function<String, ? extends ContentPane> resumeToContent) {

        if (str.startsWith("{") && str.endsWith("}")) {
            str = str.substring(1, str.length() - 1); // remove '{' '}'
            // orientation
            Orientation orientation = Objects.equals(str.charAt(0), 'H')
                ? Orientation.HORIZONTAL
                : Orientation.VERTICAL;
            // dividerPositions
            int divClose = str.indexOf(',', 2, str.length());
            String div = str.substring(2, divClose);
            double[] dividerPositions = new double[] { div.isBlank() ? 0.5 : Double.parseDouble(div) };
            // children
            List<TreeNode> children = splitBranch(str.substring(divClose + 1)).stream()
                .map(s -> fromString(ctx, s, resumeToContent))
                .filter(TreeNode.class::isInstance)
                .map(TreeNode.class::cast)
                .toList();
            // create BranchNode
            var branchNode = new BranchNode(ctx);
            branchNode.orientation(orientation);
            branchNode.addChildren(children);
            Platform.runLater(() -> branchNode.dividerPositions(dividerPositions));
            return branchNode;

        } else if (str.startsWith("[") && str.endsWith("]")) {
            str = str.substring(1, str.length() - 1); // remove '[' ']'
            // children
            String[] split = str.split(",");
            List<Tab> children = Arrays.stream(split)
                .map(TabContainers::unescape)
                .map(resumeToContent)
                .map(c -> new Tab(ctx, c))
                .toList();
            // create LeafNode
            var leafNode = new LeafNode(ctx);
            leafNode.addChildren(children);
            return leafNode;
        }
        return null;
    }

    private static List<String> splitBranch(String str) {
        Deque<Character> deque = new ArrayDeque<>();
        char p = 0;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '[' || c == '{') {
                deque.push(c);
            } else if (c == ']' && !deque.isEmpty() && deque.peek() == '[') {
                deque.pop();
            } else if (c == '}' && !deque.isEmpty() && deque.peek() == '{') {
                deque.pop();
            } else if ((p == ']' || p == '}') && c == ',' && deque.isEmpty()) {
                return List.of(
                    str.substring(0, i),
                    str.substring(i + 1));
            }
            p = c;
        }
        return List.of(str);
    }

    String[][] ESCAPES = { {"%", "%25"}, {"[", "%5B"}, {"]", "%5D"}, {"{", "%7B"}, {"}", "%7D"}, {"\"", "%22"}, {",", "%2C"} };

    private static String escape(String str) {
        if (str == null || str.isBlank()) return null;
        for (String[] rule : ESCAPES) {
            str = str.replace(rule[0], rule[1]);
        }
        return str;
    }

    private static String unescape(String str) {
        if (str == null || str.isBlank()) return null;
        for (int i = ESCAPES.length - 1; i >= 0; i--) {
            str = str.replace(ESCAPES[i][1], ESCAPES[i][0]);
        }
        return str;
    }

}
