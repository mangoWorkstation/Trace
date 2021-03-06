package cn.edu.gxu.trace.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.mysql.jdbc.ResultSetMetaData;

/**
 * 用于解决DAO无法处理的联表查询
 * @author 芒果君
 * @since 2018-08-08
 */
public class MutiTableResolver {
	private static SqlManager sqlManager = SqlManager.getInstance();
	
	/**
	 * 将返回一个数据，且用HashMap存储相关数据，全部使用String类型寄存，
	 * 后续可以根据需要进行类型转换
	 * @param sql 查询语句
	 * @return
	 */
	public static HashMap<String, String> query4row(String sql){
		HashMap<String, String> el = new HashMap<>();
		try {
			ResultSet resultSet = sqlManager.executeSqlQuery(sql);
			while(resultSet.next()) {
				ResultSetMetaData rsmd = (ResultSetMetaData) resultSet.getMetaData();
				int columnCount = rsmd.getColumnCount();
				//此处下标必须从1开始
				for(int index = 1;index<columnCount+1;index++) {
					el.put(rsmd.getColumnName(index), resultSet.getString(index));
				}
				return el;
			}
			resultSet.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return el;
	}
	
	/**
	 * 将返回一组联表查询的数据
	 * @param sql
	 * @return
	 */
	public static ArrayList<HashMap<String, String>> query4List(String sql){
		ArrayList<HashMap<String, String>> e = new ArrayList<>();
		try {
			ResultSet resultSet = sqlManager.executeSqlQuery(sql);
			while(resultSet.next()) {
				ResultSetMetaData rsmd = (ResultSetMetaData) resultSet.getMetaData();
				int columnCount = rsmd.getColumnCount();
				//此处下标必须从1开始
				HashMap<String, String> el = new HashMap<>();
				for(int index = 1;index<columnCount+1;index++) {
					el.put(rsmd.getColumnName(index), resultSet.getString(index));
				}
				e.add(el);
			}
			resultSet.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return e;
	}
}
