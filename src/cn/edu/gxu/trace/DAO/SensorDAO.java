package cn.edu.gxu.trace.DAO;

import java.util.ArrayList;

import cn.edu.gxu.trace.entity.Sensor;

public interface SensorDAO {
	
	/**
	 * 添加新的传感器
	 * @param sim 传感器SIM卡号
	 * @return
	 */
	public boolean addNewSensor(String sim);
	
	/**
	 * 绑定、变更和解除绑定传感器与基地从属关系
	 * @param sim
	 * @param base_id
	 * @return
	 */
	public boolean bindWithBase(String sim,String base_id);
	
	/**
	 * 根据传感器SIM卡号获取
	 * @param sim 传感器SIM卡号
	 * @return
	 */
	public Sensor getSensorProfileBySIM(String sim);
	
	/**
	 * 根据
	 * @param base_id
	 * @return
	 */
	public ArrayList<Sensor> getSensorsByBase_id(String base_id);
	
	/**
	 * 传感器绑定种植
	 * @param sim
	 * @param archive_id
	 * @return
	 */
	public boolean bindArchive(String sim,String archive_id);

}
