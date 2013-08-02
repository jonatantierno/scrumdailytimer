
package es.jonatantierno.scrumdailytimer;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Report with data of the meeting.
 */
public class ReportActivity extends RoboActivity {

    @InjectView(R.id.totalTimeDataTextView)
    TextView mTotalTimeDataTextView;
    @InjectView(R.id.timeOutsDataTextView)
    TextView mTimeOutsDataTextView;
    @InjectView(R.id.warmUpTimeDataTextView)
    TextView mWarmUpTimeDataTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_report);

    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();

        mTotalTimeDataTextView.setText(intent.getStringExtra(ChronoActivity.TOTAL_TIME));
        mTimeOutsDataTextView.setText(Integer.toString(intent.getIntExtra(ChronoActivity.TIMEOUTS, 0)));
        mWarmUpTimeDataTextView.setText(intent.getStringExtra(ChronoActivity.WARMUP_TIME));

    }
}
