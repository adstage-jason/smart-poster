package com.android.SmartWriter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NfcAdapter;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.util.Log;

public class WriteTagActivity extends Activity {
    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    private String tagID;
    private TagConnector tagConnector;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        setContentView(R.layout.write_tag_activity);
        
        // Get the tagID
        Bundle extras = getIntent().getExtras();
        tagID = extras.getString("Tag ID");

        tagConnector = new TagConnector();
        mAdapter = NfcAdapter.getDefaultAdapter(this);

        // Create a generic PendingIntent that will be deliver to this activity. The NFC stack
        // will fill in the intent with the details of the discovered tag before delivering to
        // this activity.
        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        // Setup an intent filter for all MIME based dispatches
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndef.addDataType("*/*");
        } catch (MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }
        mFilters = new IntentFilter[] {
                ndef,
        };

        // Setup a tech list for all NfcF tags
        mTechLists = new String[][] { new String[] { NfcF.class.getName() } };
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter != null) mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters,
                mTechLists);
    }

    @Override
    public void onNewIntent(Intent intent) {
        Log.i("Foreground dispatch", "Discovered tag with intent: " + intent);
        if (tagConnector.writeTag(intent, tagID)) {
        	AlertDialog alertDialog = new AlertDialog.Builder(this).create();  
			  alertDialog.setTitle("Tag Written!");  
			  alertDialog.setMessage("Have fun with your poster!");  
			  alertDialog.setButton("OK", new DialogInterface.OnClickListener() {  
				  public void onClick(DialogInterface dialog, int which) {  
					  Intent intent = new Intent(WriteTagActivity.this, SmartWriterActivity.class);
	     			  startActivity(intent);
				  } });
			  alertDialog.show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAdapter != null) mAdapter.disableForegroundDispatch(this);
    }
}
