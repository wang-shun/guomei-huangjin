package com.gomemyc.invest.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.gomemyc.invest.entity.ReserveQueueDetail;
import com.gomemyc.invest.entity.ReserveRequest;

/**
 * 预约申请单 
 * 
 * @author 何健
 * @creaTime 2017年3月10日
 */
public interface ReserveRequestDao {
    

    
    List<ReserveQueueDetail> listByRequestId(@Param("requestId") String requestId);
    
    /**
     * 根据id查找预约申请单。
     * 
     * @param requestId
     * @return
     */
    ReserveRequest findByRequestId(String requestId);
    
    int deleteByPrimaryKey(String id);

    int insert(ReserveRequest record);

    int insertSelective(ReserveRequest record);

    ReserveRequest selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(ReserveRequest record);

    int updateByPrimaryKey(ReserveRequest record);

}
