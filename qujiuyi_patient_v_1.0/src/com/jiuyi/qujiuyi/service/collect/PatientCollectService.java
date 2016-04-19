package com.jiuyi.qujiuyi.service.collect;

import com.jiuyi.qujiuyi.dto.collect.PatientCollectDto;
import com.jiuyi.qujiuyi.dto.common.ResponseDto;

public interface PatientCollectService {

    /**
     * @description 收藏医生
     * @param patientCollectDto
     * @throws Exception
     */
    public ResponseDto collectDoctor(PatientCollectDto patientCollectDto) throws Exception;

    /**
     * @description 删除收藏医生
     * @param patientCollectDto
     * @throws Exception
     */
    public ResponseDto deleteCollectDoctor(PatientCollectDto patientCollectDto) throws Exception;

    /**
	 * @description 查询收藏医生
	 * @param patientCollectDto
	 * @throws Exception
	 */
    public ResponseDto queryCollectDoctorList(PatientCollectDto patientCollectDto) throws Exception;

    /**
     * @description 判断医生是否已被收藏
     * @param patientCollectDto
     * @throws Exception
     */
    public ResponseDto isCollectedDoctor(PatientCollectDto patientCollectDto) throws Exception;

	/**
	 * 
	 * @number			@description	收藏医院
	 * 
	 * @param patientCollectDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2016年3月9日
	 */
	public ResponseDto collectHospital(PatientCollectDto patientCollectDto) throws Exception;

	/**
	 * 
	 * @number			@description	删除收藏医院
	 * 
	 * @param patientCollectDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2016年3月9日
	 */
	public ResponseDto deleteCollectHospital(PatientCollectDto patientCollectDto) throws Exception;

	/**
	 * 
	 * @number			@description	
	 * 
	 * @param patientCollectDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2016年3月9日
	 */
	public ResponseDto queryCollectHospitalList(PatientCollectDto patientCollectDto) throws Exception;

	/**
	 * 
	 * @number			@description	判断医院是否被收藏
	 * 
	 * @param patientCollectDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2016年3月9日
	 */
	public ResponseDto checkHospitalIsCollect(PatientCollectDto patientCollectDto) throws Exception;
}