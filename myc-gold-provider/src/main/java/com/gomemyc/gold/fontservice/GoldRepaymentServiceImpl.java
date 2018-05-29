package com.gomemyc.gold.fontservice;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.gomemyc.gold.dao.GoldDueAccountCheckDao;
import com.gomemyc.gold.dao.GoldDueSumAccountCheckDao;
import com.gomemyc.gold.dao.GoldInvestOrderDao;
import com.gomemyc.gold.dao.GoldProductDao;
import com.gomemyc.gold.entity.GoldDueAccountCheck;
import com.gomemyc.gold.entity.GoldDueSumAccountCheck;
import com.gomemyc.gold.entity.GoldInvestOrder;
import com.gomemyc.gold.entity.GoldProduct;
import com.gomemyc.gold.enums.GoldCheckType;
import com.gomemyc.gold.enums.GoldOrderStatusEnum;
import com.gomemyc.gold.service.GoldRepaymentService;
import com.gomemyc.trade.dto.InvestRepaymentDTO;
import com.gomemyc.trade.service.RepaymentService;
import com.google.common.collect.Lists;
import jdk.jfr.events.ThrowablesEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * ClassName:GoldRepaymentService <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:   TODO ADD REASON. <br/>
 * Date:     2017年4月03日 下午15:38:34 <br/>
 * @author   liujunhan
 * @version
 * @since    JDK 1.8
 * @see
 * @description   回款、还款计划
 */
@Service(timeout=6000)
public class GoldRepaymentServiceImpl implements GoldRepaymentService {

    private static final Logger logger = LoggerFactory.getLogger(GoldRepaymentServiceImpl.class);

    @Reference
    private RepaymentService repaymentService;

    @Autowired
    private GoldProductDao goldProductDao;

    @Autowired
    private GoldInvestOrderDao goldInvestOrderDao;

    @Autowired
    private GoldDueAccountCheckDao goldDueAccountCheckDao;

    @Autowired
    private GoldDueSumAccountCheckDao goldDueSumAccountCheckDao;



    /**
     * @Title 黄金回款/计划
     * @author liujunhan
     * @date 2017年4月03日
     */
    @Override
    public void goldReceivedPayments() {
        //根据时间查询当前时间后一天到期的所有商品的id
        DateTimeFormatter format =DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String clearTime = LocalDate.now().minusDays(1).format(format);
        List<GoldProduct> goldProductS = goldProductDao.getGoldProductByClearTime(clearTime);
        List<InvestRepaymentDTO> investRepaymentDTOs= null;
        if (goldProductS.size()==0)
            return;
        //判断该商品下的所有的订单并检查该订单下的所有的利息对账和本金对账是否成功，将成功能的订单添加进list
        for (GoldProduct goldProduct : goldProductS){
            Boolean boo = true;
           List<GoldInvestOrder> goldInvestOrders = goldInvestOrderDao.selectGoldInvestOrderByProductId(goldProduct.getId(), GoldOrderStatusEnum.ORDERSTATUS_CONFIRM.getStatus(),GoldOrderStatusEnum.ORDERSTATUS_CONFIRM_SUCCESS.getStatus());
            investRepaymentDTOs= Lists.newArrayListWithExpectedSize(goldInvestOrders.size());
            //便利并检查订单到期对账本金和利息是否成功
           for (GoldInvestOrder goldInvestOrder : goldInvestOrders){
               //查询对账表中的定期金到期对账表
               GoldDueAccountCheck goldDueAccountCheck = goldDueAccountCheckDao.getByReqNoAndDataCheckType(goldInvestOrder.getReqNo(), GoldCheckType.CHECK_TYPE_GOME.getStatus());
              //查询对账表中的到期利息汇总表
               GoldDueSumAccountCheck goldDueSumAccountCheck = goldDueSumAccountCheckDao.getByReqNoAndDataCheckType(goldInvestOrder.getReqNo(),GoldCheckType.CHECK_TYPE_GOME.getStatus());
               if (goldDueAccountCheck !=null&& goldDueSumAccountCheck!=null&&goldDueAccountCheck.getComparingStatus()==1 && goldDueSumAccountCheck.getComparingStatus()==1){
                   InvestRepaymentDTO investRepaymentDTO = new InvestRepaymentDTO();
                   investRepaymentDTO.setLoanId(goldProduct.getLoanId());
                   investRepaymentDTO.setInvestId(goldInvestOrder.getInvestId());
                   investRepaymentDTO.setPrincipalAmount(goldDueAccountCheck.getOrderAmount());
                   investRepaymentDTO.setInterestAmount(goldDueSumAccountCheck.getInterestAmount());
                   investRepaymentDTO.setDueDate(goldProduct.getClearTime());
                   if (null != goldDueAccountCheck.getOrderAmount()&& null !=goldDueSumAccountCheck.getInterestAmount())
                        investRepaymentDTO.setRepayAmount(goldDueAccountCheck.getOrderAmount().add(goldDueSumAccountCheck.getInterestAmount()));
                   investRepaymentDTOs.add(investRepaymentDTO);
               }else{
                   boo = false;
                   investRepaymentDTOs.clear();
                   break;
               }
           }
            //将成功的记录合成的list和商品id存入标的服务的回款/还款计划保存接口 Clearing
            if (boo) {
               try {
                   //repaymentService.saveGoldInvestPayments(goldProduct.getId(), investRepaymentDTOs);
                   repaymentService.updateGoldInvestPayments(goldProduct.getId(), investRepaymentDTOs);
                   investRepaymentDTOs.clear();
                   repaymentService.saveRepayments(goldProduct.getId());
                    logger.info("goldReceivedPayments in GoldRepaymentServiceImpl , the success [{}]", "调用repaymentService服务保存回还款计划成功");
               }catch (RuntimeException e){
                   logger.info("goldReceivedPayments in GoldRepaymentServiceImpl , the error [{}]" ,"调用repaymentService服务失败");
                   logger.info("goldReceivedPayments in GoldRepaymentServiceImpl , the exception [{}]",e);
               }
            }
        }
    }


}
