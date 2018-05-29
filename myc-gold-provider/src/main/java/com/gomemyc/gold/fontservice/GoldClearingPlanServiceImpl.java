package com.gomemyc.gold.fontservice;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gomemyc.gold.dao.GoldInvestOrderDao;
import com.gomemyc.gold.dto.GoldProductsExtendDTO;
import com.gomemyc.gold.service.GoldClearingPlanService;
import com.gomemyc.gold.service.GoldProductService;
import com.gomemyc.trade.service.RepaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Created by Administrator on 2017-04-15.
 */
public class GoldClearingPlanServiceImpl implements GoldClearingPlanService {

    private static final Logger logger = LoggerFactory.getLogger(GoldClearingPlanServiceImpl.class);
    @Reference
    private RepaymentService repaymentService;

    @Reference
    private GoldProductService goldProductService;

    @Autowired
    private GoldInvestOrderDao goldInvestOrderDao;

    @Override
    public Integer goldClearingPlan() {

        int index = 0;

        //获取当前时间
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String settleTime = LocalDate.now().format(formatter);
        //根据当前时间获取当天结标的产品集合
        List<GoldProductsExtendDTO> goldProductsExtendDTOS = goldProductService.findProductsBySettleTime(settleTime);
        //调用黄金结算接口
        try {
            for (GoldProductsExtendDTO goldProductsExtendDTO : goldProductsExtendDTOS)
                repaymentService.goldSettleLoan(goldProductsExtendDTO.getId());
            index++;
            logger.info("GoldClearingCheck in GoldClearingPlanServiceImpl , the success [{}]", "调用repaymentService服务保存回还款计划成功");
        }catch (Exception e){
            logger.info("GoldClearingCheck in GoldClearingPlanServiceImpl , the error [{}]" ,"调用repaymentService服务失败");
            logger.info("GoldClearingCheck in GoldClearingPlanServiceImpl , the exception [{}]",e);
        }

        return index;
    }
}
