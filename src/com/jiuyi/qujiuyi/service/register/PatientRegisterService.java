package com.jiuyi.qujiuyi.service.register;

import com.jiuyi.qujiuyi.dto.common.ResponseDto;
import com.jiuyi.qujiuyi.dto.register.PatientRegisterDto;

/**
 * @description 患者挂号业务层接口
 * @author zhb
 * @createTime 2015年8月13日
 */
public interface PatientRegisterService {
	/**
	 * @description 创建挂号记录
	 * @param patientRegisterDto
	 * @return
	 * @throws Exception
	 */
	public ResponseDto createRegister(PatientRegisterDto patientRegisterDto) throws Exception;

	/**
	 * @description 获取患者挂号
	 * @param patientRegisterDto
	 * @return
	 * @throws Exception
	 */
	public ResponseDto getPatientRegisterList(PatientRegisterDto patientRegisterDto) throws Exception;

	/**
	 * @description 删除挂号
	 * @param patientRegisterDto
	 * @return
	 * @throws Exception
	 */
	public ResponseDto delRegister(PatientRegisterDto patientRegisterDto) throws Exception;

	/**
	 * @description 删除挂号
	 * @param patientRegisterDto
	 * @return
	 * @throws Exception
	 */
	public ResponseDto cancelRegister(PatientRegisterDto patientRegisterDto) throws Exception;

	/**
	 * 
	 * @number @description 处理患者过期的挂号处理
	 * 
	 * @throws Exception
	 *
	 * @Date 2015年12月16日
	 */
	public void handleExpiredRegister() throws Exception;

	/**
	 * 
	 * @number			@description	取消挂号（院方读取数据）
	 * 
	 * @param patientRegisterDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2016年1月23日
	 */
	public ResponseDto cancelRegisterPlus(PatientRegisterDto patientRegisterDto) throws Exception;

	/**
	 * 	停诊取消挂号
	 * @param patientRegisterDto
	 * @return
	 * @throws Exception
	 */
	public ResponseDto stopRegister(PatientRegisterDto patientRegisterDto)throws Exception;
	
	/**
	 * @description 获取患者挂号 （院方读取数据）
	 * @param patientRegisterDto
	 * @return
	 * @throws Exception
	 */
	
	public ResponseDto getPatientRegisterListPlus(PatientRegisterDto patientRegisterDto) throws Exception;

	/**
	 * 
	 * @number			@description	预约取号
	 * 
	 * @param patientRegisterDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2016年1月23日
	 */
	public ResponseDto fetchNumber(PatientRegisterDto patientRegisterDto) throws Exception;

	/**
	 * 
	 * @number			@description	排队信息查询
	 * 
	 * @param patientRegisterDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2016年1月23日
	 */
	public ResponseDto getLineInfo(PatientRegisterDto patientRegisterDto) throws Exception;

	/**
	 * 
	 * @number			@description 判断是否已挂过指定号源
	 * 
	 * @param patientRegisterDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2016年3月8日
	 */
	public ResponseDto checkIsRegister(PatientRegisterDto patientRegisterDto) throws Exception;

	/**
	 * 
	 * @number			@description	根据就诊号查询患者挂号计划
	 * 
	 * @param patientRegisterDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2016年3月11日
	 */
	public ResponseDto queryRegisterPlanDetail(PatientRegisterDto patientRegisterDto) throws Exception;
}