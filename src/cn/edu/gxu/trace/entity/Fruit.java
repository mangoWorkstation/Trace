package cn.edu.gxu.trace.entity;

import java.lang.reflect.Field;
import java.util.HashMap;

public class Fruit implements UniversalEntity {

	private int uid;
	private String category;
	private String onSale_t;
	private String offSale_t;
	private String content;
	
	
	public Fruit() {
		// TODO Auto-generated constructor stub
	}
	

	public int getUid() {
		return uid;
	}


	public void setUid(int uid) {
		this.uid = uid;
	}


	public String getCategory() {
		return category;
	}

	public String getOnSale_t() {
		return onSale_t;
	}

	public String getOffSale_t() {
		return offSale_t;
	}

	public String getContent() {
		return content;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setOnSale_t(String onSale_t) {
		this.onSale_t = onSale_t;
	}

	public void setOffSale_t(String offSale_t) {
		this.offSale_t = offSale_t;
	}

	public void setContent(String content) {
		this.content = content;
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
