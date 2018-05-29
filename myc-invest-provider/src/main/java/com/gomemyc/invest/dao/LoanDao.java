package com.gomemyc.invest.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.gomemyc.invest.entity.Loan;
import com.gomemyc.invest.enums.LoanStatus;
import com.gomemyc.invest.enums.LoanSyncStatus;

/**
 * 标的dao
 * @author lujixiang
 * @creaTime 2017年3月5日
 */
public interface LoanDao {
    
    int save(Loan loan);
    
    Loan findById(@Param("id") String id);
    
    int updateSyncStatus(@Param("id") String id, 
                         @Param("syncStatus") LoanSyncStatus syncStatus, 
                         @Param("syncReturnTime") Date syncReturnTime);
    
    int updateSyncOrderNo(@Param("id") String id, 
                          @Param("syncOrderNo") String syncOrderNo, 
                          @Param("updateTime") Date updateTime);
    
    int updateLocalSyncStatus(@Param("id") String id, 
                              @Param("localSyncStatus") LoanSyncStatus localSyncStatus);
    
    int updateLoanStatus(@Param("id") String id, 
                         @Param("status") LoanStatus loanStatus,
                         @Param("originStatus") LoanStatus originStatus);


    Loan findByPortfolioNo(@Param("portfolioNo") String portfolioNo);


    /**
     * 查询产品下的投资数量。
     *
     * @param productId 产品id
     * @return
     */
    int selectInvestCountByProductId(@Param("productId") String productId);

    /**
     * 流标更新投资记录status = -6
     *
     * @param productId 产品id
     * @param status
     * @return
     */
    int updateInvestStatusByProductId(@Param("productId") String productId, @Param("status") Integer status, @Param("oldStatus") Integer oldStatus);

    int settleLoanValueDate(@Param("loanId") String loanId,@Param("status") LoanStatus status, @Param("settleTime") Date settleTime,
    		@Param("valueTime") Date valueTime,@Param("dueTime") Date dueTime);
    /**
     * 结标更新投资记录status = 6
     *
     * @param loanId 标id
     * @param valueTime
     * @return
     */
    int settleUpdateInvestStatusByLoanId(@Param("loanId") String loanId, @Param("valueTime") Date valueTime);

    List<String> listByLoanStatus(@Param("status") LoanStatus status);

    int updateStatus(@Param("id") String id, @Param("status") LoanStatus status);

    /**
     * 根据投资单状态列表，查询数量
     *
     * @param productId 产品id
     * @param investStatusList 投资单状态
     * @return
     */
    Integer findInvestStatusCountByProductId( @Param("productId") String productId, @Param("investStatusList") List<Integer> investStatusList);

}
