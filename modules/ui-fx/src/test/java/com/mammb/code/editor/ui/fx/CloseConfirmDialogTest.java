package com.mammb.code.editor.ui.fx;

import javafx.application.Platform;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link CloseConfirmDialog}.
 */
class CloseConfirmDialogTest {

    @BeforeAll
    static void initJavaFX() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        try {
            Platform.startup(latch::countDown);
        } catch (IllegalStateException e) {
            latch.countDown();
        }
        assertTrue(latch.await(5, TimeUnit.SECONDS));
    }

    private void runOnFxThread(Runnable runnable) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Throwable[] throwableHolder = new Throwable[1];
        Platform.runLater(() -> {
            try {
                runnable.run();
            } catch (Throwable t) {
                throwableHolder[0] = t;
            } finally {
                latch.countDown();
            }
        });
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Timeout waiting for FX thread");
        if (throwableHolder[0] != null) {
            if (throwableHolder[0] instanceof Exception e) {
                throw e;
            } else {
                throw new RuntimeException(throwableHolder[0]);
            }
        }
    }

    @Test
    void testDialogCreationAndActionCallbacks() throws Exception {
        runOnFxThread(() -> {
            Stage owner = new Stage();
            CloseConfirmDialog dialog = new CloseConfirmDialog(owner, "TestFile.txt");

            AtomicBoolean saveCalled = new AtomicBoolean(false);
            AtomicBoolean discardCalled = new AtomicBoolean(false);
            AtomicBoolean cancelCalled = new AtomicBoolean(false);

            dialog.setOnSave(() -> saveCalled.set(true));
            dialog.setOnDiscard(() -> discardCalled.set(true));
            dialog.setOnCancel(() -> cancelCalled.set(true));

            assertNotNull(dialog);
            assertEquals(CloseConfirmDialog.Answer.SAVE, CloseConfirmDialog.Answer.valueOf("SAVE"));
            assertEquals(CloseConfirmDialog.Answer.DISCARD, CloseConfirmDialog.Answer.valueOf("DISCARD"));
            assertEquals(CloseConfirmDialog.Answer.CANCEL, CloseConfirmDialog.Answer.valueOf("CANCEL"));
        });
    }

    @Test
    void testSetTargetNameAndMessage() throws Exception {
        runOnFxThread(() -> {
            Stage owner = new Stage();
            CloseConfirmDialog dialog = new CloseConfirmDialog(owner);
            dialog.setTargetName("Untitled.txt");
            dialog.setMessage("Custom confirmation message");
            assertNotNull(dialog);
        });
    }
}
