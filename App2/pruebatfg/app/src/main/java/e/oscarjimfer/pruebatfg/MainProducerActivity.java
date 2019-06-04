package e.oscarjimfer.pruebatfg;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainProducerActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPageAdapter viewPageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_producer);

        tabLayout = (TabLayout) findViewById(R.id.TL_producer);
        viewPager = (ViewPager) findViewById(R.id.VP_producer);
        viewPageAdapter = new ViewPageAdapter(getSupportFragmentManager());

        // Creamos un nuevo Bundle
        Bundle args = new Bundle();
        // Colocamos los strings
        args.putString(getResources().getString(R.string.ID_USER),
                getIntent().getStringExtra(getResources().getString(R.string.ID_USER)));
        args.putString(getResources().getString(R.string.ID_PRODUCER),
                getIntent().getStringExtra(getResources().getString(R.string.ID_PRODUCER)));
        args.putString(getResources().getString(R.string.TOKEN_USER),
                getIntent().getStringExtra(getResources().getString(R.string.TOKEN_USER)));
        //Creamos los fragments
        FragmentProfile fragmentProfile = new FragmentProfile();
        FragmentNewService fragmentNewService = new FragmentNewService();
        //Set args
        fragmentProfile.setArguments(args);
        fragmentNewService.setArguments(args);

        viewPageAdapter.AddFragment(fragmentProfile,"");
        viewPageAdapter.AddFragment(fragmentNewService,"");

        viewPager.setAdapter(viewPageAdapter);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_profile);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_new_event);

    }
}
