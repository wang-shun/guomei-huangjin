package com.gomemyc.invest.enums;

import com.gomemyc.common.constant.BaseEnum;

public enum ProductSource implements BaseEnum{
    
    
    REGULAR(0, "定期表"),
    
    PJ(1, "票据表");
    
    
    
    private Integer index;
    
    private String name;
    
    private ProductSource(Integer index, String name){
        
        this.index = index;
        this.name = name;
    }

    @Override
    public Integer getIndex() {
        return this.index;
    }

}
