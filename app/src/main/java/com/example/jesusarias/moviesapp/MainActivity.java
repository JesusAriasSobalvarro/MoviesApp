package com.example.jesusarias.moviesapp;

import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {

    private String[] mPlanetTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    private SectionPageAdapter mSectionPageAdapter;

    private ViewPager mViewPager;
    ImageView sidebarIcon;
    ImageView sidebarWallpaper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setTitle(null);

        mSectionPageAdapter = new SectionPageAdapter(getSupportFragmentManager());

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mViewPager = findViewById(R.id.vp_tab_container);
        mViewPager.setOffscreenPageLimit(3);
        setupPageViewer(mViewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        sidebarIcon = findViewById(R.id.sidebar_icon);
        sidebarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        sidebarWallpaper = findViewById(R.id.sidebar_background);
        Glide.with(this)
                .asBitmap()
                .load("https://www.topofandroid.com/wp-content/uploads/2015/05/android-L-Material-Design-Wallpapers-1.png")
                .into(sidebarWallpaper);
    }

    private void setupPageViewer(ViewPager viewPager){
        SectionPageAdapter adapter = new SectionPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new MoviesFragment(), "Movies");
        adapter.addFragment(new ShowsFragment(), "TV Shows");
        adapter.addFragment(new FavoritesFragment(), "Favorites");
        viewPager.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_items, menu);
        return true;
    }
}
