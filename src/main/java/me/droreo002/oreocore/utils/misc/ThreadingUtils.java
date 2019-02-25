package me.droreo002.oreocore.utils.misc;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

/**
 * From LuckPerms's Storage Class
 */
public final class ThreadingUtils {

    private static final ScheduledExecutorService asyncExecutor = new AsyncExecutor();

    public static  <T> CompletableFuture<T> makeFuture(Callable<T> supplier) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return supplier.call();
            } catch (Exception e) {
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                throw new CompletionException(e);
            }
        }, asyncExecutor);
    }

    private static final class WrappedRunnable implements Runnable {
        private final Runnable delegate;

        WrappedRunnable(Runnable delegate) {
            this.delegate = delegate;
        }

        @Override
        public void run() {
            try {
                this.delegate.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static final class AsyncExecutor extends ScheduledThreadPoolExecutor {
        AsyncExecutor() {
            super(4, new ThreadFactoryBuilder().setNameFormat("OreoCore-%d").build());
        }

        @Override
        public void execute(Runnable command) {
            super.execute(new WrappedRunnable(command));
        }
    }
}
