package com.android.Smart;

import android.app.Activity;
import android.os.Bundle;
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
		Button button9 = (Button)findViewById(R.id.option9_button);
		Button[] buttons = new Button[] { button1, button2, button3, button4, button5, button6,
				button7, button8, button9 };
		
		int i = 0;
		for (PollOption option : poster.getOptions()) {
			buttons[i++].setText(option.getText());
		}
		for (; i < 9; i++) { // only 9 buttons!
			buttons[i].setEnabled(false);
			buttons[i].setVisibility(4);
		}
	}

}
