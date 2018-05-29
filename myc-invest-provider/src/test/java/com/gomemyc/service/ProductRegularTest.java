package com.gomemyc.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gomemyc.invest.dto.ProductRegularDTO;
import com.gomemyc.invest.enums.LoanSyncStatus;
import com.gomemyc.invest.enums.ProductStatus;
import com.gomemyc.invest.enums.RepaymentMethod;
import com.gomemyc.invest.service.ProductRegularService;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

/**
 * Created by yudanping on 2017/4/11.
 */
public class ProductRegularTest extends BaseFunctionalTestCase {

    @Reference
    ProductRegularService productRegularService;


    @org.junit.Test
    public void save(){
       ProductRegularDTO product = new ProductRegularDTO();
        product.setId("dq-"+ UUID.randomUUID());
        product.setCreateTime(new Date());
        product.setTitle("ceshi用例Service");
        product.setEndTime(new Date());;
        product.setAmount(new BigDecimal(10000));
        product.setCompanyName("gome");
        product.setYears(0);
        product.setDays(60);
        product.setMonths(0);
        product.setMethod(RepaymentMethod.BulletRepayment);
        product.setOpenTime(new Date());
        product.setEndTime(new Date());
        product.setStatus(ProductStatus.BASE);
        product.setLoanId("   ");
        product.setUserId("~~~~~");
        product.setTypeKey("XSB");
        product.setTypeId("~~~~~");
        product.setTypeName("@@@@@@@@@@@");
        product.setLoginName("gome");
        productRegularService.save(product);
    }
}
