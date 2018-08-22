package cn.edu.gxu.trace.servlets;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import cn.edu.gxu.trace.db.MutiTableResolver;
import cn.edu.gxu.trace.manager.UniversalManager;
import cn.edu.gxu.trace.manager.UserManager;
import cn.edu.gxu.trace.mangoUtils.JsonDecodeFormatter;
import cn.edu.gxu.trace.mangoUtils.JsonEncodeFormatter;
import cn.edu.gxu.trace.mangoUtils.POST2String;
import cn.edu.gxu.trace.mangoUtils.JsonDecodeFormatter.dataType;

/**
 * Servlet implementation class RecordServlet
 */
@WebServlet({ "/api/record/get", "/api/record/consumer" })
public class RecordServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(RecordServlet.class);
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RecordServlet() {
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
			case 10023:{
				//获取指定传感器的历史数据：按照小时、日、月或固定时间范围查询
				getRecordBySensorSIM(params,response);
				return;
			}
			case 10024:{
				getArchiveForConsumer(params,response);
				return;
			}
			default:{
				response.getWriter().write(JsonEncodeFormatter.universalResponse(90001, "Invalid Request Code."));
				return;	
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void getArchiveForConsumer(HashMap<String, Object> params, HttpServletResponse response) throws IOException {
		HashMap<String, String> data = (HashMap<String, String>)params.get("data");
		String archive_id = data.get("archive_id");
		
		HashMap<String, String> briefSummary_map = UniversalManager.getArchiveSummary_info(archive_id);
		
		if(briefSummary_map!=null) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String from_t=null;
			String to_t=null;
			//如果种植周期为结束，则使用当前时间作为结束时间
			try {
				from_t = String.valueOf(simpleDateFormat.parse(briefSummary_map.get("archive_plant_t")).getTime()/1000);
				to_t = String.valueOf(simpleDateFormat.parse(briefSummary_map.get("archive_plant_end_t")).getTime()/1000);
			} catch (ParseException | NullPointerException e) {
				to_t = String.valueOf(System.currentTimeMillis()/1000);
			}
			
			ArrayList<HashMap<String, String>> ecd_data = UniversalManager.getArchiveSummary_data(archive_id, from_t, to_t);
			response.getWriter().write(JsonEncodeFormatter.parse(0, briefSummary_map,ecd_data));
			return;
		}
		else {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90006, "Illegal Parameter."));
			return;
		}
		
	}

	@SuppressWarnings("unchecked")
	private void getRecordBySensorSIM(HashMap<String, Object> params, HttpServletResponse response) throws IOException {
		HashMap<String, String> data = (HashMap<String, String>)params.get("data");
		String token = data.get("token");
		String sim = data.get("sim");
		String interval = data.get("interval");
		String from_t = data.get("from_t");
		String to_t = data.get("to_t");
		String limit = data.get("limit");
				
		UserManager userManager = new UserManager();
		//检查token，验证用户合法性
		if(userManager.checkToken(token)) {
			//以interval字段是否存在判断区分两种操作
			
			ArrayList<HashMap<String, String>> res = new ArrayList<>();
			
			//1.以当前时刻为基准向前查询
			if(interval!=null) {
				int limit_n = Integer.valueOf(limit);
				int interval_n = Integer.valueOf(interval);
				for(int index = 0;index<limit_n+1;index++) {
					String sql = String.format("call getRecordBySensorSIM('%s',%d,%d,null);", sim,index,interval_n);
					log.debug(sql);
					HashMap<String, String> res_row = MutiTableResolver.query4row(sql);
					res.add(res_row);
				}
			}
			//2.以起始和结束时间戳为基准查询，默认时间间隔为单日
			else if (from_t!=null&& to_t!=null){
				long from_t_l = Long.valueOf(from_t);
				long to_t_l = Long.valueOf(to_t);
				log.debug(from_t_l);
				log.debug(to_t_l);
				for(long index = from_t_l; index<to_t_l;index+=60*60*24) {
					log.debug(String.valueOf(index));
					String sql = String.format("call getRecordBySensorSIM('%s',null,3,'%s');", sim,String.valueOf(index));
					log.debug(sql);
					HashMap<String, String> res_row = MutiTableResolver.query4row(sql);
					res.add(res_row);
				}
			}
			else {
				response.getWriter().write(JsonEncodeFormatter.universalResponse(90012, "Implicit operation."));
				return;
			}
			
			//消息汇总
			response.getWriter().write(JsonEncodeFormatter.parse(0, res));
			return;
		}
		else {
			response.getWriter().write(JsonEncodeFormatter.universalResponse(90001, "Invalid Request Code."));
			return;	
		}
	}

}
