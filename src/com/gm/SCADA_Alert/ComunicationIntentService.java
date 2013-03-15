package com.gm.SCADA_Alert;

import android.app.IntentService;
import android.content.Intent;

public class ComunicationIntentService extends IntentService {
	
	//Constructor
	public ComunicationIntentService() {
        super("ComunicationIntentService");
    }
	
	@Override
	protected void onHandleIntent(Intent intent) 
	{
		int iter = intent.getIntExtra("iteraciones", 0);
		
		for(int i=1; i<=iter; i++) {
			tareaLarga();
			
			//Comunicamos el progreso
			Intent bcIntent = new Intent();
			//bcIntent.setAction(ALARM_RECEIVED);
			bcIntent.putExtra("AlarmMessage", "Conveyor: " + Integer.toString(i));
			sendBroadcast(bcIntent);
		}
		
		Intent bcIntent = new Intent();
		//bcIntent.setAction(ACTION_FIN);
		sendBroadcast(bcIntent);
	}
	
	private void tareaLarga()
    {
    	try { 
    		Thread.sleep(1000); 
    	} catch(InterruptedException e) {}
    }

}
