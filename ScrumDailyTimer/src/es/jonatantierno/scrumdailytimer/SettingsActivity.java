
package es.jonatantierno.scrumdailytimer;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.EditText;

/**
 * Report with data of the meeting.
 */
public class SettingsActivity extends RoboActivity {

    @InjectView(R.id.slotTimeDurationEditText)
    EditText mTimeSlotDurationEditText;
    @InjectView(R.id.numberOfParticipantsEditText)
    EditText mNumberOfParticipantsEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

    }

    @Override
    protected void onPause() {
        super.onPause();

        Editor editor = getSharedPreferences().edit();

        int timeSlotDuration = 60; // Default value

        try {
            timeSlotDuration = Integer.parseInt(mTimeSlotDurationEditText.getText().toString());
        } catch (NumberFormatException nfe) {
            // Do nothing, default value
        }

        editor.putInt(ChronoActivity.TIME_SLOT_LENGTH, timeSlotDuration);

        int numberOfParticipants = 5; // Default value
        try {
            numberOfParticipants = Integer.parseInt(mNumberOfParticipantsEditText.getText().toString());
        } catch (NumberFormatException nfe) {
            // Do nothing, default value
        }
        editor.putInt(ChronoActivity.NUMBER_OF_PARTICIPANTS, numberOfParticipants);
        editor.commit();

    }

    /**
     * For testing.
     */
    SharedPreferences getSharedPreferences() {
        return getSharedPreferences(ChronoActivity.PREFS_NAME, 0);
    }
}
