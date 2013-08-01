
package es.jonatantierno.scrumdailytimer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "../ScrumDailyTimer/AndroidManifest.xml")
public class ScrumTimerTest {

    ChronoActivity mockActivity;
    ScrumTimer out;

    @Before
    public void setUp() throws Exception {
        out = new ScrumTimer();

        // Fixture
        mockActivity = mock(ChronoActivity.class);
        out.setActivity(mockActivity);

    }

    /**
     * update timer.
     */
    @Test
    public void whenTickShouldUpdateActivityTimer() {

        out.startTimer();

        verify(mockActivity).setDailyTimer("00:00");

        out.tick();

        verify(mockActivity).setDailyTimer("00:01");

        out.tick();

        verify(mockActivity).setDailyTimer("00:02");
    }

    /**
     * Update countdown
     */
    @Test
    public void shouldUpdateCountDown() {
        out.startTimer();

        out.resetCountDown();

        out.tick();

        verify(mockActivity).setCountDown("00:59");

        out.tick();

        verify(mockActivity).setCountDown("00:58");
    }

}
