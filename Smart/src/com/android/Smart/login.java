package com.android.Smart;

import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.http.AccessToken;
import twitter4j.http.RequestToken;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.Smart.connector.TagConnector;
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

	private static Twitter twitter;
	private static RequestToken requestToken=null;
	private static String verifier=null;
	private static AccessToken accessToken=null;
	private static String tweetText="";
	
	private static Tag tagFromIntent=null;
//Please put the values of consumerKy and consumerSecret of your app 
	public final static String consumerKey = "3LBMBXOxGkK0febwY2MqA"; // "your key here";
	public final static String consumerSecret = "upBZ59iEhKMiUdcCd5kOR2bQduM8vAPp658OpF7yE"; // "your secret key here";
	private final String CALLBACKURL = "T4JOAuth://main";  //Callback URL that tells the WebView to load this activity when it finishes with twitter.com. (see manifest)


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);		
		Log.i("SmartActivity", "Login");
		intent = getIntent();
		tagConnector = new TagConnector();
		
		tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		
		//tempTag= bunde.getParcelable("tagFromIntent");
		mText=(TextView)findViewById(R.id.tagInformation);
		//if (tempTag!=null)
			//mText.setText("Your message is "+ tempTag.describeContents());

		 mText.setText("Scan a tag");
		
		
		 
		/* intent dispatch */
		if (tagFromIntent!=null)
		{
			String tagID = tagConnector.readTag(intent);
			if (tagID !=null)
			{
				LoginGetPosterTask task = new LoginGetPosterTask();
				task.execute(new String[] { tagID });
			}
		}
		
		Button logIn;
		Button sendTweet;
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
		
		sendTweet = (Button)findViewById(R.id.tweet);
		sendTweet.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v)
			{
				if (requestToken==null||verifier==null||accessToken==null)
				{
					EditText tweetTextBox=(EditText) findViewById(R.id.tweetText);
					tweetText=tweetTextBox.getText().toString();
					OAuthLogin();
				}
				else
				{
					sendTweet();
					
				}
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
	
	
/*	void resolveIntent(Intent intent) {
		   
		
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
	}*/
	void OAuthLogin() {
		String temp= "You have to sign in before sending a tweet.";
		Toast.makeText(this,temp, Toast.LENGTH_LONG).show();
		try {
			twitter = new TwitterFactory().getInstance();
			twitter.setOAuthConsumer(consumerKey, consumerSecret);
			requestToken = twitter.getOAuthRequestToken(CALLBACKURL);
			String authUrl = requestToken.getAuthenticationURL();
			Intent newIntent=new Intent(Intent.ACTION_VIEW, Uri
					.parse(authUrl));

			this.startActivity(newIntent);
		} catch (TwitterException ex) {
			Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
			Log.e("in Main.OAuthLogin", ex.getMessage());
		}
	}
	  @Override
	    public void onResume() {
	        super.onResume();
	        Log.i("Resume", "Resume login");
	        mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechLists);
	        if (twitter==null)
	        	Log.i("error","empty twitter");
	        Intent newIntent=getIntent();
	        verifier=newIntent.getStringExtra("oauth_verifier");
	        if (verifier!=null)
	         try {
			//	verifier = uri.getQueryParameter("oauth_verifier");
				accessToken = twitter.getOAuthAccessToken(requestToken,verifier);
				String token = accessToken.getToken(), secret = accessToken
						.getTokenSecret();
				displayTimeLine(token, secret); //after everything, display the first tweet 
				EditText tweetTextBox=(EditText) findViewById(R.id.tweetText);
				tweetTextBox.setText(tweetText);
			} catch (TwitterException ex) {
				Log.e("Main.onNewIntent", "" + ex.getMessage());
			}
	    }

	   @Override
	    public void onNewIntent(Intent intent) {
		   Log.i("Foreground dispatch", "Discovered tag with intent: " + intent);
	       //mText.setText("Discovered tag " + ++mCount + " with intent: " + intent);
	       String tagID = tagConnector.readTag(intent);
	       if (tagID != null)
	       {
	    /*   Log.i("Qilin", "tagID is null");
	    	   Intent newIntent = new Intent(this, login.class);
	    	   startActivity(newIntent);
	       }*/
	    	   LoginGetPosterTask task = new LoginGetPosterTask();
	    	   task.execute(new String[] { tagID });
	       }
	    }
	   @SuppressWarnings("deprecation")
		void displayTimeLine(String token, String secret) {
			if (null != token && null != secret) {
				List<Status> statuses = null;
				try {
					twitter.setOAuthAccessToken(token, secret);
					statuses = twitter.getFriendsTimeline();
					String name =twitter.getScreenName()+" has signed in.";
					
					Toast.makeText(this, name, Toast.LENGTH_LONG)
						.show();
				} catch (Exception ex) {
					Toast.makeText(this, "Error:" + ex.getMessage(),
							Toast.LENGTH_LONG).show();
					Log.d("Main.displayTimeline",""+ex.getMessage());
				}
				
			} else {
				Toast.makeText(this, "Not Verified", Toast.LENGTH_LONG).show();
			}
		}
	   
	   void sendTweet(){
		   EditText tweetTextBox=(EditText) findViewById(R.id.tweetText);
			
		   tweetText=tweetTextBox.getText().toString();
		   if (tweetText==null|| tweetText.length()==0)
		   {
			   Toast.makeText(this,"Can't send empty tweet",Toast.LENGTH_LONG).show();
			   return;
		   }
		   
		   try {
			   
			  
			  
			twitter.updateStatus(tweetText);
			  Toast.makeText(this,"Tweet is sent.",Toast.LENGTH_LONG).show();
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			 Toast.makeText(this,"Sending fails. Please try again. Error Message: "+e.toString(),Toast.LENGTH_LONG).show();
		}
		   
		   
	   }
	    @Override
	    public void onPause() {
	        super.onPause();
	        mAdapter.disableForegroundDispatch(this);
	    }
	    
	    private class LoginGetPosterTask extends GetPosterTask {
		    protected void onPostExecute(Poster poster) {   
				super.onPostExecute(poster);
				if (nspe != null) {
					AlertDialog alertDialog = new AlertDialog.Builder(login.this).create();  
					  alertDialog.setTitle("Invalid Poster");  
					  alertDialog.setButton("OK", new DialogInterface.OnClickListener() {  
						  public void onClick(DialogInterface dialog, int which) {  
						  } });
					  alertDialog.show();
				} else if (rpe != null) {
					AlertDialog alertDialog = new AlertDialog.Builder(login.this).create();  
					  alertDialog.setTitle("Disabled Poster");
					  alertDialog.setButton("OK", new DialogInterface.OnClickListener() {  
						  public void onClick(DialogInterface dialog, int which) {  
						  } });
					  alertDialog.show();
				}
				if (poster instanceof LinkPoster) {
					LinkPoster lp = (LinkPoster) poster;
			    	   Intent newIntent = new Intent(login.this, LinkPosterActivity.class);
			    	   Log.i("Hello", String.valueOf(lp.getDescription()));
			    	   newIntent.putExtra("LinkPoster", lp);
			    	   startActivity(newIntent);
				} else if (poster instanceof PollPoster) {
					Intent newIntent = new Intent(login.this, PollPosterActivity.class);
			    	   PollPoster pp = (PollPoster) poster;
			    	   newIntent.putExtra("PollPoster", pp);
			    	   startActivity(newIntent);
				}
		    }
	    }
	    
	  
}