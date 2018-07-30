package cn.edu.gxu.trace.manager;

import java.util.ArrayList;

import cn.edu.gxu.trace.DAO.DAO;
import cn.edu.gxu.trace.DAO.FruitDAO;
import cn.edu.gxu.trace.entity.Fruit;

public class FruitManager extends DAO<Fruit> implements FruitDAO {

	@Override
	public boolean addNewFruit(Fruit fruit) {
		String sql = String.format("insert into FRUIT values(DEFAULT,'%s','%s','%s','%s','%s');",
				fruit.getName(),
				fruit.getCategory(),
				fruit.getOnSale_t(),
				fruit.getOffSale_t(),
				fruit.getContent());
		try {
			super.update(sql);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean updateFruitProfile(Fruit fruit) {
		String sql = String.format("update FRUIT set name = '%s',category = '%s',onSale_t = '%s',"
				+ "offSale_t = '%s',content = '%s' where uid = '%s';", 
				fruit.getName(),
				fruit.getCategory(),
				fruit.getOnSale_t(),
				fruit.getOffSale_t(),
				fruit.getContent(),
				fruit.getUid());
		try {
			super.update(sql);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public ArrayList<Fruit> getFruitProfile(String key) {
		String sql;
		try {
			sql = "select * from FRUIT where uid = "+Integer.valueOf(key)+" or name like '%"+key+"%' or category like '%"+key+"%';";
		} catch (NumberFormatException e) {
			sql = "select * from FRUIT where name like '%"+key+"%' or category like '%"+key+"%';";
		}
		return (ArrayList<Fruit>) super.getForList(sql);
	}


}
