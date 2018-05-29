package com.gomemyc.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gomemyc.trade.dao.ReserveRequestDao;
import com.gomemyc.trade.dto.ReserveRequestDTO;
import com.gomemyc.trade.entity.ReserveRequest;
import com.gomemyc.trade.enums.InvestStatus;
import com.gomemyc.trade.service.ReserveRequestService;
import org.junit.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

/**
 * 预约申请单元测试类.
 * 
 * @author Administrator
 *
 */
public class ReserveRequestTest extends BaseFunctionalTestCase {
	
	/** 预约申请单  */
    @Reference
    private ReserveRequestService reserveRequestService;

    /**
     * 显示预约申请单
     */
    @org.junit.Test
    public void findListByStatus() {
        System.out.println(reserveRequestService.findById("00003BB4-BC10-486D-9CA5-16B6E00B042a"));
        List<ReserveRequestDTO> list =  reserveRequestService.findListByStatus(1);
        System.out.println(list);
    }

    /**
     * 显示预约申请单
     */
    @org.junit.Test
    public void updateStatus() {
        int updateCount = reserveRequestService.updateStatusById("8A015E745B41D8240015B015B41D8246D00000", InvestStatus.BJ_DF_SUCCESS.getIndex());
        System.out.println(updateCount);
    }






}
