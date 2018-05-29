package com.gomemyc.util;

import com.gomemyc.gold.dao.GoldDueAccountCheckDao;
import com.gomemyc.gold.entity.GoldDueAccountCheck;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
@Service
public class GoldDueAccountCheckUtil {

    @Autowired
   private GoldDueAccountCheckDao goldDueAccountCheckDao;

    public void goldInvestAccountCheck(){

        //根据当前时间，查出所有今天创建的所有需要比对的订单
        DateTimeFormatter format =DateTimeFormatter.ofPattern("yyyy-MM-dd");
        //查询数据库，取出前一天呢完成的订单并对比并更新对比结果
        List<GoldDueAccountCheck> goldDueAccountChecks = goldDueAccountCheckDao.getByCreateTime( LocalDate.now().format(format));
        //对比相同的两笔订单是否相同
        Map<String ,Object> maps = Maps.newHashMap();
        for (GoldDueAccountCheck goldDueAccountCheck : goldDueAccountChecks){
            if (maps.get(goldDueAccountCheck.getReqNo())==null) {
                maps.put(goldDueAccountCheck.getReqNo(), goldDueAccountCheck);
            }else {
                GoldDueAccountCheck goldDueAccount = (GoldDueAccountCheck)maps.get(goldDueAccountCheck.getReqNo());
                if(goldDueAccount.getOrderAmount()==goldDueAccountCheck.getOrderAmount()){
                    goldDueAccountCheckDao.updateComparingStatusBuReqNo(goldDueAccountCheck.getReqNo(),1);
                }else {
                    goldDueAccountCheckDao.updateComparingStatusBuReqNo(goldDueAccountCheck.getReqNo(),2);
                }
                //比对数据后删除相应的map中的存储
                maps.remove(goldDueAccountCheck.getReqNo());
            }
        }
        //判断map中是否还有数据，有即代表两边订单数量不一致
        if (maps.size()!=0) {
            Iterator in = maps.keySet().iterator();
            while (in.hasNext()){
                GoldDueAccountCheck goldDueAccountCheck = (GoldDueAccountCheck) maps.get(in.next()) ;
                goldDueAccountCheckDao.updateComparingStatusBuReqNo(goldDueAccountCheck.getReqNo(),2);
            }
        }



    }

}
