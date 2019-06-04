package e.oscarjimfer.pruebatfg;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPageAdapter extends FragmentPagerAdapter {

    private final List<Fragment> listFragment = new ArrayList<>();
    private final List<String> listTitles = new ArrayList<>();

    public ViewPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        return listFragment.get(i);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return listTitles.get(position);
    }

    @Override
    public int getCount() {
        return listTitles.size();
    }

    public void AddFragment(Fragment fragment, String title){
        listFragment.add(fragment);
        listTitles.add(title);
    }
}
