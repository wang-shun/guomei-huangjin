package com.gomemyc.gold.dao;

import com.gomemyc.gold.entity.GoldStatisticsDayPrice;
import com.gomemyc.gold.entity.extend.GoldStatisticsDayPriceExtend;

import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface GoldStatisticsDayPriceDao {
    int deleteByPrimaryKey(Long id);

    int insert(GoldStatisticsDayPrice record);

    int insertSelective(GoldStatisticsDayPrice record);

    GoldStatisticsDayPrice selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(GoldStatisticsDayPrice record);

    int updateByPrimaryKey(GoldStatisticsDayPrice record);

    /**
     *
     * 查询天的统计数据
     *
     * @param currentDate
     *           开始日期
     * @param pageSize
     *            显示多少条
     * @return List<GoldEarnings>
     * @since JDK 1.8
     * @author tianbin
     * @date 2017年3月17日
     */

    List<GoldStatisticsDayPrice> selectListPageBeforePriceDate(@Param("currentDate") Date currentDate, @Param("offset") int offset, @Param("pageSize")int pageSize);

    /**
     *
     * 查询天的数量
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
     * 查询月的统计数据
     *
     * @param dateMonth
     *           月份
     * @return
     * @since JDK 1.8
     * @author tianbin
     * @date 2017年3月19日
     */
    GoldStatisticsDayPriceExtend selectDayObjByMonth(@Param("dateMonth") Integer dateMonth);

}