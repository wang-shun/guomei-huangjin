package com.gomemyc.gold.enums;

/**
 * Created by Administrator on 2017-04-10.
 */
public enum CheckEnums {


    COMPARING_STATUS_SUCCESS(1,"成功"),


    COMPARING_STATUS_FAILURE(2,"失败");


    private Integer index;
    private String key;

    private CheckEnums(final Integer index, final String key) {
        this.index = index;
        this.key = key;
    }


    public Integer getIndex() {
        return this.index;
    }

    public String getKey() {
        return this.key;
    }

}
