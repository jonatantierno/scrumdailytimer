
package es.jonatantierno.scrumdailytimer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "../ScrumDailyTimer/AndroidManifest.xml")
public class SlotSeekBarControllerTest {

    SlotSeekBarController out;
    OnSeekBarChangeListener capturedListener;
    SeekBar mockSeekBar;
    ChronoInterface mockChronoInterface;

    @Before
    public void setUp() throws Exception {

        // Fixture
        mockSeekBar = mock(SeekBar.class);
        mockChronoInterface = mock(ChronoInterface.class);

        out = new SlotSeekBarController();
        out.configure(mockSeekBar, mockChronoInterface);

        capturedListener = out.getListener();

    }

    /**
     * A listener must be assigned to the seekbar.
     */
    @Test
    public void whenSeekBarIsSetThenAssignListener() {

        verify(mockSeekBar).setOnSeekBarChangeListener(out.getListener());
    }

    /**
     * Values allowed are multiples of the mininum increment (30 secs). Also, miminum is 30.
     */
    @Test
    public void whenDragThenMoveToNearestMultipleOfTheMinimumIncrement() {

        capturedListener.onProgressChanged(mockSeekBar, 50, false);
        verify(mockSeekBar).setProgress(60);

        capturedListener.onProgressChanged(mockSeekBar, 100, false);
        verify(mockSeekBar).setProgress(90);

        capturedListener.onProgressChanged(mockSeekBar, 0, false);
        verify(mockSeekBar).setProgress(30);

        capturedListener.onProgressChanged(mockSeekBar, 290, false);
        verify(mockSeekBar).setProgress(300);
    }

    /**
     * Values allowed are multiples of the mininum increment (30 secs). Also, miminum is 30.
     */
    @Test
    public void whenStoppedThenMoveToNearestMultipleOfTheMinimumIncrement() {

        when(mockSeekBar.getProgress()).thenReturn(50).thenReturn(100).thenReturn(0).thenReturn(290);
        capturedListener.onStopTrackingTouch(mockSeekBar);
        verify(mockSeekBar).setProgress(60);

        capturedListener.onStopTrackingTouch(mockSeekBar);
        verify(mockSeekBar).setProgress(90);

        capturedListener.onStopTrackingTouch(mockSeekBar);
        verify(mockSeekBar).setProgress(30);

        capturedListener.onStopTrackingTouch(mockSeekBar);
        verify(mockSeekBar).setProgress(300);
    }

    /**
     * Should update chrono view
     */
    @Test
    public void shouldUpdateChrono() {
        capturedListener.onProgressChanged(mockSeekBar, 50, false);
        verify(mockChronoInterface).setTime(60);

        capturedListener.onProgressChanged(mockSeekBar, 100, false);
        verify(mockChronoInterface).setTime(90);

        capturedListener.onProgressChanged(mockSeekBar, 0, false);
        verify(mockChronoInterface).setTime(30);

        capturedListener.onProgressChanged(mockSeekBar, 290, false);
        verify(mockChronoInterface).setTime(300);
    }
}
