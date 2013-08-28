
package es.jonatantierno.scrumdailytimer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

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

    }

    /**
     * When dissapearing to the left (chrono screen), move, get more transparent, and get bigger.
     */
    @Test
    public void whenDissapearingToTheLeft() {

        out.transformPage(mockView, -0.5f);

        verify(mockView).setAlpha(0.50f);
        verify(mockView).setTranslationX(-0.5f);
        verify(mockView).setScaleX(2f);
        verify(mockView).setScaleY(2f);
    }

}
