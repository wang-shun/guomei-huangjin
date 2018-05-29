package com.gomemyc.gold.util;

/**
 * Created by Administrator on 2017-04-13.
 */
public class GetProductStatusUtil {

    public static String getProductStatus(Integer status){

        switch (status) {

            case 1:
                return "SCHEDULED";
            case 3:
                return "OPENED";
            case 4:
                return "FINISHED";
            case 5:
                return "FAILED";
            case 6:
                return "SETTLED";
            case 7:
                return "CLEARED";
            default:
                return "ERROR";

        }
    }
}
