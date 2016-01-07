package com.skyfishjy.pageradapter;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;

public abstract class SurvivableFragmentPagerAdapter extends PagerAdapter {
  private static final String TAG = SurvivableFragmentPagerAdapter.class.getSimpleName();
  private static final boolean DEBUG = false;

  private final FragmentManager mFragmentManager;
  private FragmentTransaction mCurTransaction = null;

  private ArrayList<Fragment.SavedState> mSavedState = new ArrayList<Fragment.SavedState>();
  private ArrayList<Fragment> mFragments = new ArrayList<Fragment>();
  private Fragment mCurrentPrimaryItem = null;
  private Fragment mSurvivableFragment = null;

  public SurvivableFragmentPagerAdapter(FragmentManager fm) {
    mFragmentManager = fm;
  }

  /**
   * Return the Fragment associated with a specified position.
   */
  public abstract Fragment getItem(int position);

  protected void setSurvivableFragment(Fragment fragment) throws Exception {
    if (getCount() != 1) {
      throw new Exception("getCount must return 1");
    }
    if (fragment == null || !mFragments.contains(fragment)) {
      throw new Exception("only support survive not destroyed fragment");
    }
    this.mSurvivableFragment = fragment;
    notifyDataSetChanged();
  }

  protected void setSurvivableFragment(int position) throws Exception {
    setSurvivableFragment(getFragment(position));
  }

  @Override public void startUpdate(ViewGroup container) {
  }

  @Override public Object instantiateItem(ViewGroup container, int position) {
    if (mFragments.size() > position) {
      if (mSurvivableFragment == null) {
        Fragment f = mFragments.get(position);
        if (f != null) {
          return f;
        }
      } else {
        if (position == 0) {
          mFragments.set(0, mSurvivableFragment);
          for (int i = 1; i < mFragments.size(); i++) {
            mFragments.set(i, null);
          }
          return mSurvivableFragment;
        }
      }
    }

    if (mCurTransaction == null) {
      mCurTransaction = mFragmentManager.beginTransaction();
    }

    Fragment fragment = getItem(position);
    if (DEBUG) Log.v(TAG, "Adding item #" + position + ": f=" + fragment);
    if (mSavedState.size() > position) {
      Fragment.SavedState fss = mSavedState.get(position);
      if (fss != null) {
        fragment.setInitialSavedState(fss);
      }
    }
    while (mFragments.size() <= position) {
      mFragments.add(null);
    }
    fragment.setMenuVisibility(false);
    fragment.setUserVisibleHint(false);
    mFragments.set(position, fragment);
    mCurTransaction.add(container.getId(), fragment);

    return fragment;
  }

  @Override public void destroyItem(ViewGroup container, int position, Object object) {
    Fragment fragment = (Fragment) object;

    if (mSurvivableFragment != null && mSurvivableFragment == fragment) {
      return;
    }

    if (mCurTransaction == null) {
      mCurTransaction = mFragmentManager.beginTransaction();
    }
    if (DEBUG) {
      Log.v(TAG,
          "Removing item #" + position + ": f=" + object + " v=" + ((Fragment) object).getView());
    }
    while (mSavedState.size() <= position) {
      mSavedState.add(null);
    }
    mSavedState.set(position, mFragmentManager.saveFragmentInstanceState(fragment));
    mFragments.set(position, null);

    mCurTransaction.remove(fragment);
  }

  @Override public void setPrimaryItem(ViewGroup container, int position, Object object) {
    Fragment fragment = (Fragment) object;
    if (fragment != mCurrentPrimaryItem) {
      if (mCurrentPrimaryItem != null) {
        mCurrentPrimaryItem.setMenuVisibility(false);
        mCurrentPrimaryItem.setUserVisibleHint(false);
      }
      if (fragment != null) {
        fragment.setMenuVisibility(true);
        fragment.setUserVisibleHint(true);
      }
      mCurrentPrimaryItem = fragment;
    }
  }

  @Override public void finishUpdate(ViewGroup container) {
    if (mCurTransaction != null) {
      mCurTransaction.commitAllowingStateLoss();
      mCurTransaction = null;
      mFragmentManager.executePendingTransactions();
    }
  }

  @Override public boolean isViewFromObject(View view, Object object) {
    return ((Fragment) object).getView() == view;
  }

  @Override public Parcelable saveState() {
    Bundle state = null;
    if (mSavedState.size() > 0) {
      state = new Bundle();
      Fragment.SavedState[] fss = new Fragment.SavedState[mSavedState.size()];
      mSavedState.toArray(fss);
      state.putParcelableArray("states", fss);
    }
    for (int i = 0; i < mFragments.size(); i++) {
      Fragment f = mFragments.get(i);
      if (f != null && f.isAdded()) {
        if (state == null) {
          state = new Bundle();
        }
        String key = "f" + i;
        mFragmentManager.putFragment(state, key, f);
      }
    }
    return state;
  }

  @Override public void restoreState(Parcelable state, ClassLoader loader) {
    if (state != null) {
      Bundle bundle = (Bundle) state;
      bundle.setClassLoader(loader);
      Parcelable[] fss = bundle.getParcelableArray("states");
      mSavedState.clear();
      mFragments.clear();
      if (fss != null) {
        for (int i = 0; i < fss.length; i++) {
          mSavedState.add((Fragment.SavedState) fss[i]);
        }
      }
      Iterable<String> keys = bundle.keySet();
      for (String key : keys) {
        if (key.startsWith("f")) {
          int index = Integer.parseInt(key.substring(1));
          Fragment f = mFragmentManager.getFragment(bundle, key);
          if (f != null) {
            while (mFragments.size() <= index) {
              mFragments.add(null);
            }
            f.setMenuVisibility(false);
            mFragments.set(index, f);
          } else {
            Log.w(TAG, "Bad fragment at key " + key);
          }
        }
      }
    }
  }

  public Fragment getFragment(int position) {
    if (position >= 0 && position < mFragments.size()) {
      return mFragments.get(position);
    }
    return null;
  }

  @Override public int getItemPosition(Object object) {
    return PagerAdapter.POSITION_NONE;
  }
}
