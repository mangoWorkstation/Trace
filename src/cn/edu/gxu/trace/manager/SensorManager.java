package cn.edu.gxu.trace.manager;

import java.util.ArrayList;

import cn.edu.gxu.trace.DAO.DAO;
import cn.edu.gxu.trace.DAO.SensorDAO;
import cn.edu.gxu.trace.entity.Sensor;

public class SensorManager extends DAO<Sensor> implements SensorDAO {

	@Override
	public boolean addNewSensor(String sim) {
		String sql = String.format("insert into SENSOR values('%s',null,0,null,null);", sim);
		try {
			super.update(sql);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	@Override
	public boolean bindWithBase(String sim, String base_id) {
		String sql;
		if(base_id==null) {
			sql = String.format("update SENSOR set base_id = NULL where sim = '%s';", sim);
		}
		else {
			sql = String.format("update SENSOR set base_id = '%s' where sim = '%s';", base_id,sim);
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
	public Sensor getSensorProfileBySIM(String sim) {
		String sql = String.format("select * from SENSOR where sim = '%s';", sim);
		return super.get(sql);
	}

	@Override
	public ArrayList<Sensor> getSensorsByBase_id(String base_id) {
		String sql = String.format("select * from SENSOR where base_id = '%s';", base_id);
		return (ArrayList<Sensor>) super.getForList(sql);
	}

	

}
