package com.sean.whiteboard;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.sean.whiteboard.menu.BoardMenu;

import java.io.File;

public class MainActivity extends AppCompatActivity {
	private WhiteBoardView boardView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		boardView = new WhiteBoardView(this);
		setContentView(R.layout.activity_main);
		boardView = findViewById(R.id.doodle_view);
//		Button btnSwitch = findViewById(R.id.button_switch);
//		btnSwitch.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				//doodleView.revoke();
//			}
//		});
//
//		Button buttonRecovery = findViewById(R.id.button_recovery);
//		buttonRecovery.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				//doodleView.recovery();
//			}
//		});
		if (ContextCompat.checkSelfPermission(this,
				Manifest.permission.READ_EXTERNAL_STORAGE)
				!= PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this,
					new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
					1);
		}else {
			boardView.displayFile(new File("/storage/emulated/0/test.docx"));
		}

		BoardMenu boardMenu = findViewById(R.id.board_menu);
		boardMenu.setConfigChangeListener(boardView);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (grantResults.length > 0
				&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
			boardView.displayFile(new File("/storage/emulated/0/test.docx"));
		}
	}
}
