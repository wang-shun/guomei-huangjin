package com.gomemyc.util;

import com.gomemyc.gold.dao.GoldDueSumAccountCheckDao;
import com.gomemyc.gold.entity.GoldDueSumAccountCheck;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017-03-30.
 */
@Service
public class GoldDueSumAccountCheckUtil {

    @Autowired
   private GoldDueSumAccountCheckDao goldDueSumAccountCheckDao;

    public void goldDueSumAccountCheck(){

        //根据当前时间，查出所有今天创建的所有需要比对的订单
        DateTimeFormatter format =DateTimeFormatter.ofPattern("yyyy-MM-dd");
        //查询数据库，取出前一天呢完成的订单并对比并更新对比结果
        List<GoldDueSumAccountCheck> goldDueSumAccountChecks = goldDueSumAccountCheckDao.getByCreateTime( LocalDate.now().format(format));
        //对比相同的两笔订单是否相同
        Map<String ,Object> maps = Maps.newHashMap();
        for (GoldDueSumAccountCheck goldDueSumAccountCheck : goldDueSumAccountChecks){
            if (maps.get(goldDueSumAccountCheck.getReqNo())==null) {
                maps.put(goldDueSumAccountCheck.getReqNo(), goldDueSumAccountCheck);
            }else {
                GoldDueSumAccountCheck goldDueSumAccount = (GoldDueSumAccountCheck)maps.get(goldDueSumAccountCheck.getReqNo());
                if(goldDueSumAccount.getInterestAmount()==goldDueSumAccountCheck.getInterestAmount()){
                    goldDueSumAccountCheckDao.updateComparingStatusBuReqNo(goldDueSumAccountCheck.getReqNo(),1);
                }else {
                    goldDueSumAccountCheckDao.updateComparingStatusBuReqNo(goldDueSumAccountCheck.getReqNo(),2);
                }
                //比对数据后删除相应的map中的存储
                maps.remove(goldDueSumAccountCheck.getReqNo());
            }
        }
        //判断map中是否还有数据，有即代表两边订单数量不一致
        if (maps.size()!=0) {
            Iterator in = maps.keySet().iterator();
            while (in.hasNext()){
                GoldDueSumAccountCheck goldDueSumAccountCheck = (GoldDueSumAccountCheck) maps.get(in.next()) ;
                goldDueSumAccountCheckDao.updateComparingStatusBuReqNo(goldDueSumAccountCheck.getReqNo(),2);
            }
        }



    }
}
