package com.shuaiwang.connecthelper;

import java.net.ContentHandler;

import com.example.qr_codescan.QRcodeScanActivity;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class QRCodeFragment extends Fragment {

	private Button scanButton;
	
	public QRCodeFragment() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		View view =  inflater.inflate(R.layout.qrcode_fragment,container,false);
		
		scanButton = (Button) view.findViewById(R.id.scan_button);
		
		scanButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.e("ws", "---->>scan button checked");
				scanQR();

			}
		});
		
		return view;
	}
	
	private void scanQR(){
		Log.e("ws", "---->>scanQR start");
		
		Intent intent = new Intent();
		intent.setClass(getActivity(), QRcodeScanActivity.class);
		getActivity().startActivity(intent);
		
		
	}
	
	
	
	
	private void attach() {
		// TODO Auto-generated method stub
		Context context = getActivity();
		
		
	}
	
	

}
