package com.example.qr_codescan;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.net.io.Util;
import org.apache.http.client.entity.UrlEncodedFormEntity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.shuaiwang.connecthelper.R;


public class QRcodeScanActivity extends Activity {
	private final static int SCANNIN_GREQUEST_CODE = 1;
	/**
	 * 显示扫描结果
	 */
	private TextView mTextView ;
	/**
	 * 显示扫描拍的图片
	 */
	private ImageView mImageView;
	
	private Button scanButton;
	private Button downloadButton;
	private String resultUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.qrcode_main);
		
		mTextView = (TextView) findViewById(R.id.result); 
		mImageView = (ImageView) findViewById(R.id.qrcode_bitmap);
		
		//点击按钮跳转到二维码扫描界面，这里用的是startActivityForResult跳转
		//扫描完了之后调到该界面
		Button scanButton = (Button) findViewById(R.id.button1);
		Button downloadButton = (Button) findViewById(R.id.dowmload_button);
		scanButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(QRcodeScanActivity.this, MipcaActivityCapture.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
			}
		});
		
		
		downloadButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				downloadFileFromUrl();
			}
		});
	}
	
	
	private void downloadFileFromUrl() {
		//String urlStr="http://172.17.54.91:8080/download/down.txt";  
		
		String urlStr = null;  
		
		if (resultUri != null) {
			urlStr = resultUri;
		}
		else {
			Toast.makeText(QRcodeScanActivity.this, "地址有误", 1).show();
		}
		
		  
          String path="connecthelperDownload";   
          // String fileName="2.mp3";   
          String fileName="filename";   
          OutputStream output=null;   
          try {   
             
        	  StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().
      				detectDiskWrites().detectNetwork().penaltyLog().build());
      		  StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()       
              .detectLeakedSqlLiteObjects()    
              .penaltyLog()       
              .penaltyDeath()       
              .build());
        	  
              URL url=new URL(urlStr);   
              HttpURLConnection conn=(HttpURLConnection)url.openConnection();  
              conn.setRequestMethod("POST");  
              conn.setConnectTimeout(5000);  
              conn.connect(); 
              conn.getResponseCode();
              Log.e("ws", "---->>getResponseCode " + conn.getResponseCode());
              URL absUrl = conn.getURL();// 获得真实Url
              
              InputStream input=conn.getInputStream(); 
              Log.e("ws", "---->>input: "  + input);
              
              
              //获取下载文件的真实filename
              fileName = conn.getHeaderField("Content-Disposition");// 通过Content-Disposition获取文件名，这点跟服务器有关，需要灵活变通
              
              String contentType = conn.getHeaderField("Content-Type");
              //String fileExt = Util.getFileEndWitsh(contentType);
              Log.e("ws", "---->>contentType :" + contentType);
  			// 根据内容类型判断文件扩展名
  			//String fileExt = WeixinUtil.getFileEndWitsh(contentType);
              if (fileName == null || fileName.length() < 1) {
            	  fileName = absUrl.getFile();
              }
              fileName.endsWith(contentType);
              Log.e("ws", "---->>filename: " + fileName);
             
              String SDCard=Environment.getExternalStorageDirectory()+"";   
              String pathName=SDCard+"/"+path + fileName;//文件存储路径   
              Log.e("ws", "---->>pathname: " + pathName);
              
              File file=new File(pathName);   
                
              if(file.exists()){   
            	  Log.e("ws", "---->>file exits");
                  //return;   
              }else{   
            	  Log.e("ws", "---->>file not exits");
                  String dir=SDCard+"/"+path;   
                  new File(dir).mkdir();//新建文件夹   
                  file.createNewFile();//新建文件 
                  Log.e("ws", "---->>file created");
                  output=new FileOutputStream(file);   
                  //读取大文件   
                  byte[] buffer=new byte[4*1024];   
                  while(input.read(buffer)!=-1){   
                      output.write(buffer);   
                  }   
                  output.flush();   
              }   
          } catch (MalformedURLException e) {   
              e.printStackTrace();   
          } catch (IOException e) {   
              e.printStackTrace();   
          }finally{   
              /*try {   
                     // output.close();   
                     // System.out.println("success");  
                      Log.e("ws", "---->>success");
                  } catch (IOException e) {   
                     // System.out.println("fail");  
                	  Log.e("ws", "---->>fail");
                      e.printStackTrace();   
                  }   */
          }   
      }   
         
  
  	
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
		case SCANNIN_GREQUEST_CODE:
			if(resultCode == RESULT_OK){
				Bundle bundle = data.getExtras();
				//显示扫描到的内容
				mTextView.setText(bundle.getString("result"));
				resultUri = bundle.getString("result");
				//显示
				mImageView.setImageBitmap((Bitmap) data.getParcelableExtra("bitmap"));
			}
			break;
		}
    }	

}
