package com.android.Smart;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.Smart.poster.PollOption;
import com.android.Smart.poster.PollPoster;

public class PollPosterActivity extends Activity {
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.poll_poster);
		
		final PollPoster poster = (PollPoster) getIntent().getSerializableExtra("PollPoster");
		
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
		for (PollOption option : poster.getOptions()) {
			buttons[i].setText(option.getText());
			buttons[i].setOnClickListener(new View.OnClickListener() {
	             public void onClick(View v) {
	            	 AlertDialog alertDialog = new AlertDialog.Builder(PollPosterActivity.this).create();  
					  alertDialog.setTitle("Thanks for your input!");
					  alertDialog.setButton("OK", new DialogInterface.OnClickListener() {  
						  public void onClick(DialogInterface dialog, int which) {  
							  Intent intent = new Intent(PollPosterActivity.this, login.class);
							  startActivity(intent);
							  PollPosterActivity.this.finish();
						  } });
					  alertDialog.show();
	             }
	         });
			i++;
		}
		for (; i < 8; i++) { // only 8 buttons!
			buttons[i].setEnabled(false);
			buttons[i].setVisibility(4);
		}
	}

}
