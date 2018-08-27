package cn.edu.gxu.trace.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import cn.edu.gxu.trace.aliyun.AliSmsSender;
import cn.edu.gxu.trace.entity.User;
import cn.edu.gxu.trace.entity.User.type;
import cn.edu.gxu.trace.manager.UserManager;
import cn.edu.gxu.trace.mangoUtils.AuthCodeCreator;
import cn.edu.gxu.trace.mangoUtils.JsonDecodeFormatter;
import cn.edu.gxu.trace.mangoUtils.JsonEncodeFormatter;
import cn.edu.gxu.trace.mangoUtils.POST2String;
import cn.edu.gxu.trace.mangoUtils.JsonDecodeFormatter.dataType;

/**
 * Servlet implementation class UserServlet
 */
@WebServlet("/api/user")
public class UserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(UserServlet.class);
	private static final String TEL_REX = "^1([358][0-9]|4[579]|66|7[0135678]|9[89])[0-9]{8}$"; 

       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.sendRedirect("http://120.78.177.77/error.html");
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
			case 10004:{
				//请求更新用户基础信息
				updateUserProfile(params, response);
				return;
			}
			case 10026:{
				//获取当前用户基本信息，需要同时验证token和uuid字段
				getCurrentUserProfile(params,response);
				return;
			}
			case 10027:{
				//系统管理员根据名称或uuid获取其他用户信息
				getUserProfiles_root(params,response);
				return;
			}
			case 10028:{
				//请求核验当前用户身份的短信验证码
				request4Authcode(params,response);
				return;
			}
			case 10029:{
				//请求变更手机号，包含两步操作
				changeTel(params,response);
				return;
			}
			case 10030:{
				resetPwd(params,response);
				return;
			}
			default:{
				response.getWriter().write(JsonEncodeFormatter.universalResponse(90001, "Invalid Request Code."));
				return;	
			}
			
		}
	}

	@SuppressWarnings("unchecked")
	private void updateUserProfile(HashMap<String, Object> params, HttpServletResponse response) throws IOException {
		HashMap<String, String> data = (HashMap<String, String>)params.get("data");
		String token = data.get("token");
		UserManager userManager = new UserManager();

		if(userManager.checkToken(token)) {
			User user = new User();
			user.setName(data.get("name"));
			user.setProvince(data.get("province"));
			user.setCity(data.get("city"));
			user.setCounty(data.get("county"));
			//证件号检查
			if(data.get("idNum").length()!=18) {
				response.getWriter().write(JsonEncodeFormatter.universalResponse(90006, "Illegal parameters.Check Again."));
			}
			user.setIdNum(data.get("idNum"));
			user.setIdType(Integer.valueOf(data.get("idType")));
			user.setAddress(data.get("address"));
			if(userManager.updateUserProfile(user, token)) {
				response.getWriter().write(JsonEncodeFormatter.universalResponse(0, "success"));
				return;
			}
			else {
				response.getWriter().write(JsonEncodeFormatter.universalResponse(90006, "Illegal parameters.Check Again."));
			}
		}
		else {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90009, "Token Invalid or expired."));
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private void getCurrentUserProfile(HashMap<String, Object> params, HttpServletResponse response) throws IOException {
		HashMap<String, String> data = (HashMap<String, String>)params.get("data");
		String token = data.get("token");
		String uuid = data.get("uuid");
		UserManager userManager = new UserManager();

		if(userManager.checkToken(token)) {
			User cUser = userManager.getBasicProfile(token);
			if(cUser!=null) {
				//需要对uuid和token两个字段同时进行校验
				if(uuid.compareTo(cUser.getUuid())==0 && token.compareTo(cUser.getToken())==0) {
					response.getWriter().write(JsonEncodeFormatter.parse(0, cUser.toSecureHashMap()));
					return;
				}
				else {
					response.getWriter().write(JsonEncodeFormatter.universalResponse(90010, "Authority limited."));
					return;
				}
			}else {
				response.getWriter().write(JsonEncodeFormatter.universalResponse(90006, "Illegal Parameters."));
				return;
			}
		}else {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90009, "Token Invalid or expired."));
		}
	}
	
	@SuppressWarnings("unchecked")
	private void getUserProfiles_root(HashMap<String, Object> params, HttpServletResponse response) throws IOException {
		HashMap<String, String> data = (HashMap<String, String>)params.get("data");
		String token = data.get("token");
		String uuid = data.get("uuid");
		String name = data.get("name");
		UserManager userManager = new UserManager();

		if(userManager.checkToken(token)) {
			//检查账户权限
			if(userManager.getAccessLevel(token)==type.ROOT) {
				ArrayList<HashMap<String, String>> res_data = new ArrayList<>();
				ArrayList<User> res_users = new ArrayList<>();
				//过滤不明确的操作
				if(name!=null&&uuid!=null) {
					response.getWriter().write(JsonEncodeFormatter.universalResponse(90012, "Implicit operation."));
					return;
				}
				
				//按照用户名称查
				if(name!=null) {
					res_users =userManager.getBasicProfileByName(name);
				}
				
				//按照uuid查
				if(uuid!=null) {
					res_users.add(userManager.getBasicProfile(uuid));
					
				}
				
				//信息汇总处理
				Iterator<User> iterator_users = res_users.iterator();
				while(iterator_users.hasNext()) {
					res_data.add(iterator_users.next().toSecureHashMap());
				}
				
				response.getWriter().write(JsonEncodeFormatter.parse(0, res_data));
				return;
				
			}
			else {
				response.getWriter().write(JsonEncodeFormatter.universalResponse(90010, "Authority limited."));
				return;
			}
		}
		else {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90009, "Token Invalid or expired."));
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private void request4Authcode(HashMap<String, Object> params, HttpServletResponse response) throws IOException {
		HashMap<String, String> data = (HashMap<String, String>)params.get("data");
		String token = data.get("token");
		UserManager userManager = new UserManager();

		if(userManager.checkToken(token)) {
			User cUser = userManager.getBasicProfile(token);
			//检查短信验证码是否仍有效，防止重复请求
			if(cUser.getAuthCode()!=null) {
				response.getWriter().write(JsonEncodeFormatter.universalResponse(90003, "AuthCode NOT Expired."));
				return;
			}
			
			//发送短信验证码
			String newAuthCode = AuthCodeCreator.create();
			if(AliSmsSender.sendAuthCodeSms(cUser.getTel(), newAuthCode)) {
				userManager.refreshAuthCode(newAuthCode, token);
				response.getWriter().write(JsonEncodeFormatter.universalResponse(0, "success."));
				return;
			}
			else {
				response.getWriter().write(JsonEncodeFormatter.universalResponse(90015, "SMS Send Failed."));
				return;
			}
			
			
		}
		else {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90009, "Token Invalid or expired."));
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private void changeTel(HashMap<String, Object> params, HttpServletResponse response) throws IOException {
		HashMap<String, String> data = (HashMap<String, String>)params.get("data");
		String token = data.get("token");
		String newTel = data.get("newTel");
		String authCode = data.get("authCode");
		String step = data.get("step");
		
		UserManager userManager = new UserManager();

		if(userManager.checkToken(token)) {
			//检查提交的新手机号是否已经绑定了其他账户
			User tUser = userManager.getBasicProfile(newTel);
			if(tUser!=null) {
				response.getWriter().write(JsonEncodeFormatter.universalResponse(90016, "Tel already binded with other account."));
				return;
			}
			
			//正则表达式校验新手机号
			if(newTel.matches(UserServlet.TEL_REX)==false) {
				response.getWriter().write(JsonEncodeFormatter.universalResponse(90002, "Illegal Phone Number."));
				return;	
			}
			
			//验证旧手机号
			if("1".compareTo(step)==0) {
				
				//检查短信验证码是否正确
				User cUser = userManager.getBasicProfile(token);
				if(authCode.compareTo(cUser.getTel())==1) {
					response.getWriter().write(JsonEncodeFormatter.universalResponse(90004, "Wrong Auth Code."));
					return;
				}
				//发送短信验证码
				String newAuthCode = AuthCodeCreator.create();
				if(AliSmsSender.sendAuthCodeSms(newTel, newAuthCode)) {
					userManager.refreshAuthCode(newAuthCode, token);
					response.getWriter().write(JsonEncodeFormatter.universalResponse(0, "AuthCode OK.Notify client for new SMS."));
					return;
				}
				else {
					response.getWriter().write(JsonEncodeFormatter.universalResponse(90015, "SMS Send Failed."));
					return;
				}
				
				
			}
			//验证新手机号
			if("2".compareTo(step)==0){
				User cUser = userManager.getBasicProfile(token);
				if(cUser!=null) {
					//核验验证码
					if(authCode.compareTo(cUser.getAuthCode())==0) {
						userManager.updateTel(newTel, cUser.getUuid());
						userManager.refreshAuthCode(null, token);
						response.getWriter().write(JsonEncodeFormatter.universalResponse(0, "User's tel altered."));
						return;
					}
					else {
						response.getWriter().write(JsonEncodeFormatter.universalResponse(90004, "Wrong Auth Code."));
						return;
					}
				}
			}
			
		}
		else {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90009, "Token Invalid or expired."));
		}
	}
	
	@SuppressWarnings("unchecked")
	private void resetPwd(HashMap<String, Object> params, HttpServletResponse response) throws IOException {
		HashMap<String, String> data = (HashMap<String, String>)params.get("data");
		String token = data.get("token");
		String authCode = data.get("authCode");
		String newSHApwd = data.get("newSHApwd");
		
		UserManager userManager = new UserManager();

		if(userManager.checkToken(token)) {
			User cUser = userManager.getBasicProfile(token);
			if(cUser!=null&&cUser.getAuthCode()!=null) {
				//检查短信验证码是否正确
				if(authCode.compareTo(cUser.getAuthCode())==0) {
					//更新新密码
					userManager.resetPwd(newSHApwd, cUser.getUuid());
					//使验证码失效
					userManager.refreshAuthCode(null, token);
					response.getWriter().write(JsonEncodeFormatter.universalResponse(0, "success."));
					return;
				}
				else {
					response.getWriter().write(JsonEncodeFormatter.universalResponse(90004, "Wrong Auth Code."));
					return;  
				}
			}
			else {
				response.getWriter().write(JsonEncodeFormatter.universalResponse(90013, "Invalid user."));
				return;
			}
		}
		else {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90009, "Token Invalid or expired."));
		}
	}

}
