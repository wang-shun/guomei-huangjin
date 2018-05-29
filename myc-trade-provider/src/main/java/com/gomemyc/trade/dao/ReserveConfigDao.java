package com.gomemyc.trade.dao;

import com.gomemyc.trade.entity.ReserveConfig;
import org.apache.ibatis.annotations.Param;
import java.util.List;

import org.apache.ibatis.annotations.Param;

/**
 * 配置表
 * 
 * @author 何健
 * @creaTime 2017年3月11日
 */
public interface ReserveConfigDao {
    
	/**
	 * 查询所有配置。
	 * 
	 * @return 配置集合
	 */
    List<ReserveConfig> findByList();

	ReserveConfig findByName(@Param("parameterName") String parameterName);

}
