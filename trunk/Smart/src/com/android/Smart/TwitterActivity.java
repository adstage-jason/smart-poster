package com.android.Smart;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class TwitterActivity extends SPANActivity {
	
	private WebView authView;
	Intent intent;
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signinwithtwitter_screen);
		
		authView = (WebView)findViewById(R.id.signinwithtwitter_webview);
		WebSettings webSettings = authView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		authView.clearCache(true);
		authView.clearView();
		authView.setWebViewClient(new AuthenticateWebViewClient());
		intent=getIntent();
		
		authView.loadUrl(intent.getStringExtra("url"));
		
	}
	
	private class AuthenticateWebViewClient extends WebViewClient {
	    @Override
	    public void onPageFinished(WebView view, String url) {
	    	Log.i("SmartActivity","webfinish");
	    	super.onPageFinished(view, url);
	        CookieManager mgr = CookieManager.getInstance();
	        if (url.equals(serverURL) && mgr.getCookie(serverURL) != null 
	        		&& mgr.getCookie(serverURL).contains("pubcookie_s")) {
	        	parseAndStoreCookie();
				//Intent intent = new Intent(AuthenticateActivity.this, login.class);
				Intent intent = getIntent();
				intent.setClass(TwitterActivity.this, login.class);
				Log.i("SmartActivity", "web");
	        	startActivity(intent);
	        	TwitterActivity.this.finish();
	        }
	    }
	}

}
