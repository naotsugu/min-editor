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
package com.mammb.code.editor.ui.base;

import com.mammb.code.editor.core.FontMetrics;
import com.mammb.code.editor.core.Rgba;

/**
 * The graphics draw.
 * @author Naotsugu Kobayashi
 */
public interface GraphicsDraw {

    /**
     * Clears the entire drawing surface, typically by resetting all drawn content
     * and setting the surface to a default state, such as a transparent or blank background.
     */
    void clear();

    /**
     * Clears the specified rectangular area on the drawing surface,
     * resetting it to a default state, such as transparent or blank.
     *
     * @param x The x-coordinate of the upper-left corner of the rectangle to clear.
     * @param y The y-coordinate of the upper-left corner of the rectangle to clear.
     * @param w The width of the rectangle to clear.
     * @param h The height of the rectangle to clear.
     */
    void clearRect(double x, double y, double w, double h);

    /**
     * Draws and fills a rectangle with the specified color and dimensions.
     *
     * @param color The color to fill the rectangle, defined as an {@code Rgba} object.
     * @param x The x-coordinate of the upper-left corner of the rectangle.
     * @param y The y-coordinate of the upper-left corner of the rectangle.
     * @param w The width of the rectangle.
     * @param h The height of the rectangle.
     */
    void fillRect(Rgba color, double x, double y, double w, double h);

    /**
     * Draws a straight line on the drawing surface with the specified color,
     * line width, and coordinates.
     *
     * @param color The color of the line, defined as an {@code Rgba} object.
     * @param lineWidth The width of the line.
     * @param x1 The x-coordinate of the starting point of the line.
     * @param y1 The y-coordinate of the starting point of the line.
     * @param x2 The x-coordinate of the ending point of the line.
     * @param y2 The y-coordinate of the ending point of the line.
     */
    void strokeLine(Rgba color, double lineWidth, double x1, double y1, double x2, double y2);

    /**
     * Draws the outline of a rectangle with the specified color, line width, and dimensions.
     *
     * @param color The color of the rectangle's outline, defined as an {@code Rgba} object.
     * @param lineWidth The width of the rectangle's outline.
     * @param x The x-coordinate of the upper-left corner of the rectangle.
     * @param y The y-coordinate of the upper-left corner of the rectangle.
     * @param w The width of the rectangle.
     * @param h The height of the rectangle.
     */
    void strokeRect(Rgba color, double lineWidth, double x, double y, double w, double h);

    /**
     * Renders and fills text on the drawing surface at the specified position
     * using the given color.
     *
     * @param color The color to fill the text, defined as an {@code Rgba} object.
     * @param text The string to be rendered on the drawing surface.
     * @param x The x-coordinate where the text rendering starts.
     * @param y The y-coordinate where the text rendering starts.
     */
    void fillText(Rgba color, String text, double x, double y);

    /**
     * Draws the outline of a polygon with the specified color and line width.
     *
     * @param color The color of the polygon's outline, defined as an {@code Rgba} object.
     * @param lineWidth The width of the polygon's outline.
     * @param xPoints An array containing the x-coordinates of the polygon's vertices.
     * @param yPoints An array containing the y-coordinates of the polygon's vertices.
     * @param nPoints The number of vertices in the polygon. Must match the length of {@code xPoints} and {@code yPoints}.
     */
    void strokePolygon(Rgba color, double lineWidth, double[] xPoints, double[] yPoints, int nPoints);

    /**
     * Fills the area defined by the vertices of a polygon with the specified color.
     *
     * @param color The color to fill the polygon, defined as an {@code Rgba} object.
     * @param xPoints An array containing the x-coordinates of the polygon's vertices.
     * @param yPoints An array containing the y-coordinates of the polygon's vertices.
     * @param nPoints The number of vertices in the polygon. Must match the length of {@code xPoints} and {@code yPoints}.
     */
    void fillPolygon(Rgba color, double[] xPoints, double[] yPoints, int nPoints);

    /**
     * Increases the font size used for rendering text on the drawing surface by the specified amount.
     *
     * @param sizeDelta The amount by which to increase the font size. This value can be positive to enlarge the font size or negative to reduce it. A value of 0 retains the current
     *  font size without change.
     */
    void increaseFontSize(double sizeDelta);

    /**
     * Generates a new instance of {@code FontMetrics}, which provides detailed
     * measurements and properties of the currently applied font, such as ascent,
     * descent, line height, and character width.
     *
     * @return a {@code FontMetrics} object that contains font-related metrics
     *         for text formatting and rendering.
     */
    FontMetrics buildFontMetrics();

}
