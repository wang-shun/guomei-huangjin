package com.gomemyc.gold.fontservice;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.gomemyc.gold.dto.GoldDayInterestAccountCheckDTO;
import com.gomemyc.gold.enums.CheckEnums;
import com.gomemyc.gold.service.GoldDayInterestAccountCheckService;
import com.gomemyc.gold.service.GoldDayInterestAccountCheckUtilService;
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
public class GoldDayInterestAccountCheckUtilServiceImpl implements GoldDayInterestAccountCheckUtilService {


    @Reference
    private GoldDayInterestAccountCheckService goldDayInterestAccountCheckService;

    /**
     *
     * 每日利息对账工具方法
     * @serialData 2017-03-30
     * @author   liujunhan
     */
    @Override
    public void goldDayInterestAccount() {
        //根据当前时间，查出所有今天创建的所有需要比对的订单
        DateTimeFormatter format =DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<GoldDayInterestAccountCheckDTO> goldDayInterestAccountCheckList  =  goldDayInterestAccountCheckService.getBycreateTime(LocalDate.now().format(format));

        //对比相同的两笔订单计算的每日利息是否相同
        Map<String ,Object> maps = Maps.newHashMap();
        for (GoldDayInterestAccountCheckDTO goldDayInterestAccountCheck : goldDayInterestAccountCheckList){
            if (null != goldDayInterestAccountCheck.getComparingStatus())
                continue;
            if (maps.get(goldDayInterestAccountCheck.getOrderNo())==null) {
                maps.put(goldDayInterestAccountCheck.getOrderNo(), goldDayInterestAccountCheck);
            }else {
                GoldDayInterestAccountCheckDTO goldDayInterestAccount = (GoldDayInterestAccountCheckDTO)maps.get(goldDayInterestAccountCheck.getOrderNo());
                if(goldDayInterestAccount.getDayInterestMoney()==goldDayInterestAccountCheck.getDayInterestMoney()&&goldDayInterestAccount.getDataCheckType()!=goldDayInterestAccountCheck.getDataCheckType()){
                    goldDayInterestAccountCheckService.updateComparingStatusByOrderNo(goldDayInterestAccountCheck.getOrderNo(), CheckEnums.COMPARING_STATUS_SUCCESS.getIndex());
                }else {
                    goldDayInterestAccountCheckService.updateComparingStatusByOrderNo(goldDayInterestAccountCheck.getOrderNo(), CheckEnums.COMPARING_STATUS_FAILURE.getIndex());
                }
                //比对数据后删除相应的map中的存储
                maps.remove(goldDayInterestAccountCheck.getOrderNo());
            }
        }
        //判断map中是否还有数据，有即代表两边订单数量不一致
        if (maps.size()!=0) {
            Iterator in = maps.keySet().iterator();
            while (in.hasNext()){
                GoldDayInterestAccountCheckDTO goldDayInterestAccount = (GoldDayInterestAccountCheckDTO) maps.get(in.next()) ;
                goldDayInterestAccountCheckService.updateComparingStatusByOrderNo(goldDayInterestAccount.getOrderNo(),CheckEnums.COMPARING_STATUS_FAILURE.getIndex());
            }
        }
    }
}
