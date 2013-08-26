
package es.jonatantierno.scrumdailytimer;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.inject.Inject;

/**
 * Timer to use in a Scrum Daily Meeting. Main Screen.
 */
public class ChronoActivity extends RoboActivity {

    public static final String TOTAL_TIME = "TOTAL_TIME";
    public static final String TIMEOUTS = "TIMEOUTS";
    public static final String WARMUP_TIME = "WARMUP_TIME";
    public static final String PREFS_NAME = "CHRONO_PREFERENCES";
    public static final String NUMBER_OF_PARTICIPANTS = "NUMBER_OF_PARTICIPANTS";
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
    @InjectView(R.id.settingsButton)
    private ImageButton mSettingsButton;
    @InjectView(R.id.seekBar1)
    private SeekBar mSeekBar;

    @Inject
    private ScrumTimer mScrumTimer;

    @Inject
    private Provider mProvider;

    private MediaPlayer mAlarmPlayer;

    private int mNumberOfParticipants = 5;
    private int mCurrentParticipant = 1;
    private int mNumberOfTimeouts = 0;
    private String mWarmUpTime = "00:00";

    private ChronoStatus mStatus = ChronoStatus.NOT_STARTED;
    private Vibrator mVibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Ensure screen does not turn off
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        new Thread() {
            public void run() {
                mAlarmPlayer = mProvider.getAlarmPlayer(ChronoActivity.this);
            }
        }.start();

        setContentView(R.layout.activity_fullscreen);

        mScrumTimer.setActivity(this);

        mSeekBar.setMax(300);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Nothing (STORE VALUE?)
                int progress = seekBar.getProgress();
                int progress_slot = (int) (Math.round((double) progress / (double) 30)) * 30;

                seekBar.setProgress(progress_slot);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Set value?

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Set Value
                int progress_slot = (int) (Math.round((double) progress / (double) 30)) * 30;
                mCountDownTextView.setText(mScrumTimer.getPrettyTime(progress_slot));

            }
        });

        mCurrentParticipant = 1;
        mNumberOfTimeouts = 0;

        mSettingsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                startActivity(new Intent(ChronoActivity.this, SettingsActivity.class));
            }
        });

        mWholeLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                vibrate();

                switch (mStatus) {
                    case NOT_STARTED:
                        mStatus = ChronoStatus.STARTED;
                        mWholeLayout.setBackgroundColor(getResources().getColor(R.color.meeting_background));
                        mSettingsButton.setVisibility(View.GONE);
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
                        mCurrentParticipant++;
                        repaintParticipants();

                        mScrumTimer.resetCountDown();
                        mCountDownTextView.setText(mScrumTimer.getPrettyCountDown());
                        resetBackground();

                        if (mCurrentParticipant == mNumberOfParticipants) {
                            mTapForNextTextView.setText(R.string.tap_when_done);
                            mStatus = ChronoStatus.LAST_COUNTDOWN;
                        }
                        break;
                    case LAST_COUNTDOWN:
                        mTapForNextTextView.setText(R.string.tap_to_finish_daily);
                        mParticipantTextView.setVisibility(View.GONE);
                        mCountDownTextView.setText("00:00");
                        mStatus = ChronoStatus.END;

                        mScrumTimer.stopCountDown();
                        break;
                    case END:

                        mScrumTimer.stopTimer();

                        goToReportActivity();
                        break;
                    default:
                }
            }

        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Get settings
        mNumberOfParticipants = getSharedPreferences().getInt(NUMBER_OF_PARTICIPANTS, -1);

        int timeSlotLength = getSharedPreferences().getInt(TIME_SLOT_LENGTH, -1);

        if (mNumberOfParticipants == -1 || timeSlotLength == -1) {
            // Go to settings activity
            startActivity(new Intent(this, SettingsActivity.class));
            return;
        }
        mScrumTimer.setTimeSlotLength(timeSlotLength);
    };

    @Override
    protected void onPause() {
        super.onPause();
        // Temporal solution: finish the meeting.
        finish();
    };

    /**
     * For testing.
     */
    SharedPreferences getSharedPreferences() {
        return getSharedPreferences(PREFS_NAME, 0);
    }

    @Override
    protected void onStop() {
        super.onStop();

        mScrumTimer.stopTimer();
        if (mAlarmPlayer != null) {
            mAlarmPlayer.release();
            mAlarmPlayer = null;
        }
    }

    /**
     * Go to report activity. Send and intent with all relevant data.
     */
    void goToReportActivity() {
        Intent intent = new Intent(ChronoActivity.this, ReportActivity.class);
        intent.putExtra(TOTAL_TIME, mScrumTimer.getPrettyTime());
        intent.putExtra(TIMEOUTS, mNumberOfTimeouts);
        intent.putExtra(WARMUP_TIME, mWarmUpTime);
        startActivity(intent);
        finish();

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
        sb.append(mCurrentParticipant);
        sb.append('/');
        sb.append(mNumberOfParticipants);
        mParticipantTextView.setText(sb.toString());
    }

    /**
     * Sets time for the total daily meeting timer.
     * 
     * @param string time to show.
     */
    public void setDailyTimer(final String string) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mTotalTimeTextView.setText(getString(R.string.total_meeting_time) + string);
            }
        });

    }

    /**
     * Sets time for the countdown of the current participant.
     * 
     * @param string time to show.
     */
    public void setCountDown(final String string) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mCountDownTextView.setText(string);
            }
        });

    }

    /**
     * Call when countdown expires
     */
    public void timeOut() {

        mNumberOfTimeouts++;

        if (mAlarmPlayer != null) {
            mAlarmPlayer.start();
        }

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mWholeLayout.setBackgroundColor(getResources().getColor(R.color.timeout_background));
            }
        });

    }

    /**
     * Set normal background
     */
    public void resetBackground() {
        mWholeLayout.setBackgroundColor(getResources().getColor(R.color.meeting_background));
    }
}

enum ChronoStatus {
    NOT_STARTED, STARTED, COUNTDOWN, LAST_COUNTDOWN, END;
}
