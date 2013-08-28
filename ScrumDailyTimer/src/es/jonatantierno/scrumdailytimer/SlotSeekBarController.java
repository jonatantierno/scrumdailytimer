
package es.jonatantierno.scrumdailytimer;

import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.google.inject.Inject;

/**
 * This class controls the seekbar that defines the slot duration. It limits the possible values to intervals of 30
 * seconds, the min duration to 30 seconds and the max duration to 5 minutes (300 seconds)
 * 
 * @author root
 */
public class SlotSeekBarController {
    /**
     * Minimum slot interval (and minimum slot value) in seconds.
     */
    public static final int MINIMUM_INTERVAL = 30;
    /**
     * Default value of slot: 1 minute
     */
    public static final int DEFAULT_VALUE = 60;

    private SeekBar mSeekBar;
    private final OnSeekBarChangeListener mListener;
    private ChronoInterface mChronoInterface;

    @Inject
    public SlotSeekBarController() {
        mListener = new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mSeekBar.setProgress(getClosestSlot(mSeekBar.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int closest = getClosestSlot(progress);
                mSeekBar.setProgress(closest);
                mChronoInterface.setTime(closest);
            }
        };
    }

    /**
     * For testing.
     * 
     * @return listener.
     */
    OnSeekBarChangeListener getListener() {
        return mListener;
    }

    private int getClosestSlot(int progress) {

        int progress_slot = (int) (Math.round((double) progress / (double) MINIMUM_INTERVAL)) * MINIMUM_INTERVAL;

        if (progress_slot < MINIMUM_INTERVAL) {
            progress_slot = MINIMUM_INTERVAL;
        }
        return progress_slot;
    }

    /**
     * Configure controller
     * 
     * @param seekBar seekbar view that controls the slot
     * @param chronoInterface interface to the chrono visual element.
     */
    public void configure(SeekBar seekBar, ChronoInterface chronoInterface) {
        mChronoInterface = chronoInterface;
        mSeekBar = seekBar;
        mSeekBar.setOnSeekBarChangeListener(mListener);
    }
}
