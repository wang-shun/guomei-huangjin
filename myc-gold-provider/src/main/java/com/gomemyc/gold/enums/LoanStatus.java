package com.gomemyc.gold.enums;

/**
 * Created by yudanping on 2017/4/5.
 */
public enum LoanStatus implements BaseEnum{

    ABORTED(-1, "已驳回"),

    PENDING(0, "待处理"),

    PUBLISHING(1, "正在开户中"),

    PUBLISHED(2, "已开户"),

    SETTLED(3, "已出账"),

    REPAYED(4, "已还款");

    private Integer index;

    private String key; // 描述


    private LoanStatus(final Integer index, final String key) {

        this.index = index;
        this.key = key;
    }


    public String getKey() {
        // TODO Auto-generated method stub
        return this.key;
    }

    public Integer getIndex() {
        return this.index;
    }

}
