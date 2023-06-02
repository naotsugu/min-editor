/*
 * Copyright 2019-2023 the original author or authors.
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
package com.mammb.code.editor.model.layout;

/**
 * A simple object which carries bounds information as floats.
 * @author Naotsugu Kobayashi
 */
public interface Bounds {

    /**
     * Get the minimum x value of bounding box.
     * @return the minimum x value of bounding box
     */
    float minX();

    /**
     * Get the minimum y value of bounding box.
     * @return the minimum y value of bounding box
     */
    float minY();

    /**
     * Get the minimum z value of bounding box.
     * @return the minimum z value of bounding box
     */
    default float minZ()  { return 0.0f; }

    /**
     * Get the maximum x value of bounding box.
     * @return the maximum x value of bounding box
     */
    float maxX();

    /**
     * Get the maximum y value of bounding box.
     * @return the maximum y value of bounding box
     */
    float maxY();

    /**
     * Get the maximum z value of bounding box.
     * @return the maximum z value of bounding box
     */
    default float maxZ() {
        return 0.0f;
    }

    /**
     * Get the width of this Bounds.
     * @return the width of this Bounds
     */
    default float width() {
        return maxX() - minX();
    }

    /**
     * Get the height of this Bounds.
     * @return the height of this Bounds
     */
    default float height() {
        return maxY() - minY();
    }

    /**
     * Get the depth of this Bounds.
     * The dimension along the Z-Axis, since this is a 2D bounds the return value is always 0.0f.
     * @return the depth of this Bounds.
     */
    default float depth() {
        return maxZ() - minZ();
    }

}
