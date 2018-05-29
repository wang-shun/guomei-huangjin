package com.gomemyc.gold.dao;

import com.gomemyc.gold.entity.GoldInvestAccountCheck;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface GoldInvestAccountCheckDao {

    int deleteByPrimaryKey(String id);

    int insert(GoldInvestAccountCheck record);

    int insertSelective(GoldInvestAccountCheck record);

    GoldInvestAccountCheck selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(GoldInvestAccountCheck record);

    int updateByPrimaryKey(GoldInvestAccountCheck record);
    /**
     *
     * 根据创建时间，查询前一天下的单
     *
     * @param createTime  当前时间
     * @return   List<GoldInvestAccountCheck>
     * @since JDK 1.8
     * @author liujunhan
     * @date 2017年3月28日
     */
    List<GoldInvestAccountCheck> getByCreateTime(@Param("createTime") String createTime);


    /**
     *
     * 根据订单号，更新订单比较结果
     *@param orderNo 订单号
     *@param comparingStatus 比对结果
     * @return  Integer
     * @since JDK 1.8
     * @author liujunhan
     * @date 2017年3月28日
     */
    int updateComparingStatusByOrderNo(@Param("orderNo") String orderNo, @Param("comparingStatus") Integer comparingStatus);

    /**
     *
     * 根据订单号和数据类型查询黄金钱包返回的金价
     *@param reqNo 订单号
     * @param dataCheckType 对账数据记录类型   黄金OR国美
     * @return  Integer
     * @since JDK 1.8
     * @author liujunhan
     * @date 2017年3月28日
     */
    BigDecimal getRealPriceByDataCheckType(@Param("reqNo") String reqNo, @Param("dataCheckType") Integer dataCheckType);
}