package com.gomemyc.trade.util;

import org.apache.commons.lang3.StringUtils;

public class NumberUtil {
    
    public static boolean isNumeric(String number){
        
        if(StringUtils.isBlank(number)){
            return false;
        }
        
        return number.matches("^\\d+\\.{0,1}\\d*$"); 
    }

}
