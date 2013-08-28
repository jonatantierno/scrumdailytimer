
package es.jonatantierno.scrumdailytimer;

import android.support.v4.view.ViewPager.PageTransformer;
import android.view.View;

public class EndMeetingPageTransformer implements PageTransformer {
    private static float MIN_SCALE = 0.75f;

    @Override
    public void transformPage(View view, float position) {
        // TODO Auto-generated method stub
        int pageWidth = view.getWidth();

        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            view.setAlpha(0);

        } else if (position <= 0) { // [-1,0]
            // Screen exiting: To the left, grow and get transparent.
            view.setAlpha((1 + position) * (1 + position));
            view.setTranslationX(pageWidth * position);
            view.setScaleX(1 - position);
            view.setScaleY(1 - position);

        } else if (position <= 1) { // (0,1]

            // Counteract the default slide transition
            view.setTranslationX(pageWidth * -position);

            // Scale the page down (between MIN_SCALE and 1)
            float scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position));
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);

        } else { // (1,+Infinity]
            // This page is way off-screen to the right.
            view.setAlpha(0);
        }

    }

}
