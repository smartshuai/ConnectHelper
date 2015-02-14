package com.shuaiwang.connecthelper;

import java.util.Locale;

import com.example.qr_codescan.QRcodeScanActivity;

import android.app.Activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class MainActivity extends Activity
//public class MainActivity extends PreferenceActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    
    
    private static final int UPLOADFRAGMENTID = 0;
    private static final int DOWNLOADFRAGMENTID = 1;
    private static final int BLUETOOTHRAGMENTID = 2;
    private static final int SEETINGSFRAGMENTID = 3;
    
    private static DownloadFragment mDownloadFragment;
    private static UploadFragment mUploadFragment;
    //private static BluetoothFragment mBluetoothFragment;
    private static QRCodeFragment mQrCodeFragment;
   // private static SettingsFragment mSettingsFragment;
    private static AboutFragment mAboutFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initAllFragment();
        setContentView(R.layout.activity_main);
        
        /*Configuration config = getResources().getConfiguration();//获取系统的配置
        config.locale = Locale.CHINESE;//将语言更改为中文
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());//更新配置
         */  
        
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }
    
    private void initAllFragment() {
  		//初始化各个fragment
      	mUploadFragment = new UploadFragment();
      	mDownloadFragment = new DownloadFragment();
      	//mSettingsFragment = new SettingsFragment();
      	//mBluetoothFragment = new BluetoothFragment();
      	//mQrCodeFragment = new QRCodeFragment();
      	mAboutFragment = new AboutFragment();
      	
  	}

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        
        if (position == UPLOADFRAGMENTID && (mUploadFragment != null)) {
        	fragmentManager.beginTransaction()
            .replace(R.id.container, mUploadFragment)
            .commit();
		}
        else if (position == DOWNLOADFRAGMENTID && (mDownloadFragment) != null) {
        	fragmentManager.beginTransaction()
            .replace(R.id.container, mDownloadFragment)
            .commit();
		}else if (position == SEETINGSFRAGMENTID && (mAboutFragment) != null) {
			fragmentManager.beginTransaction()
            .replace(R.id.container, mAboutFragment)
            .commit();
		}else if (position == BLUETOOTHRAGMENTID ) {
			/*fragmentManager.beginTransaction()
            .replace(R.id.container, mQrCodeFragment)
            .commit();*/
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, QRcodeScanActivity.class);
			MainActivity.this.startActivity(intent);
			
		}else {
			Log.e("ws", "---->>onNavigationDrawerItemSelected :ID error");
		}
        
        
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
            case 4:
            	 mTitle = getString(R.string.title_section4);
                 break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    
}
