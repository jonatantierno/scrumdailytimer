
package es.jonatantierno.scrumdailytimer;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.view.MotionEventCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

    private ChronoStatus mStatus = ChronoStatus.STARTED;
    private Vibrator mVibrator;

    private boolean mPausedInTimeout = false;
    /*
     * Used to reset back press from main activity.
     */
    boolean isBackPressReset = false;

    private void start() {

        mVibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        new Thread() {
            public void run() {
                mAlarmPlayer = mProvider.getAlarmPlayer(getActivity());
            }
        }.start();
        new Thread() {
            public void run() {
                mTickPlayer = mProvider.getTickPlayer(getActivity());
            }
        }.start();

        mScrumTimer.configure(this);

        mSeekBarController.configure(mSeekBar, this);

        mNumberOfParticipants = 1;
        mNumberOfTimeouts = 0;
        mStatus = ChronoStatus.STARTED;

        mWholeLayout.setBackgroundResource(R.drawable.background_gradient);

        mTapForNextTextView.setText(R.string.tap_for_first_participant);

        mScrumTimer.startTimer();

        mWholeLayout.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                if (mStatus == ChronoStatus.STARTED) {
                    return false;
                }
                mWholeLayout.setBackgroundResource(R.drawable.pause_background_gradient);
                mScrumTimer.pauseCountDown();

                // If tick is playing, then we are in timeout.
                if (mTickPlayer.isPlaying()) {
                    mPausedInTimeout = true;
                    mTickPlayer.pause();
                }

                return false;
            }
        });
        mWholeLayout.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = MotionEventCompat.getActionMasked(event);

                switch (action) {
                    case MotionEvent.ACTION_UP:
                        return endPause();
                    default:
                        break;
                }
                return false;
            }
        });

        mWholeLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mScrumTimer.isCountDownPaused()) {
                    return;
                }

                vibrate();
                isBackPressReset = true;

                switch (mStatus) {
                    case STARTED:
                        // Set time and store
                        storeSlotTime();
                        mScrumTimer.setTimeSlotLength(mSeekBar.getProgress());

                        mWholeLayout.setBackgroundResource(R.drawable.meeting_background_gradient);
                        mSeekBar.setVisibility(View.GONE);

                        mStatus = ChronoStatus.COUNTDOWN;

                        mWarmUpTime = mScrumTimer.getPrettyTime();

                        mParticipantTextView.setVisibility(View.VISIBLE);

                        repaintParticipants();

                        mTapForNextTextView.setText(R.string.tap_for_next);

                        mScrumTimer.resetCountDown();
                        mCountDownTextView.setText(mScrumTimer.getPrettyCountDown());

                        break;
                    case COUNTDOWN:
                        if (mTickPlayer.isPlaying()) {
                            mTickPlayer.pause();
                        }
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

    /**
     * For Testing
     */
    boolean endPause() {
        if (mScrumTimer.isCountDownPaused()) {

            // If in timeout, restart tick player and reset timeout background
            if (mPausedInTimeout) {
                mTickPlayer.start();
                mWholeLayout.setBackgroundResource(R.drawable.timeout_background_gradient);
            } else {
                mWholeLayout.setBackgroundResource(R.drawable.meeting_background_gradient);
            }
            mPausedInTimeout = false;
            mScrumTimer.resumeCountDown();
            return true;
        }
        return false;
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
        mSeekBar.setVisibility(View.VISIBLE);
        mParticipantTextView.setVisibility(View.GONE);
        mSeekBar.setProgress(timeSlotLength);
        setTime(timeSlotLength);
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
     * Pause ticking clock sound
     */
    public void pauseTickSound() {
        if (mTickPlayer.isPlaying()) {
            mTickPlayer.pause();
        }
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
                mWholeLayout.setBackgroundResource(R.drawable.timeout_background_gradient);
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
        mWholeLayout.setBackgroundResource(R.drawable.meeting_background_gradient);
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

        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.participant_animation);
        mParticipantTextView.startAnimation(animation);
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

        mScrumTimer.stopCountDown();
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
    STARTED, COUNTDOWN, LAST_COUNTDOWN, END;
}
