
package es.jonatantierno.scrumdailytimer;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;

import roboguice.RoboGuice;
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
    View wholeLayout;
    TextView mParticipantTextView;
    TextView mTapForNextTextView;
    TextView mCountDownTextView;
    TextView mTotalTimeTextView;

    public class TestModule extends AbstractModule {

        @Override
        protected void configure() {

            mockTimer = Mockito.mock(ScrumTimer.class);
            bind(ScrumTimer.class).toInstance(mockTimer);
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

        // Test
        assertEquals(out.getString(R.string.tap_to_start_daily), mTapForNextTextView.getText().toString());
        assertEquals(View.GONE, mParticipantTextView.getVisibility());

        // Execute.
        wholeLayout.performClick();

        // Test
        verify(mockTimer).start();
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

        // Start first participant 1/5
        wholeLayout.performClick();

        assertEquals("Participant 1/5", mParticipantTextView.getText().toString());

        // participant 2/5
        wholeLayout.performClick();

        assertEquals("Participant 2/5", mParticipantTextView.getText().toString());

        // participant 3/5
        wholeLayout.performClick();

        assertEquals("Participant 3/5", mParticipantTextView.getText().toString());

        // participant 4/5
        wholeLayout.performClick();

        assertEquals("Participant 4/5", mParticipantTextView.getText().toString());

        // participant 5/5
        wholeLayout.performClick();

        assertEquals("Participant 5/5", mParticipantTextView.getText().toString());
        assertEquals(out.getString(R.string.tap_when_done), mTapForNextTextView.getText().toString());
        assertEquals(View.VISIBLE, mParticipantTextView.getVisibility());

        // finish meeting
        wholeLayout.performClick();

        assertEquals(View.GONE, mParticipantTextView.getVisibility());
        assertEquals(out.getString(R.string.tap_to_finish_daily), mTapForNextTextView.getText().toString());

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
        assertEquals("01:00", mTotalTimeTextView.getText().toString());

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

    }
}
