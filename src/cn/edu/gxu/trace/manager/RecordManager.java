package cn.edu.gxu.trace.manager;

import java.util.UUID;

import cn.edu.gxu.trace.DAO.DAO;
import cn.edu.gxu.trace.DAO.RecordDAO;
import cn.edu.gxu.trace.entity.Record;

public class RecordManager extends DAO<Record> implements RecordDAO {

	@Override
	public boolean addNewRecord(Record record) {
		String uid = UUID.randomUUID().toString();
		String sql = String.format("insert into RECORD values('%s','%s','%s','%s',"
				+ "%f,%f,%f,%f,%f);",
				uid,
				record.getSensorSIM(),
				record.getArchive_id(),
				record.getTimestamp(),
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
