package com.ugt.premiumvpn.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.ugt.premiumvpn.Fragments.BatterySaver_Fragment;
import com.ugt.premiumvpn.Fragments.PhoneBooster_Fragment;
import com.ugt.premiumvpn.R;

import devlight.io.library.ntb.NavigationTabBar;

import java.util.ArrayList;


public class HorizontalNtbActivity extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testt);
        initUI();
    }

    private void initUI() {
        final ViewPager viewPager = (ViewPager) findViewById(R.id.vp_horizontal_ntb);
        viewPager.setOffscreenPageLimit(5); 
        viewPager.setCurrentItem(0); 
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager())  {
            
            @Override
            public int getCount() {
                return 5;
            }

            
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0: 

                        return new BatterySaver_Fragment();
                    case 1: 

                        return new PhoneBooster_Fragment();
                    case 2: 

                        return new PhoneBooster_Fragment();
                    case 3: 

                        return new PhoneBooster_Fragment();
                    case 4: 

                        return new PhoneBooster_Fragment();
                    default:
                        return new PhoneBooster_Fragment();
                }
            }

            
            @Override
            public CharSequence getPageTitle(int position) {
                return "Page" + position;
            }

        });

        if (viewPager.getCurrentItem() != 0) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 4, true);
            return;
        }


        final String[] colors = getResources().getStringArray(R.array.default_preview);

        final NavigationTabBar navigationTabBar = (NavigationTabBar) findViewById(R.id.ntb_horizontal);
        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_first),
                        Color.parseColor(colors[0]))
                        .selectedIcon(getResources().getDrawable(R.drawable.ic_sixth))
                        .title("Heart")
                        .badgeTitle("NTB")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_first),
                        Color.parseColor(colors[1]))

                        .title("Cup")
                        .badgeTitle("with")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_first),
                        Color.parseColor(colors[2]))
                        .selectedIcon(getResources().getDrawable(R.drawable.ic_sixth))
                        .title("Diploma")
                        .badgeTitle("state")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_first),
                        Color.parseColor(colors[3]))

                        .title("Flag")
                        .badgeTitle("icon")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_first),
                        Color.parseColor(colors[4]))
                        .selectedIcon(getResources().getDrawable(R.drawable.ic_sixth))
                        .title("Medal")
                        .badgeTitle("777")
                        .build()
        );

        navigationTabBar.setModels(models);

        navigationTabBar.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < navigationTabBar.getModels().size(); i++) {
                    final NavigationTabBar.Model model = navigationTabBar.getModels().get(i);
                    navigationTabBar.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            model.showBadge();
                        }
                    }, i * 100);
                }
            }
        }, 500);
    }

    private void startFragmentActivity (String REQUEST_ACTIVITY_CODE) {
        Intent intent = new Intent (getApplicationContext(), FragmentWrapperActivity.class);
        intent.putExtra(FragmentWrapperActivity.REQUEST_ACTIVITY_CODE, REQUEST_ACTIVITY_CODE);
        startActivity (intent);
    }

    private void startFragmentActivity (String REQUEST_ACTIVITY_CODE, String RUNTIME_MODE) {
        Intent intent = new Intent (getApplicationContext(), FragmentWrapperActivity.class);
        intent.putExtra(FragmentWrapperActivity.REQUEST_ACTIVITY_CODE, REQUEST_ACTIVITY_CODE);
        intent.putExtra(FragmentWrapperActivity.RUNTIME_MODE, RUNTIME_MODE);
        startActivity (intent);
    }
}