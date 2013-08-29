
package es.jonatantierno.scrumdailytimer;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

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
import android.view.View;
import android.widget.TextView;

import com.google.inject.AbstractModule;
import com.google.inject.util.Modules;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "../ScrumDailyTimer/AndroidManifest.xml")
public class MainActivityTest {

    MainActivity out;
    ShadowActivity shadowOut;
    ViewPager mViewPager;
    ViewPager.OnPageChangeListener mPagerListener;
    FragmentPagerAdapter mAdapter;
    ScrumTimer mockTimer;

    ChronoFragment mChronoFragment;
    ResultsFragment mResultsFragment;

    TextView mTapForNextTextView;
    View mWholeView;
    TextView mNumberOfParticipantsTextView;

    public class TestModule extends AbstractModule {

        @Override
        protected void configure() {
            mockTimer = mock(ScrumTimer.class);

            bind(ScrumTimer.class).toInstance(mockTimer);
        }
    }

    @Before
    public void setUp() throws Exception {
        RoboGuice.setBaseApplicationInjector(Robolectric.application, RoboGuice.DEFAULT_STAGE,
                Modules.override(RoboGuice.newDefaultRoboModule(Robolectric.application)).with(new TestModule()));

        out = Robolectric.buildActivity(MainActivity.class).create().start().resume().get();

        mPagerListener = out.getPagerListener();

        mViewPager = (ViewPager) out.findViewById(R.id.pager);
        mAdapter = (FragmentPagerAdapter) mViewPager.getAdapter();
        mChronoFragment = (ChronoFragment) mAdapter.getItem(0);
        mResultsFragment = (ResultsFragment) mAdapter.getItem(1);

        mTapForNextTextView = (TextView) out.findViewById(R.id.tapForNextTextView);
        mWholeView = out.findViewById(R.id.wholeLayout);
        mNumberOfParticipantsTextView = (TextView) out.findViewById(R.id.numberOfParticipantsReportDataTextView);

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

    /**
     * Changing view (going to result screen) stops countdown timer (prevents sound).
     */
    @Test
    public void whenResultsScreenSelectedThenStopCountdown() {

        mPagerListener.onPageSelected(1);

        verify(mockTimer).stopCountDown();

        assertTrue(mAdapter.getItem(0) instanceof ChronoFragment);
        assertTrue(mAdapter.getItem(1) instanceof ResultsFragment);
    }

    /**
     * Show results.
     */
    @Test
    public void whenResultsScreenSelectedThenShowResults() {
        mWholeView.performClick();
        mWholeView.performClick();

        mPagerListener.onPageSelected(1);

        assertEquals("1", mNumberOfParticipantsTextView.getText());
    }
}
