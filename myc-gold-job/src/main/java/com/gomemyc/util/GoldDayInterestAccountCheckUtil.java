package com.gomemyc.util;

import com.gomemyc.gold.dao.GoldDayInterestAccountCheckDao;
import com.gomemyc.gold.entity.GoldDayInterestAccountCheck;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * ClassName:GoldDayInterestAccountCheckUtil
 * Date:     2017年3月30日 下午4:26:20
 * @author   liujunhan
 * @version
 * @since    JDK 1.8
 * @see
 * @description
 */
@Service
public class GoldDayInterestAccountCheckUtil {

    @Autowired
    private GoldDayInterestAccountCheckDao goldDayInterestAccountCheckDao;

    /**
     *
     * 每日利息对账工具方法
     * @serialData 2017-03-30
     * @author   liujunhan
     */
    public void goldDayInterestAccount() {

        //根据当前时间，查出所有今天创建的所有需要比对的订单
        DateTimeFormatter format =DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<GoldDayInterestAccountCheck> goldDayInterestAccountCheckList  =  goldDayInterestAccountCheckDao.getDayInterestMoneyBycreateTime(LocalDate.now().format(format));

        //对比相同的两笔订单计算的每日利息是否相同
       Map<String ,Object> maps = Maps.newHashMap();
       for (GoldDayInterestAccountCheck goldDayInterestAccountCheck : goldDayInterestAccountCheckList){
           if (maps.get(goldDayInterestAccountCheck.getReqNo())==null) {
               maps.put(goldDayInterestAccountCheck.getReqNo(), goldDayInterestAccountCheck);
           }else {
               GoldDayInterestAccountCheck goldDayInterestAccount = (GoldDayInterestAccountCheck)maps.get(goldDayInterestAccountCheck.getReqNo());
              if(goldDayInterestAccount.getDayInterestMoney()==goldDayInterestAccountCheck.getDayInterestMoney()){
                  goldDayInterestAccountCheckDao.updateComparingStatusByReqNo(goldDayInterestAccountCheck.getReqNo(),1);
              }else {
                  goldDayInterestAccountCheckDao.updateComparingStatusByReqNo(goldDayInterestAccountCheck.getReqNo(),2);
              }
              //比对数据后删除相应的map中的存储
                maps.remove(goldDayInterestAccountCheck.getReqNo());
           }
       }
       //判断map中是否还有数据，有即代表两边订单数量不一致
       if (maps.size()!=0) {
           Iterator in = maps.keySet().iterator();
           while (in.hasNext()){
               GoldDayInterestAccountCheck goldDayInterestAccount = (GoldDayInterestAccountCheck) maps.get(in.next()) ;
               goldDayInterestAccountCheckDao.updateComparingStatusByReqNo(goldDayInterestAccount.getReqNo(),2);
           }
       }
    }

}
