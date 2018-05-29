package com.gomemyc.gold.fontservice;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.gomemyc.gold.dto.GoldDueSumAccountCheckDTO;
import com.gomemyc.gold.enums.CheckEnums;
import com.gomemyc.gold.service.GoldDueSumAccountCheckService;
import com.gomemyc.gold.service.GoldDueSumAccountCheckUtilService;
import com.google.common.collect.Maps;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 每日订单对账工具类
 * @serialData 2017-03-30
 * @author   liujunhan
 */
@Service(timeout=6000)
public class GoldDueSumAccountCheckUtilServiceImpl implements GoldDueSumAccountCheckUtilService {

    @Reference
    private GoldDueSumAccountCheckService goldDueSumAccountCheckService;
    @Override
    public void goldDueSumAccountCheck() {
        //根据当前时间，查出所有今天创建的所有需要比对的订单
        DateTimeFormatter format =DateTimeFormatter.ofPattern("yyyy-MM-dd");
        //查询数据库，取出前一天呢完成的订单并对比并更新对比结果
        List<GoldDueSumAccountCheckDTO> goldDueSumAccountChecks = goldDueSumAccountCheckService.getByCreateTime( LocalDate.now().format(format));
        //对比相同的两笔订单是否相同
        Map<String ,Object> maps = Maps.newHashMap();
        for (GoldDueSumAccountCheckDTO goldDueSumAccountCheck : goldDueSumAccountChecks){
            if (null != goldDueSumAccountCheck.getComparingStatus())
                continue;
            if (maps.get(goldDueSumAccountCheck.getOrderNo())==null) {
                maps.put(goldDueSumAccountCheck.getOrderNo(), goldDueSumAccountCheck);
            }else {
                GoldDueSumAccountCheckDTO goldDueSumAccount = (GoldDueSumAccountCheckDTO)maps.get(goldDueSumAccountCheck.getOrderNo());
                if(goldDueSumAccount.getDayInterestMoney()==goldDueSumAccountCheck.getDayInterestMoney()&&goldDueSumAccount.getDataCheckType()!=goldDueSumAccountCheck.getDataCheckType()){
                    goldDueSumAccountCheckService.updateComparingStatusByOrderNo(goldDueSumAccountCheck.getOrderNo(), CheckEnums.COMPARING_STATUS_SUCCESS.getIndex());
                }else {
                    goldDueSumAccountCheckService.updateComparingStatusByOrderNo(goldDueSumAccountCheck.getOrderNo(),CheckEnums.COMPARING_STATUS_FAILURE.getIndex());
                }
                //比对数据后删除相应的map中的存储
                maps.remove(goldDueSumAccountCheck.getOrderNo());
            }
        }
        //判断map中是否还有数据，有即代表两边订单数量不一致
        if (maps.size()!=0) {
            Iterator in = maps.keySet().iterator();
            while (in.hasNext()){
                GoldDueSumAccountCheckDTO goldDueSumAccountCheck = (GoldDueSumAccountCheckDTO) maps.get(in.next()) ;
                goldDueSumAccountCheckService.updateComparingStatusByOrderNo(goldDueSumAccountCheck.getOrderNo(),CheckEnums.COMPARING_STATUS_FAILURE.getIndex());
            }
        }
    }
}
