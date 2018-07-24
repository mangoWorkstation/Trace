package cn.edu.gxu.trace.test;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;

import cn.edu.gxu.trace.mangoUtils.JsonEncodeFormatter;

public class TestEveryThing {
	
	private static Logger logger = Logger.getLogger(TestEveryThing.class);  

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		String text = JSON.toJSONString(new TestEveryThing());
//		System.out.println(text);
		
		HashMap<String, String> eHashMap = new HashMap<>();
		
		eHashMap.put("hello", "hello_1");
		eHashMap.put("world", "world_1");
		
		ArrayList<HashMap<String, String>> eArrayList = new ArrayList<>();
		eArrayList.add(eHashMap);
		eArrayList.add(eHashMap);
		
		System.out.println(JsonEncodeFormatter.parse(10001, eArrayList));
		
		logger.debug("hello");
	}

}
