
package es.jonatantierno.scrumdailytimer;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;

import roboguice.RoboGuice;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.inject.AbstractModule;
import com.google.inject.util.Modules;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "../ScrumDailyTimer/AndroidManifest.xml")
public class ChronoActivityTest {

    ChronoActivity out;
    ShadowActivity shadowOut;
    ScrumTimer mockTimer;

    Provider mockPlayerProvider;
    MediaPlayer mockPlayer;
    View wholeLayout;
    TextView mParticipantTextView;
    TextView mTapForNextTextView;
    TextView mCountDownTextView;
    TextView mTotalTimeTextView;

    public class TestModule extends AbstractModule {

        @Override
        protected void configure() {

            mockTimer = Mockito.mock(ScrumTimer.class);
            mockPlayer = Mockito.mock(MediaPlayer.class);
            mockPlayerProvider = Mockito.mock(Provider.class);
            when(mockPlayerProvider.getAlarmPlayer(Mockito.any(Context.class))).thenReturn(mockPlayer);

            bind(ScrumTimer.class).toInstance(mockTimer);
            bind(Provider.class).toInstance(mockPlayerProvider);
        }

    }

    @Before
    public void setUp() throws Exception {
        RoboGuice.setBaseApplicationInjector(Robolectric.application, RoboGuice.DEFAULT_STAGE,
                Modules.override(RoboGuice.newDefaultRoboModule(Robolectric.application)).with(new TestModule()));

        out = new ChronoActivity();
        shadowOut = Robolectric.shadowOf(out);

        // Fixture
        out.onCreate(new Bundle());

        wholeLayout = out.findViewById(R.id.wholeLayout);
        mParticipantTextView = (TextView) out.findViewById(R.id.participantTextView);
        mTapForNextTextView = (TextView) out.findViewById(R.id.tapForNextTextView);
        mCountDownTextView = (TextView) out.findViewById(R.id.countDownTextView);
        mTotalTimeTextView = (TextView) out.findViewById(R.id.totalTimeTextView);
    }

    @Test
    public void whenTapShouldStartTimer() {

        verify(mockTimer).setActivity(out);

        // Test
        assertEquals(out.getString(R.string.tap_to_start_daily), mTapForNextTextView.getText().toString());
        assertEquals(View.GONE, mParticipantTextView.getVisibility());

        // Execute.
        wholeLayout.performClick();

        // Test
        verify(mockTimer).startTimer();
        assertEquals(out.getString(R.string.tap_for_first_participant), mTapForNextTextView.getText().toString());
    }

    @Test
    public void whenTapAgainShouldStartCountDown() {
        // Fixture

        // Execute.
        // Start timer
        wholeLayout.performClick();

        // Start first countdown
        wholeLayout.performClick();

        // Test

        assertEquals("Participant 1/5", mParticipantTextView.getText().toString());
        assertEquals(View.VISIBLE, mParticipantTextView.getVisibility());
        assertEquals(out.getString(R.string.tap_for_next), mTapForNextTextView.getText().toString());
    }

    @Test
    public void whenTapFiveTimesShouldIncrementParticipantCounterAndStop() {
        // Fixture

        // Execute.
        // Start timer
        wholeLayout.performClick();
        verify(mockTimer, times(0)).resetCountDown();

        // Start first participant 1/5
        wholeLayout.performClick();

        assertEquals("Participant 1/5", mParticipantTextView.getText().toString());
        verify(mockTimer).resetCountDown();

        // participant 2/5
        wholeLayout.performClick();

        assertEquals("Participant 2/5", mParticipantTextView.getText().toString());
        verify(mockTimer, times(2)).resetCountDown();

        // participant 3/5
        wholeLayout.performClick();

        assertEquals("Participant 3/5", mParticipantTextView.getText().toString());
        verify(mockTimer, times(3)).resetCountDown();

        // participant 4/5
        wholeLayout.performClick();

        assertEquals("Participant 4/5", mParticipantTextView.getText().toString());
        verify(mockTimer, times(4)).resetCountDown();

        // participant 5/5
        wholeLayout.performClick();

        assertEquals("Participant 5/5", mParticipantTextView.getText().toString());
        assertEquals(out.getString(R.string.tap_when_done), mTapForNextTextView.getText().toString());
        assertEquals(View.VISIBLE, mParticipantTextView.getVisibility());
        verify(mockTimer, times(5)).resetCountDown();

        // finish last participant
        wholeLayout.performClick();

        assertEquals(View.GONE, mParticipantTextView.getVisibility());
        assertEquals(out.getString(R.string.tap_to_finish_daily), mTapForNextTextView.getText().toString());
        verify(mockTimer, times(1)).stopCountDown();

        // Finish meeting and go to report activty
        wholeLayout.performClick();

        assertTrue(out.isFinishing());

        Intent intent = shadowOut.getNextStartedActivity();
        assertNotNull(intent);
        assertEquals(ReportActivity.class.getCanonicalName(), intent.getComponent().getClassName());

    }

    /**
     * Tests that timer and countdown are properly displayed
     */
    @Test
    public void shouldDisplayTime() {
        // Execute.
        // Start timer
        wholeLayout.performClick();

        // Start first participant 1/5
        wholeLayout.performClick();

        out.setDailyTimer("01:00");
        assertEquals("Total meeting time: 01:00", mTotalTimeTextView.getText().toString());

        out.setCountDown("12:34");
        assertEquals("12:34", mCountDownTextView.getText().toString());

    }

    /**
     * When time is over, go to next participant
     */
    @Test
    public void whenCountDownOverThenAlarm() {
        // Execute.
        // Start timer
        wholeLayout.performClick();

        // Start first participant 1/5
        wholeLayout.performClick();

        assertEquals("Participant 1/5", mParticipantTextView.getText().toString());

        out.timeOut();

        assertEquals(0xFFFF0000, Robolectric.shadowOf(wholeLayout).getBackgroundColor());

        verify(mockPlayer).start();

    }

    @Test
    public void whenResetCountDownThenUndoTimeout() {
        // Execute.
        // Start timer
        wholeLayout.performClick();

        // Start first participant 1/5
        wholeLayout.performClick();

        assertEquals("Participant 1/5", mParticipantTextView.getText().toString());

        out.timeOut();

        assertEquals(0xFFFF0000, Robolectric.shadowOf(wholeLayout).getBackgroundColor());

        // Start first participant 2/5
        wholeLayout.performClick();

        assertFalse(0xFFFF0000 == Robolectric.shadowOf(wholeLayout).getBackgroundColor());
    }

    /**
     * Release resources onDestroy().
     */
    @Test
    public void whenStopThenCancelTimerReleaseMedia() {
        out.onStop();

        verify(mockTimer).stopTimer();
        verify(mockPlayer).release();
    }

    /**
     * Test report data.
     */
    @Test
    public void shouldSendDataToReportActivity() {
        // Send total time
        when(mockTimer.getPrettyTime()).thenReturn("00:03").thenReturn("20:13");

        // Send time elapsed between meeting start and first participant

        // Meeting start
        wholeLayout.performClick();

        // first participant
        wholeLayout.performClick();

        // Send number of timeouts.
        out.timeOut();
        out.timeOut();
        out.timeOut();

        out.goToReportActivity();
        Intent intent = shadowOut.getNextStartedActivity();

        assertEquals("20:13", intent.getStringExtra(ChronoActivity.TOTAL_TIME));
        assertEquals(3, intent.getIntExtra(ChronoActivity.TIMEOUTS, 0));
        assertEquals("00:03", intent.getStringExtra(ChronoActivity.WARMUP_TIME));

    }
}
