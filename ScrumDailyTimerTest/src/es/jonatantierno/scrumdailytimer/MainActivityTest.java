
package es.jonatantierno.scrumdailytimer;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
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
import android.media.MediaPlayer;
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
    View mWholeReportView;
    TextView mNumberOfParticipantsTextView;
    TextView mTimeTextView;
    TextView mTapToFinish;
    MediaPlayer mockPlayer;

    public class TestModule extends AbstractModule {

        @Override
        protected void configure() {
            mockTimer = mock(ScrumTimer.class);
            mockPlayer = mock(MediaPlayer.class);

            bind(ScrumTimer.class).toInstance(mockTimer);
            bind(MediaPlayer.class).toInstance(mockPlayer);
        }
    }

    @Before
    public void setUp() throws Exception {
        RoboGuice.setBaseApplicationInjector(Robolectric.application, RoboGuice.DEFAULT_STAGE,
                Modules.override(RoboGuice.newDefaultRoboModule(Robolectric.application)).with(new TestModule()));

        out = Robolectric.buildActivity(MainActivity.class).create().start().resume().get();
        shadowOut = Robolectric.shadowOf(out);

        mPagerListener = out.getPagerListener();

        mViewPager = (ViewPager) out.findViewById(R.id.pager);
        mAdapter = (FragmentPagerAdapter) mViewPager.getAdapter();
        mChronoFragment = (ChronoFragment) mAdapter.getItem(0);
        mResultsFragment = (ResultsFragment) mAdapter.getItem(1);

        mTapForNextTextView = (TextView) out.findViewById(R.id.tapForNextTextView);
        mWholeView = out.findViewById(R.id.wholeLayout);
        mWholeReportView = out.findViewById(R.id.wholeReportLayout);
        mNumberOfParticipantsTextView = (TextView) out.findViewById(R.id.numberOfParticipantsReportDataTextView);
        mTimeTextView = (TextView) out.findViewById(R.id.totalTimeDataTextView);
        mTapToFinish = (TextView) out.findViewById(R.id.tapToFinishDaily);

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
     * Changing view (going to result screen) stops countdown timer (stops tick).
     */
    @Test
    public void whenResultsScreenSelectedThenStopCountdown() {

        mPagerListener.onPageSelected(1);

        verify(mockTimer).stopCountDown();
        verify(mockPlayer).pause();

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

    /**
     * Time continues to pass
     */
    @Test
    public void whenResultScreenThentimeShouldAdvance() {
        verify(mockTimer).configure(mChronoFragment);

        mPagerListener.onPageSelected(1);

        verify(mockTimer).configure(mResultsFragment);

        when(mockTimer.getPrettyTime()).thenReturn("00:00").thenReturn("00:01");

        mResultsFragment.setDailyTimer("00:00");

        assertEquals("00:00", mTimeTextView.getText());

        mResultsFragment.setDailyTimer("00:01");

        assertEquals("00:01", mTimeTextView.getText());
    }

    /**
     * When click on result screen, stop time.
     */
    @Test
    public void whenClickOnResultScreenShouldStopTimer() {
        mPagerListener.onPageSelected(1);
        assertEquals(View.VISIBLE, mTapToFinish.getVisibility());

        mWholeReportView.performClick();

        verify(mockTimer).stopTimer();
        assertEquals(View.GONE, mTapToFinish.getVisibility());
    }

    /**
     * when back to first result, restart for next meeting.
     */
    @Test
    public void whenBackToChronoThenRestart() {
        mPagerListener.onPageSelected(1);

        // Back
        mPagerListener.onPageSelected(0);

        assertTrue(out.isFinishing());
        assertEquals(MainActivity.class.getName(), shadowOut.getNextStartedActivity().getComponent().getClassName());
    }

    /**
     * When back pressed in result then back to chrono
     */
    @Test
    public void onBackInResultsThenToChrono() {
        out.onBackPressed();

        assertTrue(out.isFinishing());
    }
}
