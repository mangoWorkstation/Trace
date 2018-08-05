package cn.edu.gxu.trace.entity;

import java.lang.reflect.Field;
import java.util.HashMap;

public class Sensor implements UniversalEntity {

	private String sim;
	private String base_id;
	private int state;
	private String lastReport_t;
	private String archive_id;
	private double now_temp_air;
	private double now_temp_soil;
	private double now_humidity_soil;
	private double now_humidity_air;
	private double now_lux;
	
	

	public String getSim() {
		return sim;
	}
	public void setSim(String sim) {
		this.sim = sim;
	}
	public int getState() {
		return state;
	}
	public String getLastReport_t() {
		return lastReport_t;
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

	public double getNow_temp_air() {
		return now_temp_air;
	}
	public double getNow_temp_soil() {
		return now_temp_soil;
	}
	public double getNow_humidity_soil() {
		return now_humidity_soil;
	}
	public double getNow_humidity_air() {
		return now_humidity_air;
	}
	public double getNow_lux() {
		return now_lux;
	}
	public void setNow_temp_air(double now_temp_air) {
		this.now_temp_air = now_temp_air;
	}
	public void setNow_temp_soil(double now_temp_soil) {
		this.now_temp_soil = now_temp_soil;
	}
	public void setNow_humidity_soil(double now_humidity_soil) {
		this.now_humidity_soil = now_humidity_soil;
	}
	public void setNow_humidity_air(double now_humidity_air) {
		this.now_humidity_air = now_humidity_air;
	}
	public void setNow_lux(double now_lux) {
		this.now_lux = now_lux;
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
