package com.skyfishjy.demo.sfpa;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ContentFragment extends Fragment {

  public static final String TAG = ContentFragment.class.getSimpleName();

  public String title;

  public static ContentFragment getInstance(String title) {
    ContentFragment mainFragment = new ContentFragment();
    Bundle args = new Bundle();
    args.putString("title", title);
    mainFragment.setArguments(args);
    return mainFragment;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    title = getArguments().getString("title");
    log(title + " onCreate()");
  }

  @Override public void onResume() {
    super.onResume();
    log(title + " onResume()");
  }

  @Override public void onPause() {
    super.onPause();
    log(title + " onPause()");
  }

  @Override public void onDestroy() {
    super.onDestroy();
    log(title + " onDestroy()");
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    log(title + " onCreateView()");
    View view = inflater.inflate(R.layout.fragment_content, container, false);
    TextView textView = (TextView) view.findViewById(R.id.text_view);
    textView.setText(title);
    return view;
  }

  private void log(String content) {
    Log.d(TAG, content);
  }
}