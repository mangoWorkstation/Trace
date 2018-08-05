package cn.edu.gxu.trace.servlets;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import cn.edu.gxu.trace.entity.User;
import cn.edu.gxu.trace.manager.UserManager;
import cn.edu.gxu.trace.mangoUtils.Encryptor;
import cn.edu.gxu.trace.mangoUtils.JsonDecodeFormatter;
import cn.edu.gxu.trace.mangoUtils.JsonEncodeFormatter;
import cn.edu.gxu.trace.mangoUtils.POST2String;
import cn.edu.gxu.trace.mangoUtils.SaltCreator;
import cn.edu.gxu.trace.mangoUtils.JsonDecodeFormatter.dataType;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/api/user/login")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(LoginServlet.class);
	private static final String TEL_REX = "^1([358][0-9]|4[579]|66|7[0135678]|9[89])[0-9]{8}$"; 

       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
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
			case 10005:{
				requestForLogin(params, response);
				return;
			}
			case 10006:{
				//请求注册的短信验证码
				appKey_login(params, response);
				return;
			}
			case 10007:{
				token_login(params, response);
				return;
			}
			default:{
				response.getWriter().write(JsonEncodeFormatter.universalResponse(90001, "Invalid Request Code."));
				return;	
			}
			
		}
	}
	
	@SuppressWarnings("unchecked")
	private void requestForLogin(HashMap<String, Object> params , HttpServletResponse response) throws IOException{
		HashMap<String, String> data = (HashMap<String, String>)params.get("data");
		String tel = data.get("tel");
		if(tel.matches(LoginServlet.TEL_REX)==false) {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90002, "Illegal Phone Number."));
			return;	
		}
		
		UserManager userManager = new UserManager();
		User cUser = userManager.getBasicProfile(tel);
		if(cUser!=null) {
			String timestamp = String.valueOf(System.currentTimeMillis()/1000);
			String salt = SaltCreator.create();
			if(userManager.updateTimestampSalt(timestamp, salt, tel)) {
				HashMap<String, String> res_data = new HashMap<>();
				res_data.put("timestamp", timestamp);
				res_data.put("str", salt);
				response.getWriter().write(JsonEncodeFormatter.parse(0, res_data));
				return;
			}
		}
		else {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90007, "Invalid tel."));
			return;
		}
	}
	
	@SuppressWarnings("unchecked")
	private void appKey_login(HashMap<String, Object> params , HttpServletResponse response) throws IOException {
		HashMap<String, String> data = (HashMap<String, String>)params.get("data");
		String tel = data.get("tel");
		String echo = data.get("echo");
		
		
		if(tel.matches(LoginServlet.TEL_REX)==false) {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90002, "Illegal Phone Number."));
			return;	
		}
		
		UserManager userManager = new UserManager();
		User cUser = userManager.getBasicProfile(tel);
		if(cUser != null) {
			//从数据库中计算用户验证信息
			String timestamp = cUser.getRequest_login_t();
			String str = cUser.getSalt();
			String appKey = cUser.getAppKey();
			String testStr = new Encryptor().SHA512(appKey+timestamp+str);
			
			//如果加密结果和接收的结果相同，则视为合法用户
			if(echo.compareTo(testStr)==0) {
				String uuid = cUser.getUuid();
				String newToken = userManager.refreshToken(tel);
				userManager.updateTimestampSalt(null, null, tel);
				HashMap<String, String> res_data = new HashMap<>();
				res_data.put("token", newToken);
				res_data.put("uuid", uuid);
				response.getWriter().write(JsonEncodeFormatter.parse(0, res_data));
				return;
			}
			else {
				response.getWriter().write(JsonEncodeFormatter.universalResponse(90008, "Invalid pwd."));
			}
			
		}
		else {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90007, "Invalid tel"));
			return;
		}
	}
	
	@SuppressWarnings("unchecked")
	private void token_login(HashMap<String, Object> params , HttpServletResponse response) throws IOException{
		HashMap<String, String> data = (HashMap<String, String>)params.get("data");
		String token = data.get("token");
		UserManager userManager = new UserManager();
		if(userManager.checkToken(token)) {
			User cUser = userManager.getBasicProfile(token);
			HashMap<String, String> res_data = new HashMap<>();
			res_data.put("uuid", cUser.getUuid());
			response.getWriter().write(JsonEncodeFormatter.parse(0, res_data));
			return;
		}
		else {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90009, "Token Invalid or expired."));
			return;
		}
		
	}

}
