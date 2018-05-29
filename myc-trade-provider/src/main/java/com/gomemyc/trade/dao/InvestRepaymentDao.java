package com.gomemyc.trade.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.gomemyc.common.exception.ServiceException;
import com.gomemyc.trade.dto.InvestRepaymentDTO;
import com.gomemyc.trade.entity.InvestRepayment;
import com.gomemyc.trade.enums.RepaymentStatus;


public interface InvestRepaymentDao {

	/**
	 * 根据ID查询
	 * @param id
	 * @return
	 */
	InvestRepayment findById(@Param("id") String id);
	/**
	 * 根据投资ID查询
	 * @param investId 投资ID
	 * @return
	 */
	InvestRepayment findByInvestId(@Param("investId") String investId);
	
	int update(InvestRepayment investRepayment);
	
	int insert(InvestRepayment investRepayment);
	
	/**
	 * @Title 批量插入投资还款计划
	 * @param investRepayments 投资计划列表
	 * @author lifeifei
	 * @date 2017年3月29日
	 */
	int insertBatch(List<InvestRepayment> investRepayments);
	
	/**
	 * 根据投资标的编号查询还款计划
	 * @param loanId 标的ID
	 * @author zhangWei
	 * @return
	 */
	List<InvestRepayment> findByLoanId(@Param("loanId") String loanId);
	
	/**
	 * @Title 更新标的所有还款计划状态为：未到期
	 * @param @param loanId
	 * @param @return
	 * @author lifeifei
	 * @date 2017年3月30日
	 */
	int updateStatus(@Param("loanId") String loanId,@Param("status") int status);
	
	/**
	 * 根据投资人 按年月查询投资人的应该本金和应还利息（基础利息+加息利息）
	 * @param userId  投资人
	 * @param year 年
	 * @param month 月
	 * @param statusList 状态集合
	 * @author zhangWei
	 * @return
	 * @throws ServiceException
	 */
	Map<String, Object> sumByMonth(@Param("userId") String userId,@Param("dueDate") String dueDate,@Param("statusList") List<RepaymentStatus> statusList);
	
	/**
	 * 根据投资标的编号和状态查询还款计划
	 * @param loanId 标的ID
	 * @param statuses 还款状态集合
	 * @author zhangWei
	 * @return
	 */
	List<InvestRepayment> findByLoanIdAndStatuses(@Param("loanId") String loanId,@Param("statuses") RepaymentStatus... statuses);
	/**
	 * 根据投资ID集合查询还款计划
	 * @param investIds 投资ID集合
	 * @return
	 */
	List<InvestRepayment> findByInvestIds(@Param("investIds") List<String> investIds);
	
	int insertSelective(InvestRepayment investRepayment);
	int updateByPrimaryKeySelective(InvestRepayment investRepayment);
	/**
	 * 
	 * @Description (TODO这里用一句话描述这个方法的作用)
	 * @param investId
	 * @param status
	 * @return
	 */
	List<InvestRepayment> findListByInvest(@Param("investId")String investId, @Param("statuses")List<Integer> status);
	/**
	 * 获取应还本金
	 * @Description (TODO这里用一句话描述这个方法的作用)
	 * @param userId
	 * @param status
	 * @return
	 */
	BigDecimal getPrincipalPaid(@Param("userId")String userId, @Param("status")Integer status);
	/**
	 * 获取待收收益(元)
	 * @Description (TODO这里用一句话描述这个方法的作用)
	 * @param userId
	 * @param status
	 * @return
	 */
	BigDecimal getIncomeReceived(@Param("userId")String userId, @Param("status")Integer status);
	
	
	List<InvestRepayment> getInvestRepayment(@Param("userId")String userId, @Param("dueDate")String dueDate,
			 @Param("statuses")List<RepaymentStatus> statusList);
	
	/**
	 * 获取每日还款收益
	 * @param userId
	 * @param dueDate
	 * @return
	 * @author lujixiang
	 * @date 2017年4月22日
	 *
	 */
	BigDecimal sumAverageDayInterest(@Param("userId") String userId, @Param("valueDate") Date dueDate);
	/**
	 * 
	 * @Description (TODO这里用一句话描述这个方法的作用)
	 * @param userId
	 * @param status
	 * @return
	 */
	BigDecimal getPrincipalPaidByUserId(@Param("userId")String userId);
	/**
	 * 我的理财下的总待收收益(元)
	 * @Description (TODO这里用一句话描述这个方法的作用)
	 * @param userId
	 * @return
	 */
	BigDecimal getIncomeReceivedByUserId(String userId);
	
	/**
     * 累计到昨日还款收益
     * @param userId
     * @param dueDate
     * @return
     * @author lujixiang
     * @date 2017年4月22日
     *
     */
    BigDecimal sumTototalAverageDayInterest(@Param("userId") String userId, @Param("valueDate") Date dueDate);
	
}
