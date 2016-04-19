package com.jiuyi.qujiuyi.dao.detail;

import java.util.List;

import com.jiuyi.qujiuyi.dto.detail.PatientAccountDetailDto;

/**
 * @description 患者收支明细dao层接口
 * @author zhb
 * @createTime 2015年5月26日
 */
public interface PatientAccountDetailDao {
    /**
     * @description 获取用户收支明细
     * @param patientAccountDetailDto
     * @return
     * @throws Exception
     */
    public List<PatientAccountDetailDto> queryPatientAccountDetail(PatientAccountDetailDto patientAccountDetailDto) throws Exception;

    /**
     * @description 保存用户收支明细
     * @param patientAccountDetailDto
     * @throws Exception
     */
    public void savePatientAccountDetail(PatientAccountDetailDto patientAccountDetailDto) throws Exception;
}