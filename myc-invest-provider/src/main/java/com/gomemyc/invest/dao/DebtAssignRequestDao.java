package com.gomemyc.invest.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.gomemyc.common.exception.ServiceException;
import com.gomemyc.invest.dto.DebtassignRequestDTO;
import com.gomemyc.invest.entity.DebtassignRequest;
import com.gomemyc.invest.enums.DebtAssignStatus;


/**
 * 债转申请
 * @author zhangWei
 *
 */
public interface DebtAssignRequestDao {

	
	/**
	 * 添加
	 * @param debtassignRequest
	 * @return
	 */
	int add(DebtassignRequest debtassignRequest);
	
	/**
	 * 查询唯一对象
	 * @return
	 */
	DebtassignRequest findById(@Param("id") String id);
	
	  /**
     * 根据债转人和债转状态查询债转票据
     * @param userId 债转人
     * @param status  产品状态
     * @param startRow 开始行数
     * @param pageSize 显示的行数
     * @author zhangWei
     * @return
     */
    List<DebtassignRequest> findPageByByUserIdAndStatus(@Param("userId")  String userId,@Param("status")  DebtAssignStatus status,@Param("startRow")  Integer startRow,
    		@Param("pageSize")  Integer pageSize);
    
    /**
     * 根据债转人和债转状态查询债转票据条数
     * @param userId 债转人
     * @param status  产品状态
     * @author zhangWei
     * @return
     */
    Integer getCountByByUserIdAndStatus(@Param("userId")  String userId,@Param("status")  DebtAssignStatus status);
	
	/**
	 * 修改
	 * @param debtassignRequest
	 * @return
	 */
	int update(DebtassignRequest debtassignRequest);
	
	/**
	 * 根据ID修改debtassignProductId字段
	 * @param debtassignRequest
	 * @return
	 */
	int updateDebtassignProductId(@Param("id") String id,@Param("debtAssignProductId") String debtAssignProductId,@Param("lastOperationTime") Date lastOperationTime);
	
	/**
	 * 根据条件查询债转申请集合（从起始债转状态到结束债转状态的区间查询）
	 * @param userId
	 * @param investId
	 * @param startStatus 起始债转状态
	 * @param endStatus 结束债转状态
	 * @return
	 */
	List<DebtassignRequest> listByInvestIdsAndUserIdAndStatusSection(@Param("userId") String userId,@Param("investId") String investId,@Param("startStatus") DebtAssignStatus startStatus,@Param("endStatus") DebtAssignStatus endStatus);
	/**
	 * 根据条件查询债转申请
	 * @param debtassignProductId
	 * @return
	 */
	DebtassignRequest getByDebtassignProductId(@Param("debtAssignProductId") String debtAssignProductId);
	
	/**
	 * 根据ID修改状态
	 * @param id
	 * @param status 债转状态
	 * @return
	 */
	int updateStatusById(@Param("id") String id,@Param("status") DebtAssignStatus status,@Param("lastOperationTime") Date lastOperationTime);
	
	/**
	 * 查询可结算的债转申请
	 * @param status 债转申请状态
	 * @param time 转让终止时间
	 * @return
	 * @throws ServiceException
	 */
	List<DebtassignRequestDTO> findByStatusAndEndTime(@Param("status") DebtAssignStatus status,@Param("time") String time);
	
	
	List<Map<String, Object>> statistiscDebtRequest(@Param("startStatus") DebtAssignStatus startStatus,@Param("endStatus") DebtAssignStatus endStatus,@Param("startTime") String startTime, @Param("endTime") String endTime);
	
	BigDecimal statistiscDebtSuccess(@Param("startStatus") DebtAssignStatus startStatus,@Param("endStatus") DebtAssignStatus endStatus,@Param("startTime") String startTime, @Param("endTime") String endTime);
	
	List<DebtassignRequest> listByInvestIdsAndUserIdAndStatus(@Param("userId") String userId,@Param("investIds") List<String> investIds,@Param("statusList") DebtAssignStatus... statusList);

	Integer countByUserIdAndStatus(@Param("userId")String userId, @Param("status")DebtAssignStatus status);
}
