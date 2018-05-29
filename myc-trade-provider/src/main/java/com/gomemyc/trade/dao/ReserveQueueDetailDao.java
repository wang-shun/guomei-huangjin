package com.gomemyc.trade.dao;

import com.gomemyc.trade.entity.ReserveQueueDetail;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 预约队列明细 
 * 
 * @author 何健
 * @creaTime 2017年3月10日
 */
public interface ReserveQueueDetailDao {

    /**
     * 根据预约队列id查找预约队列明细列表。
     *
     * @param investQueueId
     * @return
     */
    List<ReserveQueueDetail> findListByQueueId(@Param("investQueueId") String investQueueId);

    /**
     * 根据申请单id 和 队列状态，查找队列明细
     *
     * @param applyId 申请单id
     * @param status 队列状态
     * @return
     */
    List<ReserveQueueDetail> listByRequestIdAndQueueStatus(@Param("applyId") String applyId, @Param("status") Integer status);

    /**
     * 根据申请单id，查询队列明细
     *
     * @param applyId 申请单id
     * @return
     */
    List<ReserveQueueDetail> findListByApplyId(@Param("applyId") String applyId);


    int insert(ReserveQueueDetail record);


    ReserveQueueDetail findById(String id);

    int updateBySelective(ReserveQueueDetail record);


}
