package ca.rheinmetall.atak.thread;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * {@link MainThreadScheduledExecutorService} propose most of the functionalities of a regular {@link ScheduledExecutorService}. However,
 * since it use the post method of an {@link Handler} to run {@link Runnable} and {@link Callable} on the Main Thread, if a task is cancelled
 * after being posted on the main thread, it will not be cancelled.
 */
@Singleton
public class MainThreadScheduledExecutorService implements ScheduledExecutorService
{
    private static final String TAG = MainThreadScheduledExecutorService.class.getSimpleName();

    private final Handler _mainThreadHandler = new Handler(Looper.getMainLooper());
    private final ScheduledExecutorService _executor = Executors.newSingleThreadScheduledExecutor();

    @Inject
    MainThreadScheduledExecutorService()
    {
        // for dagger
    }

    @NonNull
    @Override
    public ScheduledFuture<?> schedule(@NonNull final Runnable command, final long delay, @NonNull final TimeUnit unit)
    {
        return _executor.schedule(runCommandOnMainThread(command), delay, unit);
    }

    @NonNull
    @Override
    public <V> ScheduledFuture<V> schedule(@NonNull final Callable<V> callable, final long delay, @NonNull final TimeUnit unit)
    {
        return _executor.schedule(() -> callCommandOnMainThread(callable), delay, unit);
    }

    @NonNull
    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(@NonNull final Runnable command, final long initialDelay, final long period, @NonNull final TimeUnit unit)
    {
        return _executor.scheduleAtFixedRate(runCommandOnMainThread(command), initialDelay, period, unit);
    }

    @NonNull
    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(@NonNull final Runnable command, final long initialDelay, final long delay, @NonNull final TimeUnit unit)
    {
        return _executor.scheduleAtFixedRate(runCommandOnMainThread(command), initialDelay, delay, unit);
    }

    @Override
    public void shutdown()
    {
        _executor.shutdown();
    }

    @NonNull
    @Override
    public List<Runnable> shutdownNow()
    {
        return _executor.shutdownNow();
    }

    @Override
    public boolean isShutdown()
    {
        return _executor.isShutdown();
    }

    @Override
    public boolean isTerminated()
    {
        return _executor.isTerminated();
    }

    @Override
    public boolean awaitTermination(final long timeout, @NonNull final TimeUnit unit) throws InterruptedException
    {
        return _executor.awaitTermination(timeout, unit);
    }

    @NonNull
    @Override
    public <T> Future<T> submit(@NonNull final Callable<T> task)
    {
        return _executor.submit(() -> callCommandOnMainThread(task));
    }

    @NonNull
    @Override
    public <T> Future<T> submit(@NonNull final Runnable task, final T result)
    {
        return _executor.submit(runCommandOnMainThread(task), result);
    }

    @NonNull
    @Override
    public Future<?> submit(@NonNull final Runnable task)
    {
        return _executor.submit(runCommandOnMainThread(task));
    }

    @NonNull
    @Override
    public <T> List<Future<T>> invokeAll(@NonNull final Collection<? extends Callable<T>> tasks)
    {
        return tasks.stream().map(this::submit).collect(Collectors.toList());
    }

    @NonNull
    @Override
    public <T> List<Future<T>> invokeAll(@NonNull final Collection<? extends Callable<T>> tasks, final long timeout, @NonNull final TimeUnit unit)
    {
        throw new UnsupportedOperationException();
    }

    @NonNull
    @Override
    public <T> T invokeAny(@NonNull final Collection<? extends Callable<T>> tasks)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T invokeAny(@NonNull final Collection<? extends Callable<T>> tasks, final long timeout, @NonNull final TimeUnit unit)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void execute(@NonNull final Runnable command)
    {
        if(!_executor.isShutdown())
            _mainThreadHandler.post(command);
        else
            throw new RejectedExecutionException("Executor is shutdown");
    }
    
    public void runInMainThread(@NonNull final Runnable command)
    {
        if (Objects.equals(Looper.getMainLooper(), Looper.myLooper()))
            command.run();
        else
            execute(command);
    }

    private Runnable runCommandOnMainThread(@NonNull final Runnable command)
    {
        return () -> _mainThreadHandler.post(runSafely(command));
    }

    private static Runnable runSafely(final Runnable command)
    {
        return () -> {
            try
            {
                command.run();
            }
            catch (Exception e)
            {
                Log.e(TAG, "task throw exception", e);
            }
        };
    }

    private <V> V callCommandOnMainThread(@NonNull final Callable<V> command) throws Exception
    {
        final ValueHolder<V> valueHolder = new ValueHolder<>();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        _mainThreadHandler.post(() -> {
            try
            {
                valueHolder.setValue(command.call());
                countDownLatch.countDown();
            }
            catch (Exception e)
            {
                valueHolder.setValue(null);
                countDownLatch.countDown();
            }
        });

        if(!countDownLatch.await(1, TimeUnit.SECONDS))
            throw new MainThreadExecutionException("task took more than a second to execute on main thread");

        final V result = valueHolder.getValue();
        if(result == null)
            throw new MainThreadExecutionException("Exception while calling callable.");

        return valueHolder.getValue();
    }

    private static class ValueHolder<V>
    {
        private final AtomicReference<V> _value = new AtomicReference<>();

        public void setValue(V value)
        {
            _value.set(value);
        }

        public V getValue()
        {
            return _value.get();
        }
    }

    public static class MainThreadExecutionException extends ExecutionException
    {
        public MainThreadExecutionException(final String message)
        {
            super(message);
        }
    }
}
