package com.android.Smart;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.Smart.poster.LinkPoster;
import com.android.Smart.poster.Poster.AlreadyVotedException;
import com.android.Smart.poster.Poster.NoSuchPosterException;
import com.android.Smart.poster.Poster.RevokedPosterException;

public class LinkPosterActivity extends SPANActivity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.link_poster);
		
		final LinkPoster poster = (LinkPoster) getIntent().getSerializableExtra("LinkPoster");
		
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
				try {
					submitVote(poster.getTagID());
					AlertDialog alertDialog = new AlertDialog.Builder(LinkPosterActivity.this).create();  
					  alertDialog.setTitle("Thanks! You've 'liked' this poster.");  
					  alertDialog.setButton("OK", new DialogInterface.OnClickListener() {  
						  public void onClick(DialogInterface dialog, int which) {  
						  } });
					  alertDialog.show();
				} catch (AlreadyVotedException e) {
					AlertDialog alertDialog = new AlertDialog.Builder(LinkPosterActivity.this).create();  
					  alertDialog.setTitle("You've already 'liked' this poster.");  
					  alertDialog.setButton("OK", new DialogInterface.OnClickListener() {  
						  public void onClick(DialogInterface dialog, int which) {  
						  } });
					  alertDialog.show();
				} catch (NoSuchPosterException e) {
					AlertDialog alertDialog = new AlertDialog.Builder(LinkPosterActivity.this).create();  
					  alertDialog.setTitle("Invalid Poster");  
					  alertDialog.setButton("OK", new DialogInterface.OnClickListener() {  
						  public void onClick(DialogInterface dialog, int which) {  
						  } });
					  alertDialog.show();
				} catch (RevokedPosterException e) {
					AlertDialog alertDialog = new AlertDialog.Builder(LinkPosterActivity.this).create();  
					  alertDialog.setTitle("Disabled Poster");
					  alertDialog.setButton("OK", new DialogInterface.OnClickListener() {  
						  public void onClick(DialogInterface dialog, int which) {  
						  } });
					  alertDialog.show();
				}  
		    }});
	}
}
