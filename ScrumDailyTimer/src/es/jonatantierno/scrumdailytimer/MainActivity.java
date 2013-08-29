
package es.jonatantierno.scrumdailytimer;

import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.InjectView;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.WindowManager;

import com.google.inject.Inject;

/**
 * Main Activity containing a view pager.
 */
public class MainActivity extends RoboFragmentActivity {
    private MediaPlayer mAlarmPlayer;

    @InjectView(R.id.pager)
    ViewPager mPager;

    @Inject
    ScrumTimer mScrumTimer;

    final ViewPager.OnPageChangeListener mPagerListener;

    ChronoFragment mChronoFragment;
    ResultsFragment mResultsFragment;

    public MainActivity() {
        mPagerListener = new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                mScrumTimer.stopCountDown();

                mResultsFragment.displayData(mChronoFragment);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // Nothing to do
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // Nothing to do

            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Ensure screen does not turn off
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        ChronoPagerAdapter adapter = new ChronoPagerAdapter(getSupportFragmentManager(), this);
        mChronoFragment = adapter.mChronoFragment;
        mResultsFragment = adapter.mResultsFragment;

        mPager.setAdapter(adapter);
        mPager.setOnPageChangeListener(mPagerListener);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mPager.setPageTransformer(true, new EndMeetingPageTransformer());
        }

    }

    /**
     * For testing.
     * 
     * @return listener that receives the page change.
     */
    OnPageChangeListener getPagerListener() {
        return mPagerListener;
    }

}
