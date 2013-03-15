package com.gm.SCADA_Alert;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;


public class AlarmActivity extends Activity {
	
	public static ArrayList<AlarmMessage> AlarmList = new ArrayList<AlarmMessage>(); 
	
	private EditText filterText = null;
	private File sd_path; // = Environment.getExternalStorageDirectory();
	FilteredAlarmArrayAdapter adapter = null;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*
		String sAlarmID = savedInstanceState.getString("sAlarmID");
		String sAlarmID = getIntent().getStringExtra("sAlarmID");
		
		if (sAlarmID.length() > 0) {
			Toast.makeText(AlarmActivity.this, sAlarmID, Toast.LENGTH_SHORT).show();
		}
		*/
		
		setContentView(R.layout.alarm_layout);
		
		filterText = (EditText) findViewById(R.id.AlarmFilterText);
		filterText.addTextChangedListener(filterTextWatcher);
		
		ListView lstOpciones = (ListView)findViewById(R.id.AlarmlistView);
		
		lstOpciones.setTextFilterEnabled(true);
		 
		sd_path = new File(getExternalCacheDir(), "/" + getString(R.string.AlarmFileName));
		if (AlarmList.isEmpty()) {
			if (sd_path.exists()) {
				AlarmList.addAll((ArrayList<AlarmMessage>)loadClassFile(sd_path));
				Log.i("AlarmActivity", "File " + sd_path.getAbsolutePath() + " exists");
				LogActivity.Log(3, "File " + sd_path.getAbsolutePath() + " read successfully");
			} else {
				Log.w("AlarmActivity", "File " + sd_path.getAbsolutePath() + " does not exists");
				LogActivity.Log(2, "File " + sd_path.getAbsolutePath() + " does not exists");
			}
		}
		
		adapter = new FilteredAlarmArrayAdapter (this, 0, AlarmList);
		
		lstOpciones.setAdapter(adapter);
		lstOpciones.requestFocus(); //Para evitar que salga el teclado de android por que el edittext coge el focus
		
	}
	
	/**
	 * Filter for filtering items in the list.
	 */
	private TextWatcher filterTextWatcher = new TextWatcher() {
 
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
 
		}
 
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
 
		}
 
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if (adapter != null) {
				adapter.getFilter().filter(s);
			} else {
				Log.d("filter", "no filter availible");
			}
		}
	};
	
	
	public static void SaveAlarm(String sMessage) {
    	AlarmList.add(new AlarmMessage(sMessage));	
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.alarm_activity_menu, menu);
	    return true;
	    };
	    
	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	    	switch (item.getItemId()) {
	    		case R.id.ClearLog:
	    			AlarmList.clear();
	    			adapter.notifyDataSetChanged();
	    			return true;
	    		default:
	    			return super.onOptionsItemSelected(item);
	    	}
	    }
	
    @Override
	protected void onDestroy() {
    	super.onDestroy();
    	
    	saveClassFile(sd_path, AlarmList);
	}
	
	public Object loadClassFile(File f)
    {
    	String estado = Environment.getExternalStorageState();
    	if ((estado.equals(Environment.MEDIA_MOUNTED)) || (estado.equals(Environment.MEDIA_MOUNTED_READ_ONLY))) {
	        try
	        {
	            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
	            Object o = ois.readObject();
	            Log.i("AlarmActivity", "File readed on: " + f.getAbsolutePath());
	            return o;
	        }
	        catch(Exception ex)
	        {
	        	Log.e("AlarmActivity",ex.getMessage());
	        	LogActivity.Log(1, ex.getMessage());
	    	    ex.printStackTrace();
	        }
    	} else {
    		Log.e("Address Book","External storage is not ready");
    		LogActivity.Log(1, "External storage is not ready");
    	}
    	return null;
    }
    
    
    public void saveClassFile(File f, Object oObject) {
    	String estado = Environment.getExternalStorageState();
    	if (estado.equals(Environment.MEDIA_MOUNTED)) {    	
	    	try	{
	    		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f)); //Select where you wish to save the file...
	    	    oos.writeObject(oObject); // write the class as an 'object'
	    	    oos.flush(); // flush the stream to insure all of the information was written to 'save.bin'
	    	    oos.close();// close the stream
	    	    Log.i("AlarmActivity", "File saved on: " + f.getAbsolutePath());
	    	}
	    	catch(Exception ex)
	    	{
	    	    Log.e("AlarmActivity",ex.getMessage());
	    	    LogActivity.Log(1, ex.getMessage());
	    	    ex.printStackTrace();
	    	}
    	} else {
    		Log.e("AlarmActivity","External storage is not ready");
    		LogActivity.Log(1, "External storage is not ready");
    	}
    }

}
