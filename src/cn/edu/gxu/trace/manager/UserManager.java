package cn.edu.gxu.trace.manager;

import java.util.UUID;

import cn.edu.gxu.trace.DAO.DAO;
import cn.edu.gxu.trace.DAO.UserDAO;
import cn.edu.gxu.trace.entity.User;

public class UserManager extends DAO<User> implements UserDAO {


	@Override
	public boolean registerNewUser(String tel, String authCode) {
		
		String uuid = UUID.randomUUID().toString();
		String authCode_expire_t = String.valueOf(System.currentTimeMillis()/1000 + 60);
		String sql_query = String.format("select * from USER where tel='%s';", tel);
		User aUser = super.get(sql_query);
		if(aUser==null) {
			String sql = String.format("insert into USER values('%s','%s',null,null,null,null,null,null,null,null,null,null,null,'%s','%s',null,null)",uuid,tel,authCode,authCode_expire_t);
			super.update(sql);
			return true;
		}
		return false;
	}

	@Override
	public boolean verifyRegister(String tel, String authCode) {
		String sql = String.format("select * from USER where tel='%s' and authCode='%s';", tel,authCode);
		User aUser = super.get(sql);
		if(aUser!=null) {
			String sql_1 = String.format("update USER set appKey='registered',authCode=null,authCode_expire_t=null where tel='%s';", tel);
			super.update(sql_1);
			return true;
		}
		return false;
	}

	@Override
	public String forcedPresetPassword(String passwordSHA512, String tel) {
		String sql_1 = String.format("select * from USER where tel='%s' and appKey='registered'", tel);
		User aUser = super.get(sql_1);
		if(aUser!=null) {
			String appKey = passwordSHA512;
			String newToken = UUID.randomUUID().toString();
			String newTokenExpire_t = String.valueOf(System.currentTimeMillis()/1000 + 60*60*2);
			String sql_2 = String.format("update USER set appKey='%s',token='%s',token_expire_t='%s' where tel='%s';", appKey,newToken,newTokenExpire_t,tel);
			super.update(sql_2);
			return newToken;
		}
		return null;
	}

	@Override
	public boolean checkToken(String token) {
		String sql = String.format("select * from USER where token='%s';",token);
		User user = super.get(sql);
		if(user!=null) {
			String newExpire_t = String.valueOf(System.currentTimeMillis()/1000+60*60*2);
			String sql_2 = String.format("update USER set token_expire_t = '%s' where uuid = '%s';", newExpire_t,user.getUuid());
			super.update(sql_2);
			return true;
		}
		return false;
	}

	@Override
	public boolean updateUserProfile(User user, String token) {
		String sql = "update USER set name='"+user.getName()+"',province='"+user.getProvince()+"',city='"+user.getCity()+"',county='"+user.getCounty()+"',idNum='"+user.getIdNum()+"',idType= "+user.getIdType()+",address='"+user.getAddress()+"' where token='"+token+"';";
		try {
			super.update(sql);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
