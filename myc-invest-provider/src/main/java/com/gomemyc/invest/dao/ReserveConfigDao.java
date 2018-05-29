package com.gomemyc.invest.dao;

import java.util.List;
import com.gomemyc.invest.entity.ReserveConfig;

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
    
}
