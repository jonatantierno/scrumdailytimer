
package es.jonatantierno.scrumdailytimer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.view.View;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "../ScrumDailyTimer/AndroidManifest.xml")
public class EndMeetingPageTransformerTest {

    EndMeetingPageTransformer out;
    View mockView;

    @Before
    public void setUp() throws Exception {

        // Fixture
        out = new EndMeetingPageTransformer();
        mockView = mock(View.class);
        when(mockView.getWidth()).thenReturn(100);
    }

    /**
     * If not moving, no transformations
     */
    @Test
    public void whenStillThenShowsProperly() {

        out.transformPage(mockView, 0);

        verify(mockView).setAlpha(1f);
        verify(mockView).setTranslationX(0.0f);
        verify(mockView).setScaleX(1f);
        verify(mockView).setScaleY(1f);
    }

    /**
     * When dissapearing to the left (chrono screen), move (extra to compensate the increment in size), get more
     * transparent, and get bigger.
     */
    @Test
    public void whenDissapearingToTheLeft() {

        out.transformPage(mockView, -0.25f);

        verify(mockView).setAlpha(0.5f);

    }

    /**
     * When appearing from the right (results screen), stay almost still (compensate movenent in the list) and get
     * bigger.
     */
    @Test
    public void whenAppearingFromTheRight() {

        out.transformPage(mockView, 0.5f);

        verify(mockView).setAlpha(1f);
        verify(mockView).setTranslationX(-35f);
        verify(mockView).setScaleX((float) (1 - 0.5 / 8));
        verify(mockView).setScaleY((float) (1 - 0.5 / 8));

    }
}
