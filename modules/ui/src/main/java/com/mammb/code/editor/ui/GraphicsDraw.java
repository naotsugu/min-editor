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
package com.mammb.code.editor.ui;

import com.mammb.code.editor.core.Rgba;

/**
 * The graphics draw.
 * @author Naotsugu Kobayashi
 */
public interface GraphicsDraw {

    void clearRect();
    void clearRect(double x, double y, double w, double h);
    void fillRect(Rgba color, double x, double y, double w, double h);
    void strokeLine(Rgba color, double lineWidth, double x1, double y1, double x2, double y2);
    void strokeRect(Rgba color, double lineWidth, double x, double y, double w, double h);
    void fillText(Rgba color, String text, double x, double y);
    void strokePolygon(Rgba color, double lineWidth, double xPoints[], double yPoints[], int nPoints);
    void fillPolygon(Rgba color, double xPoints[], double yPoints[], int nPoints);
    void increaseFontSize(double sizeDelta);
}
