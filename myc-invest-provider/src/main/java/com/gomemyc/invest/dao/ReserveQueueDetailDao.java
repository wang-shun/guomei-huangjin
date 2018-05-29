package com.gomemyc.invest.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.gomemyc.invest.entity.ReserveQueueDetail;

/**
 * 预约队列明细 
 * 
 * @author 何健
 * @creaTime 2017年3月10日
 */
public interface ReserveQueueDetailDao {
    

    
    List<ReserveQueueDetail> listByRequestId(@Param("requestId") String requestId);
    
    int deleteByPrimaryKey(String id);

    int insert(ReserveQueueDetail record);

    int insertSelective(ReserveQueueDetail record);

    ReserveQueueDetail selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(ReserveQueueDetail record);

    int updateByPrimaryKey(ReserveQueueDetail record);

}
