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

import javafx.application.Platform;
import javafx.scene.control.TreeItem;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link PathTreeView}.
 * @author Naotsugu Kobayashi
 */
class PathTreeViewTest {

//    @BeforeAll
//    static void initJavaFX() throws InterruptedException {
//        CountDownLatch latch = new CountDownLatch(1);
//        try {
//            Platform.startup(latch::countDown);
//        } catch (IllegalStateException e) {
//            // Already started
//            latch.countDown();
//        }
//        assertTrue(latch.await(5, TimeUnit.SECONDS));
//    }
//
//    private void runOnFxThread(Runnable runnable) throws Exception {
//        CountDownLatch latch = new CountDownLatch(1);
//        Throwable[] throwableHolder = new Throwable[1];
//        Platform.runLater(() -> {
//            try {
//                runnable.run();
//            } catch (Throwable t) {
//                throwableHolder[0] = t;
//            } finally {
//                latch.countDown();
//            }
//        });
//        assertTrue(latch.await(5, TimeUnit.SECONDS), "Timeout waiting for FX thread");
//        if (throwableHolder[0] != null) {
//            if (throwableHolder[0] instanceof Exception e) {
//                throw e;
//            } else {
//                throw new RuntimeException(throwableHolder[0]);
//            }
//        }
//    }
//
//    @Test
//    void testMoveToParentPreservesExpansionState(@TempDir Path tempDir) throws Exception {
//        // Setup folder structure:
//        // tempDir/root
//        // tempDir/root/dir1
//        // tempDir/root/dir1/dir2
//        Path root = Files.createDirectory(tempDir.resolve("root"));
//        Path dir1 = Files.createDirectory(root.resolve("dir1"));
//        Path dir2 = Files.createDirectory(dir1.resolve("dir2"));
//
//        runOnFxThread(() -> {
//            PathTreeView treeView = new PathTreeView(dir1);
//            treeView.setCompactFolders(false);
//            PathTreeView.FileOperationHandler handler = new PathTreeView.FileOperationHandler(treeView);
//
//            // Fetch the root item (dir1)
//            assertEquals(1, treeView.getRoot().getChildren().size());
//            TreeItem<Path> dir1Item = treeView.getRoot().getChildren().getFirst();
//            assertEquals(dir1, dir1Item.getValue());
//
//            // Expand dir1 and its child dir2 to check if expansion state is preserved
//            dir1Item.setExpanded(true);
//
//            // To load children, we call getChildren() which triggers buildChildren
//            assertEquals(1, dir1Item.getChildren().size());
//            TreeItem<Path> dir2Item = dir1Item.getChildren().getFirst();
//            assertEquals(dir2, dir2Item.getValue());
//            dir2Item.setExpanded(true);
//
//            // Execute moveToParent
//            handler.moveToParent(dir1Item);
//
//            // Now root of treeView should have changed to 'root'
//            assertEquals(1, treeView.getRoot().getChildren().size());
//            TreeItem<Path> newRootItem = treeView.getRoot().getChildren().getFirst();
//            assertEquals(root, newRootItem.getValue());
//            assertTrue(newRootItem.isExpanded(), "New root should be expanded");
//
//            // Check if dir1 is still expanded under the new root
//            assertEquals(1, newRootItem.getChildren().size());
//            TreeItem<Path> newDir1Item = newRootItem.getChildren().getFirst();
//            assertEquals(dir1, newDir1Item.getValue());
//            assertTrue(newDir1Item.isExpanded(), "dir1 under root should be expanded");
//
//            // Check if dir2 is still expanded under dir1
//            assertEquals(1, newDir1Item.getChildren().size());
//            TreeItem<Path> newDir2Item = newDir1Item.getChildren().getFirst();
//            assertEquals(dir2, newDir2Item.getValue());
//            assertTrue(newDir2Item.isExpanded(), "dir2 under dir1 should be expanded");
//
//            // Check selection state - the original directory (dir1) should be selected
//            assertEquals(newDir1Item, treeView.getSelectionModel().getSelectedItem(), "Original dir1 should be selected");
//        });
//    }
//
//    @Test
//    void testMoveToParentOverlapResolution(@TempDir Path tempDir) throws Exception {
//        // Setup folder structure:
//        // tempDir/root
//        // tempDir/root/dir1
//        // tempDir/root/dir3
//        Path root = Files.createDirectory(tempDir.resolve("root"));
//        Path dir1 = Files.createDirectory(root.resolve("dir1"));
//        Path dir3 = Files.createDirectory(root.resolve("dir3"));
//
//        runOnFxThread(() -> {
//            // Add both dir1 and dir3 as roots
//            PathTreeView treeView = new PathTreeView(dir1, dir3);
//            treeView.setCompactFolders(false);
//            PathTreeView.FileOperationHandler handler = new PathTreeView.FileOperationHandler(treeView);
//
//            assertEquals(2, treeView.getRoot().getChildren().size());
//
//            TreeItem<Path> dir1Item = treeView.getRoot().getChildren().stream()
//                    .filter(item -> item.getValue().equals(dir1))
//                    .findFirst()
//                    .orElseThrow();
//            TreeItem<Path> dir3Item = treeView.getRoot().getChildren().stream()
//                    .filter(item -> item.getValue().equals(dir3))
//                    .findFirst()
//                    .orElseThrow();
//
//            dir3Item.setExpanded(true);
//
//            // Execute moveToParent on dir1
//            handler.moveToParent(dir1Item);
//
//            // Since root is the parent, and root contains dir3, dir3 should be removed from the top-level roots.
//            // The tree should now only have 'root' as the top-level root.
//            assertEquals(1, treeView.getRoot().getChildren().size());
//            TreeItem<Path> newRootItem = treeView.getRoot().getChildren().getFirst();
//            assertEquals(root, newRootItem.getValue());
//
//            // Under 'root', we should see dir1 and dir3
//            assertEquals(2, newRootItem.getChildren().size());
//
//            TreeItem<Path> subDir3Item = newRootItem.getChildren().stream()
//                    .filter(item -> item.getValue().equals(dir3))
//                    .findFirst()
//                    .orElseThrow();
//            assertTrue(subDir3Item.isExpanded(), "Nested root dir3's expansion state should have been restored");
//
//            // Check selection state - the original directory (dir1) should be selected
//            TreeItem<Path> subDir1Item = newRootItem.getChildren().stream()
//                    .filter(item -> item.getValue().equals(dir1))
//                    .findFirst()
//                    .orElseThrow();
//            assertEquals(subDir1Item, treeView.getSelectionModel().getSelectedItem(), "Original dir1 should be selected");
//        });
//    }
//
//    @Test
//    void testSetAsRootPreservesExpansionState(@TempDir Path tempDir) throws Exception {
//        // Setup folder structure:
//        // tempDir/root
//        // tempDir/root/dir1
//        // tempDir/root/dir1/dir2
//        Path root = Files.createDirectory(tempDir.resolve("root"));
//        Path dir1 = Files.createDirectory(root.resolve("dir1"));
//        Path dir2 = Files.createDirectory(dir1.resolve("dir2"));
//
//        runOnFxThread(() -> {
//            PathTreeView treeView = new PathTreeView(root);
//            treeView.setCompactFolders(false);
//            PathTreeView.FileOperationHandler handler = new PathTreeView.FileOperationHandler(treeView);
//
//            // Expand root and dir1 to load dir2
//            assertEquals(1, treeView.getRoot().getChildren().size());
//            TreeItem<Path> rootItem = treeView.getRoot().getChildren().getFirst();
//            rootItem.setExpanded(true);
//
//            assertEquals(1, rootItem.getChildren().size());
//            TreeItem<Path> dir1Item = rootItem.getChildren().getFirst();
//            dir1Item.setExpanded(true);
//
//            assertEquals(1, dir1Item.getChildren().size());
//            TreeItem<Path> dir2Item = dir1Item.getChildren().getFirst();
//            dir2Item.setExpanded(true);
//
//            // Execute setAsRoot on dir1
//            handler.setAsRoot(dir1Item);
//
//            // Now root of treeView should have changed to 'dir1'
//            assertEquals(1, treeView.getRoot().getChildren().size());
//            TreeItem<Path> newRootItem = treeView.getRoot().getChildren().getFirst();
//            assertEquals(dir1, newRootItem.getValue());
//            assertTrue(newRootItem.isExpanded(), "New root (dir1) should be expanded");
//
//            // Check if dir2 under new root (dir1) is still expanded
//            assertEquals(1, newRootItem.getChildren().size());
//            TreeItem<Path> newDir2Item = newRootItem.getChildren().getFirst();
//            assertEquals(dir2, newDir2Item.getValue());
//            assertTrue(newDir2Item.isExpanded(), "dir2 under new root should be expanded");
//
//            // Check selection state - the new root (dir1) should be selected
//            assertEquals(newRootItem, treeView.getSelectionModel().getSelectedItem(), "New root (dir1) should be selected");
//        });
//    }
}
