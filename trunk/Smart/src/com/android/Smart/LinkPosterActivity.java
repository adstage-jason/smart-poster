package com.android.Smart;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.Smart.poster.LinkPoster;

public class LinkPosterActivity extends Activity {

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
	}
}
