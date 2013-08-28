
package es.jonatantierno.scrumdailytimer;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ExamplePagerAdapter extends FragmentPagerAdapter {

    public ExamplePagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int arg0) {
        return new FragmentExampleFragment();
    }

    @Override
    public int getCount() {
        return 2;
    }

}
