package com.skyfishjy.demo.sfpa;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import com.skyfishjy.pageradapter.SurvivableFragmentPagerAdapter;

public class ContentPagerAdapter extends SurvivableFragmentPagerAdapter {

  int pagerCount = 0;

  public ContentPagerAdapter(FragmentManager fm) {
    super(fm);
  }

  public void setPagerCount(int pagerCount) {
    this.pagerCount = pagerCount;
  }

  public void addPager(int count) {
    this.pagerCount += count;
    notifyDataSetChanged();
  }

  public void setSurvivable(int position) {
    setPagerCount(1);
    try {
      setSurvivableFragment(position);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override public Fragment getItem(int position) {
    return ContentFragment.getInstance("Fragment-" + position);
  }

  @Override public int getCount() {
    return pagerCount;
  }
}
