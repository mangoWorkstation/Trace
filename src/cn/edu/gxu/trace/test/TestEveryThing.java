package cn.edu.gxu.trace.test;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;

import cn.edu.gxu.trace.db.MutiTableResolver;
import cn.edu.gxu.trace.db.SqlManager;
import cn.edu.gxu.trace.mangoUtils.AlertTextTemplate;
import cn.edu.gxu.trace.mangoUtils.Encryptor;
import cn.edu.gxu.trace.mangoUtils.JsonEncodeFormatter;
import cn.edu.gxu.trace.mangoUtils.AlertTextTemplate.ALERT_TYPE;
import cn.edu.gxu.trace.mangoUtils.AlertTextTemplate.TEMPLATE_TYPE;

public class TestEveryThing {
	
	private static Logger logger = Logger.getLogger(TestEveryThing.class);  

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		String text = JSON.toJSONString(new TestEveryThing());
//		System.out.println(text);
		
//		HashMap<String, String> eHashMap = new HashMap<>();
//		
//		eHashMap.put("hello", "hello_1");
//		eHashMap.put("world", "world_1");
//		
//		ArrayList<HashMap<String, String>> eArrayList = new ArrayList<>();
//		eArrayList.add(eHashMap);
//		eArrayList.add(eHashMap);
//		
//		System.out.println(JsonEncodeFormatter.parse(10001, eArrayList));
//		
//		logger.debug("hello");
//		String timestamp = "1532487333";
//		String str = "q7b5jo7joq";
//		
//		String source = "common_gxu";
//		String appKey = new Encryptor().SHA512(source);
		
//		String result = new Encryptor().SHA512(appKey+timestamp+str);
		
//		logger.debug(appKey);
//		logger.debug(result);
		
//		logger.debug(Integer.valueOf("好"));
//		
//		SqlManager sqlManager = SqlManager.getInstance();
//		String sql = "select * from USER where idNum='45260919880706334X';";
//		ArrayList<HashMap<String, String>> eArrayList = MutiTableResolver.query(sql);
//		logger.debug(eArrayList.toString());
		
		logger.debug(AlertTextTemplate.getString(ALERT_TYPE.TEMP_AIR, TEMPLATE_TYPE.MSG, 50, "西大", "hello"));
		
	}

}
