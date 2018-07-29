package cn.edu.gxu.trace.entity;

import java.lang.reflect.Field;
import java.util.HashMap;

public class User implements UniversalEntity{
	
	private String uuid;
	private String tel;
	private String appKey;
	private String token;
	private String token_expire_t;
	private String name;
	private String province;
	private String city;
	private String county;
	private String idNum;
	private int idType;
	private String address;
	private String icon_filePath;
	private int accessLevel;
	private String authCode;
	private String authCode_expire_t;
	private String request_login_t;
	private String salt;
	
	public enum type{
		ROOT,
		COMMON,
		NONE //Represents illegal user only.
	}
	
	public User() {
		// TODO Auto-generated constructor stub
	}

	public String getUuid() {
		return uuid;
	}

	public String getTel() {
		return tel;
	}

	public String getAppKey() {
		return appKey;
	}

	public String getToken() {
		return token;
	}

	public String getToken_expire_t() {
		return token_expire_t;
	}

	public String getName() {
		return name;
	}

	public String getProvince() {
		return province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCounty() {
		return county;
	}

	public String getIdNum() {
		return idNum;
	}

	public int getIdType() {
		return idType;
	}

	public String getAddress() {
		return address;
	}

	public String getAuthCode() {
		return authCode;
	}

	public String getAuthCode_expire_t() {
		return authCode_expire_t;
	}

	public String getRequest_login_t() {
		return request_login_t;
	}

	public String getSalt() {
		return salt;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public void setToken_expire_t(String token_expire_t) {
		this.token_expire_t = token_expire_t;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public void setCounty(String county) {
		this.county = county;
	}

	public void setIdNum(String idNum) {
		this.idNum = idNum;
	}

	public void setIdType(int idType) {
		this.idType = idType;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}

	public void setAuthCode_expire_t(String authCode_expire_t) {
		this.authCode_expire_t = authCode_expire_t;
	}

	public void setRequest_login_t(String request_login_t) {
		this.request_login_t = request_login_t;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}
	
	public String getIcon_filePath() {
		return icon_filePath;
	}

	public void setIcon_filePath(String icon_filePath) {
		this.icon_filePath = icon_filePath;
	}

	public int getAccessLevel() {
		return accessLevel;
	}

	public void setAccessLevel(int accessLevel) {
		this.accessLevel = accessLevel;
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
	
	/**
	 * 此方法与toHashMap方法相同，但过滤了用户的关键安全信息，主要用于向其他用户提供别人的基本信息
	 * @return
	 */
	public HashMap<String, String> toSecureHashMap(){
		HashMap<String, String> eHashMap = new HashMap<>();
		
		eHashMap.put("uuid", uuid);
		eHashMap.put("name", name);
		eHashMap.put("tel",tel);
		eHashMap.put("province", province);
		eHashMap.put("city", city);
		eHashMap.put("county", county);
		eHashMap.put("idNum", idNum);
		eHashMap.put("idType", String.valueOf(idType));
		eHashMap.put("address", address);
		eHashMap.put("accessLevel", String.valueOf(accessLevel));
		
		return eHashMap;
	}
	
	
}
