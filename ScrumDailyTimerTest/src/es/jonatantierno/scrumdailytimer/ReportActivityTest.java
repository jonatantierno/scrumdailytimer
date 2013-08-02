
package es.jonatantierno.scrumdailytimer;

import static junit.framework.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;

import roboguice.RoboGuice;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.inject.AbstractModule;
import com.google.inject.util.Modules;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "../ScrumDailyTimer/AndroidManifest.xml")
public class ReportActivityTest {

    ReportActivity out;
    ShadowActivity shadowOut;

    TextView mTotalTimeDataTextView;
    TextView mTimeOutsDataTextView;
    TextView mWarmUpTimeDataTextView;

    public class TestModule extends AbstractModule {

        @Override
        protected void configure() {

        }

    }

    @Before
    public void setUp() throws Exception {
        RoboGuice.setBaseApplicationInjector(Robolectric.application, RoboGuice.DEFAULT_STAGE,
                Modules.override(RoboGuice.newDefaultRoboModule(Robolectric.application)).with(new TestModule()));

        out = new ReportActivity();
        shadowOut = Robolectric.shadowOf(out);

        // Fixture
        out.onCreate(new Bundle());

        mTotalTimeDataTextView = (TextView) out.findViewById(R.id.totalTimeDataTextView);
        mTimeOutsDataTextView = (TextView) out.findViewById(R.id.timeOutsDataTextView);
        mWarmUpTimeDataTextView = (TextView) out.findViewById(R.id.warmUpTimeDataTextView);
    }

    @Test
    public void shouldShowDataFromIntent() {
        Intent intent = new Intent();
        intent.putExtra(ChronoActivity.TOTAL_TIME, "12:05");
        intent.putExtra(ChronoActivity.WARMUP_TIME, "01:32");
        intent.putExtra(ChronoActivity.TIMEOUTS, 2);

        out.setIntent(intent);

        out.onResume();

        assertEquals("12:05", mTotalTimeDataTextView.getText().toString());
        assertEquals("01:32", mWarmUpTimeDataTextView.getText().toString());
        assertEquals("2", mTimeOutsDataTextView.getText().toString());
    }

}
