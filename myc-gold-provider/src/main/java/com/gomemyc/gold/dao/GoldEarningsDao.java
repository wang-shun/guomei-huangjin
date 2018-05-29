package com.gomemyc.gold.dao;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.gomemyc.gold.entity.GoldEarnings;

public interface GoldEarningsDao {
	int deleteByPrimaryKey(String id);

	int insert(GoldEarnings record);

	GoldEarnings selectByPrimaryKey(String id);

	int updateByPrimaryKeySelective(GoldEarnings record);

	int updateByPrimaryKey(GoldEarnings record);

	/**
	 *
	 * 向收益表中插入对象
	 *
	 * @param GoldEarnings对象
	 *            用户id
	 * @return int 插入的行数，插入为1.
	 * @since JDK 1.8
	 * @author liujunhan
	 * @date 2017年3月23日
	 */
	int insertGoldEarnings(GoldEarnings record);

	/**
	 * 
	 * 查询昨日收益
	 * 
	 * @param userId
	 *            用户id
	 * @param date
	 *            当前日期
	 * @return BigDecimal
	 * @since JDK 1.8
	 * @author liuqiangbin
	 * @date 2017年3月14日
	 */
	GoldEarnings getYesterdayEarnings(@Param("userId") String userId, @Param("date") String date);

	/**
	 * 
	 * 按照页数查询历史收益
	 * 
	 * @param userId
	 *            用户id
	 * @param page
	 *            页码
	 * @param pageSize
	 *            每一页显示的条数
	 * @return List<GoldEarnings>
	 * @since JDK 1.8
	 * @author liuqiangbin
	 * @date 2017年3月14日
	 */
	List<GoldEarnings> getHistoryEarnings(@Param("userId") String userId, @Param("pageStart") int pageStart,
			@Param("pageSize") int pageSize);

	/**
	 * 
	 * 查询历史收益总记录数
	 * 
	 * @param userId
	 *            用户id
	 * @return int
	 * @since JDK 1.8
	 * @author liuqiangbin
	 * @date 2017年3月14日
	 */
	int getHistoryEarningrEcord(@Param("userId")  String userId);
	/**
	 *
	 * 查询历史收益总数
	 *
	 * @param userId
	 *            用户id
	 * @return BigDecimal
	 * @since JDK 1.8
	 * @author liuqiangbin
	 * @date 2017年3月14日
	 */

	BigDecimal getHistoryTotalEarning(@Param("userId") String userId);
	/**
	 *
	 * 根据订单id查询历史收益总数
	 *
	 * @param investOrderId
	 * @param userId
	 * @return BigDecimal
	 * @since JDK 1.8
	 * @author liuqiangbin
	 * @date 2017年3月14日
	 */

	BigDecimal getTotalEarningByInvestOrderId(@Param("userId") String userId,@Param("investOrderId") String investOrderId);
}