package com.gomemyc.invest.bridge;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.dangdang.ddframe.rdb.sharding.api.HintManager;
import com.gomemyc.common.util.UUIDGenerator;
import com.gomemyc.invest.dao.LoanDao;
import com.gomemyc.invest.dao.ProductRegularDao;
import com.gomemyc.invest.dao.ProductRulesDao;
import com.gomemyc.invest.dto.ProductRulesDTO;
import com.gomemyc.invest.entity.ProductRegular;
import com.gomemyc.invest.entity.ProductRules;
import com.gomemyc.invest.enums.RulesType;

@Component
public class ProductBridge {
    
	@Autowired
	private ProductRulesDao productRulesDao;
	
	@Autowired
    private ProductRegularDao productRegularDao;
	
    
    @Transactional
    public Boolean saveProductRule(List<ProductRulesDTO> productRulesDTOs) throws Exception{
    	for (ProductRulesDTO dto : productRulesDTOs) {
			ProductRules rule=new ProductRules();
			rule.setProductId(dto.getProductId());
			rule.setRulesId(dto.getRulesId());
			rule.setType(dto.getType());
			rule.setValue(dto.getValue());
			productRulesDao.save(rule);
		}
    	return true;
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {Exception.class})
    public void saveLoan() {
        
        HintManager hintManager = HintManager.getInstance();
        hintManager.setMasterRouteOnly();
        
        productRegularDao.findById("1231213");
        
        
        ProductRules rule=new ProductRules();
        rule.setId(UUIDGenerator.generate());
        rule.setProductId("loan-333333");
        rule.setRulesId("3434");
        rule.setType(RulesType.MAX_TIME);
        rule.setValue("2233");
        productRulesDao.save(rule);
        
    }
    
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void test(){
        
        productRegularDao.findById("1231213");
        this.saveLoan();
        
    }
}
