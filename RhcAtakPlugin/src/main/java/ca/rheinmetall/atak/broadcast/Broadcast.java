package ca.rheinmetall.atak.broadcast;

import android.content.Intent;
import android.os.Bundle;

public interface Broadcast
{
    String getAction();
    default Intent createIntent()
    {
        return new Intent(getAction());
    }

    default Intent createIntent(final Bundle extras)
    {
        final Intent intent = new Intent(getAction());
        intent.putExtras(extras);
        return intent;
    }
}