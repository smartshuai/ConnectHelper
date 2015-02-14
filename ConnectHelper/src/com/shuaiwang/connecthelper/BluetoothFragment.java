package com.shuaiwang.connecthelper;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import javax.security.auth.PrivateCredentialPermission;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;
import android.view.View.OnClickListener;

public class BluetoothFragment extends Fragment {

	private Button button;
	
	BluetoothAdapter mBluetoothAdapter;
	private static final String SERVER_MAC_ADDRESS = "90:4C:E5:CA:1C:95";
	private static final String SERVER_NAME = "bluetoothServerName";
	private static final String MY_UUID = "00001106-0000-1000-8000-00805F9B34FB";
	ArrayAdapter<String> mBluDeviceArrayAdapter;
	
	
	
	
	public BluetoothFragment() {
		// TODO Auto-generated constructor stub
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		//mBluDeviceArrayAdapter =
				//new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.bluetooth_fragment,container, false);
		button = (Button) view.findViewById(R.id.button1);
		
		
		
		initListener();
		return view;
	}
	
	
	
	private void startBluetoothServer() {
		// TODO Auto-generated method stub
		AcceptThread mAcceptThread = new AcceptThread();
		mAcceptThread.run();
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		
		mBluDeviceArrayAdapter =
				new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
		startBluetoothServer();
	}

	private void initListener() {

	button.setOnClickListener(new OnClickListener() {
		
		BluetoothDevice  mBluetoothDevice;
		@Override
		public void onClick(View v) {
			Log.e("ws", "---->>button checked");
			BLEDownloadFileFromServer();
			/*ConnectThread mConnectThread = new ConnectThread(device);
			mConnectThread.run();*/
			Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
			// If there are paired devices
			if (pairedDevices.size() > 0) {
			    // Loop through paired devices
			    for (BluetoothDevice device : pairedDevices) {
			        // Add the name and address to an array adapter to show in a ListView
			    	mBluDeviceArrayAdapter.add(device.getName() + "\n" + device.getAddress());
			    	Log.e("ws", "---->>bluetooth: " + device.getName() + device.getAddress());
			    	mBluetoothDevice = device;
			    }
			}
			
			  
			ConnectThread mConnectThread = new ConnectThread(mBluetoothDevice);
			mConnectThread.run();
			
			
			
			
		}
	});
		
	}
	
	
	private class AcceptThread extends Thread {
	    private final BluetoothServerSocket mmServerSocket;
	 
	    public AcceptThread() {
	        // Use a temporary object that is later assigned to mmServerSocket,
	        // because mmServerSocket is final
	        BluetoothServerSocket tmp = null;
	        try {
	            // MY_UUID is the app's UUID string, also used by the client code
	        	UUID mUuid = UUID.fromString(MY_UUID);
	            tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(SERVER_NAME, mUuid);
	        } catch (IOException e) { }
	        mmServerSocket = tmp;
	    }
	 
	    public void run() {
	        BluetoothSocket socket = null;
	        // Keep listening until exception occurs or a socket is returned
	        while (true) {
	           /* try {
	            	
	                //socket = mmServerSocket.accept();
	            } catch (IOException e) {
	                break;
	            }*/
	            // If a connection was accepted
	            if (socket != null) {
	                // Do work to manage the connection (in a separate thread)
	                //manageConnectedSocket(socket);
	                try {
						mmServerSocket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	                break;
	            }
	        }
	    }
	 
	    /** Will cancel the listening socket, and cause the thread to finish */
	    public void cancel() {
	        try {
	            mmServerSocket.close();
	        } catch (IOException e) { }
	    }
	}
	
	private boolean BLEDownloadFileFromServer(){
		boolean isDownloadSuccess = false;
		
		
		if (!getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
		    Toast.makeText(getActivity(), getResources().getString(R.string.ble_not_supported), Toast.LENGTH_SHORT).show();
		   
		}
		
		//BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
		    // Device does not support Bluetooth
			Log.e("ws", "---->>can not support bluetooth"); 
		}else {
			Log.e("ws", "---->>can support bluetooth"); 
		}
		
		if (!mBluetoothAdapter.isEnabled()) {
			//打开蓝牙
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, 0);
			//也可以直接打开蓝牙
		    //mBluetoothAdapter.enable();
		}else {
			String address = mBluetoothAdapter.getAddress();
			String name = mBluetoothAdapter.getName();
			Log.e("ws", "---->>bluetooth: " + address + name);
		}
		
		//查找是否有已经配对的蓝牙设备
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		// If there are paired devices
		if (pairedDevices.size() > 0) {
		    // Loop through paired devices
		    for (BluetoothDevice device : pairedDevices) {
		        // Add the name and address to an array adapter to show in a ListView
		    	mBluDeviceArrayAdapter.add(device.getName() + "\n" + device.getAddress());
		    	Log.e("ws", "---->>bluetooth: " + device.getName() + device.getAddress());
		    }
		}
		
		BroadcastReceiver mReceiver = new BroadcastReceiver(){

			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				//final ArrayAdapter<String> tmpaAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
				
				if (BluetoothDevice.ACTION_FOUND.equals(action)) {
					BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					mBluDeviceArrayAdapter.add(device.getName() + "\n" + device.getAddress());
				}
				
			}
			
			
			
			
		};
		
		// Register the BroadcastReceiver
		// Don't forget to unregister during onDestroy
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		getActivity().registerReceiver(mReceiver, filter);
		
		//设置设备可见
		Intent discoverableIntent = new
				Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
				discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
				startActivity(discoverableIntent);
		
		//连接到服务端
	    BluetoothDevice serverDevice = mBluetoothAdapter.getRemoteDevice(SERVER_MAC_ADDRESS);
		ConnectThread mConnectThread = new ConnectThread(serverDevice);
		mConnectThread.run();
		
		return isDownloadSuccess;
	}
	
	//初始化连接的线程
	private class ConnectThread extends Thread {
	    private final BluetoothSocket mmSocket;
	    private final BluetoothDevice mmDevice;
	 
	    public ConnectThread(BluetoothDevice device) {
	        // Use a temporary object that is later assigned to mmSocket,
	        // because mmSocket is final
	        BluetoothSocket tmp = null;
	        mmDevice = device;
	 
	        // Get a BluetoothSocket to connect with the given BluetoothDevice
	      try {
	           
	    	    UUID mUuid = UUID.fromString(MY_UUID);
	            tmp = device.createRfcommSocketToServiceRecord(mUuid);
	      } catch (IOException e) { 
	        	e.printStackTrace();
	        }
	        mmSocket = tmp;
	    }
	 
	    public void run() {
	        // Cancel discovery because it will slow down the connection
	        mBluetoothAdapter.cancelDiscovery();
	 
	        try {
	            // Connect the device through the socket. This will block
	            // until it succeeds or throws an exception
	            mmSocket.connect();
	            Log.e("ws", "---->>after connect");
	        } catch (IOException connectException) {
	            // Unable to connect; close the socket and get out
	        	Log.e("ws", "---->>Unable to connect");
	        	connectException.printStackTrace();
	            try {
	                mmSocket.close();
	            } catch (IOException closeException) { }
	            return;
	        }
	 
	        // Do work to manage the connection (in a separate thread)
	        //manageConnectedSocket(mmSocket);
	    }
	 
	    /** Will cancel an in-progress connection, and close the socket */
	    public void cancel() {
	        try {
	            mmSocket.close();
	        } catch (IOException e) { }
	    }
	    
	    
	    
	    
	    
	
	    /*
	    private class ConnectedThread extends Thread {
	        private final BluetoothSocket mmSocket;
	        private final InputStream mmInStream;
	        private final OutputStream mmOutStream;
	     
	        public ConnectedThread(BluetoothSocket socket) {
	            mmSocket = socket;
	            InputStream tmpIn = null;
	            OutputStream tmpOut = null;
	     
	            // Get the input and output streams, using temp objects because
	            // member streams are final
	            try {
	                tmpIn = socket.getInputStream();
	                tmpOut = socket.getOutputStream();
	            } catch (IOException e) { }
	     
	            mmInStream = tmpIn;
	            mmOutStream = tmpOut;
	        }
	     
	        public void run() {
	            byte[] buffer = new byte[1024];  // buffer store for the stream
	            int bytes; // bytes returned from read()
	     
	            // Keep listening to the InputStream until an exception occurs
	            while (true) {
	                try {
	                    // Read from the InputStream
	                    bytes = mmInStream.read(buffer);
	                    // Send the obtained bytes to the UI activity
	                    mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
	                            .sendToTarget();
	                } catch (IOException e) {
	                    break;
	                }
	            }
	        }
	     
	        /* Call this from the main activity to send data to the remote device 
	        public void write(byte[] bytes) {
	            try {
	                mmOutStream.write(bytes);
	            } catch (IOException e) { }
	        }
	     
	         Call this from the main activity to shutdown the connection 
	        public void cancel() {
	            try {
	                mmSocket.close();
	            } catch (IOException e) { }
	        }*/
	    }
	  
	    
	    
	

}
