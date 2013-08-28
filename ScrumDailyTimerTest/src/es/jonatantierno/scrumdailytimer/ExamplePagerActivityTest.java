
package es.jonatantierno.scrumdailytimer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;

import roboguice.RoboGuice;
import android.os.Bundle;

import com.google.inject.AbstractModule;
import com.google.inject.util.Modules;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "../ScrumDailyTimer/AndroidManifest.xml")
public class ExamplePagerActivityTest {

    FragmentExampleActivity out;
    ShadowActivity shadowOut;

    public class TestModule extends AbstractModule {

        @Override
        protected void configure() {

        }

    }

    @Before
    public void setUp() throws Exception {
        RoboGuice.setBaseApplicationInjector(Robolectric.application, RoboGuice.DEFAULT_STAGE,
                Modules.override(RoboGuice.newDefaultRoboModule(Robolectric.application)).with(new TestModule()));

        out = Robolectric.buildActivity(FragmentExampleActivity.class).create().get();

        out.onCreate(new Bundle());

    }

    @Test
    public void whenTapShouldStartTimer() {

    }
}
