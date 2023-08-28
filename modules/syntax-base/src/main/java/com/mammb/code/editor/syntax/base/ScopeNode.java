package com.mammb.code.editor.syntax.base;

public interface ScopeNode {
    ScopeNode parent();
    Token open();
    Token close();
    default int level() {
        ScopeNode node = this;
        int level = 0;
        while (!node.isRoot()) {
            level++;
            node = node.parent();
        }
        return level;
    }
    default boolean isRoot() { return parent() == null; }
    default boolean isOpen() { return close() == null; };
    default boolean isClosed() { return !isOpen(); }
}
