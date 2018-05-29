package com.gomemyc.invest.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.gomemyc.common.exception.ServiceException;
import com.gomemyc.invest.dao.LoanTypeDao;
import com.gomemyc.invest.dto.LoanTypeCopyDTO;
import com.gomemyc.invest.dto.LoanTypeDTO;
import com.gomemyc.invest.entity.LoanType;
import com.gomemyc.invest.enums.ExceptionCode;
import com.gomemyc.invest.service.LoanTypeService;
import com.gomemyc.util.BeanMapper;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 17/3/16.
 */
@Service
public class LoanTypeServiceImpl implements LoanTypeService{

    /** 标的类型表-业务总开关2：是否允许预约 */
    public static final Integer CAN_RESERVE = 2;
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /** 标的类型表DAO */
    @Autowired
    LoanTypeDao loanTypeDao;

    @Override
    public List<LoanTypeDTO> findListByTypeSwitch(Integer typeSwitch) throws ServiceException {
        List<LoanType> typeList = loanTypeDao.findListByTypeSwitch(CAN_RESERVE);
        List<LoanTypeDTO> loanTypeDtoList = new ArrayList<>();
        for(LoanType loanType : typeList) {
            LoanTypeDTO dto = new LoanTypeDTO();
            dto.setId(loanType.getId());
            dto.setProductName(loanType.getName());
            dto.setEnable((loanType.getTypeSwitch() & CAN_RESERVE) == CAN_RESERVE);
            loanTypeDtoList.add(dto);
        }
        return loanTypeDtoList;
    }

    @Override
    public List<LoanTypeDTO> listByLoanTypeIds(String... ids) throws ServiceException {
        
        List<LoanType> result = null;
        try {
            result = loanTypeDao.listLoanTypeByIds(ids);
            
        } catch (Exception ex) {
            
            logger.error("fail to list loanType by ids, there is a exception : ", ex);
            
            throw new ServiceException(ExceptionCode.EXCEPTION.getIndex(), 
                                       ExceptionCode.EXCEPTION.getErrMsg(), 
                                       ex);
        }
        
        if (null == result || result.isEmpty()) {
            return null;
        }
        
        return BeanMapper.mapList(result, LoanTypeDTO.class);
    }

    /**
     * 根据产品键值获取产品类型
     *
     * @param loanTypeKey
     * @return
     * @throws ServiceException
     */
    @Override
    public LoanTypeCopyDTO findByLoanTypeKey(String loanTypeKey) throws ServiceException {

        if(StringUtils.isBlank(loanTypeKey)){
            throw new ServiceException(ExceptionCode.PRODUCT_TYPEKEY_REQUIRED.getIndex(),
                    ExceptionCode.PRODUCT_TYPEKEY_REQUIRED.getErrMsg());
        }
        LoanType loanType = loanTypeDao.findByKey(loanTypeKey);
        return BeanMapper.map(loanType, LoanTypeCopyDTO.class);
    }
}
