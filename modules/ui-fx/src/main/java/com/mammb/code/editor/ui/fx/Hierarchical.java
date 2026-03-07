/*
 * Copyright 2023-2025 the original author or authors.
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
package com.mammb.code.editor.ui.fx;

/**
 * The Hierarchical.
 * @param <E> the type of element
 * @author Naotsugu Kobayashi
 */
public interface Hierarchical<E extends Hierarchical<E>> {

    /**
     * Set the parent.
     * @param parent the parent
     */
    void parent(E parent);

    /**
     * Get the parent.
     * @return the parent
     */
    E parent();

    /**
     * Retrieves the root element in the hierarchy.
     * @return the root element of the hierarchy
     */
    default E root() {
        @SuppressWarnings("unchecked")
        E root = (E) this;
        while (!root.isRoot()) {
            root = root.parent();
        }
        return root;
    }

    /**
     * Determines whether this element is a root element in the hierarchy.
     * An element is considered a root if it does not have a parent.
     * @return {@code true} if this element has no parent, otherwise {@code false}.
     */
    default boolean isRoot() {
        return parent() == null;
    }

}
