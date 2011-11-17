package com.android.Smart;

import java.io.IOException;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.Smart.connector.TagConnector;
import com.android.Smart.dataobject.mifare.MifareBlock;
import com.android.Smart.dataobject.mifare.MifareClassCard;
import com.android.Smart.dataobject.mifare.MifareSector;
import com.android.Smart.poster.LinkPoster;
import com.android.Smart.poster.PollPoster;
import com.android.Smart.poster.Poster;
import com.android.Smart.poster.Poster.NoSuchPosterException;
import com.android.Smart.poster.Poster.RevokedPosterException;

public class login extends SPANActivity{
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
    private TagConnector tagConnector;
    
    Intent intent;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);		
		
		intent = getIntent();
		tagConnector = new TagConnector();
		
		Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		Bundle bunde = this.getIntent().getExtras();
		
		
		String buffer = "not displayed"; //bunde.getString("andrewID");
		TextView temp=(TextView)findViewById(R.id.text1);
		temp.setText("Your andrewID is "+buffer);
		
		temp=(TextView)findViewById(R.id.text2);
		//buffer= bunde.getString("andrewPassword");
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
			   clearCookies();
			   Toast.makeText(login.this, "Successfully logged out.", Toast.LENGTH_SHORT).show();
			   Intent intent= new Intent();
			   intent.setClass(login.this, SmartActivity.class);
		       startActivity(intent);
		       finish();
			   }    	   
		   });
		
		
       
        
	       mAdapter = NfcAdapter.getDefaultAdapter(this);
	       // Create a generic PendingIntent that will be deliver to this activity.
	   	// The NFC stack
	   	// will fill in the intent with the details of the discovered tag before
	   	// delivering to
	   	// this activity.
	          mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
	   				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
	       // Setup an intent filter for all MIME based dispatches
	          IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
	    
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
	       String tagID = tagConnector.readTag(intent);
	       Poster poster = null;
			try {
				poster = getPoster(tagID);
			} catch (NoSuchPosterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RevokedPosterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	       if (poster instanceof LinkPoster) {
	    	   LinkPoster lp = (LinkPoster) poster;
	    	   Intent newIntent = new Intent(this, LinkPosterActivity.class);
	    	   Log.i("Hello", String.valueOf(lp.getDescription()));
	    	   newIntent.putExtra("LinkPoster", lp);
	    	   startActivity(newIntent);
	       } else if (poster instanceof PollPoster) {
	    	   Intent newIntent = new Intent(this, PollPosterActivity.class);
	    	   PollPoster pp = (PollPoster) poster;
	    	   newIntent.putExtra("PollPoster", pp);
	    	   startActivity(newIntent);
	       }
	    }
	   
	    @Override
	    public void onPause() {
	        super.onPause();
	        mAdapter.disableForegroundDispatch(this);
	    }
}