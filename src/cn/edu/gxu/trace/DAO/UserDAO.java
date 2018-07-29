package cn.edu.gxu.trace.DAO;


import cn.edu.gxu.trace.entity.User;

public interface UserDAO {
	/**
	 * 注册新用户用，请求短信验证码
	 * @param tel 手机号码
	 * @param authCode 验证码
	 * @return
	 */
	public boolean registerNewUser(String tel,String authCode);
	
	/**
	 * 
	 * 注册时，验证短信验证码
	 * 1.对及时完成验证的用户，将其密码指纹appKey设为“registered”字段，使其不会被数据库定时任务清理
	 * 2.对没有按时完成验证的用户，系统将会3分钟后自动清理其验证信息
	 * 3.系统视appKey的值用于判定用户是否完成验证
	 * 4.完成验证后，请强制用户设置密码，随后是个人信息
	 * 
	 * @param tel 电话号码
	 * @param authCode 短信验证码
	 * @return
	 */
	public boolean verifyRegister(String tel, String authCode);
	
	/**
	 * 
	 * 首次注册时，强制用户设定密码
	 * @param passwordSHA512
	 * @param tel
	 * @return
	 */
	public String forcedPresetPassword(String passwordSHA512,String tel);
	
	/**
	 * 检查是否合法用户，并且更新token的过期时间
	 * @param token
	 * @return
	 */
	public boolean checkToken(String token);
	
	/**
	 * 更新用户基础信息
	 * @param user
	 * @param token
	 * @return
	 */
	public boolean updateUserProfile(User user,String token);
	
	/**
	 * 获取用户的所有信息
	 * @param key 关键字可以是后台uuid、token和电话号码
	 * @return
	 */
	public User getBasicProfile(String key);
	
	/**
	 * 用于更改用户登录时，请求的时间戳和随机串
	 * @param timestamp 时间戳
	 * @param salt 随机串
	 * @return
	 */
	public boolean updateTimestampSalt(String timestamp,String salt,String tel);
	
	/**
	 * 刷新指定用户的token
	 * @param tel
	 * @return
	 */
	public String refreshToken(String tel);
	
	/**
	 * 根据用户关键字，获取指定用户的权限级别
	 * ROOT 超级管理员 COMMON 普通用户
	 * @param key = uuid | token | tel
	 * @return
	 */
	public User.type getAccessLevel(String key);
	

}
