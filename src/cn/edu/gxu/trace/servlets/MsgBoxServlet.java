package cn.edu.gxu.trace.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import cn.edu.gxu.trace.entity.MsgBox;
import cn.edu.gxu.trace.entity.MsgBox.MSG_TYPE;
import cn.edu.gxu.trace.entity.User;
import cn.edu.gxu.trace.manager.MsgBoxManager;
import cn.edu.gxu.trace.manager.UniversalManager;
import cn.edu.gxu.trace.manager.UserManager;
import cn.edu.gxu.trace.mangoUtils.JsonDecodeFormatter;
import cn.edu.gxu.trace.mangoUtils.JsonEncodeFormatter;
import cn.edu.gxu.trace.mangoUtils.POST2String;
import cn.edu.gxu.trace.mangoUtils.JsonDecodeFormatter.dataType;

/**
 * Servlet implementation class MsgServlet
 */
@WebServlet("/api/user/msg")
@SuppressWarnings("unchecked")
public class MsgBoxServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(MsgBoxServlet.class);
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MsgBoxServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.sendRedirect("http://120.78.177.77/error.html");
		return;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Content-Type", "application/json;charset=utf8");
		//获取POST报文，并转换为字符串
		String rootStr = POST2String.convert(request.getInputStream());
		log.debug(rootStr);
		
		//解析JSON字符串
		HashMap<String, Object> params = JsonDecodeFormatter.decode(rootStr,dataType.obj);
		int code = Integer.valueOf((String)params.get("code"));
		
		switch (code) {
			case 10031:{
				//获取当前用户的警报和通知列表及详情
				getAlertNotice4CurrentUser(params,response);
				return;
			}
			case 10032:{
				//将单个消息设置为已读
				setMsgRead(params,response);
				return;
			}
			case 10033:{
				//溯源用户向系统管理员发送信息
				sendMsg_common_root(params,response);
				return;
			}
			case 10034:{
				//溯源用户获取自己已发起的客服消息
				getCustomerServiceMsg4CommonUser(params,response);
				return;
			}
			case 10035:{
				//系统管理员获取自己已接收的客服消息
				getCustomerServiceMsg4Root(params,response);
				return;
			}
			case 10036:{
				//系统管理员回复指定客服消息
				sendMsg_root_commom(params,response);
				return;
			}
			case 10037:{
				//系统管理员发布全局通知
				sendMsg_global_notice(params,response);
				return;
			}
			default:{
				response.getWriter().write(JsonEncodeFormatter.universalResponse(90001, "Invalid Request Code."));
				return;	
			}
		}
	}


	private void getAlertNotice4CurrentUser(HashMap<String, Object> params, HttpServletResponse response) throws IOException {
		HashMap<String, String> data = (HashMap<String, String>)params.get("data");
		String token = data.get("token");
		int startIndex;
		int limit;
		int isRead;
		try {
			startIndex = Integer.parseInt(data.get("startIndex"));
			limit = Integer.parseInt(data.get("limit"));
			isRead = Integer.parseInt(data.get("isRead"));
		} catch (NumberFormatException e) {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90006, "Illegal parameters.Check Again."));
			return;
		}
		
		UserManager userManager = new UserManager();
		//检查token，验证用户合法性
		if(userManager.checkToken(token)) {
			User cUser = userManager.getBasicProfile(token);
			String receiver_id = cUser.getUuid();
			ArrayList<HashMap<String, String>> res_data = UniversalManager.getMsg4CurrentUser(startIndex, limit, isRead, receiver_id);
			if(res_data==null) {
				response.getWriter().write(JsonEncodeFormatter.universalResponse(90006, "Illegal parameters.Check Again."));
				return;
			}
			response.getWriter().write(JsonEncodeFormatter.parse(0, res_data));
			return;
		}
		else {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90009, "Token Invalid or expired."));
			return;
		}
	}
	
	private void setMsgRead(HashMap<String, Object> params, HttpServletResponse response) throws IOException{
		HashMap<String, String> data = (HashMap<String, String>)params.get("data");
		String token = data.get("token");
		String uid = data.get("msg_uid");
		UserManager userManager = new UserManager();
		MsgBoxManager msgBoxManager = new MsgBoxManager();
		//检查token，验证用户合法性
		if(userManager.checkToken(token)) {
			String receiver_id = userManager.getBasicProfile(token).getUuid();
			if(msgBoxManager.getByUid(uid)!=null && msgBoxManager.setMsgRead(uid,receiver_id)) {
				response.getWriter().write(JsonEncodeFormatter.universalResponse(0, "success."));
				return;
			}
			else {
				response.getWriter().write(JsonEncodeFormatter.universalResponse(90006, "Illegal parameters.Check Again."));
				return;
			}
		}
		else {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90009, "Token Invalid or expired."));
			return;
		}
	}
	
	private void sendMsg_common_root(HashMap<String, Object> params, HttpServletResponse response) throws IOException {
		HashMap<String, String> data = (HashMap<String, String>)params.get("data");
		String token = data.get("token");
		String title = data.get("title");
		String msg = data.get("msg");
		
		//检查字符长度
		if(title.length()>15||msg.length()>200) {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90006, "Illegal Parameters."));
			return;
		}
		
		UserManager userManager = new UserManager();
		MsgBoxManager msgBoxManager = new MsgBoxManager();
		//检查token，验证用户合法性
		if(userManager.checkToken(token)) {
			User cUser = userManager.getBasicProfile(token);
			MsgBox msgBox = new MsgBox();
			msgBox.setTitle(title);
			msgBox.setMsg(msg);
			msgBox.setSender_id(cUser.getUuid());
			msgBox.setTimestamps_created(String.valueOf(System.currentTimeMillis()/1000));
			
			if(msgBoxManager.insertNewMsg(msgBox, MSG_TYPE.COMMON_ROOT)) {
				response.getWriter().write(JsonEncodeFormatter.universalResponse(0, "success."));
				return;
			}
			
		}
		else {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90009, "Token Invalid or expired."));
			return;
		}
		
	}
	
	private void getCustomerServiceMsg4CommonUser(HashMap<String, Object> params, HttpServletResponse response) throws IOException {
		HashMap<String, String> data = (HashMap<String, String>)params.get("data");
		String token = data.get("token");
		int startIndex;
		int limit;
		int isResolved;
		try {
			startIndex = Integer.parseInt(data.get("startIndex"));
			limit = Integer.parseInt(data.get("limit"));
			isResolved = Integer.parseInt(data.get("isResolved"));
		} catch (NumberFormatException e) {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90006, "Illegal parameters.Check Again."));
			return;
		}
		
		UserManager userManager = new UserManager();
		//检查token，验证用户合法性
		if(userManager.checkToken(token)) {
			User cUser = userManager.getBasicProfile(token);
			String uuid = cUser.getUuid();
			ArrayList<HashMap<String, String>> res_data = UniversalManager.getCustomerServiceMsg4CommonUser(startIndex, limit, isResolved, uuid);
			if(res_data==null) {
				response.getWriter().write(JsonEncodeFormatter.universalResponse(90006, "Illegal parameters.Check Again."));
				return;
			}
			response.getWriter().write(JsonEncodeFormatter.parse(0, res_data));
			return;
		}
		else {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90009, "Token Invalid or expired."));
			return;
		}
		
	}
	
	private void getCustomerServiceMsg4Root(HashMap<String, Object> params, HttpServletResponse response) throws IOException {
		HashMap<String, String> data = (HashMap<String, String>)params.get("data");
		String token = data.get("token");
		int startIndex;
		int limit;
		int isResolved;
		try {
			startIndex = Integer.parseInt(data.get("startIndex"));
			limit = Integer.parseInt(data.get("limit"));
			isResolved = Integer.parseInt(data.get("isResolved"));
		} catch (NumberFormatException e) {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90006, "Illegal parameters.Check Again."));
			return;
		}
		
		UserManager userManager = new UserManager();
		//检查token，验证用户合法性
		if(userManager.checkToken(token)) {
			//检查用户权限
			if(userManager.getAccessLevel(token)!=User.type.ROOT) {
				response.getWriter().write(JsonEncodeFormatter.universalResponse(90010, "Authority limited."));
				return;
			}
			ArrayList<HashMap<String, String>> res_data = UniversalManager.getCustomerServiceMsg4Root(startIndex, limit, isResolved);
			if(res_data==null) {
				response.getWriter().write(JsonEncodeFormatter.universalResponse(90006, "Illegal parameters.Check Again."));
				return;
			}
			response.getWriter().write(JsonEncodeFormatter.parse(0, res_data));
			return;
		}
		else {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90009, "Token Invalid or expired."));
			return;
		}
		
	}
	
	private void sendMsg_root_commom(HashMap<String, Object> params, HttpServletResponse response) throws IOException{
		HashMap<String, String> data = (HashMap<String, String>)params.get("data");
		String token = data.get("token");
		String title = data.get("title");
		String msg = data.get("msg");
		String uid = data.get("uid");
		String uuid = data.get("uuid");
		
		//检查字符长度
		if(title.length()>15||msg.length()>200) {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90006, "Illegal Parameters."));
			return;
		}
		
		UserManager userManager = new UserManager();
		MsgBoxManager msgBoxManager = new MsgBoxManager();
		//检查token，验证用户合法性
		if(userManager.checkToken(token)) {
			//检查用户权限
			if(userManager.getAccessLevel(token)!=User.type.ROOT) {
				response.getWriter().write(JsonEncodeFormatter.universalResponse(90010, "Authority limited."));
				return;
			}
			
			//检查消息是否已处理，防止重复操作
			MsgBox cMsgBox = msgBoxManager.getByUid(uid);
			if(cMsgBox!=null && cMsgBox.getIsResolved()==1) {
				response.getWriter().write(JsonEncodeFormatter.universalResponse(90010, "Operation forbidden."));
				return;
			}
			
			MsgBox msgBox = new MsgBox();
			msgBox.setTitle(title);
			msgBox.setMsg(msg);
			msgBox.setReceiver_id(uuid);
			msgBox.setTimestamps_created(String.valueOf(System.currentTimeMillis()/1000));
			
			//回复的同时，将消息设置为已处理
			if(msgBoxManager.insertNewMsg(msgBox, MSG_TYPE.ROOT_COMMON) && msgBoxManager.setMsgResolved(uid)) {
				response.getWriter().write(JsonEncodeFormatter.universalResponse(0, "success."));
				return;
			}
			
		}
		else {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90009, "Token Invalid or expired."));
			return;
		}
	}
	
	private void sendMsg_global_notice(HashMap<String, Object> params, HttpServletResponse response) throws IOException {
		HashMap<String, String> data = (HashMap<String, String>)params.get("data");
		String token = data.get("token");
		String title = data.get("title");
		String msg = data.get("msg");		
		//检查字符长度
		if(title.length()>15||msg.length()>200) {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90006, "Illegal Parameters."));
			return;
		}
		
		UserManager userManager = new UserManager();
		MsgBoxManager msgBoxManager = new MsgBoxManager();
		//检查token，验证用户合法性
		if(userManager.checkToken(token)) {
			//检查用户权限
			if(userManager.getAccessLevel(token)!=User.type.ROOT) {
				response.getWriter().write(JsonEncodeFormatter.universalResponse(90010, "Authority limited."));
				return;
			}
					
			MsgBox msgBox = new MsgBox();
			msgBox.setTitle(title);
			msgBox.setMsg(msg);
			msgBox.setTimestamps_created(String.valueOf(System.currentTimeMillis()/1000));
			
			//回复的同时，将消息设置为已处理
			if(msgBoxManager.insertNewMsg(msgBox, MSG_TYPE.GLOBAL_NOTICE)) {
				response.getWriter().write(JsonEncodeFormatter.universalResponse(0, "success."));
				return;
			}
		}
		else {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90009, "Token Invalid or expired."));
			return;
		}
	}

}
