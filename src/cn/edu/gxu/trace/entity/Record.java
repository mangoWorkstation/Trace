package cn.edu.gxu.trace.entity;

import java.lang.reflect.Field;
import java.util.HashMap;

public class Record implements UniversalEntity {
	
	private String uid;
	private String sensorSIM;
	private String archive_id;
	private String timestamp;
	private double temp_air;
	private double temp_soil;
	private double humidity_air;
	private double humidity_soil;
	private double lux;
//	private double batteryLevel;
//	private int signalLevel;
	
	public Record() {
		// TODO Auto-generated constructor stub
	}
	public String getUid() {
		return uid;
	}


	public String getArchive_id() {
		return archive_id;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public double getTemp_air() {
		return temp_air;
	}

	public double getTemp_soil() {
		return temp_soil;
	}

	public double getHumidity_air() {
		return humidity_air;
	}

	public double getHumidity_soil() {
		return humidity_soil;
	}

	public double getLux() {
		return lux;
	}

//	public double getBatteryLevel() {
//		return batteryLevel;
//	}
//
//	public int getSignalLevel() {
//		return signalLevel;
//	}

	public void setUid(String uid) {
		this.uid = uid;
	}


	public void setArchive_id(String archive_id) {
		this.archive_id = archive_id;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public void setTemp_air(double temp_air) {
		this.temp_air = temp_air;
	}

	public void setTemp_soil(double temp_soil) {
		this.temp_soil = temp_soil;
	}

	public void setHumidity_air(double humidity_air) {
		this.humidity_air = humidity_air;
	}

	public void setHumidity_soil(double humidity_soil) {
		this.humidity_soil = humidity_soil;
	}

	public void setLux(double lux) {
		this.lux = lux;
	}

//	public void setBatteryLevel(double batteryLevel) {
//		this.batteryLevel = batteryLevel;
//	}
//
//	public void setSignalLevel(int signalLevel) {
//		this.signalLevel = signalLevel;
//	}

	@Override
	public String toString() {
		String str = "["+this.getClass().getName()+"] ";
		Field[] fields = this.getClass().getDeclaredFields();
		for(Field f:fields) {
			try {
				str += f.getName() + " = " + String.valueOf(f.get(this))+";";
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return str;
	}
	
	@Override
	public HashMap<String, String> toHashMap() {
		HashMap<String, String> eHashMap = new HashMap<>();
		Field[] fields = this.getClass().getDeclaredFields();
		for(Field f:fields) {
			try {
				eHashMap.put(f.getName(),String.valueOf(f.get(this)));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return eHashMap;
	}
	public String getSensorSIM() {
		return sensorSIM;
	}
	public void setSensorSIM(String sensorSIM) {
		this.sensorSIM = sensorSIM;
	}

}
