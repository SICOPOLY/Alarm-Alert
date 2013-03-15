package com.gm.SCADA_Alert;

import android.annotation.SuppressLint;
import java.io.Serializable;
import java.text.SimpleDateFormat;


@SuppressLint("SimpleDateFormat")
public class AlarmMessage implements Serializable{
	private static final long serialVersionUID = 1L;
	private String sAlarmMessage;
	private String timestamp;

	public AlarmMessage(String sAlarmMessage) {
		super();
		java.util.Date date= new java.util.Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
		this.timestamp = dateFormat.format(date);
		this.sAlarmMessage = sAlarmMessage;
	}	
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getMessage() {
		return sAlarmMessage;
	}
	public void setMessage(String message) {
		this.sAlarmMessage = message;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	
	//Es necesario este metodo si no, no funcionara el filtrado
	public String toString() {
		return this.sAlarmMessage;
	}
	

}
