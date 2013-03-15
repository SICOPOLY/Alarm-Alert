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

public class LogActivity extends Activity {
	
	public static ArrayList<LogMessage> LogList = new ArrayList<LogMessage>(); 
	
	private EditText filterText = null;
	private File sd_path;// = Environment.getExternalStorageDirectory();
	FilteredArrayAdapter adapter = null;
 
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
 
		setContentView(R.layout.log_layout);
		
		filterText = (EditText) findViewById(R.id.FilterText);
		filterText.addTextChangedListener(filterTextWatcher);
		
		ListView lstOpciones = (ListView)findViewById(R.id.loglistView);
		
		lstOpciones.setTextFilterEnabled(true);
		 
		
		sd_path = new File(getExternalCacheDir(), "/" + getString(R.string.LogFileName));
		if (LogList.isEmpty()) {
			if (sd_path.exists()) {
				LogList.addAll((ArrayList<LogMessage>)loadClassFile(sd_path));
				Log.i("LogActivity", "File " + sd_path.getAbsolutePath() + " exists");
				Log(3, "File " + sd_path.getAbsolutePath() + " read successfully");
			} else {
				Log.w("LogActivity", "File " + sd_path.getAbsolutePath() + " does not exists");
				Log(2, "File " + sd_path.getAbsolutePath() + " does not exists");
			}
		}
		
		adapter = new FilteredArrayAdapter (this, 0, LogList);
		
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
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.log_menu, menu);
	    return true;
	    };
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    		case R.id.FilterLog_All:
    			if (adapter != null) {
    				adapter.getFilter().filter("");
    			}
    			return true;
    		case R.id.FilterLog_Informations:
    			if (adapter != null) {
    				adapter.getFilter().filter("Information");
    			}
    			return true;
    		case R.id.FilterLog_Warnings:
    			if (adapter != null) {
    				adapter.getFilter().filter("Warnnings");
    			}
    			return true;
    		case R.id.FilterLog_Errors:
    			if (adapter != null) {
    				adapter.getFilter().filter("Errors");
    			}
    			return true;
    		case R.id.ClearLog:
    			LogList.clear();
    			adapter.notifyDataSetChanged();
    			return true;
    		default:
    			return super.onOptionsItemSelected(item);
    	}
    }
    
    
    public Object loadClassFile(File f)
    {
    	String estado = Environment.getExternalStorageState();
    	if ((estado.equals(Environment.MEDIA_MOUNTED)) || (estado.equals(Environment.MEDIA_MOUNTED_READ_ONLY))) {
	        try
	        {
	            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
	            Object o = ois.readObject();
	            Log.i("LogActivity", "File readed on: " + f.getAbsolutePath());
	            return o;
	        }
	        catch(Exception ex)
	        {
	        	Log.e("LogActivity",ex.getMessage());
	    	    Log(1, ex.getMessage());
	    	    ex.printStackTrace();
	        }
    	} else {
    		Log.e("Address Book","External storage is not ready");
    		Log(1, "External storage is not ready");
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
	    	    Log.i("LogActivity", "File saved on: " + f.getAbsolutePath());
	    	}
	    	catch(Exception ex)
	    	{
	    	    Log.e("LogActivity",ex.getMessage());
	    	    Log(1, ex.getMessage());
	    	    ex.printStackTrace();
	    	}
    	} else {
    		Log.e("LogActivity","External storage is not ready");
    		Log(1, "External storage is not ready");
    	}
    }

	
    //Type 1 = Errors
    //Type 2 = Warnings
    //Type 3 = Informations
    public static void Log(Integer iType, String sMessage) {
    	LogList.add(new LogMessage(iType, sMessage));	
    }
    
    
    @Override
	protected void onDestroy() {
    	super.onDestroy();
    	
    	saveClassFile(sd_path, LogList);
	}
    
}
