package cn.edu.gxu.trace.manager;


import java.util.ArrayList;

import cn.edu.gxu.trace.DAO.BaseDAO;
import cn.edu.gxu.trace.DAO.DAO;
import cn.edu.gxu.trace.entity.Base;

public class BaseManager extends DAO<Base> implements BaseDAO {

	@Override
	public boolean addNewBase(Base base) {
		String sql = String.format("insert into BASE values('%s','%s',%f,'%s','%s','%s','%s','%s',%d,%s,null,null);", 
				base.getUid(),
				base.getName(),
				base.getArea(),
				base.getStandard(),
				base.getAddress(),
				base.getProvince(),
				base.getCity(),
				base.getCounty(),
				base.getFruitType_id(),
				base.getVideoStream_url()==null?"NULL":"'"+base.getVideoStream_url()+"'");
		try {
			super.update(sql);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public ArrayList<Base> getBaseProfileByUUID(String uuid) {
		String sql = String.format("select * from BASE where owner_id = '%s';", uuid);
		ArrayList<Base> bases = (ArrayList<Base>) super.getForList(sql);
		return bases;
	}

	@Override
	public Base getBaseProfileByBase_id(String base_id) {
		String sql = String.format("select * from BASE where uid = '%s';", base_id);
		return super.get(sql);
	}

	@Override
	public ArrayList<Base> getBaseProfileByBase_name(String name) {
		String sql = "select * from BASE where name like '%"+name+"%';";
		return (ArrayList<Base>) super.getForList(sql);
	}

	@Override
	public boolean updateOwner_id(String base_id, String uuid) {
		String sql;
		//绑定、变更基地所有者
		if(uuid!=null) {
			sql = String.format("update BASE set owner_id = '%s' where uid = '%s';", uuid,base_id);
		}
		else {
			sql = String.format("update BASE set owner_id = NULL where uid = '%s';", base_id);
		}
		try {
			super.update(sql);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean updateBaseProfile(Base base) {
		String sql = String.format("update BASE set name = '%s',area = %f,standard = '%s',province = '%s',"
				+ "city = '%s',county = '%s',address = '%s',fruitType_id = %d,videoStream_url = '%s'"
				+ " where uid = '%s';", 
				base.getName(),
				base.getArea(),
				base.getStandard(),
				base.getProvince(),
				base.getCity(),
				base.getCounty(),
				base.getAddress(),
				base.getFruitType_id(),
				base.getVideoStream_url(),
				base.getUid());
		try {
			super.update(sql);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
