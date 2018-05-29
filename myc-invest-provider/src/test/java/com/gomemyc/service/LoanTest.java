package com.gomemyc.service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.config.annotation.Reference;
import com.gomemyc.invest.dto.LoanDTO;
import com.gomemyc.invest.dto.ProductRegularDTO;
import com.gomemyc.invest.entity.Loan;
import com.gomemyc.invest.enums.LoanPurpose;
import com.gomemyc.invest.enums.LoanSource;
import com.gomemyc.invest.enums.LoanStatus;
import com.gomemyc.invest.enums.LoanSyncStatus;
import com.gomemyc.invest.enums.ProductStatus;
import com.gomemyc.invest.enums.RepaymentMethod;
import com.gomemyc.invest.service.LoanService;
import com.gomemyc.util.BeanMapper;
import org.junit.*;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by yudanping on 2017/4/12.
 */

public class LoanTest extends BaseFunctionalTestCase {

    private final static Logger logger = LoggerFactory.getLogger(LoanTest.class);
    @Reference
    LoanService loanService;


    @org.junit.Test
    public void save(){
        LoanDTO product = new LoanDTO();
        product.setId("dq-"+ UUID.randomUUID());
        product.setCreateTime(new Date());
        product.setTitle("ceshi用例Service");
        product.setAmount(new BigDecimal(10000));
        product.setCompanyName("gome");
        product.setYears(0);
        product.setDays(60);
        product.setMonths(0);
        product.setMethod(RepaymentMethod.BulletRepayment);
        product.setStatus(LoanStatus.PENDING);
        product.setUserId("~~~~~");
        product.setLoanTypeId("@@@@@@@@");
        product.setLoanTypeKey("yyyy");
        product.setSyncStatus(LoanSyncStatus.INITIAL);
        product.setPurpose(LoanPurpose.CORPORATION);
        product.setSource(LoanSource.ASSET);
        loanService.createLoan(product);
    }

    @org.junit.Test
    public void findByPno(){
        try {
            LoanDTO loan = loanService.findByPortfolioNo("ceshiyuyue");
            System.out.print(loan);
//            logger.info("title={}",loan.getTitle());
        }catch (Exception e){
            logger.info(e);
        }

    }


    @Test
    public void findByLoanStatus() {
//        List<LoanDTO> loanDTOS = loanService.findByLoanStatus(LoanStatus.WAIT_OPEN_ACCOUNT);
//        System.out.println(loanDTOS);
    }
    
    
    @Test
    public void listByLoanStatus() {
      List<String> ids = loanService.listByLoanStatus(LoanStatus.WAIT_OPEN_ACCOUNT);
      System.out.println(ids);
    }
    
    @Test
    public void openLoanAccount() {
      loanService.openLoanAccount("019b2f6f-a1fb-4265-948a-3cbec732333d");
    }

}
