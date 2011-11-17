package com.android.Smart;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.android.Smart.poster.Poster;

public class SPANActivity extends Activity {
	
	public static final String serverURL = "https://www.ece.cmu.edu/~jasonwu/span/";
	private static java.net.CookieManager httpCookieMgr = new java.net.CookieManager();
	
	static {
		CookieHandler.setDefault(httpCookieMgr);
	}
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Ensure that the singleton CookieSyncManager is set up
		CookieSyncManager.createInstance(getApplicationContext());
	}
	
	protected boolean checkAuthStatus() {
		
		// First check if an app server cookie exists
		CookieManager mgr = CookieManager.getInstance();
        if (mgr.getCookie(serverURL) == null || !mgr.getCookie(serverURL).contains("pubcookie_s")) {
        	return false;
        }

        // Make sure the app server cookie is still valid
		URL url = null;
		HttpURLConnection urlConnection = null;
		try {
			
			// Send a GET request to the server
			url = new URL(serverURL);
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setInstanceFollowRedirects(false);
			urlConnection.connect();
			InputStream in = new BufferedInputStream(urlConnection.getInputStream());
			BufferedReader br = new BufferedReader(
                    new InputStreamReader(in));
			
			// WebISO does not use a 3xx redirect, so we need to check the meta tags in <head>
			String inputLine;
			while ((inputLine = br.readLine()) != null) 
				if (inputLine.contains("<meta http-equiv=\"Refresh\">")) {
					br.close();
					return false;
				}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
	    	if (urlConnection != null)
	    		urlConnection.disconnect();
	    }
		Log.i("CheckAuthStatus()", "User is authenticated!");
		return true;
	}
	
	protected void authenticateUser(Intent intent) {
		// Send the user to the WebISO login page
		//Intent intent = new Intent(SPANActivity.this, AuthenticateActivity.class);
		intent.setClass(SPANActivity.this, AuthenticateActivity.class);
		
		startActivity(intent);
	}
	
	protected void parseAndStoreCookie() {
		// Parse cookie(s) to retrieve just pubcookie_s cookie
		CookieManager mgr = CookieManager.getInstance();
		String mgrCookie = mgr.getCookie(serverURL);
		int start = mgrCookie.indexOf("pubcookie_s");
		int end = mgrCookie.indexOf("=");
		String cookieName = mgrCookie.substring(start, end);
		Log.i("AUTHENTICATE", cookieName);
		start = end + 1;
		if (mgrCookie.contains(";")) {
			end = mgrCookie.indexOf(";", start);
		} else {
			end = mgrCookie.length();
		}
		String cookieValue = mgrCookie.substring(start, end);
		
		// Store the cookie
		HttpCookie cookie = new HttpCookie(cookieName, cookieValue);
		cookie.setDomain("www.ece.cmu.edu");
		cookie.setPath("/~jasonwu/");
		cookie.setVersion(0);
		try {
			httpCookieMgr.getCookieStore().add(new URI(serverURL), cookie);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	protected void clearCookies() {
		httpCookieMgr = new java.net.CookieManager();
		CookieHandler.setDefault(httpCookieMgr);
		CookieManager.getInstance().removeAllCookie();
	}
	
	protected Poster getPoster(String tagID) throws Poster.NoSuchPosterException, Poster.RevokedPosterException {
		Poster poster = null;
		try {
			URL url = new URL(serverURL + "get_poster.php?id=" + tagID);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setInstanceFollowRedirects(true);
			urlConnection.connect();
			InputStream in = new BufferedInputStream(urlConnection.getInputStream());
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();
			SPANHandler handler = new SPANHandler();
			xr.setContentHandler(handler);
			xr.parse(new InputSource(in));
			Log.i("callGetPoster()", String.valueOf(handler.getErrorCode()));
			if (handler.getErrorCode() == 0) {
				poster = handler.getPoster();
			} else if (handler.getErrorCode() == 1) {
				throw new Poster.NoSuchPosterException();
			} else if (handler.getErrorCode() == 2) {
				throw new Poster.RevokedPosterException();
			}
		} catch (Exception e) {
			Log.i("Oh noes!", e.getMessage());
		}
		return poster;
	}

}
