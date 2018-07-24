package cn.edu.gxu.trace.entity;

import java.lang.reflect.Field;
import java.util.HashMap;

public class Sensor implements UniversalEntity {

	private String serial;
	private String base_id;
	private double batteryLevel;
	private int signaLevel;
	private int state;
	private String lastReport_t;
	private String archive_id;
	
	
	public String getSerial() {
		return serial;
	}
	
	public double getBatteryLevel() {
		return batteryLevel;
	}
	public int getSignaLevel() {
		return signaLevel;
	}
	public int getState() {
		return state;
	}
	public String getLastReport_t() {
		return lastReport_t;
	}
	
	public void setSerial(String serial) {
		this.serial = serial;
	}
	
	public void setBatteryLevel(double batteryLevel) {
		this.batteryLevel = batteryLevel;
	}
	public void setSignaLevel(int signaLevel) {
		this.signaLevel = signaLevel;
	}
	public void setState(int state) {
		this.state = state;
	}
	public void setLastReport_t(String lastReport_t) {
		this.lastReport_t = lastReport_t;
	}
	
	public String getBase_id() {
		return base_id;
	}

	public String getArchive_id() {
		return archive_id;
	}

	public void setBase_id(String base_id) {
		this.base_id = base_id;
	}

	public void setArchive_id(String archive_id) {
		this.archive_id = archive_id;
	}

	public Sensor() {
		// TODO Auto-generated constructor stub
	}
	
	
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
	

}
