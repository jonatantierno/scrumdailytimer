
package es.jonatantierno.scrumdailytimer;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.inject.Inject;

/**
 * Results screen.
 */
public class ResultsFragment extends RoboFragment {

    @InjectView(R.id.numberOfParticipantsReportDataTextView)
    private TextView mNumberOfParticipantsTextView;
    private TextView mNumberOfTimeoutsTextView;

    @Inject
    ScrumTimer mScrumTimer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_report, container, false);

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

            }
        });

    }
}
