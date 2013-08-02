
package es.jonatantierno.scrumdailytimer;

import android.content.Context;
import android.media.MediaPlayer;

import com.google.inject.Provides;

/**
 * Guice provider for MediaPlayer.
 * 
 * @author root
 */
public class Provider {
    @Provides
    public MediaPlayer getAlarmPlayer(Context context) {
        return MediaPlayer.create(context, R.raw.airhorn);
    }

}
