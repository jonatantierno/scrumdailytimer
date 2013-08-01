
package es.jonatantierno.scrumdailytimer;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * Timer to use in a Scrum Daily Meeting. Main Screen.
 * 
 */
public class ChronoActivity extends RoboActivity {

	@InjectView(R.id.wholeLayout)
    private View wholeLayout;
	@InjectView(R.id.countDownTextView)
    private TextView countDownTextView; 
	@InjectView(R.id.participantTextView)
    private TextView participantTextView; 
	@InjectView(R.id.totalTimeTextView)
    private TextView totalTimeTextView; 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

    }
}
