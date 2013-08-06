
package es.jonatantierno.scrumdailytimer;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;

import roboguice.RoboGuice;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.EditText;

import com.google.inject.AbstractModule;
import com.google.inject.util.Modules;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "../ScrumDailyTimer/AndroidManifest.xml")
public class SettingsActivityTest {

    SettingsActivity out;
    ShadowActivity shadowOut;
    SharedPreferences mockPreferences;
    Editor mockEditor;

    EditText mNumberOfParticipantsEditText;
    EditText mTimeSlotDurationEditText;

    public class TestModule extends AbstractModule {

        @Override
        protected void configure() {

        }

    }

    @Before
    public void setUp() throws Exception {
        RoboGuice.setBaseApplicationInjector(Robolectric.application, RoboGuice.DEFAULT_STAGE,
                Modules.override(RoboGuice.newDefaultRoboModule(Robolectric.application)).with(new TestModule()));

        mockPreferences = mock(SharedPreferences.class);
        mockEditor = mock(Editor.class);
        when(mockPreferences.edit()).thenReturn(mockEditor);
        when(mockPreferences.getInt(ChronoActivity.TIME_SLOT_LENGTH, -1)).thenReturn(30);
        when(mockPreferences.getInt(ChronoActivity.NUMBER_OF_PARTICIPANTS, -1)).thenReturn(3);

        out = new SettingsActivity() {
            @Override
            SharedPreferences getSharedPreferences() {
                return mockPreferences;
            }
        };
        shadowOut = Robolectric.shadowOf(out);

        // Fixture
        out.onCreate(new Bundle());

        mNumberOfParticipantsEditText = (EditText) out.findViewById(R.id.numberOfParticipantsEditText);
        mTimeSlotDurationEditText = (EditText) out.findViewById(R.id.slotTimeDurationEditText);
    }

    @Test
    public void shouldStorePreferences() {

        mNumberOfParticipantsEditText.setText("5");
        mTimeSlotDurationEditText.setText("10");

        out.onPause();

        verify(mockEditor).putInt(ChronoActivity.TIME_SLOT_LENGTH, 10);
        verify(mockEditor).putInt(ChronoActivity.NUMBER_OF_PARTICIPANTS, 5);
        verify(mockEditor).commit();

    }

    @Test
    public void whenParticipantsEmptyThenDefault() {
        mNumberOfParticipantsEditText.setText("");

        out.onPause();

        verify(mockEditor).putInt(ChronoActivity.NUMBER_OF_PARTICIPANTS, 5);
        verify(mockEditor).commit();
    }

    @Test
    public void whenDurationEmptyThenDefault() {
        mTimeSlotDurationEditText.setText("");

        out.onPause();

        verify(mockEditor).putInt(ChronoActivity.TIME_SLOT_LENGTH, 60);
        verify(mockEditor).commit();
    }

    @Test
    public void shouldObtainInitialValuesFromSettings() {

        verify(mockPreferences).getInt(ChronoActivity.TIME_SLOT_LENGTH, -1);
        verify(mockPreferences).getInt(ChronoActivity.NUMBER_OF_PARTICIPANTS, -1);

        assertEquals("30", mTimeSlotDurationEditText.getText().toString());
        assertEquals("3", mNumberOfParticipantsEditText.getText().toString());
    }

}
