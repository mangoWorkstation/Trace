package cn.edu.gxu.trace.DAO;

import cn.edu.gxu.trace.entity.Record;

public interface RecordDAO {
	
	/**
	 * 新增传感器记录
	 * @param record
	 * @return
	 */
	public boolean addNewRecord(Record record);
}
