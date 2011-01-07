/*
    Open Manager, an open source file manager for the Android system
    Copyright (C) 2009, 2010, 2011  Joe Berria nexesdevelopment@gmail.com

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.nexes.manager;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Button;

public class WirelessManager extends Activity {
	//index values to access the elements in the TextView array.
	private final int SSTRENGTH = 0;
	private final int WIFISTATE = 1;
	private final int IPADD 	= 2;
	private final int MACADD 	= 3;
	private final int SSID 		= 4;
	private final int LINKSPD 	= 5;	
	
	private TextView[] data_labels;
	private TextView name_label;
	private TextView enable_label;
	private Button state_button;
	private Button back_button;
	private WifiManager wifi;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info_layout);

		wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
		TextView[] titles = new TextView[6];
		data_labels = new TextView[6];
		
		int[] left_views = {R.id.first_title, R.id.second_title, R.id.third_title,
					   R.id.fourth_title, R.id.fifth_title};
		
		/*R.layout.info_layout is the same layout used for directory info.
		 *Re-using the layout for this activity, so id tag names may not make sense,
		 *but are in the correct order.
		 */
		int[] right_views = {R.id.dirs_label, R.id.files_label, R.id.time_stamp,
							 R.id.total_size, R.id.free_space};
		String[] labels = {"Signal strength", "WIFI State", "ip address",
						  "mac address", "SSID", "link speed"};
		
		for (int i = 0; i < 5; i++) {
			titles[i] = (TextView)findViewById(left_views[i]);
			titles[i].setText(labels[i]);
			
			data_labels[i] = (TextView)findViewById(right_views[i]);
			data_labels[i].setText("N/A");
		}
		
		name_label = (TextView)findViewById(R.id.name_label);
		enable_label = (TextView)findViewById(R.id.path_label);
		state_button = (Button)findViewById(R.id.back_button);
		back_button = (Button)findViewById(R.id.zip_button);
		back_button.setText(" Back ");
		
		state_button.setOnClickListener(new ButtonHandler());
		back_button.setOnClickListener(new ButtonHandler());
		
		ImageView icon = (ImageView)findViewById(R.id.info_icon);
		icon.setImageResource(R.drawable.wireless);
				
		get_wifi();
	}
	
	private void get_wifi() {
		WifiInfo info = wifi.getConnectionInfo();
		int state = wifi.getWifiState();
		int strength = WifiManager.calculateSignalLevel(info.getRssi(), 5);
		boolean enabled = wifi.isWifiEnabled();
		
		name_label.setText(info.getSSID());
		enable_label.setText(enabled ?"Your wifi is enabled" :"Your wifi is not enabled");
		state_button.setText(enabled ?"Disable wifi" : "Enable wifi");
		
		switch(state) {
			case WifiManager.WIFI_STATE_ENABLED:
				data_labels[WIFISTATE].setText(" Enabled");
				break;
			case WifiManager.WIFI_STATE_DISABLED:
				data_labels[WIFISTATE].setText(" Disabled");
				break;
			case WifiManager.WIFI_STATE_DISABLING:
				data_labels[WIFISTATE].setText(" Being Disabled");
				break;
			case WifiManager.WIFI_STATE_ENABLING:
				data_labels[WIFISTATE].setText(" Being Enabled");
				break;
			case WifiManager.WIFI_STATE_UNKNOWN:
				data_labels[WIFISTATE].setText(" Unknown");
				break;
		}
		if(enabled) {
			data_labels[IPADD].setText(FileManager.integerToIPAddress(info.getIpAddress()));
			data_labels[MACADD].setText(info.getMacAddress());
			data_labels[SSID].setText(info.getSSID());
			data_labels[LINKSPD].setText(info.getLinkSpeed() + " Mbps");
			data_labels[SSTRENGTH].setText("strength " + strength);
		}else {
			data_labels[IPADD].setText("N/A");
			data_labels[MACADD].setText(info.getMacAddress());
			data_labels[SSID].setText("N/A");
			data_labels[LINKSPD].setText("N/A");
			data_labels[SSTRENGTH].setText("N/A");
		}
	}
	
	private class ButtonHandler implements OnClickListener {

		public void onClick(View v) {
			
			if(v.getId() == R.id.back_button) {
				if(wifi.isWifiEnabled()){
					wifi.setWifiEnabled(false);
					state_button.setText("Enable wifi");
				}else {
					wifi.setWifiEnabled(true);
					state_button.setText("Disable wifi");
					get_wifi();
				}	
			}else if(v.getId() == R.id.zip_button)
				finish();
		}
	}

}
