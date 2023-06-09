package com.mammb;

import javafx.scene.text.Font;

public interface Span<T> {
    String text();
    Font font();
    T style();
}
