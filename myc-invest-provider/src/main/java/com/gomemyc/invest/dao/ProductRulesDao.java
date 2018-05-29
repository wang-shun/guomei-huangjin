package com.gomemyc.invest.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.gomemyc.invest.entity.ProductRules;

/**
 * 规则dao
 * @author lujixiang
 * @creaTime 2017年3月5日
 */
public interface ProductRulesDao {
    
    int save(ProductRules rules);
    
    List<ProductRules> listByProductId(@Param("productId") String productId);

}
