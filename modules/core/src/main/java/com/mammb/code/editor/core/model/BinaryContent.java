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
package com.mammb.code.editor.core.model;

import com.mammb.code.editor.core.Content;
import com.mammb.code.editor.core.Files;
import com.mammb.code.editor.core.Query;
import com.mammb.code.editor.core.tools.BinaryView;
import com.mammb.code.editor.core.tools.Source16;
import java.nio.file.Path;
import java.util.Optional;

/**
 * The BinaryContent.
 * @author Naotsugu Kobayashi
 */
public class BinaryContent extends ContentAdapter {

    /** The pear content. */
    private final Content pear;

    private String name;

    private Path source;

    private BinaryContent(Path source, Content pear) {
        this.pear = pear;
        this.source = source;
    }

    public static BinaryContent of(Path source) {
        Path temp = Files.createTempFile(null, source.getFileName().toString(), "");
        temp.toFile().deleteOnExit();
        Path view = Files.write(temp, BinaryView.run(new Source16(source)));
        Content pear = new TextEditContent(view);
        return new BinaryContent(source, pear);
    }

    @Override
    protected Content pear() {
        return pear;
    }

}
