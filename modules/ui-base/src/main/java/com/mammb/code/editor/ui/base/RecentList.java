/*
 * Copyright 2026-2026 the original author or authors.
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

/**
 * A class that represents a list with a fixed maximum size, maintaining
 * the most recently accessed or added elements. When the list exceeds
 * the specified maximum size, the least recently added element is removed.
 *
 * @param <E> The type of elements stored in the list.
 * @author Naotsugu Kobayashi
 */
public class RecentList<E> implements Iterable<E> {

    private final int maxSize;
    private final List<E> list;

    /**
     * Constructs a new RecentList with a specified maximum size. The list
     * maintains the most recently accessed or added elements up to the
     * specified size. When the list exceeds the maximum size, the least recently
     * added element is removed.
     *
     * @param maxSize the maximum number of elements the list can hold; must be greater than 0
     * @throws IllegalArgumentException if maxSize is less than or equal to 0
     */
    public RecentList(int maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize must be > 0");
        }
        this.maxSize = maxSize;
        this.list = new LinkedList<>();
    }

    /**
     * Adds an element to the list while maintaining the maximum size constraint
     * and ensuring the element is positioned as the most recently added item.
     * If the element already exists in the list, it is removed before being
     * re-added to the front. If the list exceeds the maximum size after adding
     * the element, the least recently added element is removed.
     *
     * @param e the element to be added to the list; must not be null
     * @throws NullPointerException if the specified element is null
     */
    public void push(E e) {
        list.remove(e);
        if (list.size() >= maxSize) {
            list.removeLast();
        }
        list.addFirst(e);
    }

    /**
     * Moves the specified element to the front of the list if it exists.
     * This operation signifies that the element has been recently accessed.
     *
     * @param e the element to be moved to the front of the list; must not be null
     * @throws NullPointerException if the specified element is null
     */
    public void touch(E e) {
        if (list.remove(e)) {
            list.addFirst(e);
        }
    }

    /**
     * Retrieves, but does not remove, the first element of the list, which
     * represents the most recently accessed or added item.
     *
     * @return the first element of the list, or {@code null} if the list is empty
     */
    public E peek() {
        return list.isEmpty() ? null : list.getFirst();
    }

    /**
     * Returns a copy of the current list. The returned list represents
     * the state of the recent elements, maintaining their order from
     * most recent to least recent.
     *
     * @return a new list containing the elements in this list in the same order
     */
    public List<E> list() {
        return new ArrayList<>(list);
    }

    /**
     * Returns the number of elements currently in the list.
     *
     * @return the number of elements in the list
     */
    public int size() {
        return list.size();
    }

    /**
     * Checks if the list is currently empty.
     *
     * @return {@code true} if the list contains no elements, {@code false} otherwise
     */
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public Iterator<E> iterator() {
        return list.iterator();
    }

    /**
     * Returns a sequential {@code Stream} containing the elements of the recent list.
     * The stream can be used for processing or transforming the elements while maintaining their order
     * from most recent to least recent.
     *
     * @return a sequential {@code Stream} of the elements in the list
     */
    public Stream<E> stream() {
        return list.stream();
    }

    @Override
    public String toString() {
        return list.toString();
    }

}
