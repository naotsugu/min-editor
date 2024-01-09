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
package com.mammb.code.editor.ui.app.control;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * The AddressPath.
 * @author Naotsugu Kobayashi
 */
public class AddressPath {

    /** The target path. */
    private final Path path;
    /** Whether the target path is a directory or not. */
    private final boolean directory;


    /**
     * Constructor.
     * @param path the target path
     * @param directory whether the target path is a directory or not
     */
    AddressPath(Path path, boolean directory) {
        this.path = path.normalize();
        this.directory = directory;
    }

    public static AddressPath of(Path path) {
        return new AddressPath(path, Files.isDirectory(path));
    }

    public static AddressPath of(String path) {
        return of(Path.of(path));
    }


    public AddressPath dirOn(int index) {
        var ret = directory ? path : path.getParent();
        for (var p = ret; p != null; p = p.getParent()) {
            ret = p;
            if (p.toString().length() < index) {
                break;
            }
        }
        return AddressPath.of(ret);
    }


    public Path path() {
        return path;
    }


    public List<PathItem> listItem() {
        return listItem(directory ? path : path.getParent());
    }


    private List<Path> list(Path path) {
        try (Stream<Path> s = Files.list(path)) {
            return s.sorted(Comparator.comparing(p -> Files.isDirectory(p) ? -1 : 1)).toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private List<PathItem> listItem(Path path) {
        try (Stream<Path> s = Files.list(path).filter(PathItem.exclude)) {
            return s.map(FlattenPath::of).sorted().toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public int stringLength() {
        return toString().length();
    }


    @Override
    public String toString() {
        return path.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddressPath that = (AddressPath) o;
        return Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }

}
