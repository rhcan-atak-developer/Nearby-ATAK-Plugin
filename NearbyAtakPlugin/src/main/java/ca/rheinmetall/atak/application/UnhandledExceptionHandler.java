package ca.rheinmetall.atak.application;

import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.util.Date;

public class UnhandledExceptionHandler implements Thread.UncaughtExceptionHandler
{
    private static final String TAG = "UnhandledExceptionHandler";

    private static final String DEFAULT_STACKTRACE_FOLDER = Environment.getExternalStorageDirectory() + File.separator + "rhc" + File.separator;

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private final String _stackTraceFolder;

    public UnhandledExceptionHandler()
    {
        this(DEFAULT_STACKTRACE_FOLDER);
    }

    public UnhandledExceptionHandler(final String stackTrackFolder)
    {
        _stackTraceFolder = stackTrackFolder;
        if(!new File(_stackTraceFolder).mkdirs())
            Log.i(TAG, String.format("Cannot create %s or already exists", _stackTraceFolder));
    }

    @Override
    public void uncaughtException(@NonNull final Thread thread, @NonNull final Throwable exception)
    {
        generateErrorLog(exception);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /*
    Source : https://www.oodlestechnologies.com/blogs/Gracefully-handle-uncaught-exceptions-and-force-close-issue-in-android
    */
    private void generateErrorLog(final Throwable exception)
    {
        final StringWriter stackTrace = new StringWriter();
        exception.printStackTrace(new PrintWriter(stackTrace));
        final StringBuilder errorReport = new StringBuilder();
        errorReport.append("************ CAUSE OF ERROR ************" + LINE_SEPARATOR);
        errorReport.append(stackTrace);

        errorReport.append( "************ Date ************" + new Date(Instant.now().toEpochMilli()));
        errorReport.append(LINE_SEPARATOR+ "************ DEVICE INFORMATION ***********" + LINE_SEPARATOR);
        errorReport.append("Brand: "+ Build.BRAND);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Device: "+Build.DEVICE);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Model: "+Build.MODEL);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Id: "+Build.ID);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Product: "+Build.PRODUCT);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append(LINE_SEPARATOR + "************ BUILD INFO ************" + LINE_SEPARATOR);
        errorReport.append("SDK: "+Build.VERSION.SDK_INT);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Release: "+Build.VERSION.RELEASE);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Incremental: "+Build.VERSION.INCREMENTAL);
        errorReport.append(LINE_SEPARATOR);
        final String report = errorReport.toString();
        Log.e(TAG, report);
        saveAsFile(report);
    }

    private void saveAsFile(final String errorReport)
    {
        final String stackTraceFilename = "issAtakPlugins.stack-" + Instant.now().toEpochMilli() + ".stacktrace";
        try(final FileOutputStream localFileOutputStream = new FileOutputStream(_stackTraceFolder + stackTraceFilename))
        {
            localFileOutputStream.write(errorReport.getBytes());
        }
        catch(final IOException e)
        {
            Log.e(TAG, "Exception occurred in error catcher", e);
        }
    }
}
