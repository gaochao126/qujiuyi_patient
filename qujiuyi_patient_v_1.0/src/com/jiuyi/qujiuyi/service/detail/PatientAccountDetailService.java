package com.jiuyi.qujiuyi.service.detail;

import com.jiuyi.qujiuyi.dto.common.ResponseDto;
import com.jiuyi.qujiuyi.dto.detail.PatientAccountDetailDto;

/**
 * @description 患者收支明细业务层接口
 * @author zhb
 * @createTime 2015年5月26日
 */
public interface PatientAccountDetailService {
    /**
     * @description 获取用户收支明细
     * @param patientAccountDetailDto
     * @return
     * @throws Exception
     */
    public ResponseDto queryPatientAccountDetail(PatientAccountDetailDto patientAccountDetailDto) throws Exception;
}