package com.gomemyc.invest.util;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.gomemyc.invest.enums.ProductSource;

public class LoanKeyUtil {
    
    private static final String DQ = "DQ" ; // 定期(美保宝、美融宝)
    
    private static final String PJ = "PJ";  // 票据
    
    private static final String DQZZ = "ZZ";    // 定期债转
    
    
    /**
     * 解析前端的产品类型、获取数据库里真实的标的类型
     * @param frontProductKey
     * @return
     * @author lujixiang
     * @date 2017年4月8日
     *
     */
    public static List<String> analyzeFrontKeyToLoanKey(String frontProductKey){
        
        if (StringUtils.isBlank(frontProductKey)
                || DQZZ.equals(frontProductKey)) {  // 定期债转不需要传递具体的标的类型
            return null;
        }
        
        if (DQ.equals(frontProductKey)) {
            return Arrays.asList("QYD", "XT", "BL", "XFD"); 
        }
        
        return Arrays.asList(frontProductKey);
    }
    
    
    /**
     * 解析前端的产品类型,获取真实的产品来源
     * @param frontProductKey
     * @return
     * @author lujixiang
     * @date 2017年4月8日
     *
     */
    public static ProductSource getProductSourceByFrontKey(String frontProductKey){
        
        // 票据
        if (PJ.equals(frontProductKey)) {
            
            return ProductSource.PJ;
        }
        
        return ProductSource.REGULAR;
    }
    
    /**
     * 根据产品类型判断是否债转
     * @param frontProductKey
     * @return
     * @author lujixiang
     * @date 2017年4月8日
     *
     */
    public static Boolean isDebtAnalyzeFrontKey(String frontProductKey){
        
        if (DQZZ.equals(frontProductKey)) {
            return true;
        }
        
        return false;
    }

}
