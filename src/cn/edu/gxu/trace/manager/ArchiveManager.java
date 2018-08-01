package cn.edu.gxu.trace.manager;

import java.util.ArrayList;
import java.util.UUID;

import cn.edu.gxu.trace.DAO.ArchiveDAO;
import cn.edu.gxu.trace.DAO.DAO;
import cn.edu.gxu.trace.entity.Archive;

public class ArchiveManager extends DAO<Archive> implements ArchiveDAO {

	@Override
	public boolean addNewArchive(Archive archive) {
		String uid = UUID.randomUUID().toString();
		String sql = String.format("insert into ARCHIVE values('%s','%d','%s',null,'%s');", 
				uid,
				archive.getFruit_id(),
				archive.getPlant_t(),
				archive.getBase_id());
		try {
			super.update(sql);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean updatePlant_end_t(String plant_ent_t, String uid) {
		//防止重复提交，预防重放攻击
		String sql_1 = String.format("select * from ARCHIVE where uid = '%s';", uid);
		Archive cArchive = super.get(sql_1);
		if(cArchive.getPlant_end_t()!=null) {
			return false;
		}
		String sql_2 = String.format("update ARCHIVE set plant_end_t = '%s' where uid = '%s';update SENSOR set archive_id = NULL where archive_id = '%s';",
				plant_ent_t,uid,uid);
		try {
			super.update(sql_2);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public ArrayList<Archive> getArchiveByBase_id(String base_id) {
		String sql = String.format("select * from ARCHIVE where base_id = '%s';", base_id);
		return (ArrayList<Archive>) super.getForList(sql);
	}

	@Override
	public Archive getArchiveByUid(String uid) {
		String sql = String.format("select * from ARCHIVE where uid = '%s';", uid);
		return super.get(sql);
	}
	
}
