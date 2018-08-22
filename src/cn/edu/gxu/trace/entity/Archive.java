package cn.edu.gxu.trace.entity;

import java.lang.reflect.Field;
import java.util.HashMap;

public class Archive implements UniversalEntity{
	
	private String uid;
	private int fruit_id;
	private String plant_t;
	private String plant_end_t;
	private String base_id;
	private float score;
	private float score_count;
	private float rate;
	
	public Archive() {	
	}
	
	public String getUid() {
		return uid;
	}

	public int getFruit_id() {
		return fruit_id;
	}

	public String getPlant_t() {
		return plant_t;
	}

	public String getPlant_end_t() {
		return plant_end_t;
	}

	public String getBase_id() {
		return base_id;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public void setFruit_id(int fruit_id) {
		this.fruit_id = fruit_id;
	}

	public void setPlant_t(String plant_t) {
		this.plant_t = plant_t;
	}

	public void setPlant_end_t(String plant_end_t) {
		this.plant_end_t = plant_end_t;
	}

	public void setBase_id(String base_id) {
		this.base_id = base_id;
	}

	public float getScore() {
		return score;
	}

	public float getScore_count() {
		return score_count;
	}

	public float getRate() {
		return rate;
	}

	public void setScore(float score) {
		this.score = score;
	}

	public void setScore_count(float score_count) {
		this.score_count = score_count;
	}

	public void setRate(float rate) {
		this.rate = rate;
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
