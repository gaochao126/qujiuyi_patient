package com.jiuyi.qujiuyi.service.address;

import com.jiuyi.qujiuyi.dto.address.PatientAddressDto;
import com.jiuyi.qujiuyi.dto.common.ResponseDto;

/**
 * @description 患者地址业务层接口
 * @author zhb
 * @createTime 2015年9月2日
 */
public interface PatientAddressService {
    /**
     * @description 新增患者地址
     * @param patientAddressDto
     * @return
     * @throws Exception
     */
    public ResponseDto createPatientAddr(PatientAddressDto patientAddressDto) throws Exception;

    /**
     * @description 删除患者地址
     * @param patientAddressDto
     * @return
     * @throws Exception
     */
    public ResponseDto deletePatientAddr(PatientAddressDto patientAddressDto) throws Exception;

    /**
     * @description 更新患者地址
     * @param patientAddressDto
     * @return
     * @throws Exception
     */
    public ResponseDto updatePatientAddr(PatientAddressDto patientAddressDto) throws Exception;

    /**
     * @description 查询患者地址列表
     * @param patientAddressDto
     * @return
     * @throws Exception
     */
    public ResponseDto queryPatientAddrList(PatientAddressDto patientAddressDto) throws Exception;

    /**
     * @description 根据地址id查询患者地址详情
     * @param patientAddressDto
     * @return
     * @throws Exception
     */
    public ResponseDto queryPatientAddr(PatientAddressDto patientAddressDto) throws Exception;
}