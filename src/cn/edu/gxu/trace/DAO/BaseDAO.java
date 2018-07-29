package cn.edu.gxu.trace.DAO;

import java.util.ArrayList;

import cn.edu.gxu.trace.entity.Base;

public interface BaseDAO {
	/**
	 * 添加基地信息
	 * @param base
	 * @return
	 */
	public boolean addNewBase(Base base);
	
	/**
	 * 根据用户uuid查询旗下的所有基地信息
	 * @param uuid 用户后台id
	 * @return
	 */
	public ArrayList<Base> getBaseProfileByUUID(String uuid);
	
	/**
	 * 根据基地id查询指定基地信息
	 * @param base_id
	 * @return
	 */
	public Base getBaseProfileByBase_id(String base_id);
	
	/**
	 * 根据基地名称关键字查询基地信息
	 * @param name
	 * @return
	 */
	public ArrayList<Base> getBaseProfileByBase_name(String name);
	
	/**
	 * 绑定、变更、解除绑定基地的所有者（需要管理员权限）
	 * @param base_id 基地id
	 * @param uuid	用户id
	 * @return
	 */
	public boolean updateOwner_id(String base_id,String uuid);
	
	/**
	 * 更新基地的基础信息
	 * @param base
	 * @return
	 */
	public boolean updateBaseProfile(Base base);
}
