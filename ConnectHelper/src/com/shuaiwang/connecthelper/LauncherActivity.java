package com.shuaiwang.connecthelper;

import android.app.Activity;
import android.content.Intent;
import android.widget.ViewFlipper;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class LauncherActivity extends Activity {

	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher_main);
        
       
        
    new Thread()
        {public void run()
	        {try {sleep(3000);     //等待三秒,自动进入软件主窗口   
			        Intent intent = new Intent();
			        intent.setClass(LauncherActivity.this, MainActivity.class);   
			        startActivity(intent);       
			}
		    catch (Exception e) {
		        	e.printStackTrace();
	        }
        //progressDialog.dismiss();
        }       
        }.start();
    }
}   
       


