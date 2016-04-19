package com.jiuyi.qujiuyi.dao.register;

import java.util.List;

import com.jiuyi.qujiuyi.dto.register.PatientRegisterDto;

/**
 * @description 患者挂号dao层接口
 * @author zhb
 * @createTime 2015年8月13日
 */
public interface PatientRegisterDao {
	/**
	 * @description 创建挂号记录
	 * @param patientRegisterDto
	 * @throws Exception
	 */
	public void createRegister(PatientRegisterDto patientRegisterDto) throws Exception;

	/**
	 * @description 获取患者挂号
	 * @param patientRegisterDto
	 * @return
	 * @throws Exception
	 */
	public List<PatientRegisterDto> getPatientRegisterList(PatientRegisterDto patientRegisterDto) throws Exception;

	/**
	 * @description 删除挂号
	 * @param patientRegisterDto
	 * @throws Exception
	 */
	public void delRegister(PatientRegisterDto patientRegisterDto) throws Exception;

	/**
	 * @description 取消挂号
	 * @param patientRegisterDto
	 * @throws Exception
	 */
	public void cancelRegister(PatientRegisterDto patientRegisterDto) throws Exception;

	/**
	 * @description 查询挂号详情
	 * @param patientRegisterDto
	 * @throws Exception
	 */
	public PatientRegisterDto queryRegisterDetail(PatientRegisterDto patientRegisterDto) throws Exception;

	/**
	 * 
	 * @number
	 * @description 更改已过时的就诊计划状态
	 * 
	 * @Date 2015年11月17日
	 */
	public void updateRegisterStatus(PatientRegisterDto patientRegisterDto) throws Exception;

	/**
	 * 
	 * @number			@description	修改患者挂号
	 * 
	 * @param patientRegisterDto
	 * @throws Exception
	 *
	 * @Date 2016年1月23日
	 */
	public void updatePatientRegister(PatientRegisterDto patientRegisterDto) throws Exception;

	/**
	 * 
	 * @number			@description	 取号
	 * 
	 * @param patientRegisterDto
	 * @throws Exception
	 *
	 * @Date 2016年1月26日
	 */
	public void fetchNumber(PatientRegisterDto patientRegisterDto) throws Exception;

	/**
	 * 
	 * @number			@description	查询患者挂号详情
	 * 
	 * @param patientRegisterDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2016年1月23日
	 */
	public PatientRegisterDto queryRegisterDetailPlus(PatientRegisterDto patientRegisterDto) throws Exception;

	/**
	 * 
	 * @number			@description	查询患者挂号
	 * 	
	 * @param patientRegisterDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2016年1月23日
	 */
	public List<PatientRegisterDto> getPatientRegisterListPlus(PatientRegisterDto patientRegisterDto) throws Exception;

	/**
	 * 
	 * @number			@description	挂号失败，修改挂号计划为挂号失败，并设置为隐藏状态，该Dao用于用户付款挂号时，付款成功，但回调时挂号失败时调用，该条挂号记录在数据相当于脏数据 
	 * 
	 * @param patientRegisterDto
	 * @throws Exception
	 *
	 * @Date 2016年1月31日
	 */
	public void updatePatientRegisterFail(PatientRegisterDto patientRegisterDto) throws Exception;

}