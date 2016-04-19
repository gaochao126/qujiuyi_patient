package com.jiuyi.qujiuyi.dao.address;

import java.util.List;

import com.jiuyi.qujiuyi.dto.address.PatientAddressDto;

/**
 * @description 患者地址dao层接口
 * @author zhb
 * @createTime 2015年9月2日
 */
public interface PatientAddressDao {

    /**
     * @description 新增患者地址
     * @param patientAddressDto
     * @throws Exception
     */
    public void createPatientAddr(PatientAddressDto patientAddressDto) throws Exception;

    /**
     * @description 删除患者地址
     * @param patientAddressDto
     * @throws Exception
     */
    public void deletePatientAddr(PatientAddressDto patientAddressDto) throws Exception;

    /**
     * @description 更新患者地址
     * @param patientAddressDto
     * @throws Exception
     */
    public void updatePatientAddr(PatientAddressDto patientAddressDto) throws Exception;

    /**
     * @description 查询患者地址列表
     * @param patientAddressDto
     * @return
     * @throws Exception
     */
    public List<PatientAddressDto> queryPatientAddrList(PatientAddressDto patientAddressDto) throws Exception;

    /**
     * @description 根据地址id查询患者地址详情
     * @param patientAddressDto
     * @return
     * @throws Exception
     */
    public List<PatientAddressDto> queryPatientAddr(PatientAddressDto patientAddressDto) throws Exception;

    /**
     * @description 设置患者默认地址
     * @param patientAddressDto
     * @return
     * @throws Exception
     */
    public void setPatientDefaultAddr(PatientAddressDto patientAddressDto) throws Exception;

    /**
     * @description 根据患者id更新所有默认标识
     * @param patientAddressDto
     * @return
     * @throws Exception
     */
    public void updateDefaultValByPatientId(PatientAddressDto patientAddressDto) throws Exception;
}