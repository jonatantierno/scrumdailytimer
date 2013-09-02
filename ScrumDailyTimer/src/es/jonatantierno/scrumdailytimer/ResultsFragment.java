
package es.jonatantierno.scrumdailytimer;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.google.inject.Inject;

/**
 * Results screen.
 */
public class ResultsFragment extends RoboFragment implements ChronoInterface {

    @InjectView(R.id.numberOfParticipantsReportDataTextView)
    private TextView mNumberOfParticipantsTextView;
    @InjectView(R.id.timeOutsDataTextView)
    private TextView mNumberOfTimeoutsTextView;
    @InjectView(R.id.totalTimeDataTextView)
    private TextView mTotalTimeDataTextView;
    @InjectView(R.id.warmUpTimeDataTextView)
    private TextView mPreparationTimeTextView;
    @InjectView(R.id.wholeReportLayout)
    private View mWholeLayout;
    @InjectView(R.id.tapToFinishDaily)
    private TextView mTapToFinishTextView;

    @Inject
    ScrumTimer mScrumTimer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_report, container, false);

    }

    @Override
    public void onStart() {
        super.onStart();

        mWholeLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mScrumTimer.stopTimer();
                mTapToFinishTextView.setVisibility(View.GONE);
                Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.participant_animation);
                mTotalTimeDataTextView.startAnimation(animation);
            }
        });
    }

    /**
     * Show Data from fragment
     * 
     * @param mChronoFragment
     */
    public void displayData(final ChronoFragment mChronoFragment) {
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mNumberOfParticipantsTextView.setText("" + mChronoFragment.getNumberOfParticipants());
                mNumberOfTimeoutsTextView.setText("" + mChronoFragment.getNumberOfTimeouts());
                mPreparationTimeTextView.setText("" + mChronoFragment.getPreparationTime());

            }
        });

    }

    @Override
    public void setTime(int i) {
        // Countdown timer. Do nothing.

    }

    @Override
    public void setDailyTimer(final String prettyTime) {
        // Total meeting time. Update.
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mTotalTimeDataTextView.setText(prettyTime);
            }
        });

    }

    @Override
    public void timeOut() {
        // Countdown timer. Do nothing.
    }

    @Override
    public void setCountDown(String prettyTime) {
        // Countdown timer. Do nothing.
    }
}
