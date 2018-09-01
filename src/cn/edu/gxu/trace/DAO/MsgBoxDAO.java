package cn.edu.gxu.trace.DAO;

import cn.edu.gxu.trace.entity.MsgBox;

public interface MsgBoxDAO {
	
	/**
	 * 新建消息
	 * @param msgBox
	 * @return
	 */
	public boolean insertNewMsg(MsgBox msgBox,MsgBox.MSG_TYPE type);
	
	/**
	 * 将消息设置为已读
	 * @param uid 消息唯一标识符
	 * @param receiver_id 用户uuid
	 * @return
	 */
	public boolean setMsgRead(String uid,String receiver_id);
	
	/**
	 * 将消息设置为已处理
	 * @param uid 消息唯一标识符
	 * @return
	 */
	public boolean setMsgResolved(String uid);
	/**
	 * 获取单个消息
	 * @param uid 消息唯一标识符
	 * @return
	 */
	public MsgBox getByUid(String uid);
}
