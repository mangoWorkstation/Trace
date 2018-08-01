package cn.edu.gxu.trace.DAO;

import java.util.ArrayList;

import cn.edu.gxu.trace.entity.Archive;

public interface ArchiveDAO {
	/**
	 * 新增种植档案
	 * @param archive
	 * @return
	 */
	public boolean addNewArchive(Archive archive);
	
	/**
	 * 更新收获时间，同时取消所有关联的传感器
	 * @param plant_ent_t 收获时间戳
	 * @param uid 水果批次编号
	 * @return
	 */
	public boolean updatePlant_end_t(String plant_ent_t,String uid);
	
	/**
	 * 获取指定基地的所有水果批次
	 * @param base_id 基地id
	 * @return
	 */
	public ArrayList<Archive> getArchiveByBase_id(String base_id);
	
	/**
	 * 根据水果批次获取种植档案
	 * @param uid
	 * @return
	 */
	public Archive getArchiveByUid(String uid);
	

}
