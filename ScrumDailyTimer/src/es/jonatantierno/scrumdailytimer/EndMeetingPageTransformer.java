
package es.jonatantierno.scrumdailytimer;

import android.support.v4.view.ViewPager.PageTransformer;
import android.view.View;

public class EndMeetingPageTransformer implements PageTransformer {

    @Override
    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();

        if (position < 0) {

            view.setAlpha(1 + position * 2);

        } else {

            view.setAlpha(1);
            view.setTranslationX((int) (pageWidth * -position * 0.7));

            view.setScaleX(1 - position / 8);
            view.setScaleY(1 - position / 8);
        }
    }
}
