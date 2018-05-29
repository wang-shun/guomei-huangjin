package com.gomemyc.gold.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.gomemyc.gold.entity.GoldStatisticsHourPrice;
import com.gomemyc.gold.entity.extend.GoldStatisticsHourPriceExtend;

public interface GoldStatisticsHourPriceDao {
    int deleteByPrimaryKey(Long id);

    int insert(GoldStatisticsHourPrice record);

    int insertSelective(GoldStatisticsHourPrice record);

    GoldStatisticsHourPrice selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(GoldStatisticsHourPrice record);

    int updateByPrimaryKey(GoldStatisticsHourPrice record);


    /**
     *
     * 查询小时的统计数据
     *
     * @param currentDate
     *           开始日期
     * @param pageSize
     *            显示多少条
     * @return
     * @since JDK 1.8
     * @author tianbin
     * @date 2017年3月17日
     */
    List<GoldStatisticsHourPrice> selectListPageBeforePriceDate(@Param("currentDate") Date currentDate, @Param("offset") int offset, @Param("pageSize")int pageSize);

    /**
     *
     * 查询小时的数量
     *
     * @param currentDate
     *           开始日期
     * @return
     * @since JDK 1.8
     * @author tianbin
     * @date 2017年3月17日
     */
    int selectCountBeforePriceDate(@Param("currentDate") Date currentDate);

    /**
     *
     * 根据小时统计天的数据
     *
     * @param currentDate
     *           开始日期
     * @return
     * @since JDK 1.8
     * @author tianbin
     * @date 2017年3月17日
     */
    GoldStatisticsHourPriceExtend selectHourObjByPriceDate(@Param("currentDate") Date currentDate);
}