package com.gomemyc.invest.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.gomemyc.common.exception.ServiceException;
import com.gomemyc.invest.dao.ProductBillDao;
import com.gomemyc.invest.dto.ProductBillDTO;
import com.gomemyc.invest.entity.ProductBill;
import com.gomemyc.invest.enums.ExceptionCode;
import com.gomemyc.invest.enums.ProductStatus;
import com.gomemyc.invest.service.ProductBillService;

import com.gomemyc.util.BeanMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by Administrator on 17/3/16.
 */
@Service
public class ProductBillServiceImpl implements ProductBillService{
	
	@Autowired
	private ProductBillDao productBillDao;

	@Override
	public Integer updateStatusById(ProductStatus status, String id) throws ServiceException {
		if(status==null){
			throw new ServiceException(ExceptionCode.PRODUCT_STATUS_REQUIRED.getIndex(),ExceptionCode.PRODUCT_STATUS_REQUIRED.getErrMsg());
		}
		if(StringUtils.isBlank(id)){
			throw new ServiceException(ExceptionCode.PRODUCT_ID_REQUIRED.getIndex(),ExceptionCode.PRODUCT_ID_REQUIRED.getErrMsg());
		}
		return productBillDao.updateProductStatus(status, id);
	}

	/**
	 * 保存票据产品
	 *
	 * @param productBillDTO
	 * @return
	 * @throws ServiceException
	 */
	@Override
	public Boolean saveBillProduct(ProductBillDTO productBillDTO) throws ServiceException {

		try{
			if(productBillDTO==null){
				throw new ServiceException(ExceptionCode.PRODCT_NOT_REQUIRED.getIndex(),
						ExceptionCode.PRODCT_NOT_REQUIRED.getErrMsg());
			}
			if(1 != productBillDao.save(BeanMapper.map(productBillDTO, ProductBill.class))){
				throw new ServiceException(ExceptionCode.PRODUCT_SAVE_ERROR.getIndex(),
						ExceptionCode.PRODUCT_SAVE_ERROR.getErrMsg());
			}
		} catch (Exception ex){
			throw new ServiceException(ExceptionCode.EXCEPTION.getIndex(),
					ExceptionCode.EXCEPTION.getErrMsg(),
					ex);

		}
		return true;
	}

	@Override
	public List<String> findWaitFailRegular(Integer status, Integer productSwitch) throws ServiceException {

		return productBillDao.findWaitFailRegular(status, productSwitch);
	}

}
