package transit;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import com.afollestad.cardsui.CardAdapter;
import com.afollestad.cardsui.CardBase;
import com.afollestad.cardsui.CardTheme;
import com.afollestad.silk.fragments.list.SilkListFragment;
import com.astuetz.PagerSlidingTabStrip;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.sromku.simple.storage.SimpleStorage;
import com.sromku.simple.storage.Storage;

public class MainActivity extends Activity {

    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    public MyPagerAdapter adapter;
    private SystemBarTintManager tintManager;
    public boolean reverseOrder = false;
    
    public Storage mystorage = SimpleStorage.getInternalStorage(this);
    private Toast mToast;
   
    
    /* BEGIN TRANSLUCENCY UTILITY METHODS */

    public void setupTransparentTints(Activity context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return;
        tintManager = new SystemBarTintManager(context);
        tintManager.setStatusBarTintEnabled(true);
    }

    public static void setInsets(SilkListFragment fragment) {
        setInsets(fragment.getActivity(), fragment.getListView(), true, true, true);
        setInsets(fragment.getActivity(), fragment.getProgressContainer(), false, true);
        setInsets(fragment.getActivity(), fragment.getStandardEmptyView(), false, true);
    }

    public static void setInsets(Activity context, View view, boolean includeTop, boolean includeBottom) {
        setInsets(context, view, includeTop, includeBottom, false);
    }

    public static void setInsets(Activity context, View view, boolean includeTop, boolean includeBottom, boolean noTranslucencyTop) {
        int topPadding = 0;
        int bottomPadding = 0;
        int rightPadding = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarTintManager tintManager = new SystemBarTintManager(context);
            SystemBarTintManager.SystemBarConfig config = tintManager.getConfig();
            if (!noTranslucencyTop) topPadding = config.getPixelInsetTop(true);
            bottomPadding = config.getPixelInsetBottom();
            rightPadding = config.getPixelInsetRight();
        }
        view.setPadding(0, includeTop ? topPadding : 0,
                rightPadding, includeBottom ? bottomPadding : 0);
    }

    /* END TRANSLUCENCY UTILITY METHODS */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setTitle(R.string.app_name);
        setContentView(R.layout.main);
        setupTransparentTints(this);
        setInsets(this, findViewById(R.id.content_frame), true, false);

        Typeface tf = Typeface.createFromAsset(getAssets(), "Roboto-Bold.ttf");
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setIndicatorColorResource(android.R.color.holo_blue_dark);
        tabs.setTextColor(getResources().getColor(android.R.color.primary_text_dark));
        tabs.setTypeface(tf, 0);
        pager = (ViewPager) findViewById(R.id.pager);
        
        adapter = new MyPagerAdapter(getFragmentManager());
        pager.setAdapter(adapter);
        int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
        pager.setPageMargin(pageMargin);
        tabs.setViewPager(pager);
  
        
    }
    

	public void setReverseOrder(){
		if (reverseOrder == true){
			reverseOrder = false;
		}else{
			reverseOrder = true;
		}
	}
    
    public void changeTheme(CardTheme theme, int index) {
        int color;
        if (index == 0) {
            tabs.setBackgroundColor(getResources().getColor(R.color.card_gray));
            tabs.setTextColorResource(android.R.color.primary_text_light);
            color = getResources().getColor(android.R.color.holo_blue_dark);
        } else if (index == 1){
            tabs.setTextColorResource(android.R.color.primary_text_light);
            color = Color.parseColor("#FF899AB1");        
        }else{
            tabs.setTextColorResource(android.R.color.primary_text_light);
            color = getResources().getColor(android.R.color.holo_orange_light);
        }
        tabs.setIndicatorColor(color);
        tintManager.setTintColor(color);
        getActionBar().setBackgroundDrawable(new ColorDrawable(color));
        tabs.invalidate();
        tabs.requestLayout();
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES;
        MyWebView webview = MyWebView.newMyWebView(1);

        
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
            TITLES = getResources().getStringArray(R.array.tab_titles);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
        	if( position == 0){
        		CardFragment c = CardFragment.newCardFragment(0);
        		c.setStorage(mystorage);
        		return c;
        	}else if (position == 1){
        		return webview;
        	}else {
        		SettingsView s = SettingsView.newSettingsView(position);
        		s.setStorage(mystorage);
        		return s;
        	}
        	
        }
        
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
        
    }
    
 
	private void showToast(String message) {
		if (mToast != null) mToast.cancel();
		mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
		mToast.show();
	}
	
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_refresh:
            	adapter.notifyDataSetChanged();
                return true;
            case R.id.action_about:
            	new AlertDialog.Builder(this)
                .setTitle("About")
                .setMessage("Developed by Rohan Roy Choudhury.")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) { 
                        // continue with delete
                    }
                 })
                 .show();
            	return true;
            case R.id.action_reverse:
            	setReverseOrder();
            	adapter.notifyDataSetChanged();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    public void onBackPressed() {
		// Pop the browser back stack or exit the activity
		MyWebView webfrag = (MyWebView) adapter.getItem(1);

		if (webfrag.isVisible()){
			WebView webView = webfrag.webview;
			if(webView.canGoBack()){
				webView.goBack();
			}else{
				super.onBackPressed();
			}	
		}
		else{
				super.onBackPressed();
		}
	}
    
    private boolean isNetworkConnected() {
    	  ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    	 return cm.getActiveNetworkInfo() != null;
    }
    
}


