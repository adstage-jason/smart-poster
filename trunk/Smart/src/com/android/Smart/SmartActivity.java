package com.android.Smart;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ListActivity;

import android.os.Parcelable;
import android.os.Bundle;
import android.os.Parcelable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.*;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;

import java.io.IOException;
import java.nio.charset.Charset;

import android.util.Log;
import java.util.Locale;

import com.android.Smart.dataobject.mifare.*;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.*;

public class SmartActivity extends SPANActivity {
	
	private EditText andrewID;
    private EditText andrewPassword;

 // NFC parts
 	private NfcAdapter mAdapter;
 	private PendingIntent mPendingIntent;
 	private IntentFilter[] mFilters;
 	private String[][] mTechLists;
 	
    private TextView mText;
    private int mCount = 0;

	Intent intent;
    /** Called when the activity is first created. */

    @Override
    public void onCreate(Bundle savedState) {
       super.onCreate(savedState);
       setContentView(R.layout.main);
       
       mAdapter = NfcAdapter.getDefaultAdapter(this);
    // Create a generic PendingIntent that will be deliver to this activity.
	// The NFC stack
	// will fill in the intent with the details of the discovered tag before
	// delivering to
	// this activity.
       mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    // Setup an intent filter for all MIME based dispatches
       IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
 
		try {
			ndef.addDataType("*/*");
		} catch (MalformedMimeTypeException e) {
			throw new RuntimeException("fail", e);
		}
		mFilters = new IntentFilter[] { ndef, };

		// Setup a tech list for all NfcF tags
		mTechLists = new String[][] { new String[] { MifareClassic.class
				.getName() } };

		intent = getIntent();
	//	resolveIntent(intent);
       
       
       Button logIn;
       logIn=(Button)findViewById(R.id.button1);
       logIn.setOnClickListener(new Button.OnClickListener(){
    	   public void onClick(View v)
    	   {
    		   andrewID=(EditText)findViewById(R.id.editText2);
    	       andrewPassword=(EditText)findViewById(R.id.editText1);
    	       
    	       String buffer="";
    	       
    	       Intent newIntent= new Intent();
    	       
    	       if (!this.checkCookie())
    	       {
	    	       Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
	    	      	    	       
	    	       if (tagFromIntent!= null)
	    	    	   newIntent=intent;
	    	       
    	    	   Bundle bundle=new Bundle();
	    	       
    	    	   buffer= andrewID.getText().toString();
    	    	   bundle.putCharSequence("andrewID", buffer);
	    	       
	    	       buffer= andrewPassword.getText().toString();
	    	       bundle.putCharSequence("andrewPassword", buffer);
	    	      
	    	       newIntent.setClass(SmartActivity.this, login.class);
	    	       newIntent.putExtras(bundle);
    	       }
    	       
    	       startActivity(newIntent);  	       
    	       finish();
	       }

		private boolean checkCookie() {
			// checkCookie
			return false;
		}	   
       });
    }
    


	
    @Override
    public void onResume() {
        super.onResume();
     //   mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechLists);
    }

   @Override
    public void onNewIntent(Intent intent) {
        Log.i("Foreground dispatch", "Discovered tag with intent: " + intent);
       // mText.setText("Discovered tag " + ++mCount + " with intent: " + intent);
    }
   
    @Override
    public void onPause() {
        super.onPause();
     //   mAdapter.disableForegroundDispatch(this);
    }
}
