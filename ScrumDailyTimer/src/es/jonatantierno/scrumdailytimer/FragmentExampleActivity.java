
package es.jonatantierno.scrumdailytimer;

import roboguice.activity.RoboFragmentActivity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

/**
 * Example of activity with fragments
 */
public class FragmentExampleActivity extends RoboFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.example_fragment_activity);

        // Instantiate a ViewPager and a PagerAdapter.
        ViewPager mPager = (ViewPager) findViewById(R.id.pager);
        ExamplePagerAdapter mPagerAdapter = new ExamplePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        mPager.setPageTransformer(true, new EndMeetingPageTransformer());
    }
}
