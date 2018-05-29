package com.gomemyc.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gomemyc.trade.util.JsonHelper;

public class Test {
    public static void main(String[] args) throws JsonProcessingException {
        System.out.println(StringUtils.isBlank("null"));
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("status", -9);
        map.put("BjDfCode", "w222");

        System.out.println((Integer)map.get("status"));
        System.out.println((String)map.get("BjDfCode"));
    }

}
