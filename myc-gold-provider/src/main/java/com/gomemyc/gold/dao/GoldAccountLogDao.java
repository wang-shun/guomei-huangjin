package com.gomemyc.gold.dao;

import com.gomemyc.gold.entity.GoldAccountLog;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface GoldAccountLogDao {
	
	
    int deleteByPrimaryKey(String id);

    int insert(GoldAccountLog record);

    int insertSelective(GoldAccountLog record);

    GoldAccountLog selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(GoldAccountLog record);

    int updateByPrimaryKey(GoldAccountLog record);
    
    /**
	 * 
	 * 查询指定用户记录总数
	 * @return  int
	 * @since JDK 1.8
	 * @author zhuyunpeng
	 * @date 2017年3月23日
	 */
    int countRecord(@Param("userId")String userId);
    
    /**
  	 * 
  	 * 查询所有记录
  	 * @return  List
  	 * @since JDK 1.8
  	 * @author zhuyunpeng
  	 * @date 2017年3月23日
  	 */
    List<GoldAccountLog> selectAllRecord();
}