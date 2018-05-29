package com.gomemyc.gold.enums;

import com.gomemyc.common.constant.BaseEnum;

public enum InvestStatus implements BaseEnum{


    ABORTIVE(-6, "已流标"),

    CANCELED(-5, "已取消"),

    COUPON_FAILED(-4, "红包使用失败"),

    CHARGE_FAILED(-3, "本地资金扣款失败"),

    SYNC_FAILED(-2, "北京银行同步资金失败"),

    FROZEN_FAILED(-1, "本地资金冻结失败"),

    INITIAL(0, "申请提交"),

    LOCAL_FROZEN_SUCCESS(1, "本地资金冻结成功（待北京解冻，北京银行解冻中）"),

    BJ_FROZEN_SUCCESS(2, "北京资金冻结成功,债转"),

    BJ_DF_SUCCESS(3, "北京银行解冻成功（待北京同步，北京银行同步中）"),

    //招募中
    BJ_SYN_SUCCESS(4, "银行资金同步成功"),

    SUCCESS(5, "本地同步成功，只有投资成功才能取消投资和成标"),

    //收益中
    SETTLED(6, "已结算"),

    //已完结
    CLEARED(7, "已还款"),

    ASSIGNING(8, "转让中"),

    ASSIGNED(9, "已转让");

    
    private Integer index;
    private String key;
    
    private InvestStatus(final Integer index, final String key) {
        this.index = index;
        this.key = key;
    }

    @Override
    public Integer getIndex() {
        return this.index;
    }

    public String getKey() {
        return this.key;
    }
    

}
