package cn.edu.gxu.trace.manager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

//import org.apache.log4j.Logger;

import cn.edu.gxu.trace.db.MutiTableResolver;
import cn.edu.gxu.trace.entity.Archive;
import cn.edu.gxu.trace.entity.Base;
import cn.edu.gxu.trace.entity.Fruit;
import cn.edu.gxu.trace.entity.User;

/**
 * 用于处理多表联查的汇总数据
 * @author 芒果君
 * @since 2018-08-10
 */
public class UniversalManager {
//	private static Logger log = Logger.getLogger(UniversalManager.class);

	/**
	 * 将提供与水果种植档案所关联的信息汇总
	 * @return 汇总信息，详见API文档第10.2章
	 */
	public static HashMap<String, String> getArchiveSummary_info(String archive_id){
		HashMap<String, String> e = new HashMap<>();
		
		ArchiveManager archiveManager = new ArchiveManager();
		FruitManager fruitManager = new FruitManager();
		BaseManager baseManager = new BaseManager();
		UserManager userManager = new UserManager();
		
		
		Archive cArchive = archiveManager.getArchiveByUid(archive_id);
		
		if(cArchive!=null) {
			e.put("archive_plant_t", cArchive.getPlant_t());
			e.put("archive_plant_end_t", cArchive.getPlant_end_t());
			
			Fruit cFruit = fruitManager.getFruitProfile(String.valueOf(cArchive.getFruit_id())).get(0);
			Base cBase = baseManager.getBaseProfileByBase_id(cArchive.getBase_id());
			User cUser = userManager.getBasicProfile(cBase.getOwner_id());
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			e.put("archive_plant_t", sdf.format(new Date(Long.valueOf(cArchive.getPlant_t())*1000)));
			if(cArchive.getPlant_end_t()!=null) {
				e.put("archive_plant_end_t", sdf.format(new Date(Long.valueOf(cArchive.getPlant_end_t())*1000)));
			}
			e.put("fruit_name", cFruit.getName());
			e.put("fruit_category", cFruit.getCategory());
			e.put("fruit_onSale_t", cFruit.getOnSale_t());
			e.put("fruit_offSale_t", cFruit.getOffSale_t());
			e.put("fruit_content", cFruit.getContent());
			
			e.put("base_name", cBase.getName());
			e.put("base_area", String.valueOf(cBase.getArea()));
			e.put("base_standard", cBase.getStandard());
			e.put("base_address", cBase.getAddress());
			e.put("base_province", cBase.getProvince());
			e.put("base_city", cBase.getCity());
			e.put("base_county", cBase.getCounty());
			e.put("base_snapshot_filePath", cBase.getSnapshot_filePath());
			e.put("producer_tel", cUser.getTel());
			e.put("producer_name", cUser.getName());
			e.put("producer_address", cUser.getAddress());
			
			return e;
		}
		else {
			return null;
		}
		
	}
	
	/**
	 * 获取指定水果种植档案的环境数据汇总
	 * @param archive_id 种植档案id
	 * @param from_t 起始时间戳
	 * @param to_t  结束时间戳
	 * @return
	 */
	public static ArrayList<HashMap<String, String>> getArchiveSummary_data(String archive_id,String from_t,String to_t){
		ArrayList<HashMap<String, String>> e = new ArrayList<>();
		
		//因数据量暂时不足，暂定时间间隔为1日
		long interval = 24*60*60;
		long from_t_i = Integer.valueOf(from_t);
		long to_t_i = Integer.valueOf(to_t);
		
		for(long index = from_t_i;index<=to_t_i;index+=interval) {
			String sql = String.format("call getArchiveSummaryByTimeInterval('%s','%s','%s');", archive_id,String.valueOf(index),String.valueOf(index+interval));
			HashMap<String, String> eHashMap = MutiTableResolver.query4row(sql);
			e.add(eHashMap);
		}
		
		return e;
	}
	
	/**
	 * 获取指定用户接收的警报信息和系统通知
	 * @param startIndex 起始分页码
	 * @param limit	每页显示数量
	 * @param isRead 是否已读
	 * @param receiver_id 用户uuid
	 * @return
	 */
	public static ArrayList<HashMap<String, String>> getMsg4CurrentUser(int startIndex,int limit,int isRead,String receiver_id){
		ArrayList<HashMap<String, String>> raw = new ArrayList<>();
		
		//检查起始页码
		if(startIndex<=0||limit<=0) {
			return null;
		}
		
		//计算起始页码
		startIndex = (startIndex-1)*limit;
		
		try {
			String sql = String.format("select * from MSGBOX left join USER on MSGBOX.sender_id = USER.uuid "
					+ "where (receiver_id = '%s' or receiver_id = 'all') and isResolved is NULL and isRead = %d order by timestamps_created desc "
					+ "limit %d,%d;", receiver_id,isRead,startIndex,limit);
			raw = MutiTableResolver.query4List(sql);
			
			//数据过滤
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			ArrayList<HashMap<String, String>> raw_filtered = new ArrayList<>();
			Iterator<HashMap<String, String>> rawIterator = raw.iterator();
			while(rawIterator.hasNext()) {
				HashMap<String, String> el = new HashMap<>();
				HashMap<String, String> raw_el = rawIterator.next();
				el.put("uid", raw_el.get("uid"));
				el.put("sender_name", raw_el.get("name"));
				el.put("title", raw_el.get("title"));
				el.put("msg", raw_el.get("msg"));
				el.put("isRead", raw_el.get("isRead"));
				el.put("timestamps_created", simpleDateFormat.format(new Date(Long.parseLong(raw_el.get("timestamps_created"))*1000)));
				raw_filtered.add(el);
			}
			return raw_filtered;
			
		} catch (Exception e2) {
			e2.printStackTrace();
			return null;
		}
		
		
	}
	
	/**
	 * 普通溯源用户获取已经发起的客服信息列表及详情
	 * @param startIndex 起始分页码
	 * @param limit 每页显示条数
	 * @param isResolved 是否被系统管理员处理
	 * @param uuid 用户uuid
	 * @return
	 */
	public static ArrayList<HashMap<String, String>> getCustomerServiceMsg4CommonUser(int startIndex,int limit,int isResolved,String uuid){
		ArrayList<HashMap<String, String>> raw = new ArrayList<>();
		
		//检查起始页码
		if(startIndex<=0||limit<=0) {
			return null;
		}
		
		//计算起始页码
		startIndex = (startIndex-1)*limit;
		
		try {
			String sql = String.format("select * from MSGBOX left join USER on MSGBOX.sender_id = USER.uuid "
					+ "where sender_id = '%s' and isRead is NULL and isResolved = %d order by timestamps_created desc "
					+ "limit %d,%d;", uuid,isResolved,startIndex,limit);
			raw = MutiTableResolver.query4List(sql);
			
			//数据过滤
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			ArrayList<HashMap<String, String>> raw_filtered = new ArrayList<>();
			Iterator<HashMap<String, String>> rawIterator = raw.iterator();
			while(rawIterator.hasNext()) {
				HashMap<String, String> el = new HashMap<>();
				HashMap<String, String> raw_el = rawIterator.next();
				el.put("uid", raw_el.get("uid"));
				el.put("title", raw_el.get("title"));
				el.put("msg", raw_el.get("msg"));
				el.put("isResolved", raw_el.get("isResolved"));
				el.put("timestamps_created", simpleDateFormat.format(new Date(Long.parseLong(raw_el.get("timestamps_created"))*1000)));
				raw_filtered.add(el);
			}
			return raw_filtered;
			
		} catch (Exception e2) {
			e2.printStackTrace();
			return null;
		}
		
		
	}
	
	/**
	 * 系统管理员获取接收到的客服信息列表及详情
	 * @param startIndex 起始分页码
	 * @param limit 每页显示条数
	 * @param isResolved 是否被系统管理员处理
	 * @param uuid 用户uuid
	 * @return
	 */
	public static ArrayList<HashMap<String, String>> getCustomerServiceMsg4Root(int startIndex,int limit,int isResolved){
		ArrayList<HashMap<String, String>> raw = new ArrayList<>();
		
		//检查起始页码
		if(startIndex<=0||limit<=0) {
			return null;
		}
		
		//计算起始页码
		startIndex = (startIndex-1)*limit;
		
		try {
			String sql = String.format("select * from MSGBOX left join USER on MSGBOX.sender_id = USER.uuid "
					+ "where receiver_id = 'root' and isRead is NULL and isResolved = %d order by timestamps_created desc "
					+ "limit %d,%d;", isResolved,startIndex,limit);
			raw = MutiTableResolver.query4List(sql);
			
			//数据过滤
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			ArrayList<HashMap<String, String>> raw_filtered = new ArrayList<>();
			Iterator<HashMap<String, String>> rawIterator = raw.iterator();
			while(rawIterator.hasNext()) {
				HashMap<String, String> el = new HashMap<>();
				HashMap<String, String> raw_el = rawIterator.next();
				el.put("uid", raw_el.get("uid"));
				el.put("sender_id", raw_el.get("sender_id"));
				el.put("sender_name", raw_el.get("name"));
				el.put("title", raw_el.get("title"));
				el.put("msg", raw_el.get("msg"));
				el.put("isResolved", raw_el.get("isResolved"));
				el.put("timestamps_created", simpleDateFormat.format(new Date(Long.parseLong(raw_el.get("timestamps_created"))*1000)));
				raw_filtered.add(el);
			}
			return raw_filtered;
			
		} catch (Exception e2) {
			e2.printStackTrace();
			return null;
		}
		
		
	}
	
}
