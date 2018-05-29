package com.gomemyc.invest.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.gomemyc.invest.entity.DebtassigncancleLog;
import com.gomemyc.invest.enums.AssignLoanCancelStatus;
import com.gomemyc.invest.enums.DebtAssignCancelType;
import com.gomemyc.invest.enums.DebtAssignStatus;

/**
 * 债转撤销日志
 * 
 * @author zhangWei
 *
 */
public interface DebtassigncancleLogDao {

	/**
	 * 添加
	 * 
	 * @return
	 */
	int add(DebtassigncancleLog debtassigncancleLog);

	/**
	 * 根据用户和产品ID查询符合条件的日志
	 * 
	 * @param assignApplyUserId 用户
	 * @param productId 产品
	 * @return
	 */
	List<DebtassigncancleLog> findListByUserIdAndProductId(@Param("assignApplyUserId") String assignApplyUserId,
			@Param("productId") String productId);

	/**
	 * 根据条件查询数量
	 * @param productId 产品ID
	 * @param investId 投资ID
	 * @param assignApplyUserId 用户ID
	 * @param cancelType 撤销类型
	 * @param cancelStatus 撤销状态
	 * @return
	 */
	int countByOperatorType(@Param("productId") String productId, @Param("investId") String investId, @Param("assignApplyUserId") String assignApplyUserId,@Param("cancelType")  DebtAssignCancelType cancelType,@Param("cancelStatus")  AssignLoanCancelStatus cancelStatus);
	
	Map countConcelLogByUserIdAndRootLoanIds(@Param("tmp_investIds") List<String> tmp_investIds,@Param("tmp_rootLoanIds") List<String> tmp_rootLoanIds, @Param("operatorId") String operatorId, @Param("assignLoanCancelType") DebtAssignCancelType assignLoanCancelType,@Param("result")  AssignLoanCancelStatus result,@Param("beginDate")  Date beginDate,@Param("endDate")  Date endDate);

}
