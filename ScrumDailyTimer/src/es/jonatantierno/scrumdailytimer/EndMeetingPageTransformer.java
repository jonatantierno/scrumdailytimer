
package es.jonatantierno.scrumdailytimer;

import android.support.v4.view.ViewPager.PageTransformer;
import android.view.View;
import android.widget.TextView;

public class EndMeetingPageTransformer implements PageTransformer {

    @Override
    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();

        if (position < 0) {

            view.setAlpha(1 + position);
            view.setTranslationX(position * pageWidth);
            view.setScaleX(1 / (1 + position));
            view.setScaleY(1 / (1 + position));

            // Chrono Fragment. Highlight swipe tip
            TextView swipeView = (TextView) view.findViewById(R.id.swipeTextView);
            if (swipeView != null) {
                swipeView.setAlpha(1);
                swipeView.setTranslationX((int) (1.2 * position * pageWidth));
            }
        } else {

            view.setAlpha(1);
            view.setTranslationX((int) (pageWidth * -position * 0.7));

            view.setScaleX(1 - position / 6);
            view.setScaleY(1 - position / 6);
        }
    }
}
