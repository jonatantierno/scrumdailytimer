
package es.jonatantierno.scrumdailytimer;

import roboguice.RoboGuice;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

class ChronoPagerAdapter extends FragmentPagerAdapter {

    ChronoFragment mChronoFragment;
    ResultsFragment mResultsFragment;

    public ChronoPagerAdapter(FragmentManager fm, Context context) {
        super(fm);

        mChronoFragment = RoboGuice.getInjector(context).getInstance(ChronoFragment.class);
        mResultsFragment = RoboGuice.getInjector(context).getInstance(ResultsFragment.class);
    }

    @Override
    public Fragment getItem(int arg0) {
        if (arg0 == 0) {
            return mChronoFragment;
        } else {
            return mResultsFragment;
        }
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return 2;
    }

}
