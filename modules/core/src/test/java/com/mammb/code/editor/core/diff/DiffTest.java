package com.mammb.code.editor.core.diff;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class DiffTest {

    private SourcePair<String> pair(List<String> org, List<String> rev) {
        return new SourcePair<>(SourcePair.Source.of(org), SourcePair.Source.of(rev));
    }

    @Test
    void testEmpty() {
        var source = pair(List.of(), List.of());
        var changeSet = Diff.run(source);
        assertTrue(changeSet.changes().isEmpty());
    }

    @Test
    void testIdentical() {
        var source = pair(
            List.of("a", "b", "c"),
            List.of("a", "b", "c"));
        var changeSet = Diff.run(source);
        assertTrue(changeSet.changes().isEmpty());
    }

    @Test
    void testInsertion() {
        var source = pair(
            List.of("a", "c"),
            List.of("a", "b", "c"));
        var changeSet = Diff.run(source);
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
        var source = pair(
            List.of("a", "b", "c"),
            List.of("a", "c"));
        var changeSet = Diff.run(source);
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
        var source = pair(
            List.of("a", "b", "c"),
            List.of("a", "x", "c"));
        var changeSet = Diff.run(source);
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
        var source = pair(
            List.of("a", "b", "c", "d", "f", "g"),
            List.of("a", "x", "c", "e", "f", "h"));
        var changeSet = Diff.run(source);
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
        var source = pair(
            List.of("a", "b", "c"),
            List.of("d", "e", "f"));
        var changeSet = Diff.run(source);
        assertEquals(1, changeSet.changes().size());
        var change = changeSet.changes().getFirst();
        assertEquals(ChangeSet.Change.Type.CHANGE, change.type());
        assertEquals(0, change.orgFrom());
        assertEquals(3, change.orgTo());
        assertEquals(0, change.revFrom());
        assertEquals(3, change.revTo());
    }

}
