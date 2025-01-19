package com.mammb.code.editor.core.syntax2;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import com.mammb.code.editor.core.syntax2.BlockToken.Open;
import com.mammb.code.editor.core.syntax2.BlockToken.Close;

public class ScopeStack {

    private final TreeMap<Anchor, BlockToken> scopes = new TreeMap<>();
    private final Deque<BlockToken> stack = new ArrayDeque<>();


    void clear(int row) {
        scopes.subMap(Anchor.min(row), Anchor.max(row)).clear();
        fillStack();
    }

    Optional<Open> current() {
        return Optional.ofNullable(stack.peek())
            .filter(Open.class::isInstance)
            .map(Open.class::cast);
    }


    private void fillStack() {

        stack.clear();

        for (BlockToken token : scopes.values()) {

            if (token instanceof Open open) {
                stack.push(open);

            } else if (token instanceof Close close) {
                BlockToken latest = stack.peek();
                if (latest instanceof Open open &&
                    Objects.equals(token.type(), latest.type())) {
                    stack.pop();
                }
            }
        }
    }

    record Anchor(int row, int col) implements Comparable<Anchor> {
        static Anchor min(int row) { return new Anchor(row, 0); }
        static Anchor max(int row) { return new Anchor(row, Integer.MAX_VALUE); }
        @Override
        public int compareTo(Anchor that) {
            int c = Integer.compare(this.row, that.row);
            return c == 0 ? Integer.compare(this.col, that.col) : c;
        }
    }

}
