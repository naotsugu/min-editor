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
package com.mammb.code.editor.ui.app;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * The AddressPath.
 * @author Naotsugu Kobayashi
 */
public class AddressPath {

    private Path path;

    private boolean directory;


    AddressPath(Path path, boolean directory) {
        this.path = path.normalize();
        this.directory = directory;
    }

    public static AddressPath of(Path path) {
        return new AddressPath(path, Files.isDirectory(path));
    }


    public Path dirOn(int index) {
        var ret = directory ? path : path.getParent();
        for (var p = ret; p != null; p = p.getParent()) {
            ret = p;
            if (p.toString().length() < index) {
                break;
            }
        }
        return ret;
    }


    public List<Path> listSibling(int index) {
        try (Stream<Path> s = Files.list(dirOn(index))) {
            return s.sorted(Comparator.comparing(p -> Files.isDirectory(p) ? -1 : 1)).toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
