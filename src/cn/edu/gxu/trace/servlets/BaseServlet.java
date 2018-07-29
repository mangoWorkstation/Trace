package cn.edu.gxu.trace.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import cn.edu.gxu.trace.entity.Base;
import cn.edu.gxu.trace.entity.User;
import cn.edu.gxu.trace.manager.BaseManager;
import cn.edu.gxu.trace.manager.UserManager;
import cn.edu.gxu.trace.mangoUtils.JsonDecodeFormatter;
import cn.edu.gxu.trace.mangoUtils.JsonEncodeFormatter;
import cn.edu.gxu.trace.mangoUtils.POST2String;
import cn.edu.gxu.trace.mangoUtils.JsonDecodeFormatter.dataType;

/**
 * Servlet implementation class BaseServlet
 */
@WebServlet("/api/base")
public class BaseServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(BaseServlet.class);

       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BaseServlet() {
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
			//新增基地（超级管理员操作）
			case 10008:{
				toAddNewBase(params, response);
				return;
			}
			//根据用户uuid，获取其旗下所有基地的信息
			case 10009:{
				getBaseProfileByUser(params,response);
				return;
			}
			//根据基地id和名称关键字查询基地信息
			case 10010:{
				getBaseProfileByKey(params,response);
				return;
			}
			//绑定基地（超级管理员操作）
			case 10011:{
				alterBaseRelationship(params,response);
				return;
			}
			//更新基地的基础信息
			case 10012:{
				updateBaseProfile(params,response);
				return;
			}
			default:{
				response.getWriter().write(JsonEncodeFormatter.universalResponse(90001, "Invalid Request Code."));
				return;	
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void toAddNewBase(HashMap<String, Object> params, HttpServletResponse response) throws IOException	{
		HashMap<String, String> data = (HashMap<String, String>)params.get("data");
		String token = data.get("token");
				
		UserManager userManager = new UserManager();
		
		//检查token，验证用户合法性
		if(userManager.checkToken(token)) {
			//对账户进行权限检查
			if(User.type.ROOT.equals(userManager.getAccessLevel(token))) {
				Base newBase = new Base();
				newBase.setUid(UUID.randomUUID().toString());
				newBase.setName(data.get("name"));
				newBase.setArea(Double.valueOf(data.get("area")));
				newBase.setStandard(data.get("standard"));
				newBase.setAddress(data.get("address"));
				newBase.setProvince(data.get("province"));
				newBase.setCity(data.get("city"));
				newBase.setCounty(data.get("county"));
				newBase.setFruitType_id(Integer.valueOf(data.get("fruitType_id")));
				newBase.setVideoStream_url(data.get("videoStream_url")==null?null:data.get("videoStream_url"));
				
				BaseManager baseManager = new BaseManager();
				if(baseManager.addNewBase(newBase)) {
					response.getWriter().write(JsonEncodeFormatter.universalResponse(0, "success."));
					return;
				}
				else {
					response.getWriter().write(JsonEncodeFormatter.universalResponse(99999, "Internal Error on Server."));
					return;
				}
				
			}
			else {
				response.getWriter().write(JsonEncodeFormatter.universalResponse(90010, "Authority limited."));
			}
		}
		else {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90009, "Token Invalid or expired."));
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private void getBaseProfileByUser(HashMap<String, Object> params, HttpServletResponse response) throws IOException {
		HashMap<String, String> data = (HashMap<String, String>)params.get("data");
		String token = data.get("token");
		String uuid = data.get("uuid");
				
		UserManager userManager = new UserManager();
		BaseManager baseManager = new BaseManager();
		ArrayList<Base> result_arr = null;
		
		//检查token，验证用户合法性
		if(userManager.checkToken(token)) {
			User cUser = userManager.getBasicProfile(token);
			//如果是普通用户查询
			if(cUser.getAccessLevel()==1) {
				if(token.compareTo(cUser.getToken())==0 && uuid.compareTo(cUser.getUuid())==0) {
					result_arr = baseManager.getBaseProfileByUUID(uuid);
				}
				else {
					response.getWriter().write(JsonEncodeFormatter.universalResponse(90010, "Authority limited."));
					return;
				}
			}
			//如果是超级管理员查询
			else {
				//管理员不可查询本人的基地
				if(token.compareTo(cUser.getToken())==0 && uuid.compareTo(cUser.getUuid())==0) {
					response.getWriter().write(JsonEncodeFormatter.universalResponse(90012, "Operation forbidden."));
					return;
				}
				else {
					result_arr = baseManager.getBaseProfileByUUID(uuid);
				}
			}
			
			//信息组合拼接
			Iterator<Base> baseIterator = result_arr.iterator();
			ArrayList<HashMap<String, String>> res_data = new ArrayList<>();
			while(baseIterator.hasNext()) {
				Base b = baseIterator.next();
				res_data.add(b.toHashMap());
			}
			response.getWriter().write(JsonEncodeFormatter.parse(0, res_data));
			return;
			
		}
		else {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90009, "Token Invalid or expired."));
		}
	}
	
	@SuppressWarnings("unchecked")
	private void getBaseProfileByKey(HashMap<String, Object> params, HttpServletResponse response) throws IOException{
		HashMap<String, String> data = (HashMap<String, String>)params.get("data");
		String token = data.get("token");
		String base_id = data.get("base_id")==null?null:data.get("base_id");
		String name = data.get("name")==null?null:data.get("name");
		
		if(base_id!=null&&name!=null) {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90012, "Implicit operation."));
			return;
		}
				
		UserManager userManager = new UserManager();
		BaseManager baseManager = new BaseManager();
		ArrayList<Base> result_arr = new ArrayList<>();
		
		//检查token，验证用户合法性
		if(userManager.checkToken(token)) {
			//通过id查询
			if(base_id!=null) {
				Base b = baseManager.getBaseProfileByBase_id(base_id);
				result_arr.add(b);
			}
			//通过名字查询
			if(name!=null) {
				result_arr = baseManager.getBaseProfileByBase_name(name);
			}
			
			//组合信息拼接
			Iterator<Base> baseIterator = result_arr.iterator();
			ArrayList<HashMap<String, String>> res_data = new ArrayList<>();
			while(baseIterator.hasNext()) {
				Base b = baseIterator.next();
				res_data.add(b.toHashMap());
			}
			response.getWriter().write(JsonEncodeFormatter.parse(0, res_data));
			return;
		}
		else {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90009, "Token Invalid or expired."));
		}
	}
	
	@SuppressWarnings("unchecked")
	private void alterBaseRelationship(HashMap<String, Object> params, HttpServletResponse response) throws IOException {
		HashMap<String, String> data = (HashMap<String, String>)params.get("data");
		String token = data.get("token");
		String base_id = data.get("base_id");
		String uuid = data.get("uuid");
				
		UserManager userManager = new UserManager();
		BaseManager baseManager = new BaseManager();
		
		//检查token，验证用户合法性
		if(userManager.checkToken(token)) {
			//检查是否超级管理员
			if(User.type.ROOT == userManager.getAccessLevel(token)) {
				if(uuid==null) {
					if(baseManager.updateOwner_id(base_id, null)) {
						response.getWriter().write(JsonEncodeFormatter.universalResponse(0, "unbinded."));
						return;
					}
					else {
						response.getWriter().write(JsonEncodeFormatter.universalResponse(90013, "Invalid base_id or uuid."));
						return;
					}
				}
				else {
					if(baseManager.updateOwner_id(base_id, uuid)) {
						response.getWriter().write(JsonEncodeFormatter.universalResponse(0, "binded or altered."));
						return;
					}
					else {
						response.getWriter().write(JsonEncodeFormatter.universalResponse(90013, "Invalid base_id or uuid."));
						return;
					}
				}
			}
			else {
				response.getWriter().write(JsonEncodeFormatter.universalResponse(90010, "Authority limited."));
				return;
			}	
		}
		else {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90009, "Token Invalid or expired."));
			return;
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private void updateBaseProfile(HashMap<String, Object> params, HttpServletResponse response) throws IOException {
		HashMap<String, String> data = (HashMap<String, String>)params.get("data");
		String token = data.get("token");
				
		UserManager userManager = new UserManager();
		
		//检查token，验证用户合法性
		if(userManager.checkToken(token)) {
			Base b = new Base();
			b.setUid(data.get("base_id"));
			b.setName(data.get("name"));
			b.setArea(Double.valueOf(data.get("area")));
			b.setStandard(data.get("standard"));
			b.setAddress(data.get("address"));
			b.setProvince(data.get("province"));
			b.setCity(data.get("city"));
			b.setCounty(data.get("county"));
			b.setFruitType_id(Integer.valueOf(data.get("fruitType_id")));
			b.setVideoStream_url(data.get("videoStream_url")==null?null:data.get("videoStream_url"));
			
			BaseManager baseManager = new BaseManager();
			if(baseManager.updateBaseProfile(b)) {
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
		}		
	}
	

}
