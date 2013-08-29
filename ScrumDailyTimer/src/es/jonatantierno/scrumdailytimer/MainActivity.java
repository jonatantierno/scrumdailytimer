
package es.jonatantierno.scrumdailytimer;

import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.InjectView;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.WindowManager;

/**
 * Main Activity containing a view pager.
 */
public class MainActivity extends RoboFragmentActivity {
    private MediaPlayer mAlarmPlayer;

    @InjectView(R.id.pager)
    ViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Ensure screen does not turn off
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        mPager.setAdapter(new ChronoPagerAdapter(getSupportFragmentManager(), this));
    }

}
