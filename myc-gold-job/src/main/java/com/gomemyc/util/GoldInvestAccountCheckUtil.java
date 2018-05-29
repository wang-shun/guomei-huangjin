package com.gomemyc.util;

import com.gomemyc.gold.dao.GoldInvestAccountCheckDao;
import com.gomemyc.gold.entity.GoldInvestAccountCheck;
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
public class GoldInvestAccountCheckUtil {

    @Autowired
    private GoldInvestAccountCheckDao goldInvestAccountCheckDao;



    public void goldInvestAccountCheck(){

        //根据当前时间，查出所有今天创建的所有需要比对的订单
        DateTimeFormatter format =DateTimeFormatter.ofPattern("yyyy-MM-dd");
        //查询数据库，取出前一天呢完成的订单并对比并更新对比结果
        List<GoldInvestAccountCheck> goldInvestAccountChecks = goldInvestAccountCheckDao.getGoldInvestAccountChecklist( LocalDate.now().format(format));
        //对比相同的两笔订单计算的每日利息是否相同
        Map<String ,Object> maps = Maps.newHashMap();
        for (GoldInvestAccountCheck goldInvestAccountCheck : goldInvestAccountChecks){
            if (maps.get(goldInvestAccountCheck.getReqNo())==null) {
                maps.put(goldInvestAccountCheck.getReqNo(), goldInvestAccountCheck);
            }else {
                GoldInvestAccountCheck GoldInvestAccount = (GoldInvestAccountCheck)maps.get(goldInvestAccountCheck.getReqNo());
                if(goldInvestAccountCheck.getOrderAmount()==GoldInvestAccount.getOrderAmount()){
                    goldInvestAccountCheckDao.updateComparingStatusBuReqNo(goldInvestAccountCheck.getReqNo(),1);
                }else {
                    goldInvestAccountCheckDao.updateComparingStatusBuReqNo(goldInvestAccountCheck.getReqNo(),2);
                }
                //比对数据后删除相应的map中的存储
                maps.remove(goldInvestAccountCheck.getReqNo());
            }
        }
        //判断map中是否还有数据，有即代表两边订单数量不一致
        if (maps.size()!=0) {
            Iterator in = maps.keySet().iterator();
            while (in.hasNext()){
                GoldInvestAccountCheck goldInvestAccountCheck = (GoldInvestAccountCheck) maps.get(in.next()) ;
                goldInvestAccountCheckDao.updateComparingStatusBuReqNo(goldInvestAccountCheck.getReqNo(),2);
            }
        }



    }


}
