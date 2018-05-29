package com.gomemyc.invest.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import com.gomemyc.invest.enums.ProductStatus;

public class ProductStatusUtil {
    
    private static final String SCHEDULED = "SCHEDULED"; // PC调度中
    
    private static final String ALL = "ALL"; // PC调度中
    
    
    /**
     * 
     * @param frontStatus
     * @return
     * @author lujixiang
     * @date 2017年4月8日
     *
     */
    public static List<ProductStatus> analyzeFrontStatusToProductStatus(String frontStatus){
        
        // 默认显示调度中、开标中、已满标、到期未满标、结算中、已还款
        if (StringUtils.isBlank(frontStatus) || SCHEDULED.equals(frontStatus) || ALL.equals(frontStatus)) {
            
           return Arrays.asList(ProductStatus.SCHEDULED, 
                                ProductStatus.OPENED,
                                ProductStatus.FINISHED,
                                ProductStatus.FAILED,
                                ProductStatus.SETTLED,
                                ProductStatus.CLEARED);
        }
        
        // 多个状态解析
        if (frontStatus.contains(",")) {
            
            String[] statusArr = frontStatus.split(",");
            
            List<ProductStatus> statusList = new ArrayList<ProductStatus>();
            for(String status : statusArr){
                ProductStatus productStatus = EnumUtils.getEnum(ProductStatus.class, status);
                
                if (null != productStatus) {
                    statusList.add(productStatus);
                }
            }
            
            return statusList;
        }
        
        ProductStatus productStatus = EnumUtils.getEnum(ProductStatus.class, frontStatus);
        
        if (null != productStatus) {
            return Arrays.asList(productStatus);
        }
        
        return null;
    }

}
