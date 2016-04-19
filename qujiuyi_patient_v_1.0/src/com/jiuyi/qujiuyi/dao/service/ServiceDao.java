package com.jiuyi.qujiuyi.dao.service;

import java.util.List;

import com.jiuyi.qujiuyi.dto.service.ServiceDto;

/**
 * @description 服务dao层接口
 * @author zhb
 * @createTime 2015年5月9日
 */
public interface ServiceDao {
    /**
     * @description 通过医生id获取图文咨询服务
     * @param serviceDto
     * @return
     * @throws Exception
     */
    public List<ServiceDto> queryConsultServiceByDoctorId(ServiceDto serviceDto) throws Exception;

    /**
     * @description 通过医生id获取私人医生服务
     * @param serviceDto
     * @return
     * @throws Exception
     */
    public List<ServiceDto> queryPersonalDoctorServiceByDoctorId(ServiceDto serviceDto) throws Exception;

    /**
     * @description 通过id获取私人医生服务
     * @param serviceDto
     * @return
     * @throws Exception
     */
    public ServiceDto queryPersonalDoctorServiceById(ServiceDto serviceDto) throws Exception;

    /**
     * @description 通过医生id获取医生配药服务
     * @param serviceDto
     * @return
     * @throws Exception
     */
    public List<ServiceDto> queryPrescribeServiceByDoctorId(ServiceDto serviceDto) throws Exception;

    /**
     * @description 通过医生id获取医生预约服务
     * @param serviceDto
     * @return
     * @throws Exception
     */
    public List<ServiceDto> queryAppointmentServiceByDoctorId(ServiceDto serviceDto) throws Exception;
}