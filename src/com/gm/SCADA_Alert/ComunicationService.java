package com.gm.SCADA_Alert;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.Toast;

public class ComunicationService extends Service{
	
	public static final String ALARM_RECEIVED = "com.gm.intent.action.NEW_ALARM_MESSAGE_RECIEVED";
	public static final String ACTION_FIN = "com.gm.intent.action.FIN";
	
	private NotificationManager nm;
	private ConnectivityManager connManager;
	private WifiManager wifimanager;
	//private static final int ID_NOTIFICACION_CREAR = 1;
	private int ID_Nofication = 1;
	ServerSocket skServer;
	private ServerSocket serverSocket;
	Socket skClient;
	//private String IP_client; 
	private String Wifi_IP;
	int PORT = 1987; //El puerto debe ser >= 1234 que son los reservados para el sistema o dara error de acceso denegado
	//private static String Timestamp;
	private Thread serverThread;
	private WifiLock wifiLock;
    //private WakeLock wakeLock;

	


    //MediaPlayer player;
     
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
     
    @Override
    public void onCreate() {
        Toast.makeText(this, "Service Created...", Toast.LENGTH_LONG).show();
        LogActivity.Log(3, "ComunicationService.onCreate: Service Created");
         
        //player = MediaPlayer.create(this, R.raw.hyt);
        //player.setLooping(false); // Set looping
        
        startServer();
    }
    

 
    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service Stopped..", Toast.LENGTH_LONG).show();
        LogActivity.Log(3, "ComunicationService.onDestroy: Service Stopped");
        
        if (wifimanager.isWifiEnabled()) 
    	{
        	wifiLock.release();
    	}
         
        //player.stop();
        
        if (serverSocket != null) {
            try {
                serverSocket.close();
                stopServer();
                //ShowNotification("TCP: Closed server socket");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
     
    @Override //Aqui podemos recibir los parametros enviados al llamar al servicio
    public void onStart(Intent intent, int startid) {
    	//int iter = intent.getIntExtra("iteraciones", 0);
    	
    	//ShowToast(Integer.toString(iter));
        //Toast.makeText(this, "Service Started...", Toast.LENGTH_LONG).show();
        //LogActivity.Log(3, "ComunicationService.onStart: Service Started");
         
        //player.start();
    }
    
    private Runnable thread = new Runnable() {

        public synchronized void run() {
            try {

                serverSocket = new ServerSocket(PORT);
                LogActivity.Log(3, "ComunicationService.ServerSocket: Listen on " + serverSocket.getLocalSocketAddress()); // IP_Local); //serverSocket.getLocalSocketAddress().toString());

                boolean RetryConnection = true;
                while (RetryConnection) {

                    Socket client = serverSocket.accept();
                    LogActivity.Log(3, "ComunicationService.client: connexion from " + client.getInetAddress());
                    
                    BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                                       
                    String sBuffer = in.readLine();
                    
                    //Comunicamos a la actividad principal la alarma recibida
        			/*Intent bcIntent = new Intent();
        			bcIntent.setAction(ALARM_RECEIVED);
        			bcIntent.putExtra("AlarmMessage", sBuffer);
        			sendBroadcast(bcIntent);
                    */
                    ShowNotification(sBuffer);
                    
                    AlarmActivity.SaveAlarm(sBuffer);
                    
                    client.close();
                }
                    

            } catch (IOException e) {
            	LogActivity.Log(1, "ComunicationService.ServerSocket: " + e.getMessage());
            }

        }

    };
    
    private synchronized void startServer() {
    	   	
    	connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
    	
    	//For 3G check
    	boolean is3g = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();
    	
    	//For wifi check
    	boolean isWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
    			
    	if (isWifi)
    	{
    		wifimanager = (WifiManager) getSystemService(WIFI_SERVICE);
    		Wifi_IP = Formatter.formatIpAddress(wifimanager.getConnectionInfo().getIpAddress());
    		LogActivity.Log(3, "Connected under WIFI connexion " + Wifi_IP);
    		wifiLock = wifimanager.createWifiLock(WifiManager.WIFI_MODE_FULL , "@string/app_name" + " WifiLock");
    		wifiLock.acquire();
    	}
    	else if (is3g)
    	{
    		LogActivity.Log(2, "Connected over 3G");
    	} 
    	else
    	{
    		LogActivity.Log(1, "No connection detected");
    	}

    	//Creamos el hilo
        if (serverThread == null) {
            serverThread = new Thread(thread);
            serverThread.start();
        }
    }
    
    private synchronized void stopServer() {
        if(serverThread!=null){
            Thread t=serverThread;
            serverThread=null;
            t.interrupt();
        }
    }
        
   
    public void ShowToast(String sMessage){
    	Toast.makeText(this, sMessage, Toast.LENGTH_LONG).show();
    }
    
    public void ShowNotification(String sMessage) {
    	nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    	
    	Notification notificacion = new Notification(
                R.drawable.ic_alert,
                getString(R.string.Alert),
                System.currentTimeMillis());
    	
    	 Intent notificationIntent;
         notificationIntent = new Intent(this, AlarmActivity.class);
         notificationIntent.putExtra("sAlarmID", sMessage);
         notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

         PendingIntent pending = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    	
    	
    	//PendingIntent intencionPendiente = PendingIntent.getActivity(this, 0, new Intent(getApplicationContext(), AlarmActivity.class), 0);
    	//Intent notificationIntent = new Intent(getApplicationContext(),  AlarmActivity.class);
    	//PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
    	//Intent contentIntent = new Intent(this, AlarmActivity.class);
    	//contentIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
    	notificacion.setLatestEventInfo(getApplicationContext(), getString(R.string.app_name),	sMessage, pending);
    	
    	notificacion.defaults |= Notification.DEFAULT_SOUND;
    	//notificacion.sound = Uri.parse("file:///sdcard/carpeta/tono.mp3");
    	notificacion.defaults |= Notification.DEFAULT_VIBRATE;
    	notificacion.ledARGB = 0xff00ff00;
    	notificacion.ledOnMS = 300;
    	notificacion.ledOffMS = 1000;
    	notificacion.flags |= Notification.FLAG_SHOW_LIGHTS;
    	notificacion.flags |= Notification.FLAG_ONLY_ALERT_ONCE;
    	notificacion.flags |= Notification.FLAG_AUTO_CANCEL;
    	//notification.flags |= Notification.FLAG_INSISTENT; //Esta me sera muy util para la aplicacion de alert real
    	
    	ID_Nofication++;
    	
    	nm.notify(ID_Nofication, notificacion);
    }


}
