package com.android.Smart;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.Smart.poster.PollOption;
import com.android.Smart.poster.PollOption.InvalidOptionException;
import com.android.Smart.poster.PollPoster;
import com.android.Smart.poster.Poster;
import com.android.Smart.poster.Poster.AlreadyVotedException;
import com.android.Smart.poster.Poster.NoSuchPosterException;
import com.android.Smart.poster.Poster.RevokedPosterException;

public class PollPosterActivity extends SPANActivity {
	
	private PollPoster poster = null;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.poll_poster);
		
		poster = (PollPoster) getIntent().getSerializableExtra("PollPoster");
		
		HashMap<Button, PollOption> buttonsToPollOptions = new HashMap<Button, PollOption>();
		
		TextView questionText = (TextView)findViewById(R.id.question_text);
		questionText.setText(poster.getQuestion());
		
		Button button1 = (Button)findViewById(R.id.option1_button);
		Button button2 = (Button)findViewById(R.id.option2_button);
		Button button3 = (Button)findViewById(R.id.option3_button);
		Button button4 = (Button)findViewById(R.id.option4_button);
		Button button5 = (Button)findViewById(R.id.option5_button);
		Button button6 = (Button)findViewById(R.id.option6_button);
		Button button7 = (Button)findViewById(R.id.option7_button);
		Button button8 = (Button)findViewById(R.id.option8_button);
		Button[] buttons = new Button[] { button1, button2, button3, button4, button5, button6,
				button7, button8 };
		
		int i = 0;
		for (final PollOption option : poster.getOptions()) {
			Button currentButton = buttons[i];
			buttonsToPollOptions.put(currentButton, option);
			currentButton.setText(option.getText());
			currentButton.setOnClickListener(new View.OnClickListener() {
	             public void onClick(View v) {
	            	 SubmitVoteTask task = new SubmitVoteTask();
	            	 task.execute(new PollOption[] { option } );
	             }
	         });
			i++;
		}
		for (; i < 8; i++) { // only 8 buttons!
			buttons[i].setEnabled(false);
			buttons[i].setVisibility(4);
		}
	}
	
	protected class SubmitVoteTask extends AsyncTask<PollOption, Void, Boolean> {
		
		private ProgressDialog progressDialog;
		protected NoSuchPosterException nspe;
		protected RevokedPosterException rpe;
		protected AlreadyVotedException ave;
		protected InvalidOptionException ioe;
		
	    @Override
	    protected void onPreExecute() {
	    	progressDialog = ProgressDialog.show(PollPosterActivity.this, "", "Submitting Vote...");
	    	progressDialog.show();
	    }

		@Override
		protected Boolean doInBackground(PollOption... options) {
			for (PollOption option : options) {
				try {
					return submitVoteHelper(option);
				} catch (NoSuchPosterException e) {
					nspe = e;
				} catch (RevokedPosterException e) {
					rpe = e;
				} catch (AlreadyVotedException e) {
					ave = e;
				} catch (InvalidOptionException e) {
					ioe = e;
				}
			}
			return false;
		}
		
		@Override
	    protected void onPostExecute(Boolean wasSuccessful) {
			progressDialog.dismiss();
			if (wasSuccessful) {
				AlertDialog alertDialog = new AlertDialog.Builder(PollPosterActivity.this).create();  
				alertDialog.setTitle("Thanks for your input!");
				alertDialog.setButton("OK", new DialogInterface.OnClickListener() {  
					public void onClick(DialogInterface dialog, int which) {  
						//Intent intent = new Intent(PollPosterActivity.this, login.class);
						//startActivity(intent);
						PollPosterActivity.this.finish();
					} });
				alertDialog.show();
			} else if (ioe != null) {
				AlertDialog alertDialog = new AlertDialog.Builder(PollPosterActivity.this).create();  
				  alertDialog.setTitle("Invalid option for this poster.");  
				  alertDialog.setButton("OK", new DialogInterface.OnClickListener() {  
					  public void onClick(DialogInterface dialog, int which) {  
					  } });
				  alertDialog.show();
			} else if (ave != null) {
				AlertDialog alertDialog = new AlertDialog.Builder(PollPosterActivity.this).create();  
				  alertDialog.setTitle("You've already voted in this poll.");  
				  alertDialog.setButton("OK", new DialogInterface.OnClickListener() {  
					  public void onClick(DialogInterface dialog, int which) {  
					  } });
				  alertDialog.show();
			} else if (nspe != null) {
				AlertDialog alertDialog = new AlertDialog.Builder(PollPosterActivity.this).create();  
				  alertDialog.setTitle("Invalid Poster");  
				  alertDialog.setButton("OK", new DialogInterface.OnClickListener() {  
					  public void onClick(DialogInterface dialog, int which) {  
					  } });
				  alertDialog.show();
			} else {
				AlertDialog alertDialog = new AlertDialog.Builder(PollPosterActivity.this).create();  
				  alertDialog.setTitle("Disabled Poster");
				  alertDialog.setButton("OK", new DialogInterface.OnClickListener() {  
					  public void onClick(DialogInterface dialog, int which) {  
					  } });
				  alertDialog.show();
			}
	    }
	}
	
	protected boolean submitVoteHelper(PollOption option) throws Poster.AlreadyVotedException, 
		NoSuchPosterException, RevokedPosterException, InvalidOptionException {
		try {
			URL url = new URL(serverURL + "poll_vote.php?id=" + poster.getTagID()
					+ "&option=" + option.getOptionId());
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setInstanceFollowRedirects(true);
			urlConnection.connect();
			InputStream in = new BufferedInputStream(urlConnection.getInputStream());
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();
			VoteResponseHandler handler = new VoteResponseHandler();
			xr.setContentHandler(handler);
			xr.parse(new InputSource(in));
			Log.i("submitVoteHelper()", String.valueOf(handler.getErrorCode()));
			if (handler.getErrorCode() == 0) {
				return true;
			} else if (handler.getErrorCode() == 1) {
				throw new Poster.NoSuchPosterException();
			} else if (handler.getErrorCode() == 2) {
				throw new Poster.RevokedPosterException();
			} else if (handler.getErrorCode() == 3) {
				throw new Poster.AlreadyVotedException();
			} else if (handler.getErrorCode() == 4) {
				throw new PollOption.InvalidOptionException();
			}
		} catch (IOException e) {
			Log.i("getPoster - IO", e.getMessage());
		} catch (SAXException e) {
			Log.i("getPoster - SAX", e.getMessage());
		} catch (ParserConfigurationException e) {
			Log.i("getPoster - ParserConfig", e.getMessage());
		}
		return false;
	}

}
