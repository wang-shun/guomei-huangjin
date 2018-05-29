package com.gomemyc.trade.bridge;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.dangdang.ddframe.rdb.sharding.api.HintManager;
import com.gomemyc.trade.dao.InvestDao;
import com.gomemyc.trade.enums.InvestStatus;

@Component
public class InvestBridge {
    
    
    @Autowired
    private InvestDao investDao;
    
    /**
     * 从主库中统计投资金额
     * @param productId
     * @param investStatus
     * @return
     * @author lujixiang
     * @date 2017年4月21日
     *
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public BigDecimal sumInvestAmountByProductIdAndStatus(String productId, InvestStatus... investStatus){
        
        HintManager hintManager = HintManager.getInstance();
        hintManager.setMasterRouteOnly();
        return investDao.sumInvestAmountByProductAndStatus(productId, investStatus);
    }
    
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Long countInvestNumByProductAndStatus(String productId, InvestStatus... investStatus){
        
        HintManager hintManager = HintManager.getInstance();
        hintManager.setMasterRouteOnly();
        return investDao.countInvestNumByProductAndStatus(productId, investStatus);
    }
    

}
