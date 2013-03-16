package com.gm.SCADA_Alert;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

public class SicoAlertActivity extends Activity {
	
	public static final int APP_ID_NOTIFICATION = 1;
	private int iIndex = 0;

	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
       
        //Para  recibir los mensajes del servicio
        IntentFilter filter = new IntentFilter();
        filter.addAction(ComunicationService.ALARM_RECEIVED);
        filter.addAction(ComunicationService.ACTION_FIN);
        MessagesReceiver rcv = new MessagesReceiver();
        registerReceiver(rcv, filter);
        
		
        
        
        
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
        
  
        
        
        Button StartServiceButton;
        
        StartServiceButton = (Button)this.findViewById(R.id.ActiveService);
        StartServiceButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//startService(new Intent(getApplicationContext(), ComunicationService.class));
				
				Intent msgIntent = new Intent(getApplicationContext(), ComunicationService.class);
				//msgIntent.putExtra("iteraciones", 100); //Con esto le podemos pasar parametros al servicio que los recibira en el onStart
				startService(msgIntent);
				
			}
        });
        
        Button StopServiceButton;
        
        StopServiceButton = (Button)this.findViewById(R.id.StopService);
        StopServiceButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				stopService(new Intent(getApplicationContext(), ComunicationService.class));
				
			}
        });
        
        Button AlarmHistoryButton;
     
        AlarmHistoryButton = (Button)this.findViewById(R.id.ShowAlarmHistory);
        AlarmHistoryButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(), AlarmActivity.class);
    			startActivity(intent);
			}
        });
        
        
        
        Button alertButton;
        
        alertButton = (Button)this.findViewById(R.id.button1);
        alertButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method
				
				iIndex++;
				//Toast.makeText(SicoAlertActivity.this, "Alarm recieved", Toast.LENGTH_LONG).show();
				 
			    //Prepara la notificacion
				NotificationManager notificador  = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			    Notification notification = new Notification(R.drawable.ic_alert, getString(R.string.app_name), System.currentTimeMillis());
			    Intent notificacionIntent = new Intent(getApplicationContext(), AlarmActivity.class); 
			    //notificacionIntent.putExtra("sAlarmID", "Conveyor " + iIndex + " stopped");
			    notificacionIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			    PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificacionIntent, 0);         
			    notification.setLatestEventInfo(getApplicationContext(), getString(R.string.app_name), "Conveyor " + iIndex + " stopped", contentIntent);
			    
			    
			    notification.defaults |= Notification.DEFAULT_SOUND;
			    // b. Custom sound from SD card
			    //notification.sound = Uri.parse("file:///sdcard/notification/ringer.mp3");


			    // ----------------------
			    //Add Vibration
			    // ----------------------
			    // a. Default vibration
			    notification.defaults |= Notification.DEFAULT_VIBRATE;
			    // b. Custom vibration
			    //long[] vibrate = {0,100,200,300};
			    //notification.vibrate = vibrate;
			    
			    // ------------------------
				//   Add Flashing Lights
				// ------------------------
				// a. Default lights
				//notification.defaults |= Notification.DEFAULT_LIGHTS;
	
 			 	 // b. Custom lights
				 notification.ledARGB = 0xff00ff00;
				 notification.ledOnMS = 300;
				 notification.ledOffMS = 1000;
				 notification.flags |= Notification.FLAG_SHOW_LIGHTS;
				 notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE;
			     notification.flags |= Notification.FLAG_AUTO_CANCEL;
			     //notification.flags |= Notification.FLAG_INSISTENT; //Esta me sera muy util para la aplicacion de alert real
			     
			     final CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox1);
		         if (checkBox.isChecked()) 
		         {
		        	 //Lanza la notificación
				     notificador.notify(iIndex, notification);
				     AlarmActivity.SaveAlarm("Conveyor " + iIndex + " stopped");
				     //LogActivity.Log((int) Math.floor(Math.random()*(3-1+1)+1), "Conveyor " + iIndex + " stopped");
		             //checkBox.setChecked(false);
		         }
		         else
		         {
		        	 //Lanza la notificación
				     notificador.notify(APP_ID_NOTIFICATION, notification);
				     AlarmActivity.SaveAlarm("Conveyor " + iIndex + " stopped");
				     //LogActivity.Log((int) Math.floor(Math.random()*(3-1+1)+1), "Conveyor " + iIndex + " stopped");
		         }	
			}
		});
        
    }
    
    
    public class MessagesReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(ComunicationService.ALARM_RECEIVED)) {
				String AlarmMessage = intent.getStringExtra ("AlarmMessage");
				AlarmActivity.SaveAlarm(AlarmMessage);
			}
			else if(intent.getAction().equals(ComunicationService.ACTION_FIN)) {
				Toast.makeText(SicoAlertActivity.this, "Tarea finalizada!", Toast.LENGTH_SHORT).show();
			}
		}
	}
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
        };
        
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    		case R.id.ActiveService:
    			startService(new Intent(getApplicationContext(), ComunicationService.class));
    			return true;
    		case R.id.StopService:
    			stopService(new Intent(getApplicationContext(), ComunicationService.class));
    			return true;
    		case R.id.Log:
    			Intent intent = new Intent(getApplicationContext(), LogActivity.class);
    			startActivity(intent);
    			return true;
    		case R.id.Options:
    			return true;
    		default:
    			return super.onOptionsItemSelected(item);
    	}
    }
              
        
    
}

