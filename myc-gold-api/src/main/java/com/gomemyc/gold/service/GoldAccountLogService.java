package com.gomemyc.gold.service;

import java.util.List;

import com.gomemyc.gold.dto.GoldAccountLogDTO;
/**
 *@ClassName:GoldAccountLog.java
 *@Description:
 *@author zhuyunpeng
 *@date 2017年3月21日
 */
public interface GoldAccountLogService {


	/**
	 * 依据用户ID查询GoldAccountLog记录数
	 * @return int
	 */
	int countLogRecord(String userId);

	/**
	 * 接收GoldAccountLog对象并插入数据库
	 * @return int
	 */
	int addGoldAccountLog(GoldAccountLogDTO goldAccountLogDTO);
	
	/**
	 * 查询GoldAccountLog所有记录
	 * @return List<GoldAccountLogDTO>
	 */
	List<GoldAccountLogDTO> selectAllRecord();
	
}
