package com.gomemyc.gold.fontservice;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.gomemyc.gold.dto.GoldDueAccountCheckDTO;
import com.gomemyc.gold.enums.CheckEnums;
import com.gomemyc.gold.service.GoldDueAccountCheckService;
import com.gomemyc.gold.service.GoldDueAccountCheckUtilService;
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
@Service(timeout = 6000)
public class GoldDueAccountCheckUtilServiceImpl implements GoldDueAccountCheckUtilService {


    @Reference
    private GoldDueAccountCheckService goldDueAccountCheckService;

    @Override
    public void goldInvestAccountCheck() {

        //根据当前时间，查出所有今天创建的所有需要比对的订单
        DateTimeFormatter format =DateTimeFormatter.ofPattern("yyyy-MM-dd");
        //查询数据库，取出前一天呢完成的订单并对比并更新对比结果
        List<GoldDueAccountCheckDTO> goldDueAccountChecks = goldDueAccountCheckService.getByCreateTime( LocalDate.now().format(format));
        //对比相同的两笔订单是否相同
        Map<String ,Object> maps = Maps.newHashMap();
        for (GoldDueAccountCheckDTO goldDueAccountCheck : goldDueAccountChecks){

            if (null!=goldDueAccountCheck.getComparingStatus())
                continue;
            if (maps.get(goldDueAccountCheck.getOrderNo())==null) {
                maps.put(goldDueAccountCheck.getOrderNo(), goldDueAccountCheck);
            }else {
                GoldDueAccountCheckDTO goldDueAccount = (GoldDueAccountCheckDTO)maps.get(goldDueAccountCheck.getOrderNo());
                if(goldDueAccount.getOrderAmount()==goldDueAccountCheck.getOrderAmount()&&goldDueAccount.getDataCheckType()!=goldDueAccountCheck.getDataCheckType()){
                    goldDueAccountCheckService.updateComparingStatusBuOrderNo(goldDueAccountCheck.getOrderNo(), CheckEnums.COMPARING_STATUS_SUCCESS.getIndex());
                }else {
                    goldDueAccountCheckService.updateComparingStatusBuOrderNo(goldDueAccountCheck.getOrderNo(), CheckEnums.COMPARING_STATUS_FAILURE.getIndex());
                }
                //比对数据后删除相应的map中的存储
                maps.remove(goldDueAccountCheck.getOrderNo());
            }
        }
        //判断map中是否还有数据，有即代表两边订单数量不一致
        if (maps.size()!=0) {
            Iterator in = maps.keySet().iterator();
            while (in.hasNext()){
                GoldDueAccountCheckDTO goldDueAccountCheck = (GoldDueAccountCheckDTO) maps.get(in.next()) ;
                goldDueAccountCheckService.updateComparingStatusBuOrderNo(goldDueAccountCheck.getOrderNo(),CheckEnums.COMPARING_STATUS_FAILURE.getIndex());
            }
        }


    }
}
