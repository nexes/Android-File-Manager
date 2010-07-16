package com.nexes.manager;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.app.ListActivity;
import android.content.pm.PackageManager;
import android.content.pm.ApplicationInfo;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

public class ApplicationBackup extends ListActivity {
	private static final String BACKUP_LOC = "/sdcard/AppBackup/";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		
	}
}
