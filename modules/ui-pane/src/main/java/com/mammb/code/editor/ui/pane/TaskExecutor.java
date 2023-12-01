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
package com.mammb.code.editor.ui.pane;

import javafx.concurrent.Task;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * TaskExecutor.
 * @author Naotsugu Kobayashi
 */
class TaskExecutor {

    /** logger. */
    private static final System.Logger log = System.getLogger(TaskExecutor.class.getName());

    /** The executor. */
    private static final ExecutorService executor = createExecutorService();


    /**
     * Submits a Runnable task for execution and returns a Future representing that task.
     * @param task the task to submit
     */
    public static void submit(Task<?> task) {
        executor.submit(task);
    }


    /**
     * Create executor service.
     * @return the executor service
     */
    private static ExecutorService createExecutorService() {
        ExecutorService executor = Executors.newSingleThreadExecutor(new DaemonThreadFactory());
        Runtime.getRuntime().addShutdownHook(new Thread(executor::shutdownNow));
        return executor;
    }


    /**
     * DaemonThreadFactory.
     */
    private static class DaemonThreadFactory implements ThreadFactory {
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        }
    }

}
