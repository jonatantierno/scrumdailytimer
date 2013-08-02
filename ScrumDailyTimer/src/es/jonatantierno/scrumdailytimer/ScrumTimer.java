
package es.jonatantierno.scrumdailytimer;

import java.util.Timer;
import java.util.TimerTask;

/**
 * This class implements a timer, with a precision of seconds, and provides the content for both the total meeting time
 * and the countdown. It uses a {@link java.util.Timer} that will call tick() once every second.
 * 
 * @author jonatantierno
 */
public class ScrumTimer {
    private ChronoActivity mActivity = null;
    private long mNumberOfSeconds = 0;
    private long mCountDown = 0;
    private long mCountDownMax = 60;

    private StringBuffer mPrettyTime = new StringBuffer();

    private Timer mTimer = null;
    private boolean mCountingDown = false;

    /**
     * Call to start meeting timer.
     */
    public void startTimer() {
        mTimer = new Timer();

        mTimer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                tick();

            }
        }, 0, 1000);

        _startTimer();
    }

    /**
     * Like startTimer(), only without the timer itself. for testing purposes.
     */
    void _startTimer() {

        mNumberOfSeconds = 0;

        mActivity.setDailyTimer(getPrettyTime(mNumberOfSeconds));

    }

    /**
     * Call when participant is done to reset countdown and start next participant.
     */
    public void resetCountDown() {
        mCountingDown = true;
        mCountDown = mCountDownMax;
    }

    /**
     * Call when last participant is done to stop countdown. It is not started again.
     */
    public void stopCountDown() {
        mCountingDown = false;
    }

    /**
     * Call to stop timer and ent meeting.
     */
    public void stopTimer() {
        mTimer.cancel();

    }

    /**
     * Setter for the activity. Should be in the constructor, but...
     * 
     * @param activity main activity.
     */
    public void setActivity(ChronoActivity activity) {
        mActivity = activity;
    }

    /**
     * Called once per second by a {@link java.util.Timer}
     */
    public void tick() {
        mNumberOfSeconds++;
        mActivity.setDailyTimer(getPrettyTime(mNumberOfSeconds));

        if (mCountingDown) {
            mCountDown--;

            if (mCountDown < 0) {
                mActivity.timeOut();
                stopCountDown();
            } else {
                mActivity.setCountDown(getPrettyTime(mCountDown));
            }
        }
    }

    private String getPrettyTime(long seconds) {
        long minutesToShow = seconds / 60;
        long secondsToShow = seconds % 60;

        mPrettyTime.delete(0, mPrettyTime.length());

        if (minutesToShow < 10) {
            mPrettyTime.append(0);
        }
        mPrettyTime.append(minutesToShow);
        mPrettyTime.append(':');

        if (secondsToShow < 10) {
            mPrettyTime.append(0);
        }
        mPrettyTime.append(secondsToShow);

        return mPrettyTime.toString();
    }

    /**
     * Sets the countdown seconds. For testing.
     * 
     * @param i countdown seconds
     */
    void setCountDownSeconds(int i) {
        mCountDown = i;
    }

    /**
     * Provides current elapsed time, or time when timer stopped.
     * 
     * @return the time as a string in in the format MM:SS
     */
    public String getPrettyTime() {
        return getPrettyTime(mNumberOfSeconds);
    }

}
