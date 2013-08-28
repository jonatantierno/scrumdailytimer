
package es.jonatantierno.scrumdailytimer;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;

import roboguice.RoboGuice;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import com.google.inject.AbstractModule;
import com.google.inject.util.Modules;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "../ScrumDailyTimer/AndroidManifest.xml")
public class MainActivityTest {

    MainActivity out;
    ShadowActivity shadowOut;
    ViewPager mViewPager;
    FragmentPagerAdapter mAdapter;

    ChronoFragment mChronoFragment;
    ResultsFragment mResultsFragment;

    TextView mTapForNextTextView;

    public class TestModule extends AbstractModule {

        @Override
        protected void configure() {

        }

    }

    @Before
    public void setUp() throws Exception {
        RoboGuice.setBaseApplicationInjector(Robolectric.application, RoboGuice.DEFAULT_STAGE,
                Modules.override(RoboGuice.newDefaultRoboModule(Robolectric.application)).with(new TestModule()));

        out = Robolectric.buildActivity(MainActivity.class).create().start().resume().get();

        mViewPager = (ViewPager) out.findViewById(R.id.pager);
        mAdapter = (FragmentPagerAdapter) mViewPager.getAdapter();
        mChronoFragment = (ChronoFragment) mAdapter.getItem(0);
        mResultsFragment = (ResultsFragment) mAdapter.getItem(1);

        mTapForNextTextView = (TextView) out.findViewById(R.id.tapForNextTextView);

    }

    @Test
    public void shouldCreateFragments() {
        assertNotNull(mViewPager.getAdapter());

        assertTrue(mAdapter.getItem(0) instanceof ChronoFragment);
        assertTrue(mAdapter.getItem(1) instanceof ResultsFragment);

    }

    @Test
    public void testShouldAccessViews() {

        // Test
        assertEquals(out.getString(R.string.tap_to_start_daily), mTapForNextTextView.getText().toString());
    }

}
