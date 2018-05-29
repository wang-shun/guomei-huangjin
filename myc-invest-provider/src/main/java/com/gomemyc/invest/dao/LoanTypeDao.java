package com.gomemyc.invest.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.gomemyc.invest.entity.LoanType;

/**
 * 标的类型dao
 * @author lujixiang
 * @creaTime 2017年3月3日
 */
public interface LoanTypeDao {
    
    /**
     * 根据id获取标的类型
     * @param id: 标的类型id
     * @return
     * @author lujixiang
     * @date 2017年3月5日
     *
     */
    LoanType findById(@Param("id") String id);
    
    /**
     * 根据标的类型的键值获取标的类型
     * @param key:标的类型键值
     * @return 
     * @author lujixiang
     * @date 2017年3月5日
     *
     */
    LoanType findByKey(@Param("key") String key);
    
    /**
     * 新增标的类型
     * @param loanType:标的类型
     * @return
     * @author lujixiang
     * @date 2017年3月5日
     *
     */
    int save(LoanType loanType);
    
    /**
     * 根据业务开关字段获得标的类型集合。
     * 
     * @param typeSwitch 标的业务开关
     * @author 何健
     * @date 2017年3月10日
     * 
     * @return 标的类型集合
     *
     */
    List<LoanType> findListByTypeSwitch(Integer typeSwitch);
    
    /**
     * 获取标的类型列表
     * @param typeIds
     * @return
     * @author lujixiang
     * @date 2017年3月30日
     *
     */
    List<LoanType> listLoanTypeByIds(@Param("typeIds") String... typeIds);

}
