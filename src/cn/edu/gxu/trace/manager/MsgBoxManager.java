package cn.edu.gxu.trace.manager;

import cn.edu.gxu.trace.DAO.DAO;
import cn.edu.gxu.trace.DAO.MsgBoxDAO;
import cn.edu.gxu.trace.entity.MsgBox;

public class MsgBoxManager extends DAO<MsgBox> implements MsgBoxDAO {
	

	@Override
	public boolean insertNewMsg(MsgBox msgBox,MsgBox.MSG_TYPE type) {
		String sql = "";
		switch (type) {
			case ALERT:{
				sql = String.format("insert into MSGBOX values(default,'system','%s','%s','%s',0,NULL,'%s');", 
						msgBox.getReceiver_id(),
						msgBox.getTitle(),
						msgBox.getMsg(),
						msgBox.getTimestamps_created());
				break;
			}
			case GLOBAL_NOTICE:{
				sql = String.format("insert into MSGBOX values(default,'root','all','%s','%s',0,NULL,'%s');", 
						msgBox.getTitle(),
						msgBox.getMsg(),
						msgBox.getTimestamps_created());
				break;
			}
			case ROOT_COMMON:{
				sql = String.format("insert into MSGBOX values(default,'root','%s','%s','%s',0,NULL,'%s');", 
						msgBox.getReceiver_id(),
						msgBox.getTitle(),
						msgBox.getMsg(),
						msgBox.getTimestamps_created());
				break;
			}
			case COMMON_ROOT:{
				sql = String.format("insert into MSGBOX values(default,'%s','root','%s','%s',NULL,0,'%s');", 
						msgBox.getSender_id(),
						msgBox.getTitle(),
						msgBox.getMsg(),
						msgBox.getTimestamps_created());
				break;
			}
		}
//		log.debug(sql);
		try {
			super.update(sql);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	@Override
	public boolean setMsgRead(String uid,String receiver_id) {
		String sql = String.format("update MSGBOX set isRead = 1 where uid = '%s' and receiver_id = '%s';", uid,receiver_id);
//		log.debug(sql);
		try {
			super.update(sql);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public MsgBox getByUid(String uid) {
		String sql = String.format("select * from MSGBOX where uid = '%s';", uid);
		return super.get(sql);
	}

	@Override
	public boolean setMsgResolved(String uid) {
		String sql = String.format("update MSGBOX set isResolved = 1 where receiver_id = 'root' and uid = '%s';",uid);
		try {
			super.update(sql);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	

}
