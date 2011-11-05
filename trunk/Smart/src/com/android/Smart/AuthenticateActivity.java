package com.android.Smart;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class AuthenticateActivity extends SPANActivity {
	
	private WebView authView;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.auth_screen);
		
		authView = (WebView)findViewById(R.id.auth_webview);
		WebSettings webSettings = authView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		authView.clearCache(true);
		authView.clearView();
		authView.setWebViewClient(new AuthenticateWebViewClient());
		authView.loadUrl(serverURL);
	}
	
	private class AuthenticateWebViewClient extends WebViewClient {
	    @Override
	    public void onPageFinished(WebView view, String url) {
	    	super.onPageFinished(view, url);
	        CookieManager mgr = CookieManager.getInstance();
	        if (url.equals(serverURL) && mgr.getCookie(serverURL) != null 
	        		&& mgr.getCookie(serverURL).contains("pubcookie_s")) {
	        	parseAndStoreCookie();
				Intent intent = new Intent(AuthenticateActivity.this, login.class);
				startActivity(intent);
	        	AuthenticateActivity.this.finish();
	        }
	    }
	}

}
