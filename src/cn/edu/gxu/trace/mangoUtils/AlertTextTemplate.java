package cn.edu.gxu.trace.mangoUtils;

/**
 * 预警信息模版
 * @author 芒果君
 * @since 2018-08-31
 * @version V1.0
 */

public class AlertTextTemplate {
	
	public enum ALERT_TYPE{
		TEMP_AIR, //空气温度警报
		TEMP_SOIL, //土壤温度警报
		HUMIDITY_AIR,//空气湿度警报
		HUMIDITY_SOIL,//土壤湿度警报
		LUX //光照警报
	}
	
	public enum TEMPLATE_TYPE{
		TITLE, //标题
		MSG //消息内容
	}
	
	/**
	 * 获取警报信息模版
	 * @param alertType 警报类型
	 * @param templateType 标题/消息的模版
	 * @param extremeValue 传感器触发的极大值
	 * @param base_name 基地名称
	 * @param sim SIM卡号
	 * @return
	 */
	public static String getString(ALERT_TYPE alertType,TEMPLATE_TYPE templateType,double extremeValue,String base_name,String sim) {
		switch (templateType) {
		case TITLE:
			return getTitle(alertType);
		case MSG:
			return getMsg(alertType, extremeValue, base_name, sim);
		default:
			return null;
		}
	}
	
	/**
	 * 获取标题
	 * @param alertType
	 * @return
	 */
	private static String getTitle(ALERT_TYPE alertType) {
		switch (alertType) {
		case TEMP_AIR:
			return "NB-IoT传感器空气温度警报";
		case TEMP_SOIL:
			return "NB-IoT传感器土壤温度警报";
		case HUMIDITY_AIR:
			return "NB-IoT传感器空气湿度警报";
		case HUMIDITY_SOIL:
			return "NB-IoT传感器土壤湿度警报";
		default:
			return "NB-IoT传感器光照强度警报";	
		}
	}
	
	/**
	 * 获取消息内容
	 * @param alertType
	 * @param extremeValue
	 * @param base_name
	 * @param sim
	 * @return
	 */
	private static String getMsg(ALERT_TYPE alertType,double extremeValue,String base_name,String sim) {
		switch (alertType) {
		case TEMP_AIR:
			return String.format("您的基地%s中，传感器空气温度为%.2f摄氏度，SIM卡号%s，达到危险阈值，请及时处理。", base_name,extremeValue,sim);
		case TEMP_SOIL:
			return String.format("您的基地%s中，传感器土壤温度为%.2f摄氏度，SIM卡号%s，达到危险阈值，请及时处理。", base_name,extremeValue,sim);
		case HUMIDITY_AIR:
			return String.format("您的基地%s中，传感器空气湿度为%.2f（百分比），SIM卡号%s，达到危险阈值，请及时处理。", base_name,extremeValue,sim);
		case HUMIDITY_SOIL:
			return String.format("您的基地%s中，传感器土壤湿度为%.2f（百分比），SIM卡号%s，达到危险阈值，请及时处理。", base_name,extremeValue,sim);
		default:
			return String.format("您的基地%s中，传感器光照强度为%.2f lux，SIM卡号%s，达到危险阈值，请及时处理。", base_name,extremeValue,sim);
		}
	}
	
}
