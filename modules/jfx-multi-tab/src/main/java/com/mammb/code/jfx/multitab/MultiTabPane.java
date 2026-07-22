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

import com.mammb.code.jfx.multitab.internal.BranchNode;
import com.mammb.code.jfx.multitab.internal.Context;
import com.mammb.code.jfx.multitab.internal.LeafNode;
import com.mammb.code.jfx.multitab.internal.ParentOf;
import com.mammb.code.jfx.multitab.internal.Tab;
import com.mammb.code.jfx.multitab.internal.TreeNode;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MultiTabPane extends StackPane {

    private final Context ctx;

    public MultiTabPane(Stage stage, String string,
            Function<String, ? extends ContentPane> contentSupplier,
            Function<Path, ? extends ContentPane> pathContentSupplier) {

        this.ctx = new Context(stage);

        if  (contentSupplier != null)
            ctx.contentSupplier(contentSupplier);
        if (pathContentSupplier != null)
            ctx.pathContentSupplier(pathContentSupplier);

        Node branchNode = (string != null && !string.isBlank())
            ? fromString(string)
            : new BranchNode(ctx, ctx.contentSupplier().apply(""));

        getChildren().add(branchNode);
    }

    public MultiTabPane(Stage stage) {
        this(stage, "", null, null);
    }

    public String asString() {
        return asStringRecursive((BranchNode) getChildren().getFirst());
    }

    private String asStringRecursive(ParentOf<?> parentOf) {

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
                        .map(this::asStringRecursive)
                        .collect(Collectors.joining(","))
                    ) + "}";

            case LeafNode leafNode -> leafNode.children().stream()
                .map(Tab::content)
                .map(ContentPane::asString)
                .map(MultiTabPane::escape)
                .collect(Collectors.joining(",", "[", "]"));

            default -> "";
        };
    }

    private Node fromString(String str) {

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
                .map(this::fromString)
                .filter(TreeNode.class::isInstance)
                .map(TreeNode.class::cast)
                .toList();
            // create BranchNode
            var branchNode = new BranchNode(ctx);
            branchNode.orientation(orientation);
            branchNode.dividerPositions(dividerPositions);
            branchNode.addChildren(children);
            return branchNode;

        } else if (str.startsWith("[") && str.endsWith("]")) {
            str = str.substring(1, str.length() - 1); // remove '[' ']'
            // children
            String[] split = str.split(",");
            List<Tab> children = Arrays.stream(split)
                .map(MultiTabPane::unescape)
                .map(ctx.contentSupplier())
                .map(c -> new Tab(ctx, c))
                .toList();
            // create LeafNode
            var leafNode = new LeafNode(ctx);
            leafNode.addChildren(children);
            return leafNode;
        }
        return null;
    }

    private List<String> splitBranch(String str) {
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

    private static final String[][] ESCAPES = {
        {"%", "%25"}, {"[", "%5B"}, {"]", "%5D"}, {"{", "%7B"}, {"}", "%7D"}, {"\"", "%22"}, {",", "%2C"}
    };

    public static String escape(String str) {
        if (str == null || str.isBlank()) return null;
        for (String[] rule : ESCAPES) {
            str = str.replace(rule[0], rule[1]);
        }
        return str;
    }

    public static String unescape(String str) {
        if (str == null || str.isBlank()) return null;
        for (int i = ESCAPES.length - 1; i >= 0; i--) {
            str = str.replace(ESCAPES[i][1], ESCAPES[i][0]);
        }
        return str;
    }

}
