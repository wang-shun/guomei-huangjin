package com.gomemyc.invest.utils;

import org.apache.commons.lang3.StringUtils;
public class ReserveUtils {

    /**
     * 显示预约起投金额
     * 如限额信息为12000元，处理后的结果为：1.2万，最多保留两位小数，两位小数之后的值会被舍去
     * 入参是空字符串或不是数字，则返回空字符串
     * @param limit 银行限额信息
     * @return
     */
    public static String getReserveAmountLimitDisplay(String limit) {

        String limitDisplay = "";
        if(StringUtils.isBlank(limit)) {
            return limitDisplay;
        }

        //判断是否是小数，如果是，则去掉小数位
        if (limit.contains(".")) {
            limit = limit.substring(0, limit.indexOf("."));
        }

        Integer limitInt = 0;
        try {
            limitInt = Integer.parseInt(limit);
        } catch(Exception e) {
            return limitDisplay;
        }

        //modify by wangweiyu in 2016-10-10 文案修改
        if(limitInt <= 0) {
            return limit = "";
        } else if(limitInt > 0 && limitInt < 10000) {
            return limit + "元";
        } else {
            int firstModRemain = limitInt % 10000;
            int firstModInt = limitInt / 10000;
            int secondModRemain = firstModRemain % 1000;
            int secondModInt = firstModRemain / 1000;
            int thirdModInt = secondModRemain / 100;

            if (firstModInt != 0 && firstModRemain == 0) {
                return new StringBuffer().append(firstModInt)
                    .append("万元").toString();
            } else if (secondModInt != 0 && secondModRemain == 0) {
                return new StringBuffer().append(firstModInt).append(".")
                    .append(secondModInt).append("万元").toString();
            } else if (thirdModInt != 0) {
                return new StringBuffer().append(firstModInt).append(".")
                    .append(secondModInt).append(thirdModInt)
                    .append("万元").toString();
            } else {
                return new StringBuffer().append(firstModInt)
                    .append("万元").toString();
            }
        }
    }

}
