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
package com.mammb.code.editor.core.layout;

import com.mammb.code.editor.core.Content;
import com.mammb.code.editor.core.FontMetrics;
import com.mammb.code.editor.core.ScreenScroll;
import com.mammb.code.editor.core.text.Text;
import java.util.List;
import java.util.Optional;

/**
 * The screen layout.
 * @author Naotsugu Kobayashi
 */
public interface ScreenLayout extends LineLayout {

    void setScreenSize(double width, double height);
    void scrollNext(int lineDelta);
    void scrollPrev(int lineDelta);
    void scrollAt(int line);
    void scrollX(double x);
    void refreshBuffer(int startRow, int endRow);
    List<Text> texts();
    List<Text> lineNumbers();
    Optional<Loc> locationOn(int row, int col);
    int yToLineOnScreen(double y);
    double screenWidth();
    double screenHeight();
    int screenLineSize();
    int topLine();
    void applyScreenScroll(ScreenScroll screenScroll);

    void wrapWith(int width);

    static ScreenLayout of(Content content, FontMetrics fm) {
        ContentLayout layout = new RowLayout(content, fm);
        return new BasicScreenLayout(layout);
    }

    static ScreenLayout wrapOf(Content content, FontMetrics fm) {
        ContentLayout layout = new WrapLayout(content, fm);
        return new BasicScreenLayout(layout);
    }

}
