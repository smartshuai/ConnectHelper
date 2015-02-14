package com.shuaiwang.connecthelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import javax.security.auth.PrivateCredentialPermission;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import com.shuaiwang.connecthelper.UploadFragment.UploadAsyncTask;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.database.CursorJoiner.Result;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.os.StrictMode;
import android.text.StaticLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;


public class DownloadFragment extends Fragment {

	private Button downloadButton;
	private ProgressDialog downloadDialog;
	//private Button bluetoothDownloadButton;
	BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	private static final String SERVER_MAC_ADDRESS = "90:4C:E5:CA:1C:95";
	private static final String MY_UUID = "00001106-0000-1000-8000-00805F9B34FB";
	
	
	private EditText filePathText;
	private EditText adderssText;
	private EditText portText;
	private EditText usernameText;
	private EditText passwordText;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.download_fragment, container, false);
		downloadButton = (Button) view.findViewById(R.id.download_button);
		filePathText = (EditText) view.findViewById(R.id.path_editText);
		usernameText = (EditText) view.findViewById(R.id.username_editText);
		passwordText = (EditText) view.findViewById(R.id.password_editText);
		adderssText = (EditText) view.findViewById(R.id.address_editText);
		portText = (EditText) view.findViewById(R.id.port_editText);
		initListener();
		
		return view;
	}
	
	private void initDialog(){
		downloadDialog = new ProgressDialog(getActivity());
		downloadDialog.setTitle(R.string.dialog_title);
		downloadDialog.setMessage(getResources().getString(R.string.dialog_download));
		downloadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		downloadDialog.setCancelable(false);
	}
	
	private void initListener() {
		// TODO Auto-generated method stub
		if (downloadButton != null) {
			downloadButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Log.e("ws", "---->>downloadButton checked");
					initDialog();
					
					//uploadAsyncTask.execute();
					WIFIDownloadAsyncTask mWIFIUploadAsyncTask = new WIFIDownloadAsyncTask();
					mWIFIUploadAsyncTask.execute();
				}
			});
		}
		
		
	}
	
	class WIFIDownloadAsyncTask extends AsyncTask<Void, Void, Boolean>{

		boolean isDownloadTaskSuccess = false;
		
		String mAddress = adderssText.getText().toString();
		String mPort = portText.getText().toString();
		String mUsername = usernameText.getText().toString();
		String mPossward = passwordText.getText().toString();
		String mFilepath = filePathText.getText().toString();
		
		
		@Override
		protected Boolean doInBackground(Void... params) {
			if (WIFIDownloadFileFromServer(mAddress,mPort,mUsername,mPossward,mFilepath)) {
				isDownloadTaskSuccess = true;
			}
			publishProgress();
			return isDownloadTaskSuccess;
		}
		
		protected void onProgressUpdate(Boolean[] values) {
			for(Boolean value : values){
				if (value == true) {
					Log.e("ws", "---->>downloadtask success!!!!");
				}else {
					Toast.makeText(getActivity(), R.string.download_error, Toast.LENGTH_LONG).show();
				}
			}
		};
		
		protected void onPostExecute(Boolean result) {
			downloadDialog.setProgress(100);
			downloadDialog.dismiss();
			Toast.makeText(getActivity(), R.string.download_completed, Toast.LENGTH_LONG).show();
		};
		
		
	}

	//private Boolean WIFIDownloadFileFromServer(){
	private Boolean WIFIDownloadFileFromServer(String address, String port,
			String username, String password,
			String filePath){
		Boolean isDownloadSuccess = false;
		
		FTPClient ftpClient = new FTPClient();
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().
				detectDiskWrites().detectNetwork().penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()       
        .detectLeakedSqlLiteObjects()    
        .penaltyLog()       
        .penaltyDeath()       
        .build());
		
		
		try {
			int reply;
			//ftpClient.connect("192.168.1.114", 21);
			ftpClient.connect(address, Integer.parseInt(port));
			Log.e("ws", "---->>after connect");
			reply = ftpClient.getReplyCode();

			//如果连接失败
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftpClient.disconnect();
				Log.e("ws", "---->>FTP server refused connection");
				return isDownloadSuccess;
			}
			

			//if ((ftpClient.login("ftpusername", "123"))) {
			if ((ftpClient.login(username, password))) {
				Log.e("ws", "---->>FTP log success");
			}else {
				Log.e("ws", "---->>FTP log  failure");
				return isDownloadSuccess;
			}
			
			Thread myThread = new Thread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Message msg = new Message();
					msg.what = 2;
					handler.sendMessage(msg);
				}
			});
			
			myThread.start();
			
			
			ftpClient.changeWorkingDirectory("C:/MyFTPTest");
			
			String loaclPath = "/storage/sdcard1/";
			
			File localFile = new File(loaclPath+"/"+"download.txt");  
            
			OutputStream output = new FileOutputStream(filePath);
			
			if (ftpClient.retrieveFile("download.txt",output)) {
				Log.e("ws", "---->>FTP retrieveFile success");
				
				isDownloadSuccess = true;
			}else {
				Log.e("ws", "---->>FTP retrieveFile failure");
			}
			
			output.close();
			ftpClient.logout();
			ftpClient.retrieveFile("uploadtest.txt",output);
			output.close();
			
			
			/*
			 * if (ftpClient.storeFile("ftptest.txt", input)) {
				Log.e("ws", "---->>FTP storeFile success");
				input.close();
				ftpClient.logout();
				isUploadSuccess = true;
			}else {
				Log.e("ws", "---->>FTP storeFile failure");
				input.close();
			}*/
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (ftpClient.isConnected()) {
				try {
					//input.close();
					ftpClient.disconnect();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return isDownloadSuccess;
	}
	
	
	Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			if (msg.what == 2) {
				downloadDialog.show();
			}
		};
	};
	
	    
	
	
	
}
