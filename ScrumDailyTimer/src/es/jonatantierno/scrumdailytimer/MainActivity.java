
package es.jonatantierno.scrumdailytimer;

import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.InjectView;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

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

        setContentView(R.layout.activity_main);

        mPager.setAdapter(new ChronoPagerAdapter(getSupportFragmentManager(), this));

        mPager.setPageTransformer(true, new EndMeetingPageTransformer());
    }

}
