package com.jiuyi.qujiuyi.dao.patient;

import java.util.List;

import com.jiuyi.qujiuyi.dto.patient.PatientDto;

/**
 * @description 患者dao层接口
 * @author zhb
 * @createTime 2015年4月3日
 */
public interface PatientDao {
	/**
	 * @description 根据手机号查询患者
	 * @param patientDto
	 * @return
	 * @throws Exception
	 */
	public PatientDto queryPatientByPhone(PatientDto patientDto) throws Exception;

	/**
	 * @description 根据token查询患者
	 * @param patientDto
	 * @return
	 * @throws Exception
	 */
	public PatientDto queryPatientByToken(String token);

	/**
	 * @description 根据id查询患者
	 * @param patientDto
	 * @return
	 * @throws Exception
	 */
	public PatientDto queryPatientById(PatientDto patientDto) throws Exception;

	/**
	 * @description 根据微信号查询患者
	 * @param patientDto
	 * @return
	 * @throws Exception
	 */
	public PatientDto queryPatientByWeixinOpenId(PatientDto patientDto) throws Exception;

	/**
	 * @description 患者注册
	 * @param patientDto
	 * @throws Exception
	 */
	public void register(PatientDto patientDto) throws Exception;

	/**
	 * @description 修改密码
	 * @param patientDto
	 * @throws Exception
	 */
	public void modifyPassword(PatientDto patientDto) throws Exception;

	/**
	 * @description 重置密码
	 * @param patientDto
	 * @throws Exception
	 */
	public void resetPassword(PatientDto patientDto) throws Exception;

	/**
	 * @description 解除手机号绑定
	 * @param patientDto
	 * @throws Exception
	 */
	public void relievePhoneBound(PatientDto patientDto) throws Exception;

	/**
	 * @description 编辑个人信息
	 * @param patientDto
	 * @throws Exception
	 */
	public void editPersonalInfo(PatientDto patientDto) throws Exception;

	/**
	 * @description 绑定微信号
	 * @param patientDto
	 * @throws Exception
	 */
	public void bindWeixin(PatientDto patientDto) throws Exception;

	/**
	 * @description 更新用户余额
	 * @param patientDto
	 * @throws Exception
	 */
	public void updateBalance(PatientDto patientDto) throws Exception;

	/**
	 * @description 根据邀请码获取患者
	 * @param patientDto
	 * @return
	 * @throws Exception
	 */
	public List<PatientDto> queryPatientByInvitationCode(PatientDto patientDto) throws Exception;

	/**
	 * @description 设置提现密码
	 * @param patientDto
	 * @return
	 * @throws Exception
	 */
	public void setWithdrawalPassword(PatientDto patientDto) throws Exception;

	/**
	 * 保存token
	 * 
	 * @param dto
	 */
	public void saveToken(PatientDto dto);

	/**
	 * 移除token
	 * 
	 * @param patientDto
	 */
	public void removeToken(PatientDto patientDto);

	/**
	 * 
	 * @number			@description	解绑微信
	 * 
	 * @param patientDto
	 * @throws Exception
	 *
	 * @Date 2016年1月7日
	 */
	public void removeWinxinBind(PatientDto patientDto) throws Exception;
}