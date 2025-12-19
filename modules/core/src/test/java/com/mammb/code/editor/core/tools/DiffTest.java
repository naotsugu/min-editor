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
package com.mammb.code.editor.core.tools;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class DiffTest {

    @Test
    void testEmpty() {
        var source = new SourcePair<>(
            Source.of(List.of()),
            Source.of(List.of()));
        @SuppressWarnings("unchecked")
        ChangeSet<String> changeSet = (ChangeSet<String>) Diff.run(source);
        assertTrue(changeSet.changes().isEmpty());
    }

    @Test
    void testIdentical() {
        var source = new SourcePair<>(
            Source.of(List.of("a", "b", "c")),
            Source.of(List.of("a", "b", "c")));
        @SuppressWarnings("unchecked")
        var changeSet = (ChangeSet<String>) Diff.run(source);
        assertTrue(changeSet.changes().isEmpty());
    }

    @Test
    void testInsertion() {
        var source = new SourcePair<>(
            Source.of(List.of("a", "c")),
            Source.of(List.of("a", "b", "c")));
        @SuppressWarnings("unchecked")
        var changeSet = (ChangeSet<String>) Diff.run(source);
        assertEquals(1, changeSet.changes().size());
        var change = changeSet.changes().getFirst();
        assertEquals(ChangeSet.Change.Type.INSERT, change.type());
        assertEquals(1, change.orgFrom());
        assertEquals(1, change.orgTo());
        assertEquals(1, change.revFrom());
        assertEquals(2, change.revTo());
    }

    @Test
    void testDeletion() {
        var source = new SourcePair<>(
            Source.of(List.of("a", "b", "c")),
            Source.of(List.of("a", "c")));
        @SuppressWarnings("unchecked")
        var changeSet = (ChangeSet<String>) Diff.run(source);
        assertEquals(1, changeSet.changes().size());
        var change = changeSet.changes().getFirst();
        assertEquals(ChangeSet.Change.Type.DELETE, change.type());
        assertEquals(1, change.orgFrom());
        assertEquals(2, change.orgTo());
        assertEquals(1, change.revFrom());
        assertEquals(1, change.revTo());
    }

    @Test
    void testChange() {
        var source = new SourcePair<>(
            Source.of(List.of("a", "b", "c")),
            Source.of(List.of("a", "x", "c")));
        @SuppressWarnings("unchecked")
        var changeSet = (ChangeSet<String>) Diff.run(source);
        assertEquals(1, changeSet.changes().size());
        var change = changeSet.changes().getFirst();
        assertEquals(ChangeSet.Change.Type.CHANGE, change.type());
        assertEquals(1, change.orgFrom());
        assertEquals(2, change.orgTo());
        assertEquals(1, change.revFrom());
        assertEquals(2, change.revTo());
    }

    @Test
    void testMixed() {
        var source = new SourcePair<>(
            Source.of(List.of("a", "b", "c", "d", "f", "g")),
            Source.of(List.of("a", "x", "c", "e", "f", "h")));
        @SuppressWarnings("unchecked")
        var changeSet = (ChangeSet<String>) Diff.run(source);
        var changes = changeSet.changes();
        assertEquals(3, changes.size());

        var change1 = changes.get(0);
        assertEquals(ChangeSet.Change.Type.CHANGE, change1.type());
        assertEquals(1, change1.orgFrom());
        assertEquals(2, change1.orgTo());
        assertEquals(1, change1.revFrom());
        assertEquals(2, change1.revTo());

        var change2 = changes.get(1);
        assertEquals(ChangeSet.Change.Type.CHANGE, change2.type());
        assertEquals(3, change2.orgFrom());
        assertEquals(4, change2.orgTo());
        assertEquals(3, change2.revFrom());
        assertEquals(4, change2.revTo());

        var change3 = changes.get(2);
        assertEquals(ChangeSet.Change.Type.CHANGE, change3.type());
        assertEquals(5, change3.orgFrom());
        assertEquals(6, change3.orgTo());
        assertEquals(5, change3.revFrom());
        assertEquals(6, change3.revTo());
    }

    @Test
    void testAnotherMixed() {
        var source = new SourcePair<>(
            Source.of(List.of("a", "b", "c")),
            Source.of(List.of("d", "e", "f")));
        @SuppressWarnings("unchecked")
        var changeSet = (ChangeSet<String>) Diff.run(source);
        assertEquals(1, changeSet.changes().size());
        var change = changeSet.changes().getFirst();
        assertEquals(ChangeSet.Change.Type.CHANGE, change.type());
        assertEquals(0, change.orgFrom());
        assertEquals(3, change.orgTo());
        assertEquals(0, change.revFrom());
        assertEquals(3, change.revTo());
    }

    @Test
    void testToUnifiedDiffString() {
        var source = new SourcePair<>(
            Source.of(List.of("a", "b", "c", "d", "f", "g"), "org.txt"),
            Source.of(List.of("a", "x", "c", "e", "f", "h"), "rev.txt"));
        var result = Diff.run(source);
        String expected = """
            --- org.txt
            +++ rev.txt
            @@ -1,6 +1,6 @@
              a
            - b
            + x
              c
            - d
            + e
              f
            - g
            + h
            """;
        var actual = result.asUnifiedFormText(3);
        assertEquals(expected.lines().toList(), actual);
    }

    @Test
    void testToUnifiedDiffStringWithTwoHunks() {
        var source = new SourcePair<>(
            Source.of(List.of("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m"), "org.txt"),
            Source.of(List.of("a", "X", "c", "d", "e", "f", "g", "h", "i", "j", "k", "Y", "m"), "rev.txt"));
        var result = Diff.run(source);
        String expected = """
            --- org.txt
            +++ rev.txt
            @@ -1,5 +1,5 @@
              a
            - b
            + X
              c
              d
              e
            @@ -9,5 +9,5 @@
              i
              j
              k
            - l
            + Y
              m
            """;
        var actual = result.asUnifiedFormText(3);
        assertEquals(expected.lines().toList(), actual);
    }


}
