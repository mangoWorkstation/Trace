package cn.edu.gxu.trace.manager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;

import cn.edu.gxu.trace.db.MutiTableResolver;
import cn.edu.gxu.trace.entity.Archive;
import cn.edu.gxu.trace.entity.Base;
import cn.edu.gxu.trace.entity.Fruit;
import cn.edu.gxu.trace.entity.User;
import cn.edu.gxu.trace.servlets.RecordServlet;

/**
 * 用于处理多表联查的汇总数据
 * @author 芒果君
 * @since 2018-08-10
 */
public class UniversalManager {
	private static Logger log = Logger.getLogger(UniversalManager.class);

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
}
