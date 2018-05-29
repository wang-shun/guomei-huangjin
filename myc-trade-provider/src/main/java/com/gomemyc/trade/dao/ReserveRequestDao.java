package com.gomemyc.trade.dao;

import com.alibaba.dubbo.config.support.Parameter;
import com.gomemyc.trade.entity.ReserveRequest;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 预约申请单。
 * 
 * @author 何健
 * @creaTime 2017年3月10日
 */
public interface ReserveRequestDao {

    /**
     * 根据id查找预约申请单。
     *
     * @param requestId
     * @return
     */
    public ReserveRequest findById(String requestId);

    /**
     * 根据预约单状态，查询预约单集合。
     *
     * @param status 预约单状态值
     * @return List
     */
    List<ReserveRequest> findListByStatus(@Param("status") Integer status);

   /**
    * 根据用户id查询预约金额，给个人中心用户使用。
    *
    * @return
    */
    BigDecimal reserveAmountByUserId( @Param("userId") String userId, @Param("requestStatusList") List<Integer> requestStatusList);

    /**
     * 根据预约单状态，查询预约单集合。
     *
     * @param status 预约单状态值
     * @return List
     */
    List<ReserveRequest> findListByStatusAndIsExpired(@Param("status") Integer status);

    /**
     * 根据申请单id更新成交金额、剩余金额。
     *
     * @param reserveRequest 申请单对象，只设置该对象的三个属性
     *                       id , investedAmount, balanceAmount
     * @return
     */
    public int updateAmoutById(ReserveRequest reserveRequest);


    /**
     * 根据申请单id 更新申请单状态。
     *
     * @param reserveRequest 申请单对象，只设置该对象的 2 个属性
     *                       id , status
     * @return
     */
    public int updateStatusById(ReserveRequest reserveRequest);


    /**
     * 插入申请单对象。
     *
     * @param reserveRequest 申请单对象
     * @return
     */
    int insert(ReserveRequest reserveRequest);

    /**
     * 更新申请单状态和取消状态。
     *
     * @param reserveRequest 申请单对象，只设置该对象的 3 个属性
     *                       id, status, enableCancelled
     * @return
     */
    int updateStatusAndCancelled(ReserveRequest reserveRequest);

    /**
     * 更新取消类型和取消时间。
     *
     * @param id id
     * @param reserveCancelType 取消类型
     * @param cancelTime 取消时间
     * @return
     */
    int updateCancelTypeAndCancelTime(@Param("id") String id, @Param("reserveCancelType") Integer reserveCancelType, @Param("cancelTime") Date cancelTime);


    /**
     * 更新预约申请单状态为已取消
     * @param reserveRequest
     * @return
     */
    int updateStatusCancelById(@Param("id") String id, @Param("status") Integer status, @Param("enableCancelled") Integer enableCancelled);

    /**
     * 更新预约单状态
     *
     * @param reserveRequest
     * @return
     */
    int updateReserveRequest(ReserveRequest reserveRequest);

    /**
     * 保存预约申请单
     *
      * @param reserveRequest
     * @return
     */
	public int saveReserveRequest(ReserveRequest reserveRequest);

    /**
     * 查询预约中，且可以取消的预约申请单。
     *
     * @param requestId
     * @return
     */
	public ReserveRequest findByRequestId(String requestId);

    /**
     * 分页查询用户的预约申请单。
     *
     * @param userId
     * @param pageStart
     * @param pageSize
     * @return
     */
	public List<ReserveRequest> findReserveRequestsById(@Param("userId")String userId,@Param("pageStart")Integer pageStart, @Param("pageSize")Integer pageSize);

    /**
     * 分页查询用户的预约申请单。
     *
     * @param userId
     * @param startRow
     * @param pageSize
     * @return
     */
	List<ReserveRequest> findReserveRequests(@Param("userId")String userId,@Param("startRow")Integer startRow, @Param("pageSize")Integer pageSize);

    /**
     * 分页查询用户的预约申请单。
     *
     * @param userId
     * @return
     */
	Integer findReserveRequestsCount(@Param("userId")String userId);


    /**
     * 更新订单产品状态。
     *
     * @param id 产品id
     * @param status 状态
     * @return
     */
    int updateRegularStatus(@Param("id") String id, @Param("status") Integer status);


 /**
  * 根据预约申请单id查询首次预约失败时的oprateId
  *
  * @param businessId
  * @return
  */
    List<String> findFundOperate(@Param("businessId") String businessId);




}
