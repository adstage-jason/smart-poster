package edu.cmu.span;

import android.app.Activity;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class TestPageActivity extends Activity {
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_screen);
        
		WebView testView = (WebView)findViewById(R.id.test_webview);
		WebSettings webSettings = testView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		testView.setWebViewClient(new TestWebViewClient());
		testView.loadUrl("https://www.ece.cmu.edu/~jasonwu/test.html");
		Log.i("TestPageActivity", "onCreate()");

	}
	
	private class TestWebViewClient extends WebViewClient {
	    @Override
	    public void onPageFinished(WebView view, String url) {
	    	super.onPageFinished(view, url);
	        CookieManager mgr = CookieManager.getInstance();
	        if (url != null) Log.i( "HelloAndroid", url );
	        if (mgr.getCookie(url) != null) Log.i( "HelloAndroid", mgr.getCookie( url ) );
	        if (mgr.getCookie("https://www.ece.cmu.edu/~jasonwu") != null) Log.i( "HelloAndroid", mgr.getCookie("https://www.ece.cmu.edu/~jasonwu"));
	    }
	    
	    public boolean shouldOverrideUrlLoading(WebView view, String url){
	    	if (url != null) Log.i( "OVERRIDE", url );
	        view.loadUrl(url);
	        return true;
	    }
	    
	    public void onLoadResource(WebView view, String url) {
	    	if (url != null) Log.i( "ONLOADRESOURCE", url );
	    }
	    
	    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
	        Toast.makeText(TestPageActivity.this, "Oh no! " + description, Toast.LENGTH_SHORT).show();
	    }
	    
	    public void onReceivedSslError (WebView view, SslErrorHandler handler, SslError error) {
	    	 handler.proceed();
	    }


	}

}
