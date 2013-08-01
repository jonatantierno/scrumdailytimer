
package es.jonatantierno.scrumdailytimer;

import static junit.framework.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.os.Bundle;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "../ScrumDailyTimer/AndroidManifest.xml")
public class ChronoActivityTest {

    ChronoActivity out;

    @Before
    public void setUp() throws Exception {
        out = new ChronoActivity();
    }

    @Test
    public void firstTest() {
        out.onCreate(new Bundle());

        assertNotNull(out.findViewById(R.id.wholeLayout));
    }

}
