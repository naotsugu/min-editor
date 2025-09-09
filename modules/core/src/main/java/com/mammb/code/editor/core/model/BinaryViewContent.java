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
import com.mammb.code.editor.core.tools.BinaryView;
import com.mammb.code.editor.core.tools.Source16;
import java.nio.file.Path;

/**
 * The BinaryViewContent.
 * @author Naotsugu Kobayashi
 */
public class BinaryViewContent extends ContentAdapter {

    /** The pear content. */
    private final Content pear;

    private Path path;

    public BinaryViewContent(Path path, Content pear) {
        this.pear = pear;
        this.path = path;
    }

    public static BinaryViewContent of(Path path) {
        var viewPath = Files.write(
            Files.createTempFile(null, path.getFileName().toString(), ""),
            BinaryView.run(new Source16(path)));
        return new BinaryViewContent(path, Content.of(viewPath));
    }

    @Override
    protected Content pear() {
        return pear;
    }

}
