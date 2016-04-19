package com.jiuyi.qujiuyi.dao.collect;

import java.util.List;

import com.jiuyi.qujiuyi.dto.collect.PatientCollectDto;


/**
 * @description 患者收藏dao接口
 * @author zhb
 * @createTime 2015年4月14日
 */
public interface PatientCollectDao {
    /**
     * @description 收藏医生
     * @param patientCollectDto
     * @throws Exception
     */
    public void collectDoctor(PatientCollectDto patientCollectDto) throws Exception;

    /**
     * @description 删除收藏医生
     * @param patientCollectDto
     * @throws Exception
     */
    public void deleteCollectDoctor(PatientCollectDto patientCollectDto) throws Exception;

    /**
     * @description 根据患者id和医生id查询收藏
     * @param patientCollectDto
     * @throws Exception
     */
    public List<PatientCollectDto> queryCollectByPatientIdAndDoctorId(PatientCollectDto patientCollectDto) throws Exception;

    /**
     * @description 查询患者收藏列表
     * @param patientCollectDto
     * @throws Exception
     */
    public List<PatientCollectDto> queryCollectDoctorList(PatientCollectDto patientCollectDto) throws Exception;

	/**
	 * 
	 * @number			@description	收藏医院
	 * 
	 * @param patientCollectDto
	 * @throws Exception
	 *
	 * @Date 2016年3月9日
	 */
	public void collectHospital(PatientCollectDto patientCollectDto) throws Exception;

	/**
	 * 
	 * @number			@description	删除收藏医院
	 * 
	 * @param patientCollectDto
	 * @throws Exception
	 *
	 * @Date 2016年3月9日
	 */
	public void deleteColectHospital(PatientCollectDto patientCollectDto) throws Exception;

	/**
	 * 
	 * @number			@description	获得收藏医院列表
	 * 
	 * @param patientCollectDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2016年3月9日
	 */
	public List<PatientCollectDto> queryCollectHospitalList(PatientCollectDto patientCollectDto) throws Exception;
}