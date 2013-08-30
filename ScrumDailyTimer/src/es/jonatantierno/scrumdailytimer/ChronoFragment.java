
package es.jonatantierno.scrumdailytimer;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.inject.Inject;

/**
 * Timer to use in a Scrum Daily Meeting. Main Screen.
 */
public class ChronoFragment extends RoboFragment implements ChronoInterface {
    public static final String TOTAL_TIME = "TOTAL_TIME";
    public static final String TIMEOUTS = "TIMEOUTS";
    public static final String WARMUP_TIME = "WARMUP_TIME";
    public static final String PREFS_NAME = "CHRONO_PREFERENCES";
    public static final String TIME_SLOT_LENGTH = "TIME_SLOT_LENGTH";

    @InjectView(R.id.wholeLayout)
    private View mWholeLayout;
    @InjectView(R.id.countDownTextView)
    private TextView mCountDownTextView;
    @InjectView(R.id.participantTextView)
    private TextView mParticipantTextView;
    @InjectView(R.id.totalTimeTextView)
    private TextView mTotalTimeTextView;
    @InjectView(R.id.tapForNextTextView)
    private TextView mTapForNextTextView;

    @InjectView(R.id.seekBar1)
    private SeekBar mSeekBar;

    @Inject
    private ScrumTimer mScrumTimer;

    @Inject
    SlotSeekBarController mSeekBarController;

    @Inject
    private Provider mProvider;

    private MediaPlayer mAlarmPlayer;
    private MediaPlayer mTickPlayer;

    private int mNumberOfParticipants = 1;
    private int mNumberOfTimeouts = 0;
    private String mWarmUpTime = "00:00";

    private ChronoStatus mStatus = ChronoStatus.NOT_STARTED;
    private Vibrator mVibrator;

    private void start() {

        mVibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        new Thread() {
            public void run() {
                mAlarmPlayer = mProvider.getAlarmPlayer(getActivity());
                mTickPlayer = mProvider.getTickPlayer(getActivity());
            }
        }.start();

        mScrumTimer.configure(this);

        mSeekBarController.configure(mSeekBar, this);

        mNumberOfParticipants = 1;
        mNumberOfTimeouts = 0;

        mTotalTimeTextView.setVisibility(View.GONE);
        mSwipeTextView.setVisibility(View.GONE);
        mTotalTimeTextView.setText("");

        mWholeLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                vibrate();

                switch (mStatus) {
                    case NOT_STARTED:
                        mStatus = ChronoStatus.STARTED;
                        mWholeLayout.setBackgroundColor(getResources().getColor(R.color.meeting_background));
                        mSeekBar.setVisibility(View.GONE);
                        mTotalTimeTextView.setVisibility(View.VISIBLE);
                        mSwipeTextView.setVisibility(View.VISIBLE);

                        storeSlotTime();
                        mScrumTimer.setTimeSlotLength(mSeekBar.getProgress());

                        mTapForNextTextView.setText(R.string.tap_for_first_participant);
                        mScrumTimer.startTimer();
                        break;
                    case STARTED:
                        mStatus = ChronoStatus.COUNTDOWN;

                        mWarmUpTime = mScrumTimer.getPrettyTime();

                        mParticipantTextView.setVisibility(View.VISIBLE);

                        repaintParticipants();

                        mTapForNextTextView.setText(R.string.tap_for_next);

                        mScrumTimer.resetCountDown();
                        mCountDownTextView.setText(mScrumTimer.getPrettyCountDown());

                        break;
                    case COUNTDOWN:
                        mTickPlayer.pause();
                        mNumberOfParticipants++;
                        repaintParticipants();

                        mScrumTimer.resetCountDown();
                        mCountDownTextView.setText(mScrumTimer.getPrettyCountDown());
                        resetBackground();

                        break;
                    default:
                }
            }

        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.activity_fullscreen, container, false);

    }

    @Override
    public void onStart() {
        super.onStart();

        start();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Get settings

        int timeSlotLength = getSharedPreferences().getInt(TIME_SLOT_LENGTH, -1);

        if (timeSlotLength == -1) {
            timeSlotLength = SlotSeekBarController.DEFAULT_VALUE;
        }
        mScrumTimer.setTimeSlotLength(timeSlotLength);
        mSeekBar.setProgress(timeSlotLength);
    }

    public int getNumberOfParticipants() {
        return mNumberOfParticipants;
    }

    /**
     * This time is called by the seekbar when a new value is set.
     * 
     * @param time time in seconds to show.
     */
    @Override
    public void setTime(int time) {
        mCountDownTextView.setText(mScrumTimer.getPrettyTime(time));
    }

    /**
     * Sets time for the total daily meeting timer.
     * 
     * @param string time to show.
     */
    @Override
    public void setDailyTimer(final String string) {
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mTotalTimeTextView.setText(getString(R.string.total_meeting_time) + string);
            }
        });

    }

    @Override
    public void timeOut() {

        mNumberOfTimeouts++;

        if (mAlarmPlayer != null) {
            mAlarmPlayer.start();
        }
        if (mTickPlayer != null) {
            mTickPlayer.start();
        }

        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mWholeLayout.setBackgroundColor(getResources().getColor(R.color.timeout_background));
            }
        });

    }

    @Override
    public void setCountDown(final String string) {
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mCountDownTextView.setText(string);
            }
        });

    }

    /**
     * Set normal background
     */
    public void resetBackground() {
        mWholeLayout.setBackgroundColor(getResources().getColor(R.color.meeting_background));
    }

    /**
     * Haptic feedback. For > Honeycomb that would be:
     * mWholeLayout.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY); But seems to subtle. Will use
     * {@link Vibrator}, and be Gingerbread compatible besides.
     */
    private void vibrate() {
        if (mVibrator != null) {
            mVibrator.vibrate(170);
        }
    }

    private void repaintParticipants() {

        StringBuffer sb = new StringBuffer(getString(R.string.participant));
        sb.append(mNumberOfParticipants);
        mParticipantTextView.setText(sb.toString());
    }

    void storeSlotTime() {
        Editor editor = getSharedPreferences().edit();

        int timeSlotDuration = mSeekBar.getProgress();

        editor.putInt(ChronoFragment.TIME_SLOT_LENGTH, timeSlotDuration);
        editor.commit();

    }

    /**
     * For testing.
     * 
     * @return preferences.
     */
    SharedPreferences getSharedPreferences() {
        return getActivity().getSharedPreferences(PREFS_NAME, 0);
    }

    @Override
    public void onStop() {
        super.onStop();

        mScrumTimer.stopTimer();

        if (mTickPlayer != null) {
            mTickPlayer.release();
            mTickPlayer = null;
        }
        if (mAlarmPlayer != null) {
            mAlarmPlayer.release();
            mAlarmPlayer = null;
        }

        storeSlotTime();
    }

    public int getNumberOfTimeouts() {
        return mNumberOfTimeouts;
    }

    public String getPreparationTime() {
        return mWarmUpTime;
    }
}

enum ChronoStatus {
    NOT_STARTED, STARTED, COUNTDOWN, LAST_COUNTDOWN, END;
}
