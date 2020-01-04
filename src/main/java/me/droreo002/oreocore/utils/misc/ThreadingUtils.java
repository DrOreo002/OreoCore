package me.droreo002.oreocore.utils.misc;

import co.aikar.taskchain.TaskChain;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import me.droreo002.oreocore.OreoCore;

import java.util.concurrent.*;

/**
 * From LuckPerms's Storage Class
 */
public final class ThreadingUtils {

    public static final ScheduledExecutorService asyncExecutor = new AsyncExecutor();

    /**
     * Make a new future task
     *
     * @param supplier : The supplier, A.K.A Callbacks
     * @param <T> : Object type
     * @return a CompletableFuture with specified type
     */
    public static <T> CompletableFuture<T> makeFuture(Callable<T> supplier) {
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

    /**
     * Make new task chain
     *
     * @param <T> : Object type
     * @return the TaskChain
     */
    public static <T> TaskChain<T> makeChain() {
        return OreoCore.getInstance().getTaskChainFactory().newChain();
    }

    /**
     * Make new shared chain
     *
     * @param name : The name of the shared chain
     * @param <T> : Object type
     * @return the TaskChain
     */
    public static <T> TaskChain<T> makeSharedChain(String name) {
        return OreoCore.getInstance().getTaskChainFactory().newSharedChain(name);
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
