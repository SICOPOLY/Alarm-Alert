package com.gm.SCADA_Alert;

import android.annotation.SuppressLint;
import java.io.Serializable;
import java.text.SimpleDateFormat;


@SuppressWarnings("serial")
public class LogMessage implements Serializable{
	
	private String timestamp;
	private Integer type;
	private String message;
	
	
	
	@SuppressLint("SimpleDateFormat")
	public LogMessage(Integer iType, String sMessage) {
		super();
		java.util.Date date= new java.util.Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
		this.timestamp=dateFormat.format(date);
		this.setType(iType);
		this.setMessage(sMessage);
	}
	
	
	public Integer getType() {
		return type;
	}
	
	public String getypeStr() {
		if (this.type == 3) {
			return "Information";
		} else if (this.type == 2) {
			return "Warnnings";
		} else if (this.type == 1) {
			return "Errors";
		} else
		{
			return "All";
		}
	}

	public void setType(Integer type) {
		this.type = type;
	}
	
	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	//Es necesario este metodo si no, no funcionara el filtrado
	public String toString() {
		return this.message;
	}
	
}
