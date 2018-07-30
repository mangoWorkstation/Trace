package cn.edu.gxu.trace.DAO;

import java.util.ArrayList;

import cn.edu.gxu.trace.entity.Fruit;

public interface FruitDAO {
	/**
	 * 新增水果信息
	 * @param fruit
	 * @return
	 */
	public boolean addNewFruit(Fruit fruit);
	
	
	/**
	 * 更新水果信息
	 * @param fruit
	 * @return
	 */
	public boolean updateFruitProfile(Fruit fruit);
	
	/**
	 * 根据关键字查询水果信息，包含uid、名称和品种
	 * @param fruit
	 * @return
	 */
	public ArrayList<Fruit> getFruitProfile(String key);
	
}
