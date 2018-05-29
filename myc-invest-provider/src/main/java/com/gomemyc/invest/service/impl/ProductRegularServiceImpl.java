package com.gomemyc.invest.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.gomemyc.common.exception.ServiceException;
import com.gomemyc.invest.dao.LoanTypeDao;
import com.gomemyc.invest.dao.ProductRegularDao;
import com.gomemyc.invest.dto.LoanTypeDTO;
import com.gomemyc.invest.dto.ProductDTO;
import com.gomemyc.invest.dto.ProductRegularDTO;
import com.gomemyc.invest.entity.LoanType;
import com.gomemyc.invest.entity.ProductRegular;
import com.gomemyc.invest.enums.ExceptionCode;
import com.gomemyc.invest.enums.ProductStatus;
import com.gomemyc.invest.service.LoanTypeService;
import com.gomemyc.invest.service.ProductRegularService;
import com.gomemyc.util.BeanMapper;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 17/3/16.
 */
@Service
public class ProductRegularServiceImpl implements ProductRegularService{


    /** 产品表DAO */
    @Autowired
    ProductRegularDao productRegularDao;

    @Override
    public ProductRegularDTO findByTitle(String title) throws ServiceException {
        ProductRegular productRegular = productRegularDao.findByTitle(title);
        ProductRegularDTO dto = BeanMapper.map(productRegular, ProductRegularDTO.class);
        return dto;
    }

    @Override
    public Integer updateInvestById(BigDecimal investAmount, Integer investNumber, String id) throws ServiceException {
        return productRegularDao.updateInvestById(investAmount, investNumber, id);
    }

    @Override
    public Integer updateFinshAndStatusById(Date finishTime, Integer status, String id) throws ServiceException {
        return productRegularDao.updateFinshAndStatusById(finishTime, status, id);
    }

	@Override
	public Integer updateStatusById(ProductStatus status, String id) throws ServiceException {
		if(status==null){
			throw new ServiceException(ExceptionCode.PRODUCT_STATUS_REQUIRED.getIndex(),ExceptionCode.PRODUCT_STATUS_REQUIRED.getErrMsg());
		}
		if(StringUtils.isBlank(id)){
			throw new ServiceException(ExceptionCode.PRODUCT_ID_REQUIRED.getIndex(),ExceptionCode.PRODUCT_ID_REQUIRED.getErrMsg());
		}
		return productRegularDao.updateProductStatus(status, id);
	}

    /**
     * 保存定期产品
     *
     * @param productRegular
     * @return
     */
    @Override
    public Boolean save(ProductRegularDTO productRegular) throws ServiceException{
        try{
            if(productRegular==null){
                throw new ServiceException(ExceptionCode.PRODCT_NOT_REQUIRED.getIndex(),
                        ExceptionCode.PRODCT_NOT_REQUIRED.getErrMsg());
            }
            if(1 != productRegularDao.saveProductRegular(BeanMapper.map(productRegular, ProductRegular.class))){
                throw new ServiceException(ExceptionCode.PRODUCT_SAVE_ERROR.getIndex(),
                                       ExceptionCode.PRODUCT_SAVE_ERROR.getErrMsg());
            }
            return true;
        } catch (Exception ex){

            throw new ServiceException(ExceptionCode.EXCEPTION.getIndex(),
                                       ExceptionCode.EXCEPTION.getErrMsg(),
                                       ex);

        }

    }

    @Override
    public List<String> findWaitFailRegular(Integer status, Integer productSwitch) throws ServiceException {

        return productRegularDao.findWaitFailRegular(status, productSwitch);

    }
}
