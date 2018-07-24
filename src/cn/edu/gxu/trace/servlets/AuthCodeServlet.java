package cn.edu.gxu.trace.servlets;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import cn.edu.gxu.trace.aliyun.AliSmsSender;
import cn.edu.gxu.trace.manager.UserManager;
import cn.edu.gxu.trace.mangoUtils.AuthCodeCreator;
import cn.edu.gxu.trace.mangoUtils.JsonDecodeFormatter;
import cn.edu.gxu.trace.mangoUtils.JsonEncodeFormatter;
import cn.edu.gxu.trace.mangoUtils.POST2String;
import cn.edu.gxu.trace.mangoUtils.JsonDecodeFormatter.dataType;

@WebServlet("/api/authcode")
public class AuthCodeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(AuthCodeServlet.class);
	private static final String TEL_REX = "^1([358][0-9]|4[579]|66|7[0135678]|9[89])[0-9]{8}$"; 
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AuthCodeServlet() {
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
			case 10001:{
				//请求注册的短信验证码
				requestRegisterAuthCode(params, response);
				return;
			}
			case 10002:{
				//验证注册
				verifyRegister(params, response);
				return;
			}
			case 10003:{
				//首次强制设置密码
				forcedPresetPassword(params, response);
				return;
			}
			default:{
				response.getWriter().write(JsonEncodeFormatter.universalResponse(90001, "Invalid Request Code."));
				return;	
			}
			
		}
	}
	
	@SuppressWarnings("unchecked")
	private void requestRegisterAuthCode(HashMap<String, Object> params, HttpServletResponse response) throws IOException {
		HashMap<String, String> data = (HashMap<String, String>)params.get("data");
		String tel = data.get("tel");
		//强正则表达式进行校验,不满足的将拒绝，
		// TODO 测试通过时间
		if(tel.matches(AuthCodeServlet.TEL_REX)==false) {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90002, "Illegal Phone Number."));
			return;	
		}
		
		String authCode = AuthCodeCreator.create();
		if(AliSmsSender.sendAuthCodeSms(tel, authCode)) {
			UserManager userManager = new UserManager();
			if(userManager.registerNewUser(tel, authCode)) {
				response.getWriter().write(JsonEncodeFormatter.universalResponse(0, "success"));
			}
			else {
				response.getWriter().write(JsonEncodeFormatter.universalResponse(90003, "Phone Number Existed OR AuthCode NOT Expired! Tell client input auth code again."));
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void verifyRegister(HashMap<String, Object> params , HttpServletResponse response) throws IOException {
		HashMap<String, String> data = (HashMap<String, String>)params.get("data");
		String tel = data.get("tel");
		String authCode = data.get("authCode");
		UserManager userManager = new UserManager();
		if(userManager.verifyRegister(tel, authCode)) {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(0, "success"));
			return;
		}
		else {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90004, "Wrong Auth Code.Register Failed."));
			return;
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private void forcedPresetPassword(HashMap<String, Object> params, HttpServletResponse response) throws IOException {
		HashMap<String, String> data = (HashMap<String, String>)params.get("data");
		String tel = data.get("tel");
		String pwdSHA512 = data.get("SHApwd");
		UserManager userManager = new UserManager();
		String newToken = userManager.forcedPresetPassword(pwdSHA512, tel);
		if(newToken!=null) {
			HashMap<String, String> res_addition = new HashMap<>();
			HashMap<String, String> res_data = new HashMap<>();
			
			res_addition.put("timestamp", String.valueOf(System.currentTimeMillis()));
			res_data.put("token", newToken);
			response.getWriter().write(JsonEncodeFormatter.parse(0, res_addition,res_data));
			return;
		}
		else {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90005, "Not New User.Preset Password NOT Allowed."));
			return;
		}
	}

}
