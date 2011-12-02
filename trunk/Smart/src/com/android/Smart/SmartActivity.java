package com.android.Smart;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NfcAdapter;
import android.nfc.tech.MifareUltralight;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.Smart.connector.TagConnector;

public class SmartActivity extends SPANActivity {
	
	private EditText andrewID;
    private EditText andrewPassword;
    private TagConnector tagConnector;

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
       
       tagConnector = new TagConnector();
       
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
		mTechLists = new String[][] { new String[] { MifareUltralight.class
				.getName() } };

		intent = getIntent();
		
	//	resolveIntent(intent);
		
		// First check if the user is authenticated
		// If so, then there is no need to show the login screen
		if (checkAuthStatus()) {
			
		}
		
		
       Button logIn;
       logIn=(Button)findViewById(R.id.button1);Log.i("flow1", "flow");
       logIn.setOnClickListener(new Button.OnClickListener(){
    	   public void onClick(View v)
    	   {
    		 //  andrewID=(EditText)findViewById(R.id.editText2);
    	      // andrewPassword=(EditText)findViewById(R.id.editText1);
    	       
    	       String buffer="";
    	       
    	       Log.i("SmartActivity", String.valueOf(checkAuthStatus()));
    	       if (checkAuthStatus()) {
					Toast.makeText(SmartActivity.this, "User already authenticated!", Toast.LENGTH_SHORT).show();
					Intent newintent = new Intent(SmartActivity.this, login.class);
					if (intent.getExtras()== null)
					{
						startActivity(newintent);
						Log.i("SmartActivity", "null intent");
					}
					else
					{
						intent.setClass(SmartActivity.this, login.class);
						startActivity(intent);
						Log.i("SmartActivity", "not null intent");
					}
					
				} else {
					Log.i("SmartActivity", "Authenticating user!");
					
					authenticateUser(intent);
				}
    	       finish();
	       }

       });
    }
    


	
    @Override
    public void onResume() {
        super.onResume();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            Log.i("Test", tagConnector.readTag(getIntent()));
        }
     //   mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechLists);
    }

   @Override
    public void onNewIntent(Intent intent) {
        Log.i("Foreground dispatch", "Discovered tag with intent: " + intent);
        Log.i("Test", tagConnector.readTag(intent));
       // mText.setText("Discovered tag " + ++mCount + " with intent: " + intent);
    }
   
    @Override
    public void onPause() {
        super.onPause();
     //   mAdapter.disableForegroundDispatch(this);
    }
    
    private void showTagDialog() {
    	AlertDialog alertDialog = new AlertDialog.Builder(this).create();  
    	alertDialog.setTitle("Smart Poster Detected!");  
    	alertDialog.setMessage("To read the poster content, please sign in!");  
    	alertDialog.setButton("OK", new DialogInterface.OnClickListener() {  
    		public void onClick(DialogInterface dialog, int which) {  
    			return;  
    		} });
    	alertDialog.show();
    }
}
