package cn.edu.gxu.trace.entity;

import java.lang.reflect.Field;
import java.util.HashMap;

public class MsgBox implements UniversalEntity {
	
	public enum MSG_TYPE{
		ALERT,//系统自动警报
		GLOBAL_NOTICE,//全局通知
		ROOT_COMMON,//管理员向溯源用户发送定向消息
		COMMON_ROOT//溯源用户向管理员发送请求
	}
	
	private String uid;
	private String sender_id;
	private String receiver_id;
	private String title;
	private String msg;
	private int isRead;
	private int isResolved;
	private String timestamps_created;
	
	public MsgBox() {
		
	}
	
	public MsgBox(String uid, String sender_id, String receiver_id, String title, String msg, int isRead,
			int isResolved, String timestamps_created) {
		super();
		this.uid = uid;
		this.sender_id = sender_id;
		this.receiver_id = receiver_id;
		this.title = title;
		this.msg = msg;
		this.isRead = isRead;
		this.isResolved = isResolved;
		this.timestamps_created = timestamps_created;
	}

	public String getUid() {
		return uid;
	}

	public String getSender_id() {
		return sender_id;
	}

	public String getReceiver_id() {
		return receiver_id;
	}

	public String getTitle() {
		return title;
	}

	public String getMsg() {
		return msg;
	}

	public int getIsRead() {
		return isRead;
	}

	public int getIsResolved() {
		return isResolved;
	}

	public String getTimestamps_created() {
		return timestamps_created;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public void setSender_id(String sender_id) {
		this.sender_id = sender_id;
	}

	public void setReceiver_id(String receiver_id) {
		this.receiver_id = receiver_id;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public void setIsRead(int isRead) {
		this.isRead = isRead;
	}

	public void setIsResolved(int isResolved) {
		this.isResolved = isResolved;
	}

	public void setTimestamps_created(String timestamps_created) {
		this.timestamps_created = timestamps_created;
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
