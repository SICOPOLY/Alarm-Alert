package com.gm.sicoalert;

import android.app.Activity;



import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SicoAlertActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button closeButton;

        closeButton = (Button)this.findViewById(R.id.close);
        closeButton.setOnClickListener(new View.OnClickListener() 
        {
        	public void onClick(View v) 
        	{
				// TODO Auto-generated method stub
        		finish();
			}
        });
        
    }
}