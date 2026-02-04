package com.mammb.code.editor.platform;

public class Gc {

    private Gc() { }

    private static Thread daemon = null;

    private static class LatencyLock { }
    private static final LatencyLock lock = new Gc.LatencyLock();

    private static class Daemon implements Runnable {

        @Override
        public void run() {
            for (;;) {
                long l;
                synchronized (lock) {
                    Runtime runtime = Runtime.getRuntime();
                    if (runtime.freeMemory() * 2 > runtime.totalMemory()) {
                        System.gc();
                    }
                    // wait for the latency period to expire, or for notification that the period has changed
                    try {
                        lock.wait(15 * 1000);
                    } catch (InterruptedException x) { }
                }
            }
        }

        public static void create() {
            Thread t = new Thread(new Gc.Daemon(), "GC Daemon");
            t.setDaemon(true);
            t.setPriority(Thread.MIN_PRIORITY + 1);
            t.start();
            Gc.daemon = t;
        }
    }

}
