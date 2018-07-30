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

import cn.edu.gxu.trace.entity.Fruit;
import cn.edu.gxu.trace.entity.User;
import cn.edu.gxu.trace.manager.FruitManager;
import cn.edu.gxu.trace.manager.UserManager;
import cn.edu.gxu.trace.mangoUtils.JsonDecodeFormatter;
import cn.edu.gxu.trace.mangoUtils.JsonEncodeFormatter;
import cn.edu.gxu.trace.mangoUtils.POST2String;
import cn.edu.gxu.trace.mangoUtils.JsonDecodeFormatter.dataType;

/**
 * Servlet implementation class FruitServlet
 */
@WebServlet("/api/fruit")
public class FruitServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(FruitServlet.class);

    
    public FruitServlet() {
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
			//新增水果信息（超级管理员操作）
			case 10013:{
				toAddNewFruit(params, response);
				return;
			}
			case 10014:{
				toGetFruitProfle(params,response);
				return;
			}
			//更新水果信息（超级管理员操作）
			case 10015:{
				toUpdateFruitProfile(params,response);
				return;
			}
			default:{
				response.getWriter().write(JsonEncodeFormatter.universalResponse(90001, "Invalid Request Code."));
				return;	
			}
		}
	}


	@SuppressWarnings("unchecked")
	private void toAddNewFruit(HashMap<String, Object> params, HttpServletResponse response) throws IOException {
		HashMap<String, String> data = (HashMap<String, String>)params.get("data");
		String token = data.get("token");
				
		UserManager userManager = new UserManager();
		FruitManager fruitManager = new FruitManager();
		
		//检查token，验证用户合法性
		if(userManager.checkToken(token)) {
			//检查用户权限
			if(User.type.ROOT.equals(userManager.getAccessLevel(token))) {
				Fruit cFruit = new Fruit();
				cFruit.setName(data.get("name"));
				cFruit.setCategory(data.get("category"));
				cFruit.setOnSale_t(data.get("onSale_t"));
				cFruit.setOffSale_t(data.get("offSale_t"));
				cFruit.setContent(data.get("content"));
				if(fruitManager.addNewFruit(cFruit)) {
					response.getWriter().write(JsonEncodeFormatter.universalResponse(0, "success."));
					return;
				}
				else {
					response.getWriter().write(JsonEncodeFormatter.universalResponse(90006, "Illegal parameters.Check Again."));
					return;
				}
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
	private void toUpdateFruitProfile(HashMap<String, Object> params, HttpServletResponse response) throws IOException {
		HashMap<String, String> data = (HashMap<String, String>)params.get("data");
		String token = data.get("token");
				
		UserManager userManager = new UserManager();
		FruitManager fruitManager = new FruitManager();
		
		//检查token，验证用户合法性
		if(userManager.checkToken(token)) {
			//检查用户权限
			if(User.type.ROOT.equals(userManager.getAccessLevel(token))) {
				Fruit cFruit = new Fruit();
				cFruit.setUid(Integer.valueOf(data.get("fruit_id")));
				cFruit.setName(data.get("name"));
				cFruit.setCategory(data.get("category"));
				cFruit.setOnSale_t(data.get("onSale_t"));
				cFruit.setOffSale_t(data.get("offSale_t"));
				cFruit.setContent(data.get("content"));
				if(fruitManager.updateFruitProfile(cFruit)) {
					response.getWriter().write(JsonEncodeFormatter.universalResponse(0, "success."));
					return;
				}
				else {
					response.getWriter().write(JsonEncodeFormatter.universalResponse(90006, "Illegal parameters.Check Again."));
					return;
				}
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
	private void toGetFruitProfle(HashMap<String, Object> params, HttpServletResponse response) throws IOException{
		HashMap<String, String> data = (HashMap<String, String>)params.get("data");
		String token = data.get("token");
		String fruit_id = data.get("fruit_id");
		String name = data.get("name");
		
		if(fruit_id!=null&&name!=null) {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90012, "Implicit operation."));
			return;
		}
				
		UserManager userManager = new UserManager();
		FruitManager fruitManager = new FruitManager();
		ArrayList<Fruit> res_fruits = new ArrayList<>();
		
		//检查token，验证用户合法性
		if(userManager.checkToken(token)) {
			//查询全部水果信息
			if(fruit_id==null&&name==null) {
				res_fruits = fruitManager.getFruitProfile("");
			}
			//根据id或名称查询
			else {
				res_fruits = fruitManager.getFruitProfile(fruit_id==null?name:fruit_id);
			}
			
			//信息汇总
			ArrayList<HashMap<String, String>> res_data = new ArrayList<>();
			Iterator<Fruit> iterator = res_fruits.iterator();
			while(iterator.hasNext()) {
				Fruit tFruit = iterator.next();
				res_data.add(tFruit.toHashMap());
			}
			response.getWriter().write(JsonEncodeFormatter.parse(0, res_data));
			return;
			
		}
		else {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90009, "Token Invalid or expired."));
		}
		
	}


}
