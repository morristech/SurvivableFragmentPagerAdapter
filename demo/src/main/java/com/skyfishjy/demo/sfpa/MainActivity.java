package com.skyfishjy.demo.sfpa;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {

  ViewPager viewPager;
  ContentPagerAdapter contentPagerAdapter;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    viewPager = (ViewPager) findViewById(R.id.viewpager);

    contentPagerAdapter = new ContentPagerAdapter(getSupportFragmentManager());
    contentPagerAdapter.setPagerCount(4);
    viewPager.setAdapter(contentPagerAdapter);
    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        int currentPosition = viewPager.getCurrentItem();
        try {
          contentPagerAdapter.setSurvivable(currentPosition);
          Snackbar.make(view,
              "position " + currentPosition + " fragment survived\nsee logcat for more detail",
              Snackbar.LENGTH_LONG).setAction("Action", null).show();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_add_pager:
        contentPagerAdapter.addPager(1);
        break;
    }
    return super.onOptionsItemSelected(item);
  }
}
