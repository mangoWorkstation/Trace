package cn.edu.gxu.trace.DAO;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.log4j.Logger;

import cn.edu.gxu.trace.db.SqlManager;

public class DAO<T> {
	
	private QueryRunner queryRunner=new QueryRunner();
	private Class<T> clazz;
	protected static Logger log = Logger.getLogger(DAO.class);

	
	@SuppressWarnings("unchecked")
	public DAO(){
		Type superClass=getClass().getGenericSuperclass();
		if (superClass instanceof ParameterizedType) {
			ParameterizedType parameterizedType=(ParameterizedType) superClass;
			
			Type [] typeArgs=parameterizedType.getActualTypeArguments();
			
			if(typeArgs!=null&&typeArgs.length >0){
				if (typeArgs[0]instanceof Class) {
					clazz=(Class<T>) typeArgs[0];
				}
			}
		}
	}
	/**
	 * 获取表中单行的某列数据
	 * @param sql
	 * @param args
	 * @return
	 */
	public <E> E getForValue(String sql,Object...args){
		Connection connection=null;
		
		try {
			connection=SqlManager.getInstance().connection;
			return  queryRunner.query(connection,sql,new ScalarHandler<E>(),args);
			
		} catch (Exception e) {
			e.printStackTrace();
//			SqlManager.getInstance().closeAllConnection();
		}
		return null;
	}
	
	/**
	 * 获取实体类在表中多行数据，存放在List中
	 * @param sql
	 * @param args
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<T> getForList(String sql,Object...args){
		Connection connection=null;
		
		try {
			connection=SqlManager.getInstance().connection;
			return  (List<T>) queryRunner.query(connection,sql,new BeanListHandler(clazz),args);
			
		} catch (Exception e) {
			e.printStackTrace();
//			SqlManager.getInstance().closeAllConnection();
		}
		return null;
	}
	/**
	 * 获取实体类在表中一行数据
	 * @param sql
	 * @param args
	 * @return
	 */
	public T get(String sql,Object...args){
		Connection connection=null;
		
		try {
			connection=SqlManager.getInstance().connection;
			return  queryRunner.query(connection,sql,new BeanHandler<T>(clazz),args);
			
		} catch (Exception e) {
			e.printStackTrace();
//			SqlManager.getInstance().closeAllConnection();
		}
		return null;
	}
	/**
	 * 更新数据
	 * @param sq
	 * @param args
	 */
	public void update(String sql,Object ... args){
		Connection connection=null;
		
		try {
			connection=SqlManager.getInstance().connection;
			queryRunner.update(connection,sql,args);
		} catch (Exception e) {
			e.printStackTrace();
//			SqlManager.getInstance().closeAllConnection();
		}
	}
}

