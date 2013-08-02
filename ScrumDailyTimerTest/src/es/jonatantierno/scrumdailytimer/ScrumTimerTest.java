
package es.jonatantierno.scrumdailytimer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
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

        out._startTimer();

        verify(mockActivity).setDailyTimer("00:00");

        out.tick();

        verify(mockActivity).setDailyTimer("00:01");

        out.tick();

        verify(mockActivity).setDailyTimer("00:02");

        // Countdown is not affected
        verify(mockActivity, times(0)).setCountDown(Mockito.anyString());

    }

    /**
     * Update countdown
     */
    @Test
    public void shouldUpdateCountDown() {
        out._startTimer();

        out.resetCountDown();

        out.tick();

        verify(mockActivity).setCountDown("00:59");

        out.tick();

        verify(mockActivity).setCountDown("00:58");
    }

    @Test
    public void shouldStopCountDown() {
        out._startTimer();

        out.resetCountDown();

        out.tick();

        out.stopCountDown();

        out.tick();
        out.tick();
        out.tick();

        verify(mockActivity, times(1)).setCountDown(Mockito.anyString());
    }

    @Test
    public void whenCountDownEndsThenCallTimeout() {
        out._startTimer();

        out.resetCountDown();

        out.setCountDownSeconds(0);

        out.tick();

        // only once...
        out.tick();

        verify(mockActivity).timeOut();
    }

}
