package com.gomemyc.gold.enums;

import com.gomemyc.common.constant.BaseEnum;

/**
 * 产品状态
 * @author lujixiang
 * @creaTime 2017年3月5日
 */
public enum ProductStatus implements BaseEnum{
    
    CANCELED(-2, "取消"),

    BASE(-1,"基础"),

    INITIATED(0, "初始"),
    
    SCHEDULED(1, "准备调度中"),
    //2：调度中(前端能看见)
    STARTSCHEDULED(2, "调度中"),

    OPENED(3, "开标中"),
    
    FINISHED(4, "满标"),
    
    FAILED(5, "到期未满标"),
    
    ABORTIVE(-3, "流标"),
    
    SETTLED(6, "已结算"),
    
    CLEARED(7, "已还款"),
    
    ARCHIVED(8, "已存档"),

    ALL(10,"全部");
    
    
    private String key;
    private Integer index;
    
    
    
    private ProductStatus(Integer index, String key){
        this.key = key;
        this.index = index;
    }
    

    public String getKey() {
        return this.key;
    }

    @Override
    public Integer getIndex() {
        return this.index;
    }

}
