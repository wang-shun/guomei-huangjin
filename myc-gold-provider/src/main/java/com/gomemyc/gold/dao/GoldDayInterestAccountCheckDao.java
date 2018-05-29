package com.gomemyc.gold.dao;

import com.gomemyc.gold.entity.GoldDayInterestAccountCheck;
import com.gomemyc.gold.entity.GoldEarnings;
import com.gomemyc.gold.entity.extend.GoldDayInterestAccountCheckExtend;

import java.math.BigDecimal;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface GoldDayInterestAccountCheckDao {

    int deleteByPrimaryKey(String id);

    int insert(GoldDayInterestAccountCheck record);

    int insertSelective(GoldDayInterestAccountCheck record);

    GoldDayInterestAccountCheck selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(GoldDayInterestAccountCheck record);

    int updateByPrimaryKey(GoldDayInterestAccountCheck record);

    /**
     *
     * 查询每日每天利息对账文件的金价与利息
     *
     * @param interestDate 收益日期
     * @return  List<GoldDayInterestAccountCheckExtend>
     * @since JDK 1.8
     * @author LiuQiangBin
     * @date 2017年3月22日
     */
    List<GoldDayInterestAccountCheckExtend> selectByInterestDate(@Param("interestDate") String interestDate);
    /**
     *
     * 更新对比结果
     *
     * @param orderNo 订单号
     * @param comparingStatus 比对结果状态吗
     * @return  int
     * @since JDK 1.8
     * @author liujunhan
     * @date 2017年3月30日
     */
    int updateComparingStatusByOrderNo(@Param("orderNo") String orderNo, @Param("comparingStatus") Integer comparingStatus);
    /**
     *根据创建时间查询当天创建的所有订单
     *
     *
     * @param createTime 更新时间
     * @return  List<GoldDayInterestAccountCheckExtend>
     * @since JDK 1.8
     * @author liujunhan
     * @date 2017年3月30日
     */
    List<GoldDayInterestAccountCheck> getBycreateTime(@Param("createTime") String createTime);
    
    /**
    *
    * 根据订单号，查询这个订单的每日利息对账
    *
    * @param reqNo 订单号
    * @return  List<GoldDayInterestAccountCheck>
    * @since JDK 1.8
    * @author LiuQiangBin
    * @date 2017年3月30日
    */
    List<GoldDayInterestAccountCheck> listByReqNo(@Param("reqNo") String reqNo);

    /**
     *
     * 根据订单号和创建时间查询当前时间创建的相应的订单
     *
     * @param reqNo 订单号
     * @param createTime 创建时间
     * @return  int
     * @since JDK 1.8
     * @author liujunhan
     * @date 2017年3月30日
     */
    GoldDayInterestAccountCheck  getBycreateTimeAndReqNo(@Param("createTime")String createTime,@Param("reqNo") String reqNo );
    /**
     * 根据订单号和对账数据类型查询利息总和
     * @param reqNo 订单号
     * @param  dataCheckType 对账数据类型
     * @return GoldDueSumAccountCheck
     * @mbg.generated
     */
    BigDecimal getInterestByOrderNo(@Param("orderNo") String orderNo, @Param("dataCheckType") Integer dataCheckType);


}