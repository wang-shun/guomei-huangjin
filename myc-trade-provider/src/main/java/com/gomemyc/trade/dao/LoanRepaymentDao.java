package com.gomemyc.trade.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.gomemyc.trade.entity.LoanRepayment;
import com.gomemyc.trade.enums.RepaymentStatus;


public interface LoanRepaymentDao {

	/**
	 * 根据标的ID查询
	 * @param loanId 标的ID
	 * @return
	 */
	LoanRepayment findByLoanId(@Param("loanId") String loanId);
	/**
	 * 根据还款ID查询
	 * @param loanId 标的ID
	 * @return
	 */
	LoanRepayment findById(@Param("id") String id);
	
	/**
	 * @Title 插入回款计划
	 * @param @param loanRepayments
	 * @param @return
	 * @author lifeifei
	 * @date 2017年3月29日
	 */
	Integer insert(LoanRepayment loanRepayments);
	
	/**
	 * 修改特定字段
	 * @param status 可为空 为空时不修改
	 * @param id
	 * @param accountSrl 可为空 为空时不修改
	 * @param depositSrl 可为空 为空时不修改
	 * @return
	 */
	Integer updateStatusById(@Param("status") RepaymentStatus status,@Param("id") String id, 
			@Param("accountSrl") String accountSrl, @Param("depositSrl") String depositSrl);
	
	Integer update(LoanRepayment loanRepayment);
	
	/**
	 * 根据还款计划状态和时间查询当前时间以前的集合
	 * @param statuss 还款计划状态  为空时不做查询条件
	 * @param dueDate 应还款日期 小于当前时间的
	 * @return
	 */
	List<LoanRepayment> findByStatus(@Param("dueDate") Date dueDate,@Param("statuss") RepaymentStatus... statuss);
}
