package cn.edu.gxu.trace.entity;

import java.lang.reflect.Field;
import java.util.HashMap;

public class Base implements UniversalEntity {

	
	private String uid;
	private String name;
	private double area;
	private String standard;
	private String address;
	private String province;
	private String city;
	private String county;
	private int fruitType_id;
	private String videoStream_url;
	private String snapshot_filePath;
	private String owner_id;
	
	public Base() {
		// TODO Auto-generated constructor stub
	}
	
	public String getUid() {
		return uid;
	}

	public String getName() {
		return name;
	}

	public double getArea() {
		return area;
	}

	public String getStandard() {
		return standard;
	}

	public String getAddress() {
		return address;
	}

	public String getProvince() {
		return province;
	}

	public String getCity() {
		return city;
	}

	public String getCounty() {
		return county;
	}

	public int getFruitType_id() {
		return fruitType_id;
	}

	public String getVideoStream_url() {
		return videoStream_url;
	}

	public String getSnapshot_filePath() {
		return snapshot_filePath;
	}

	public String getOwner_id() {
		return owner_id;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setArea(double area) {
		this.area = area;
	}

	public void setStandard(String standard) {
		this.standard = standard;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setCounty(String county) {
		this.county = county;
	}

	public void setFruitType_id(int fruitType_id) {
		this.fruitType_id = fruitType_id;
	}

	public void setVideoStream_url(String videoStream_url) {
		this.videoStream_url = videoStream_url;
	}

	public void setSnapshot_filePath(String snapshot_filePath) {
		this.snapshot_filePath = snapshot_filePath;
	}

	public void setOwner_id(String owner_id) {
		this.owner_id = owner_id;
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
