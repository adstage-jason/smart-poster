package com.android.SmartWriter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SmartWriterActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button textEntryButton;
        textEntryButton = (Button)findViewById(R.id.button1);
        textEntryButton.setOnClickListener(new Button.OnClickListener(){
     	   public void onClick(View v)
     	   {
     		   EditText textBox = (EditText)findViewById(R.id.tagIDEntry);
     		   String tagID = textBox.getText().toString();
     		   if (tagID.length() != 39) {
     			  AlertDialog alertDialog = new AlertDialog.Builder(SmartWriterActivity.this).create();  
     			  alertDialog.setTitle("Error");  
     			  alertDialog.setMessage("Please check the length of your tag ID.");  
     			  alertDialog.setButton("OK", new DialogInterface.OnClickListener() {  
     				  public void onClick(DialogInterface dialog, int which) {  
     					  return;  
     				  } });
     			  alertDialog.show();
     		   } else {
     			   Intent intent = new Intent(SmartWriterActivity.this, WriteTagActivity.class);
     			   intent.putExtra("Tag ID", tagID);
     			   startActivity(intent);
     		   }
 	       }

        });
    }
}