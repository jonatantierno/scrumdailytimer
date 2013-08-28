
package es.jonatantierno.scrumdailytimer;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
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
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.inject.AbstractModule;
import com.google.inject.util.Modules;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "../ScrumDailyTimer/AndroidManifest.xml")
public class ChronoFragmentTest {

    ChronoFragment out;
    ShadowActivity shadowOut;
    ScrumTimer mockTimer;

    Provider mockPlayerProvider;
    MediaPlayer mockPlayer;
    View wholeLayout;
    SharedPreferences mockPreferences;
    Editor mockEditor;

    SlotSeekBarController mockSeekBarController;

    TextView mParticipantTextView;
    TextView mTapForNextTextView;
    TextView mCountDownTextView;
    TextView mTotalTimeTextView;
    SeekBar mSeekBar;

    public class TestModule extends AbstractModule {

        @Override
        protected void configure() {
            mockTimer = Mockito.mock(ScrumTimer.class);
            mockPlayer = Mockito.mock(MediaPlayer.class);
            mockPlayerProvider = Mockito.mock(Provider.class);
            mockSeekBarController = Mockito.mock(SlotSeekBarController.class);
            when(mockPlayerProvider.getAlarmPlayer(Mockito.any(Context.class))).thenReturn(mockPlayer);

            bind(ScrumTimer.class).toInstance(mockTimer);
            bind(Provider.class).toInstance(mockPlayerProvider);
            bind(SlotSeekBarController.class).toInstance(mockSeekBarController);
        }

    }

    @Before
    public void setUp() throws Exception {
        RoboGuice.setBaseApplicationInjector(Robolectric.application, RoboGuice.DEFAULT_STAGE,
                Modules.override(RoboGuice.newDefaultRoboModule(Robolectric.application)).with(new TestModule()));

        mockEditor = mock(Editor.class);
        mockPreferences = Mockito.mock(SharedPreferences.class);

        when(mockPreferences.edit()).thenReturn(mockEditor);
        when(mockPreferences.getInt(ChronoActivity.NUMBER_OF_PARTICIPANTS, -1)).thenReturn(5);
        when(mockPreferences.getInt(ChronoActivity.TIME_SLOT_LENGTH, -1)).thenReturn(60);

        out = new ChronoFragment() {
            @Override
            SharedPreferences getSharedPreferences() {
                return mockPreferences;
            }
        };

        FragmentActivity activity = Robolectric.buildActivity(FragmentActivity.class).create().start().resume().get();

        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(out, null);
        fragmentTransaction.commit();

        shadowOut = Robolectric.shadowOf(activity);

        wholeLayout = out.getView().findViewById(R.id.wholeLayout);
        mParticipantTextView = (TextView) out.getView().findViewById(R.id.participantTextView);
        mTapForNextTextView = (TextView) out.getView().findViewById(R.id.tapForNextTextView);
        mCountDownTextView = (TextView) out.getView().findViewById(R.id.countDownTextView);
        mTotalTimeTextView = (TextView) out.getView().findViewById(R.id.totalTimeTextView);
        mSeekBar = (SeekBar) out.getView().findViewById(R.id.seekBar1);
    }

    @Test
    public void shouldLoadViews() {
        assertNotNull(mTapForNextTextView);
    }

    @Test
    public void whenTapShouldStartTimer() {
        verify(mockTimer).configure(out);

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

        assertEquals("Participant 1", mParticipantTextView.getText().toString());
        assertEquals(View.VISIBLE, mParticipantTextView.getVisibility());
        assertEquals(out.getString(R.string.tap_for_next), mTapForNextTextView.getText().toString());
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

        // Start first participant 1
        wholeLayout.performClick();

        assertEquals("Participant 1", mParticipantTextView.getText().toString());

        out.timeOut();

        assertEquals(0xFFFF0000, Robolectric.shadowOf(wholeLayout).getBackgroundColor());

        verify(mockPlayer).start();

    }

    @Test
    public void whenResetCountDownThenUndoTimeout() {
        // Execute.
        // Start timer
        wholeLayout.performClick();

        // Start first participant
        wholeLayout.performClick();

        assertEquals("Participant 1", mParticipantTextView.getText().toString());

        out.timeOut();

        assertEquals(0xFFFF0000, Robolectric.shadowOf(wholeLayout).getBackgroundColor());

        // Start first participant 2
        wholeLayout.performClick();

        assertFalse(0xFFFF0000 == Robolectric.shadowOf(wholeLayout).getBackgroundColor());
    }

    /**
     * Number of participants and time slot length stored in settings.
     */
    @Test
    public void shouldObtainParametersFromSettings() {
        verify(mockPreferences).getInt(ChronoActivity.TIME_SLOT_LENGTH, -1);
        verify(mockTimer).setTimeSlotLength(60);

        assertEquals(60, mSeekBar.getProgress());
    }

    /**
     * Default value is 60 seconds
     */
    @Test
    public void whenTimeSlotLengthNotSetThenDefaultValue() {
        // Fixture
        when(mockPreferences.getInt(ChronoActivity.TIME_SLOT_LENGTH, -1)).thenReturn(-1);

        // Execute
        out.onResume();

        assertEquals(SlotSeekBarController.DEFAULT_VALUE, mSeekBar.getProgress());
    }

    @Test
    public void shouldChangeColorDuringMeeting() {

        assertEquals(out.getResources().getColor(R.color.background), Robolectric.shadowOf(wholeLayout)
                .getBackgroundColor());

        // Start meeting
        wholeLayout.performClick();

        assertEquals(out.getResources().getColor(R.color.meeting_background), Robolectric.shadowOf(wholeLayout)
                .getBackgroundColor());

    }

    /**
     * Hide SeekBar when meeting starts.
     */
    @Test
    public void whenStartMeetingSeekBarShoulDissapear() {
        assertEquals(View.VISIBLE, mSeekBar.getVisibility());

        wholeLayout.performClick();

        assertEquals(View.GONE, mSeekBar.getVisibility());
    }

    /**
     * when seekbar value changes, update chrono
     */
    @Test
    public void whenSeekBarValueChangesThenUpdateChrono() {
        when(mockTimer.getPrettyTime(40)).thenReturn("00:30");

        out.setTime(40);

        assertEquals("00:30", mCountDownTextView.getText());
    }

    /**
     * Configure seekbar with controller.
     */
    @Test
    public void shouldSetSeekbarController() {
        verify(mockSeekBarController).configure(mSeekBar, out);
    }

    /**
     * Store value from seekbar in settings
     */
    @Test
    public void whenPauseThenStoreValueFromSeekBar() {

        mSeekBar.setProgress(90);

        out.onStop();

        verify(mockEditor).putInt(ChronoActivity.TIME_SLOT_LENGTH, 90);
        verify(mockEditor).commit();
    }

    /**
     * when start, Store value from seekbar in settings. Also, set countdown
     */
    @Test
    public void whenStartMeetingThenStoreValueFromSeekBarAndSetTimer() {

        mSeekBar.setProgress(90);

        wholeLayout.performClick();

        verify(mockTimer).setTimeSlotLength(90);
        verify(mockEditor).putInt(ChronoActivity.TIME_SLOT_LENGTH, 90);
        verify(mockEditor).commit();
    }
}
