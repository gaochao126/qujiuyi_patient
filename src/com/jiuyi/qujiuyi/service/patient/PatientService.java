package com.jiuyi.qujiuyi.service.patient;

import com.jiuyi.qujiuyi.dto.common.ResponseDto;
import com.jiuyi.qujiuyi.dto.patient.PatientDto;

/**
 * @description 患者业务接口
 * @author zhb
 * @createTime 2015年4月3日
 */
public interface PatientService {
	/**
	 * @description 患者登录
	 * @param patientDto
	 * @return
	 * @throws Exception
	 */
	public ResponseDto signIn(PatientDto patientDto) throws Exception;

	/**
	 * @description 患者登出
	 * @param patientDto
	 * @throws Exception
	 */
	public ResponseDto signOut(PatientDto patientDto) throws Exception;

	/**
	 * @description 患者注册
	 * @param patientDto
	 * @return
	 * @throws Exception
	 */
	public ResponseDto register(PatientDto patientDto) throws Exception;

	/**
	 * @description 修改密码
	 * @param patientDto
	 * @throws Exception
	 */
	public ResponseDto modifyPassword(PatientDto patientDto) throws Exception;

	/**
	 * @description 重置密码
	 * @param patientDto
	 * @throws Exception
	 */
	public ResponseDto resetPassword(PatientDto patientDto) throws Exception;

	/**
	 * @description 编辑个人信息
	 * @param patientDto
	 * @throws Exception
	 */
	public ResponseDto editPersonalInfo(PatientDto patientDto) throws Exception;

	/**
	 * @description 查询个人信息
	 * @param patientDto
	 * @throws Exception
	 */
	public ResponseDto queryPersonalInfo(PatientDto patientDto) throws Exception;

	/**
	 * @description 判断手机号是否已注册
	 * @param patientDto
	 * @throws Exception
	 */
	public ResponseDto isRegisted(PatientDto patientDto) throws Exception;

	/**
	 * @description 同步登录信息到服务端
	 * @param patientDto
	 * @throws Exception
	 */
	public ResponseDto syncLoginInfoToServer(PatientDto patientDto) throws Exception;

	/**
	 * @description 获取短信验证码
	 * @param patientDto
	 * @throws Exception
	 */
	public ResponseDto getVerifyCode(PatientDto patientDto) throws Exception;

	/**
	 * @description 绑定微信号
	 * @param patientDto
	 * @throws Exception
	 */
	public ResponseDto bindWeixin(PatientDto patientDto) throws Exception;

	/**
	 * @description 设置提现密码
	 * @param patientDto
	 * @return
	 * @throws Exception
	 */
	public ResponseDto setWithdrawalPassword(PatientDto patientDto) throws Exception;

	/**
	 * @description 校验验证码
	 * @param patientDto
	 * @return
	 * @throws Exception
	 */
	public ResponseDto checkVerificationCode(PatientDto patientDto) throws Exception;

	/**
	 * @description 根据token查询患者
	 * @param patientDto
	 * @return
	 * @throws Exception
	 */
	public PatientDto queryPatientByToken(String token);

	/**
	 * 
	 * @number			@description	解绑微信
	 * 
	 * @param patientDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2016年1月7日
	 */
	public ResponseDto removeWeixinBind(PatientDto patientDto) throws Exception;

	/**
	 * 
	 * @number			@description	查询用户
	 * 
	 * @param patientDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2016年1月29日
	 */
	public PatientDto queryPatient(PatientDto patientDto) throws Exception;
}