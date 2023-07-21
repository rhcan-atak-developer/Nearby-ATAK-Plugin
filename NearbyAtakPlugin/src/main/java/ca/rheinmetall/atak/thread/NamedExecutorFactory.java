package ca.rheinmetall.atak.thread;

import androidx.annotation.NonNull;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class NamedExecutorFactory
{
    @Inject
    NamedExecutorFactory(){}

    @NonNull
    public ScheduledExecutorService createNamedExecutor(final String name)
    {
        return new ScheduledThreadPoolExecutor(1, runnable ->
        {
            final Thread thread = Executors.defaultThreadFactory().newThread(runnable);
            thread.setName(name);

            return thread;
        });
    }
}
