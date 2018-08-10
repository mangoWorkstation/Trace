package cn.edu.gxu.trace.manager;


import cn.edu.gxu.trace.DAO.DAO;
import cn.edu.gxu.trace.DAO.RecordDAO;
import cn.edu.gxu.trace.entity.Record;

public class RecordManager extends DAO<Record> implements RecordDAO {

	@Override
	public boolean addNewRecord(Record record) {
		String sql = String.format("insert into RECORD values(default,'%s','%s','%s',"
				+ "%f,%f,%f,%f,%f);",
				record.getSensorSIM(),
				record.getArchive_id(),
				record.getTimestamps(),
				record.getTemp_air(),
				record.getTemp_soil(),
				record.getHumidity_air(),
				record.getHumidity_soil(),
				record.getLux());
		try {
			super.update(sql);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
