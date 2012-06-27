package com.android.Smart;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.Smart.poster.LinkPoster;
import com.android.Smart.poster.Poster;
import com.android.Smart.poster.Poster.AlreadyCommentedException;
import com.android.Smart.poster.Poster.NoSuchPosterException;
import com.android.Smart.poster.Poster.RevokedPosterException;

public class LinkPosterActivity extends SPANActivity {
	
	private static final int MAX_COMMENT_LENGTH = 250;
	private LinkPoster poster;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.link_poster);
		
		poster = (LinkPoster) getIntent().getSerializableExtra("LinkPoster");
		
		TextView descriptionText = (TextView)findViewById(R.id.description_text);
		descriptionText.setText(poster.getDescription());
		
		TextView urlText = (TextView)findViewById(R.id.url_text);
		urlText.setText(poster.getURL().toString());
		
		Button browseButton = (Button)findViewById(R.id.button3);
		browseButton.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v)
			{
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(poster.getURL().toString()));
				startActivity(intent);	   
		    }});
		
		Button likeButton = (Button)findViewById(R.id.button5);
		likeButton.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v)
			{
				LinkPosterSubmitLikeTask task = new LinkPosterSubmitLikeTask();
		    	task.execute(new String[] { poster.getTagID() });
		    }});
		
		Button commentButton = (Button)findViewById(R.id.button7);
		commentButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				AlertDialog.Builder alert = new AlertDialog.Builder(LinkPosterActivity.this);                 
				alert.setTitle("Vox Populi");  
				alert.setMessage("Enter comment:");                

				// Set an EditText view to get user input   
				final EditText input = new EditText(LinkPosterActivity.this); 
				InputFilter[] filters = new InputFilter[1];
				filters[0] = new InputFilter.LengthFilter(MAX_COMMENT_LENGTH);
				input.setFilters(filters);
				alert.setView(input);

				alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {  
					public void onClick(DialogInterface dialog, int whichButton) {  
						String comment = input.getText().toString();
				        Log.i("LinkPoster", "Comment: " + comment);
				        SubmitCommentTask task = new SubmitCommentTask();
				        task.execute(new String[] { comment });               
				    }  
				});  

				alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						return;   
				    }
				});
				
				alert.show();
			}
		});
	}
	
	private class LinkPosterSubmitLikeTask extends SubmitLikeTask {
		@Override
	    protected void onPostExecute(Boolean wasSuccessful) {
			super.onPostExecute(wasSuccessful);
			if (wasSuccessful) {
				AlertDialog alertDialog = new AlertDialog.Builder(LinkPosterActivity.this).create();  
				  alertDialog.setTitle("Thanks! You've 'liked' this poster.");  
				  alertDialog.setButton("OK", new DialogInterface.OnClickListener() {  
					  public void onClick(DialogInterface dialog, int which) {  
					  } });
				  alertDialog.show();
			} else if (ave != null) {
				AlertDialog alertDialog = new AlertDialog.Builder(LinkPosterActivity.this).create();  
				  alertDialog.setTitle("You've already 'liked' this poster.");  
				  alertDialog.setButton("OK", new DialogInterface.OnClickListener() {  
					  public void onClick(DialogInterface dialog, int which) {  
					  } });
				  alertDialog.show();
			} else if (nspe != null) {
				AlertDialog alertDialog = new AlertDialog.Builder(LinkPosterActivity.this).create();  
				  alertDialog.setTitle("Invalid Poster");  
				  alertDialog.setButton("OK", new DialogInterface.OnClickListener() {  
					  public void onClick(DialogInterface dialog, int which) {  
					  } });
				  alertDialog.show();
			} else {
				AlertDialog alertDialog = new AlertDialog.Builder(LinkPosterActivity.this).create();  
				  alertDialog.setTitle("Disabled Poster");
				  alertDialog.setButton("OK", new DialogInterface.OnClickListener() {  
					  public void onClick(DialogInterface dialog, int which) {  
					  } });
				  alertDialog.show();
			}
	    }
	}
	
	protected class SubmitCommentTask extends AsyncTask<String, Void, Boolean> {
		
		private ProgressDialog progressDialog;
		protected NoSuchPosterException nspe;
		protected RevokedPosterException rpe;
		protected AlreadyCommentedException ace;
		
	    @Override
	    protected void onPreExecute() {
	    	progressDialog = ProgressDialog.show(LinkPosterActivity.this, "", "Submitting Comment...");
	    	progressDialog.show();
	    }

		@Override
		protected Boolean doInBackground(String... strings) {
			for (String comment : strings) {
				try {
					return submitCommentHelper(comment);
				} catch (NoSuchPosterException e) {
					nspe = e;
				} catch (RevokedPosterException e) {
					rpe = e;
				} catch (AlreadyCommentedException e) {
					ace = e;
				}
			}
			return false;
		}
		
		@Override
	    protected void onPostExecute(Boolean wasSuccessful) {
			progressDialog.dismiss();
			if (wasSuccessful) {
				AlertDialog alertDialog = new AlertDialog.Builder(LinkPosterActivity.this).create();  
				  alertDialog.setTitle("Thanks! Your comment was recorded.");  
				  alertDialog.setButton("OK", new DialogInterface.OnClickListener() {  
					  public void onClick(DialogInterface dialog, int which) {  
					  } });
				  alertDialog.show();
			} else if (ace != null) {
				AlertDialog alertDialog = new AlertDialog.Builder(LinkPosterActivity.this).create();  
				  alertDialog.setTitle("You've already commented on this poster.");  
				  alertDialog.setButton("OK", new DialogInterface.OnClickListener() {  
					  public void onClick(DialogInterface dialog, int which) {  
					  } });
				  alertDialog.show();
			} else if (nspe != null) {
				AlertDialog alertDialog = new AlertDialog.Builder(LinkPosterActivity.this).create();  
				  alertDialog.setTitle("Invalid Poster");  
				  alertDialog.setButton("OK", new DialogInterface.OnClickListener() {  
					  public void onClick(DialogInterface dialog, int which) {  
					  } });
				  alertDialog.show();
			} else {
				AlertDialog alertDialog = new AlertDialog.Builder(LinkPosterActivity.this).create();  
				  alertDialog.setTitle("Disabled Poster");
				  alertDialog.setButton("OK", new DialogInterface.OnClickListener() {  
					  public void onClick(DialogInterface dialog, int which) {  
					  } });
				  alertDialog.show();
			}
	    }
	}
	
	protected boolean submitCommentHelper(String comment) throws AlreadyCommentedException, 
		NoSuchPosterException, RevokedPosterException {
		try {
			URL url = new URL(serverURL + "comment.php?id=" + poster.getTagID() +
					"&comment=" + URLEncoder.encode(comment, "UTF-8"));
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
			Log.i("submitCommentHelper()", String.valueOf(handler.getErrorCode()));
			if (handler.getErrorCode() == 0) {
				return true;
			} else if (handler.getErrorCode() == 1) {
				throw new Poster.NoSuchPosterException();
			} else if (handler.getErrorCode() == 2) {
				throw new Poster.RevokedPosterException();
			} else if (handler.getErrorCode() == 5) {
				throw new Poster.AlreadyCommentedException();
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
