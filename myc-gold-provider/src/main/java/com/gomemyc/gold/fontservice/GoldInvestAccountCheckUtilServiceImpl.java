package com.gomemyc.gold.fontservice;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.gomemyc.gold.dto.GoldInvestAccountCheckDTO;
import com.gomemyc.gold.enums.CheckEnums;
import com.gomemyc.gold.service.GoldInvestAccountCheckService;
import com.gomemyc.gold.service.GoldInvestAccountCheckUtilService;
import com.google.common.collect.Maps;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017-04-19.
 */
@Service(timeout=6000)
public class GoldInvestAccountCheckUtilServiceImpl implements GoldInvestAccountCheckUtilService {

    @Reference
    private GoldInvestAccountCheckService goldInvestAccountCheckService;

    @Override
    public void goldInvestAccountCheck() {
        //根据当前时间，查出所有今天创建的所有需要比对的订单
        DateTimeFormatter format =DateTimeFormatter.ofPattern("yyyy-MM-dd");
        //查询数据库，取出前一天呢完成的订单并对比并更新对比结果
        List<GoldInvestAccountCheckDTO> goldInvestAccountChecks = goldInvestAccountCheckService.getByCreateTime( LocalDate.now().format(format));
        //对比相同的两笔订单计算的每日利息是否相同
        Map<String ,Object> maps = Maps.newHashMap();
        for (GoldInvestAccountCheckDTO goldInvestAccountCheck : goldInvestAccountChecks){
            if (null != goldInvestAccountCheck.getComparingStatus())
                continue;
            if (maps.get(goldInvestAccountCheck.getOrderNo())==null) {
                maps.put(goldInvestAccountCheck.getOrderNo(), goldInvestAccountCheck);
            }else {
                GoldInvestAccountCheckDTO GoldInvestAccount = (GoldInvestAccountCheckDTO)maps.get(goldInvestAccountCheck.getOrderNo());
                if(goldInvestAccountCheck.getOrderAmount()==GoldInvestAccount.getOrderAmount()&&goldInvestAccountCheck.getDataCheckType()!=GoldInvestAccount.getDataCheckType()){
                    goldInvestAccountCheckService.updateComparingStatusByOrderNo(goldInvestAccountCheck.getOrderNo(), CheckEnums.COMPARING_STATUS_SUCCESS.getIndex());
                }else {
                    goldInvestAccountCheckService.updateComparingStatusByOrderNo(goldInvestAccountCheck.getOrderNo(),CheckEnums.COMPARING_STATUS_FAILURE.getIndex());
                }
                //比对数据后删除相应的map中的存储
                maps.remove(goldInvestAccountCheck.getOrderNo());
            }
        }
        //判断map中是否还有数据，有即代表两边订单数量不一致
        if (maps.size()!=0) {
            Iterator in = maps.keySet().iterator();
            while (in.hasNext()){
                GoldInvestAccountCheckDTO goldInvestAccountCheck = (GoldInvestAccountCheckDTO) maps.get(in.next()) ;
                goldInvestAccountCheckService.updateComparingStatusByOrderNo(goldInvestAccountCheck.getOrderNo(), CheckEnums.COMPARING_STATUS_FAILURE.getIndex());
            }
        }
    }
}
