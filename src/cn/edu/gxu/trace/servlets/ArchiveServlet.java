package cn.edu.gxu.trace.servlets;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import cn.edu.gxu.trace.entity.Archive;
import cn.edu.gxu.trace.entity.User;
import cn.edu.gxu.trace.manager.ArchiveManager;
import cn.edu.gxu.trace.manager.UserManager;
import cn.edu.gxu.trace.mangoUtils.JsonDecodeFormatter;
import cn.edu.gxu.trace.mangoUtils.JsonEncodeFormatter;
import cn.edu.gxu.trace.mangoUtils.POST2String;
import cn.edu.gxu.trace.mangoUtils.JsonDecodeFormatter.dataType;

/**
 * Servlet implementation class ArchiveServlet
 */
@WebServlet("/api/archive")
public class ArchiveServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(ArchiveServlet.class);

   
    public ArchiveServlet() {
        super();
    }

	
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
			//新增种植档案
			case 10016:{
				toAddNewArchive(params, response);
				return;
			}
			//结束种植周期
			case 10017:{
				toFinishArchive(params,response);
				return;
			}
			//获取指定基地的所有种植档案
			case 10018:{
				toGetArchiveProfiles(params,response);
				return;
			}
			case 10025:{
				toUpdateRate(params,response);
				return;
			}
			default:{
				response.getWriter().write(JsonEncodeFormatter.universalResponse(90001, "Invalid Request Code."));
				return;	
			}
		}
	}


	@SuppressWarnings("unchecked")
	private void toAddNewArchive(HashMap<String, Object> params, HttpServletResponse response) throws IOException{
		HashMap<String, String> data = (HashMap<String, String>)params.get("data");
		String token = data.get("token");
				
		UserManager userManager = new UserManager();
		ArchiveManager archiveManager = new ArchiveManager();
		
		//检查token，验证用户合法性
		if(userManager.checkToken(token)) {
			//检查用户权限
			if(userManager.getAccessLevel(token)!=User.type.COMMON) {
				response.getWriter().write(JsonEncodeFormatter.universalResponse(90011, "Operation forbidden."));
				return;
			}
			Archive cArchive = new Archive();
			cArchive.setFruit_id(Integer.valueOf(data.get("fruit_id")));
			cArchive.setPlant_t(data.get("plant_t"));
			cArchive.setBase_id(data.get("base_id"));
			if(archiveManager.addNewArchive(cArchive)) {
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
	

	@SuppressWarnings("unchecked")
	private void toFinishArchive(HashMap<String, Object> params, HttpServletResponse response) throws IOException {
		HashMap<String, String> data = (HashMap<String, String>)params.get("data");
		String token = data.get("token");
				
		UserManager userManager = new UserManager();
		ArchiveManager archiveManager = new ArchiveManager();
		
		//检查token，验证用户合法性
		if(userManager.checkToken(token)) {
			//检查用户权限
			if(userManager.getAccessLevel(token)!=User.type.COMMON) {
				response.getWriter().write(JsonEncodeFormatter.universalResponse(90011, "Operation forbidden."));
				return;
			}
			String archive_id = data.get("archive_id");
			String plant_ent_t = String.valueOf(System.currentTimeMillis()/1000);
			if(archiveManager.updatePlant_end_t(plant_ent_t, archive_id)) {
				response.getWriter().write(JsonEncodeFormatter.universalResponse(0, "success"));
				return;
			}
			else {
				response.getWriter().write(JsonEncodeFormatter.universalResponse(90006, "Illegal parameters OR Archive already finished."));
				return;
			}
		}
		else {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90009, "Token Invalid or expired."));
			return;
		}		
	}
	
	@SuppressWarnings("unchecked")
	private void toGetArchiveProfiles(HashMap<String, Object> params, HttpServletResponse response) throws IOException {
		HashMap<String, String> data = (HashMap<String, String>)params.get("data");
		String token = data.get("token");
				
		UserManager userManager = new UserManager();
		ArchiveManager archiveManager = new ArchiveManager();
		
		//检查token，验证用户合法性
		if(userManager.checkToken(token)) {
			//检查用户权限
			if(userManager.getAccessLevel(token)!=User.type.COMMON) {
				response.getWriter().write(JsonEncodeFormatter.universalResponse(90011, "Operation forbidden."));
				return;
			}
			String base_id = data.get("base_id");
			ArrayList<Archive> res_archives = new ArrayList<>();
			res_archives = archiveManager.getArchiveByBase_id(base_id);
			
			//信息汇总
			ArrayList<HashMap<String, String>> res_data = new ArrayList<>();
			Iterator<Archive> iterator = res_archives.iterator();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			while(iterator.hasNext()) {
				Archive tArchive = iterator.next();
				String plant_t = sdf.format(new Date(Long.valueOf(tArchive.getPlant_t())*1000));
				String plant_end_t = sdf.format(new Date(Long.valueOf(tArchive.getPlant_end_t())*1000));
				tArchive.setPlant_t(plant_t);
				tArchive.setPlant_end_t(plant_end_t);
				res_data.add(tArchive.toHashMap());
			}
			response.getWriter().write(JsonEncodeFormatter.parse(0, res_data));
			return;
		}
		else {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90009, "Token Invalid or expired."));
			return;
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private void toUpdateRate(HashMap<String, Object> params, HttpServletResponse response) throws IOException{
		HashMap<String, String> data = (HashMap<String, String>)params.get("data");
		String archive_id = data.get("archive_id");
		String rate = data.get("rate");
				
		ArchiveManager archiveManager = new ArchiveManager();
		
		if(archiveManager.updateRate(archive_id, Float.valueOf(rate))) {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(0, "success."));
			return;
		}
		else {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90006, "Illegal Parameter."));
			return;
		}
	}

}
