package edu.cmu.span;

import org.apache.http.impl.client.DefaultHttpClient;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.Toast;

public class SPANTestActivity extends SPANActivity {

	private DefaultHttpClient httpClient;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		CookieSyncManager.createInstance(getApplicationContext());
		// httpClient = new DefaultHttpClient();

		Button authButton = (Button) findViewById(R.id.auth_button);
		authButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				Intent intent = new Intent(SPANTestActivity.this,
						AuthenticateActivity.class);
				startActivity(intent);
			}

		});

		Button testButton = (Button) findViewById(R.id.test_button);
		testButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				Intent intent = new Intent(SPANTestActivity.this,
						TestPageActivity.class);
				startActivity(intent);
			}

		});
		
		Button checkAuthStatusButton = (Button)findViewById(R.id.check_auth_status_button);
		checkAuthStatusButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				if (checkAuthStatus()) {
					Toast.makeText(SPANTestActivity.this, "User authenticated!", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(SPANTestActivity.this, "User not authenticated..", Toast.LENGTH_SHORT).show();
				}
			}

		});
		
		Button loginButton = (Button)findViewById(R.id.login_button);
		loginButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				if (checkAuthStatus()) {
					Toast.makeText(SPANTestActivity.this, "User already authenticated!", Toast.LENGTH_SHORT).show();
				} else {
					authenticateUser();
				}
			}

		});
		
		Button logoutButton = (Button)findViewById(R.id.logout_button);
		logoutButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				clearCookies();
				Toast.makeText(SPANTestActivity.this, "Successfully logged out.", Toast.LENGTH_SHORT).show();
			}

		});

	}
}