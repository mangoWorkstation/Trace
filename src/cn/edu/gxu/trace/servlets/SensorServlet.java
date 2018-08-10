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

import cn.edu.gxu.trace.entity.Record;
import cn.edu.gxu.trace.entity.Sensor;
import cn.edu.gxu.trace.manager.RecordManager;
import cn.edu.gxu.trace.manager.SensorManager;
import cn.edu.gxu.trace.manager.UserManager;
import cn.edu.gxu.trace.mangoUtils.JsonDecodeFormatter;
import cn.edu.gxu.trace.mangoUtils.JsonEncodeFormatter;
import cn.edu.gxu.trace.mangoUtils.POST2String;
import cn.edu.gxu.trace.mangoUtils.JsonDecodeFormatter.dataType;


@WebServlet("/api/sensor")
public class SensorServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(SensorServlet.class);

      
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SensorServlet() {
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
			case 10019:{
				//传感器上传数据接口
				toUploadDataReport(params,response);
				return;
			}
			case 10020:{
				//传感器绑定、变更或解绑基地
				toBindWithBase(params,response);
				return;
			}
			case 10021:{
				//获取传感器实时信息：按基地id查询或SIM卡号查询单个传感器
				getSensorCurrentInfo(params,response);
				return;
			}
			case 10022:{
				//将传感器绑定到指定水果批次
				toBindWithArchive(params,response);
				return;
			}
			default:{
				response.getWriter().write(JsonEncodeFormatter.universalResponse(90001, "Invalid Request Code."));
				return;	
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void toUploadDataReport(HashMap<String, Object> params, HttpServletResponse response) throws IOException {
		HashMap<String, String> data = (HashMap<String, String>)params.get("data");
		
		String sim = data.get("sim");
		SensorManager sensorManager = new SensorManager();
		RecordManager recordManager = new RecordManager();
		Sensor cSensor = sensorManager.getSensorProfileBySIM(sim);
		//检查该传感器是否已被注册
		if(cSensor!=null) {
			//检查该传感器是否绑定了水果批次，如果没有将自动丢弃将要上传数据
			if(cSensor.getArchive_id()!=null) {
				Record cRecord = new Record();
				cRecord.setSensorSIM(sim);
				cRecord.setArchive_id(cSensor.getArchive_id());
				cRecord.setTimestamps(data.get("timestamp_created"));
				cRecord.setTemp_air(Double.valueOf(data.get("at")));
				cRecord.setTemp_soil(Double.valueOf(data.get("st")));
				cRecord.setHumidity_air(Double.valueOf(data.get("ah")));
				cRecord.setHumidity_soil(Double.valueOf(data.get("sh")));
				cRecord.setLux(Double.valueOf(data.get("lux")));
				if(recordManager.addNewRecord(cRecord)) {
					response.getWriter().write(JsonEncodeFormatter.universalResponse(0, "ok"));
					return;
				}
				else {
					response.getWriter().write(JsonEncodeFormatter.universalResponse(90006, "Illegal parameters.Check Again."));
					return;
				}
			}
		}
		else {
			sensorManager.addNewSensor(sim);
			response.getWriter().write(JsonEncodeFormatter.universalResponse(0, "Register ok."));
			return;
		}
				
	}
	
	@SuppressWarnings("unchecked")
	private void toBindWithBase(HashMap<String, Object> params, HttpServletResponse response) throws IOException{
		HashMap<String, String> data = (HashMap<String, String>)params.get("data");
		String token = data.get("token");
				
		UserManager userManager = new UserManager();
		SensorManager sensorManager = new SensorManager();
		
		
		//检查token，验证用户合法性
		if(userManager.checkToken(token)) {
			String sim = data.get("sensor_sim");
			String base_id = data.get("base_id");
			//解绑基地
			if(base_id==null) {
				if(sensorManager.bindWithBase(sim, null)) {
					response.getWriter().write(JsonEncodeFormatter.universalResponse(0, "unbinded."));
					return;
				}
				else {
					response.getWriter().write(JsonEncodeFormatter.universalResponse(90006, "Illegal parameters.Check Again."));
					return;
				}
			}
			//绑定或变更基地
			else {
				if(sensorManager.bindWithBase(sim, base_id)) {
					response.getWriter().write(JsonEncodeFormatter.universalResponse(0, "binded or altered."));
					return;
				}
				else {
					response.getWriter().write(JsonEncodeFormatter.universalResponse(90006, "Illegal parameters.Check Again."));
					return;
				}
			}
		}
		else {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90009, "Token Invalid or expired."));
			return;
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private void getSensorCurrentInfo(HashMap<String, Object> params, HttpServletResponse response) throws IOException{
		HashMap<String, String> data = (HashMap<String, String>)params.get("data");
		String token = data.get("token");
		UserManager userManager = new UserManager();
		SensorManager sensorManager = new SensorManager();
		
		
		//检查token，验证用户合法性
		if(userManager.checkToken(token)) {
			String base_id = data.get("base_id");
			String sim = data.get("sim");
			if(base_id!=null&& sim!=null) {
				response.getWriter().write(JsonEncodeFormatter.universalResponse(90012, "Implicit Operation."));
				return;
			}
			if(base_id!=null) {
				ArrayList<Sensor> sensors = sensorManager.getSensorsByBase_id(base_id);
				ArrayList<HashMap<String, String>> res_data = new ArrayList<>();
				Iterator<Sensor> iterator = sensors.iterator();
				while(iterator.hasNext()) {
					Sensor tSensor = iterator.next();
					res_data.add(tSensor.toHashMap());
				}
				//信息汇总
				response.getWriter().write(JsonEncodeFormatter.parse(0, res_data));
				return;
			}
			if(sim!=null) {
				ArrayList<HashMap<String, String>> res_data = new ArrayList<>();
				Sensor cSensor = sensorManager.getSensorProfileBySIM(sim);
				if(cSensor!=null) {
					res_data.add(cSensor.toHashMap());
					response.getWriter().write(JsonEncodeFormatter.parse(0, res_data));
					return;
				}
				else {
					response.getWriter().write(JsonEncodeFormatter.universalResponse(90006, "Illegal Parameters."));
					return;
				}
			}
			
		}else {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90009, "Token Invalid or expired."));
			return;
		}
	}
	
	@SuppressWarnings("unchecked")
	private void toBindWithArchive(HashMap<String, Object> params, HttpServletResponse response) throws IOException {
		HashMap<String, String> data = (HashMap<String, String>)params.get("data");
		String token = data.get("token");
		UserManager userManager = new UserManager();
		SensorManager sensorManager = new SensorManager();
		
		
		//检查token，验证用户合法性
		if(userManager.checkToken(token)) {
			String sim = data.get("sim");
			String archive_id = data.get("archive_id");
			if(sensorManager.bindArchive(sim, archive_id)) {
				response.getWriter().write(JsonEncodeFormatter.universalResponse(0, "success."));
				return;
			}
			else {
				response.getWriter().write(JsonEncodeFormatter.universalResponse(90006, "Illegal parameters."));
				return;
			}
		}else {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90009, "Token Invalid or expired."));
			return;
		}
		
	}

}
