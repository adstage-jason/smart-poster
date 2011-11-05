package com.android.Smart;

import java.io.IOException;

import com.android.Smart.dataobject.mifare.MifareBlock;
import com.android.Smart.dataobject.mifare.MifareClassCard;
import com.android.Smart.dataobject.mifare.MifareSector;
import com.android.Smart.R;

import android.R.color;
import android.app.Activity;
import android.app.PendingIntent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.widget.*;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;

public class login extends Activity{
	private Tag tempTag;
	private NfcAdapter mAdapter;
 	private PendingIntent mPendingIntent;
 	private IntentFilter[] mFilters;
 	private String[][] mTechLists;

	private static final int AUTH = 1;
	private static final int EMPTY_BLOCK_0 = 2;
	private static final int EMPTY_BLOCK_1 = 3;
	private static final int NETWORK = 4;
	private static final String TAG = "purchtagscanact";
	
	private TextView mText;
	private int mCount=0;
    LinearLayout mTagContent;
    private MifareClassic mfc;
    private byte []data;
    
    Intent intent;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);		
		
		intent = getIntent();
		
		Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		Bundle bunde = this.getIntent().getExtras();
		
		
		String buffer = bunde.getString("andrewID");
		TextView temp=(TextView)findViewById(R.id.text1);
		temp.setText("Your andrewID is "+buffer);
		
		temp=(TextView)findViewById(R.id.text2);
		buffer= bunde.getString("andrewPassword");
		temp.setText("Your password is "+ buffer);
		
		//tempTag= bunde.getParcelable("tagFromIntent");
		mText=(TextView)findViewById(R.id.tagInformation);
		//if (tempTag!=null)
			//mText.setText("Your message is "+ tempTag.describeContents());

		 mText.setText("Scan a tag");
		
		if (tagFromIntent!=null)
			resolveIntent(intent);
		
		Button logIn;
		logIn=(Button)findViewById(R.id.button2);
		logIn.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v)
			{
			   Intent intent= new Intent();
			   intent.setClass(login.this, SmartActivity.class);
		       startActivity(intent);
		       finish();
			   }    	   
		   });
		
		
       
        
		mAdapter = NfcAdapter.getDefaultAdapter(this);
		mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
					getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
	  
        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        // Setup an intent filter for all MIME based dispatches
        try {
            ndef.addDataType("*/*");
        } catch (MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }
        mFilters = new IntentFilter[] {
                ndef,
        };

        // Setup a tech list for all MifareClassic tags
        mTechLists = new String[][] { new String[] { MifareClassic.class.getName() } };
       // resolveIntent(intent);

	}
	
	
	void resolveIntent(Intent intent) {
		   
		
		mText.setText("Discovered tag " + ++mCount + " with intent: " + intent);
		   
		   
		   
		// 1) Parse the intent and get the action that triggered this intent
		String action = intent.getAction();
		// 2) Check if it was triggered by a tag discovered interruption.
		if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
			// 3) Get an instance of the TAG from the NfcAdapter
			Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			// 4) Get an instance of the Mifare classic card from this TAG
			// intent
			mfc = MifareClassic.get(tagFromIntent);
			MifareClassCard mifareClassCard=null;

			try { // 5.1) Connect to card
				mfc.connect();
				boolean auth = false;
				// 5.2) and get the number of sectors this card has..and loop
				// thru these sectors
				int secCount = mfc.getSectorCount();
				mifareClassCard= new MifareClassCard(secCount);
				int bCount = 0;
				int bIndex = 0;
				for (int j = 0; j < secCount; j++) {
					MifareSector mifareSector = new MifareSector();
					mifareSector.sectorIndex = j;
					// 6.1) authenticate the sector
					auth = mfc.authenticateSectorWithKeyA(j,
							MifareClassic.KEY_DEFAULT);
					mifareSector.authorized = auth;
					if (auth) {
						// 6.2) In each sector - get the block count
						bCount = mfc.getBlockCountInSector(j);
						bCount =Math.min(bCount, MifareSector.BLOCKCOUNT);
						bIndex = mfc.sectorToBlock(j);
						for (int i = 0; i < bCount; i++) {

							// 6.3) Read the block
							byte []data = mfc.readBlock(bIndex);
							MifareBlock mifareBlock = new MifareBlock(data);
							mifareBlock.blockIndex = bIndex;
							// 7) Convert the data into a string from Hex
							// format.

							bIndex++;
							mifareSector.blocks[i] = mifareBlock;
	
							
						}
						mifareClassCard.setSector(mifareSector.sectorIndex,
								mifareSector);
					} else { // Authentication failed - Handle it

					}
				}
				
			} catch (IOException e) {
				Log.e(TAG, e.getLocalizedMessage());
				//showAlert(3);
			}finally{

				if(mifareClassCard!=null){
					mifareClassCard.debugPrint();
				}
			}
		}// End of method
	}
   
	  @Override
	    public void onResume() {
	        super.onResume();
	        mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechLists);
	    }

	  	@Override
	    public void onNewIntent(Intent intent) {
	        Log.i("Foreground dispatch", "Discovered tag with intent: " + intent);
	        mText.setText("Discovered tag " + ++mCount + " with intent: " + intent);
	    }
	   
	    @Override
	    public void onPause() {
	        super.onPause();
	        mAdapter.disableForegroundDispatch(this);
	    }
}