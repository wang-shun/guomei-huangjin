package com.gomemyc.trade.util;

import com.gomemyc.invest.dto.ProductRegularDTO;

/**
 * 计算产品期限
 *
 * @author 何健
 */
public class LoanDaysUtil {

    /** 一年365天 */
    final static int DAYS_PER_YEAR = 365;

    /** 一月30天 */
    final static int DAYS_PER_MONTH = 30;

    /** 一年12个月 */
    final static int MONTHS_PER_YEAR = 12;

    public static int getTotalDays(ProductRegularDTO regular) {
        return getTotalMonths(regular) * DAYS_PER_MONTH + regular.getDays();
    }

    public static int getTotalMonths(ProductRegularDTO regular) {
        return regular.getYears() * MONTHS_PER_YEAR + regular.getMonths();
    }
}
