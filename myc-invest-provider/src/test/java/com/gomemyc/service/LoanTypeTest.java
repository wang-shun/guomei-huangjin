package com.gomemyc.service;

import java.util.List;

import com.gomemyc.invest.dto.LoanTypeCopyDTO;
import org.junit.Test;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gomemyc.account.dto.AccountDTO;
import com.gomemyc.account.dto.BankCardDTO;
import com.gomemyc.account.service.AccountService;
import com.gomemyc.account.service.BankCardService;
import com.gomemyc.common.page.Page;
import com.gomemyc.invest.dto.LoanTypeDTO;
import com.gomemyc.invest.dto.ProductDTO;
import com.gomemyc.invest.dto.ProductRegularDTO;
import com.gomemyc.invest.enums.OrderPlan;
import com.gomemyc.invest.enums.ProductStatus;
import com.gomemyc.invest.service.LoanService;
import com.gomemyc.invest.service.LoanTypeService;
import com.gomemyc.invest.service.ProductService;
import com.gomemyc.invest.util.LoanKeyUtil;
import com.gomemyc.invest.util.ProductStatusUtil;

public class LoanTypeTest extends BaseFunctionalTestCase {
    
    @Reference
    private LoanService loanService;
    
    @Reference 
    ProductService productService;
    
    @Reference
    LoanTypeService loanTypeService;
    
    @Reference
    AccountService accountService;
    
    @Reference
    BankCardService bankCardService;
    
    @Test
    public void createLoan() {
//        java.sql.JDBCType
//        loanService.createLoan(null);
//        loanService.listProductByPages(null, null, true, 1, 10);
        
//        loanService.publishLoanAccount("dq-6764257a-b081-43d0-b237-b8f461c74512");
        
        List<String> ids = productService.listRegularProductIdsToOpen();
        System.out.println(ids.size());
        productService.finishProduct("dq-92fca329-598b-4dd0-a04e-a54062115806", ProductStatus.FINISHED);
        
        
//        List<ProductRegularDTO> productRegularDTOs = loanService.listRegularProductToPublish();
//
//        System.out.println(productRegularDTOs);
        
//        System.out.println(productService.countDebtOpened());
        
    }
    
    @Test
    public void getLoan(){
        ProductDTO productDTO = loanService.getProduct("dq-558db288-0669-11e7-a905-0050569426e2");
        System.out.println("productDTO == " + productDTO.toString());
    }
    
    @Test
    public void listProductByPages(){
        
//        Page<ProductDTO> productDTOs = productService.listProductByPages("XSB", null, 1, 10, false, 1, 10);
////
//        System.out.println("productDTOs size = " + productDTOs.getTotalElements());
        
//        List<LoanTypeDTO> loanTypeDTOs = loanTypeService.listByLoanTypeIds("402848815b0955c6015b095942a90002");
//        
//        System.out.println("loanTypeDTOs size = " + loanTypeDTOs.size());

//        loanService.publishLoanAccount("dq-558db288-0669-11e7-a905-0050569426e2");

//        Page<ProductDTO> products = productService.listProductByPages("DQ", null, 0, 100, OrderPlan.RATE_ASC_STATUS_ASC, 1, 10);
////
//        System.out.println(products.getTotalElements());
//        productService.finishProduct("dq-92fca329-598b-4dd0-a04e-a54062115806", ProductStatus.FINISHED);
        
//        AccountDTO accountDTO = accountService.getByUserId("D36DA29B-8F96-4729-AE4E-B533C40BBBAC");
        
        Page<ProductDTO> products = productService.listProductByPages("PJ", "OPENED", 0, 100, OrderPlan.RATE_ASC_STATUS_ASC, 1, 10);
        
//        BankCardDTO bankCardDTO = bankCardService.getBankCardById("D36DA29B-8F96-4729-AE4E-B533C40BBBAC");

        
        System.out.println( products.getContent());
    }

    @Test
    public void findByLoanType(){
        LoanTypeCopyDTO loanType = loanTypeService.findByLoanTypeKey("PJ");
        System.out.println(loanType.getName());
    }
    
    @Test
    public void testList(){
        List<String> ids = productService.listRegularProductIdsToFinish();
        
        System.out.println(ids.size());
    }

}
