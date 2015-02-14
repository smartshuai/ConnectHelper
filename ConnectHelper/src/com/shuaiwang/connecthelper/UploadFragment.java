package com.shuaiwang.connecthelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import android.R.style;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;


public class UploadFragment extends Fragment {

	private Button uploadButton;
	private ProgressDialog uploadDialog;
	private Button  bleUploadButton;
	private EditText filePathText;
	private EditText adderssText;
	private EditText portText;
	private EditText usernameText;
	private EditText passwordText;
	
	
	public UploadFragment() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		//return super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.upload_fragment, container, false);
		uploadButton = (Button) view.findViewById(R.id.upload_button); 
		filePathText = (EditText) view.findViewById(R.id.path_editText);
		usernameText = (EditText) view.findViewById(R.id.username_editText);
		passwordText = (EditText) view.findViewById(R.id.password_editText);
		adderssText = (EditText) view.findViewById(R.id.address_editText);
		portText = (EditText) view.findViewById(R.id.port_editText);
		
		Log.e("ws", "---->>filepath***: " + filePathText.getText());

		initListener();
		
		return view;
		
	} 
	
	private void initDialog(){
		uploadDialog = new ProgressDialog(getActivity());
		uploadDialog.setTitle(R.string.dialog_title);
		uploadDialog.setMessage(getResources().getString(R.string.dialog_upload));
		uploadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		uploadDialog.setCancelable(false);
	}
	
	private void initListener() {
		// TODO Auto-generated method stub
		if (uploadButton != null) {
			uploadButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Log.e("ws", "---->>uploadButton checked");
					initDialog();
					
					//uploadAsyncTask.execute();
					UploadAsyncTask mUploadAsyncTask = new UploadAsyncTask();
					mUploadAsyncTask.execute();
				}
			});
		}
	}
	
	
	class UploadAsyncTask extends AsyncTask<Void, Void, Boolean>{

		
		
		boolean isUploadTaskSuccess = false;
		@Override
		protected Boolean doInBackground(Void... params) {
			
			String mAddress = adderssText.getText().toString();
			String mPort = portText.getText().toString();
			String mUsername = usernameText.getText().toString();
			String mPossward = passwordText.getText().toString();
			String mFilepath = filePathText.getText().toString();
			
			
			if (uploadFileToServer(mAddress, mPort,	mUsername, mPossward,
					mFilepath)) {
				isUploadTaskSuccess = true;
			}
			publishProgress();
			return isUploadTaskSuccess;
		}
		
		protected void onProgressUpdate(Boolean[] values) {
			for(Boolean value : values){
				if (value == true) {
					Log.e("ws", "---->>updatetask success!!!!");
					uploadDialog.setProgress(100);
					Toast.makeText(getActivity(), R.string.upload_completed, Toast.LENGTH_LONG).show();
				}else {
					Log.e("ws", "---->>updatetask fail!!!!");
					Toast.makeText(getActivity(), R.string.upload_error, Toast.LENGTH_LONG).show();
				}
			}
		};
		
		protected void onPostExecute(Boolean result) {
			
			uploadDialog.dismiss();
			
		};
		
		
	}
	
	
	Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				uploadDialog.show();
			}
		};
	};
	
	/*本函数实现手机向服务器上传文件
	 * 参数说明
	 * url Ftp服务器hostname
	 * port Ftp服务器端口号
	 * username Ftp服务器登陆账号
	 * password Ftp服务器登陆密码
	 * serverPath FTP服务器保存目录
	 * filePath  要上传文件所在的路径 
	 * fileName 要上传文件的文件名
	 * 
	 */
	public boolean uploadFileToServer(String address, String port,
			String username, String password,
			String filePath
			){
	///public boolean WIFIUploadFileToServer(){
		Log.e("ws", "---->>uploadFileToServer");
		
		boolean isUploadSuccess = false;
		
		FTPClient ftpClient = new FTPClient();
		InputStream input;
		
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().
				detectDiskWrites().detectNetwork().penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()       
        .detectLeakedSqlLiteObjects()    
        .penaltyLog()       
        .penaltyDeath()       
        .build());
		
		try {
			int reply;
			Log.e("ws", "---->>before connect");
			//ftpClient.connect("192.168.1.114", 21);
			ftpClient.connect(address,Integer.parseInt(port));
			Log.e("ws", "---->>after connect");
			reply = ftpClient.getReplyCode();
		    
			//如果连接失败
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftpClient.disconnect();
				Log.e("ws", "---->>FTP server refused connection");
				return isUploadSuccess;
			}
			
			Log.e("ws", "---->>FTP server connect success");
			
			//if ((ftpClient.login("ftpusername", "123"))) {
			if ((ftpClient.login(username, password))) {
				Log.e("ws", "---->>FTP log success");
			}else {
				Log.e("ws", "---->>FTP log  failure");
				return isUploadSuccess;
			}
			
			//这里需要写一个handler
			/*if (uploadDialog != null) {
				uploadDialog.show();
			}*/
			
			Thread myThread = new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Message msg = new Message();
					msg.what = 1;
					handler.sendMessage(msg);
					
				}
			});
			
			myThread.start();
			
			
			ftpClient.changeWorkingDirectory("C:/MyFTPTest");
			Log.e("ws", "---->>after FTP changeWorkingDirectory ");
			
			//获取SD卡路径
			/*File sdDir = null; 
		    boolean sdCardExist = Environment.getExternalStorageState()   
		                           .equals(Environment.MEDIA_MOUNTED);   //判断sd卡是否存在 

		    if(sdCardExist)      //如果SD卡存在，则获取跟目录
		    {              
		    	Log.e("ws", "---->>sdcard mounted ");
		    	//sdDir = Environment.getExternalStorageDirectory().getAbsolutePath();//获取跟目录 
		    	sdDir = Environment.getExternalStorageDirectory();
		    	//sdDir = "/mnt/sdcard";
		    }   
		     
		    if (!sdDir.exists()) {
				sdDir.mkdir();
			}
		    String filePath = sdDir + File.separator; 
		    */
			
			File file = new File(filePath);
			if (!file.exists()) {
				file.mkdir();
			}
			
			input = new FileInputStream(filePath);
			
			
			//input = new FileInputStream("/storage/sdcard1/"+"ftptest.txt");
			Log.e("ws", "---->>after new FileInputStream ");
			
			if (input != null) {
				Log.e("ws", "---->>after new FileInputStream :" + "not null" );
			}else {
				Log.e("ws", "---->>after new FileInputStream :" + "is null" );
			}
			
			
			if (ftpClient.storeFile("ftptest.txt", input)) {
				Log.e("ws", "---->>FTP storeFile success");
				input.close();
				ftpClient.logout();
				isUploadSuccess = true;
			}else {
				Log.e("ws", "---->>FTP storeFile failure");
				input.close();
			}
			
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
		
		return isUploadSuccess;
		
	}//uploadend
	
	
}
