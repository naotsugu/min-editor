package com.mammb.code.editor.core.syntax2;

import com.mammb.code.editor.core.syntax.Syntax;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.mammb.code.editor.core.syntax2.BlockType.neutral;
import static com.mammb.code.editor.core.syntax2.BlockType.range;
import static org.junit.jupiter.api.Assertions.*;

/**
 * The test of {@link BlockScopes}.
 * @author Naotsugu Kobayashi
 */
class BlockScopesTest {

    private BlockType blockComment = range("/*", "*/");
    private BlockType fence = neutral("```", Syntax::of);

    @Test
    void readBlockComment() {
        var target = new BlockScopes(blockComment, fence);
        var src = """
            package com.example;
            /**
             * comment
             */
            class Main() {
            }
            """.lines().map(l -> l + "\n").toList();

        var source = LexerSource.of(0, src.get(0));
        assertEquals(Optional.empty(), target.read(source));

        source = LexerSource.of(1, src.get(1));
        assertEquals(blockComment, target.read(source).get().type());

        source = LexerSource.of(2, src.get(2));
        assertEquals(blockComment, target.read(source).get().type());

        source = LexerSource.of(3, src.get(3));
        assertEquals(blockComment, target.read(source).get().type());
        assertEquals('\n', source.nextRemaining().ch());

        source = LexerSource.of(4, src.get(4));
        assertEquals(Optional.empty(), target.read(source));

        source = LexerSource.of(5, src.get(5));
        assertEquals(Optional.empty(), target.read(source));

    }
}
