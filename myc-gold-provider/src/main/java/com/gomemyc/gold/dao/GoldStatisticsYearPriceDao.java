package com.gomemyc.gold.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.gomemyc.gold.entity.GoldStatisticsYearPrice;

public interface GoldStatisticsYearPriceDao {
    int deleteByPrimaryKey(Long id);

    int insert(GoldStatisticsYearPrice record);

    int insertSelective(GoldStatisticsYearPrice record);

    GoldStatisticsYearPrice selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(GoldStatisticsYearPrice record);

    int updateByPrimaryKey(GoldStatisticsYearPrice record);

    /**
     *
     * 查询年的统计数据
     *
     * @param offset
     *
     * @param pageSize
     *
     * @return
     * @since JDK 1.8
     * @author tianbin
     * @date 2017年3月17日
     */
    List<GoldStatisticsYearPrice> selectListPage(@Param("offset") int offset, @Param("pageSize")int pageSize);

    /**
     *
     * 查询年的统计数据数量
     *
     *
     * @return
     * @since JDK 1.8
     * @author tianbin
     * @date 2017年3月17日
     */
    int selectCount();
    
}