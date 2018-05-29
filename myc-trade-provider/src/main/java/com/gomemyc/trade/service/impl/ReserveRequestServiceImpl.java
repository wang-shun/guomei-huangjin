package com.gomemyc.trade.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.gomemyc.common.exception.ServiceException;
import com.gomemyc.trade.dao.ReserveRequestDao;
import com.gomemyc.trade.dto.ReserveRequestDTO;
import com.gomemyc.trade.entity.ReserveRequest;
import com.gomemyc.trade.service.ReserveRequestService;
import com.gomemyc.util.BeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;
import java.util.List;

/**
 * 预约投资接口实现类。
 * 
 * @author 何健
 * @creaTime 2017年3月10日
 */
@Service
public class ReserveRequestServiceImpl implements ReserveRequestService {

	/** 日志 */
	Logger logger = LoggerFactory.getLogger(this.getClass());

    /** 预约申请单DAO  */
    @Autowired
    private ReserveRequestDao reserveRequestDao;


    /**
     * 根据预约单状态，查询预约单集合。
     *
     * @param status 预约单状态值
     * @return DTOList
     * @throws ServiceException
     */
    @Override
    public List<ReserveRequestDTO> findListByStatus(Integer status) throws ServiceException {
        List <ReserveRequest> entityList = reserveRequestDao.findListByStatus(status);
        List<ReserveRequestDTO> dtoList = new ArrayList<ReserveRequestDTO>();
        dtoList = BeanMapper.mapList(entityList, ReserveRequestDTO.class);
        return dtoList;
    }

    /**
     * 根据申请单id 更新申请单状态。
     *
     * @param id 申请单id
     * @param status 状态
     * @return
     */
    @Override
    public Integer updateStatusById(String id, Integer status) throws ServiceException {
        ReserveRequest reserveRequest = new ReserveRequest();
        reserveRequest.setId(id);
        reserveRequest.setStatus(status);
        return reserveRequestDao.updateStatusById(reserveRequest);
    }

    /**
     * 根据预约单id，查询预约单。
     *
     * @param id 预约单id
     * @return 预约单DTO
     * @throws ServiceException
     */
    @Override
    public ReserveRequestDTO findById(String id) throws ServiceException{
        return BeanMapper.map(reserveRequestDao.findById(id), ReserveRequestDTO.class);
    }


}
