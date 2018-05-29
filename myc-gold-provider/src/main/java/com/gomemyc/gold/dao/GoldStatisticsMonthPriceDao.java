package com.gomemyc.gold.dao;

import com.gomemyc.gold.entity.GoldStatisticsMonthPrice;
import com.gomemyc.gold.entity.extend.GoldStatisticsMonthPriceExtend;

import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface GoldStatisticsMonthPriceDao {
    int deleteByPrimaryKey(Long id);

    int insert(GoldStatisticsMonthPrice record);

    int insertSelective(GoldStatisticsMonthPrice record);

    GoldStatisticsMonthPrice selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(GoldStatisticsMonthPrice record);

    int updateByPrimaryKey(GoldStatisticsMonthPrice record);


    /**
     *
     * 查询天的统计数据
     *
     * @param beginDate
     *           开始日期
     * @param pageSize
     *            显示多少条
     * @return List<GoldEarnings>
     * @since JDK 1.8
     * @author tianbin
     * @date 2017年3月17日
     */

    List<GoldStatisticsMonthPrice> selectListPageBeforePriceDate(@Param("currentDate") Date currentDate, @Param("offset") int offset, @Param("pageSize")int pageSize);

    /**
     *
     * 查询月的数量
     *
     * @param beginDate
     *           开始日期
     * @return
     * @since JDK 1.8
     * @author tianbin
     * @date 2017年3月17日
     */
    int selectCountBeforePriceDate(@Param("currentDate") Date currentDate);
    
    
    
    /**
     * 
     * 统计年的统计数据
     * 
     * @author TianBin 
     * @param currentYears 年份
     * @return 
     * @since JDK 1.8
     */
    GoldStatisticsMonthPriceExtend selectMonthObjByPriceYear(@Param("currentYears") Integer currentYears);
     
    
}