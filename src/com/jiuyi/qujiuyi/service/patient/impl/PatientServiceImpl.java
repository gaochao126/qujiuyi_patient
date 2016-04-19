package com.jiuyi.qujiuyi.service.patient.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jiuyi.qujiuyi.common.dict.CacheContainer;
import com.jiuyi.qujiuyi.common.dict.Constants;
import com.jiuyi.qujiuyi.common.util.IDCard;
import com.jiuyi.qujiuyi.common.util.MD5;
import com.jiuyi.qujiuyi.common.util.SysCfg;
import com.jiuyi.qujiuyi.common.util.URLInvoke;
import com.jiuyi.qujiuyi.common.util.Util;
import com.jiuyi.qujiuyi.dao.patient.PatientDao;
import com.jiuyi.qujiuyi.dto.BaseDto;
import com.jiuyi.qujiuyi.dto.common.RequestDto;
import com.jiuyi.qujiuyi.dto.common.ResponseDto;
import com.jiuyi.qujiuyi.dto.common.TokenDto;
import com.jiuyi.qujiuyi.dto.patient.PatientDto;
import com.jiuyi.qujiuyi.service.BusinessException;
import com.jiuyi.qujiuyi.service.patient.PatientService;
import com.qujiuyi.util.commonres.CommonResult;
import com.qujiuyi.util.sms.SmsService;

/**
 * @description 串者业务实现类
 * @author zhb
 * @createTime 2015年4月3日
 */
@Service
public class PatientServiceImpl implements PatientService {
	// private final static Logger logger =
	// Logger.getLogger(PatientServiceImpl.class);
	@Autowired
	private PatientDao patientDao;

	/**
	 * @description 患者登录
	 * @param patientDto
	 * @return
	 * @throws Exception
	 */
	@Override
	public ResponseDto signIn(PatientDto patientDto) throws Exception {
		/** step1:空异常处理. */
		if (patientDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		/** step2:账号不为空校验. */
		if (!Util.isNotEmpty(patientDto.getPhone())) {
			throw new BusinessException("账号不能为空");
		}

		/** step3:密码不为空校验. */
		if (!Util.isNotEmpty(patientDto.getPassword())) {
			throw new BusinessException("密码不能为空");
		}

		/** step4:校验账号是否存在. */
		PatientDto dto = patientDao.queryPatientByPhone(patientDto);
		if (dto == null) {
			throw new BusinessException("用户不存在");
		}

		/** step5:校验码密码是否正确. */
		if (!patientDto.getPassword().equals(dto.getPassword())) {
			throw new BusinessException("账号或密码错误");
		}

		/** step6:存放token信息. */
		String token = MD5.getMD5Code(Util.getUniqueSn());
		dto.setDeviceType(patientDto.getDeviceType());
		dto.setToken(token);
		TokenDto tokenDto = new TokenDto();
		tokenDto.setToken(token);
		tokenDto.setPatient(dto);
		tokenDto.setUpdateTime(System.currentTimeMillis());
		dto.setHaveWithdrawalPassword(Util.isNotEmpty(dto.getWithdrawalPassword()));
		dto.setWithdrawalPassword(null);
		CacheContainer.saveToken(token, tokenDto);

		/* 保存token到数据库 */
		String md5OfToken = MD5.getMD5Code(token);
		dto.setToken(md5OfToken);// 复用一下token字段
		patientDao.saveToken(dto);
		dto.setToken(token);

		syncLoginInfoToServer(dto);

		/** step8:返回结果. */
		ResponseDto responseDto = new ResponseDto();
		responseDto.setResultDesc("登录成功");
		responseDto.setDetail(dto);
		return responseDto;
	}

	/**
	 * @description 患者登出
	 * @param patientDto
	 * @return
	 * @throws Exception
	 */
	@Override
	public ResponseDto signOut(PatientDto patientDto) throws Exception {
		/** step1:空异常处理. */
		if (patientDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		/** step2:登出. */
		ResponseDto responseDto = new ResponseDto();
		if (Util.isNotEmpty(patientDto.getToken())) {
			TokenDto token = CacheContainer.getToken(patientDto.getToken());
			if (token == null) {
				responseDto.setResultDesc("登出成功");
				return responseDto;
			}
			if (token.getPatient() == null) {
				CacheContainer.removeToken(patientDto.getToken());
				responseDto.setResultDesc("登出成功");
				return responseDto;
			}
			if (patientDto.getId() != null && patientDto.getId().equals(token.getPatient().getId())) {
				CacheContainer.removeToken(patientDto.getToken());
				patientDao.removeToken(patientDto);
				responseDto.setResultDesc("登出成功");
				return responseDto;
			}
		}
		responseDto.setResultDesc("登出成功");
		return responseDto;
	}

	/**
	 * @description 患者注册
	 * @param patientDto
	 * @return
	 * @throws Exception
	 */
	@Override
	public ResponseDto register(PatientDto patientDto) throws Exception {
		/** step1:空异常处理. */
		if (patientDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		/** step2:账号不为空校验. */
		if (!Util.isNotEmpty(patientDto.getPhone())) {
			throw new BusinessException("手机号不能为空");
		}

		/** step3:校验姓名. */
		if (!Util.isNotEmpty(patientDto.getName())) {
			throw new BusinessException("用户姓氏不能为空");
		}

		/** step4:校验姓氏长度. */
		if (patientDto.getName().length() > 2) {
			throw new BusinessException("用户姓氏长度不能超过2个字符");
		}

		/** step5:校验性别. */
		if (patientDto.getGender() == null) {
			throw new BusinessException("用户性别不能为空");
		}

		/** step6:校验性别合法性. */
		if (patientDto.getGender() != 1 && patientDto.getGender() != 2) {
			throw new BusinessException("性别不合法");
		}

		/** step7:密码不为空校验. */
		if (!Util.isNotEmpty(patientDto.getPassword())) {
			throw new BusinessException("密码不能为空");
		}

		/** step8:校验微信号是否已绑定. */
		if (patientDto.getDeviceType() == 6 && Util.isNotEmpty(patientDto.getWeixinOpenId())) {
			if (patientDao.queryPatientByWeixinOpenId(patientDto) != null) {
				throw new BusinessException("微信号已被绑定");
			}
		}

		/** step9:验证码校验. */
		if (!Util.isNotEmpty(patientDto.getAccessToken())) {
			throw new BusinessException("无效accessToken");
		}

		BaseDto baseDto = CacheContainer.getAccessToken(patientDto.getAccessToken());
		String value = "";
		if (baseDto != null) {
			value = baseDto.getCachPhone();
		}

		if (!patientDto.getPhone().equals(value)) {
			throw new BusinessException("手机未通过验证");
		}

		CacheContainer.removeAccessToken(patientDto.getAccessToken());

		/** step10:注册. */
		patientDto.setInvitationCode(Util.getInvitationCode());
		patientDto.setNickname(patientDto.getName());
		PatientDto dto = patientDao.queryPatientByPhone(patientDto);
		if (dto != null) {
			throw new BusinessException("手机号码已注册");
		} else {
			// 注册账号
			Date time = new Date();
			patientDto.setRegisterTime(time);
			patientDto.setUpdateTime(time);
			patientDao.register(patientDto);
		}

		/** step11:环信注册. */
		RequestDto requestDto = new RequestDto();
		requestDto.setCmd("huanxinRegister");
		Map<String, Object> params = new HashMap<String, Object>();
		requestDto.setParams(params);
		params.put("userName", MD5.getMD5Code(patientDto.getId().toString()));
		params.put("userPassword", MD5.getMD5Code(patientDto.getPassword()));
		URLInvoke.post(SysCfg.getString("qujiuyi.chatUrl"), Constants.gson.toJson(requestDto));

		/** step12:存放token信息. */
		dto = patientDao.queryPatientByPhone(patientDto);
		String token = MD5.getMD5Code(Util.getUniqueSn());
		dto.setToken(token);
		dto.setPassword(null);
		TokenDto tokenDto = new TokenDto();
		tokenDto.setToken(token);
		tokenDto.setPatient(dto);
		tokenDto.setUpdateTime(System.currentTimeMillis());
		dto.setHaveWithdrawalPassword(Util.isNotEmpty(dto.getWithdrawalPassword()));
		dto.setWithdrawalPassword(null);
		CacheContainer.saveToken(token, tokenDto);

		/** step13:返回结果. */
		Map<String, String> map = new HashMap<String, String>();
		map.put("token", dto.getToken());
		ResponseDto responseDto = new ResponseDto();
		responseDto.setResultDesc("注册成功");
		responseDto.setDetail(map);
		return responseDto;
	}

	/**
	 * @description 修改密码
	 * @param patientDto
	 * @throws Exception
	 */
	@Override
	public ResponseDto modifyPassword(PatientDto patientDto) throws Exception {
		/** step1:空异常处理. */
		if (patientDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		/** step2:账号空校验. */
		if (patientDto.getId() == null) {
			throw new BusinessException("id不能为空");
		}

		/** step3:旧密码空校验. */
		if (!Util.isNotEmpty(patientDto.getOldPassword())) {
			throw new BusinessException("旧密码不能为空");
		}

		/** step4:新密码空校验. */
		if (!Util.isNotEmpty(patientDto.getNewPassword())) {
			throw new BusinessException("新密码不能为空");
		}

		/** step5:校验是否为本人操作. */
		TokenDto token = CacheContainer.getToken(patientDto.getToken());
		if (patientDto.getId() == null || (token != null && token.getPatient() != null && !patientDto.getId().equals(token.getPatient().getId()))) {
			throw new BusinessException("非本人操作");
		}

		/** step6:校验账号是否正确. */
		PatientDto dto = patientDao.queryPatientById(patientDto);
		if (dto == null) {
			throw new BusinessException("账号不存在");
		}

		/** step7:旧密码正确性校验. */
		if (!patientDto.getOldPassword().equals(dto.getPassword())) {
			throw new BusinessException("密码不对");
		}

		/** step8:更新数据库. */
		patientDao.modifyPassword(patientDto);

		/** step9:环信修改密码. */

		/** step11:环信注册. */
		RequestDto requestDto = new RequestDto();
		requestDto.setCmd("huanxinModifyPassword");
		Map<String, Object> params = new HashMap<String, Object>();
		requestDto.setParams(params);
		params.put("userName", MD5.getMD5Code(patientDto.getId().toString()));
		params.put("userPassword", MD5.getMD5Code(patientDto.getNewPassword()));
		URLInvoke.post(SysCfg.getString("qujiuyi.chatUrl"), Constants.gson.toJson(requestDto));

		/** step10:返回结果. */
		ResponseDto responseDto = new ResponseDto();
		responseDto.setResultDesc("修改密码成功");
		return responseDto;
	}

	/**
	 * @description 重置密码
	 * @param patientDto
	 * @throws Exception
	 */
	@Override
	public ResponseDto resetPassword(PatientDto patientDto) throws Exception {
		/** step1:空异常处理. */
		if (patientDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		/** step2:账号空校验. */
		if (!Util.isNotEmpty(patientDto.getPhone())) {
			throw new BusinessException("手机号不能为空");
		}

		/** step3:新密码空校验. */
		if (!Util.isNotEmpty(patientDto.getNewPassword())) {
			throw new BusinessException("新密码不能为空");
		}

		/** step4:验证码校验. */
		if (!Util.isNotEmpty(patientDto.getAccessToken())) {
			throw new BusinessException("无效accessToken");
		}

		BaseDto baseDto = CacheContainer.getAccessToken(patientDto.getAccessToken());
		String value = "";
		if (baseDto != null) {
			value = baseDto.getCachPhone();
		}

		if (!patientDto.getPhone().equals(value)) {
			throw new BusinessException("手机未通过验证");
		}

		CacheContainer.removeAccessToken(patientDto.getAccessToken());

		/** step5:校验账号是否存在. */
		PatientDto dto = patientDao.queryPatientByPhone(patientDto);
		if (dto == null) {
			throw new BusinessException("账号不存在");
		}

		/** step6:更新数据库. */
		patientDao.resetPassword(patientDto);

		/** step7:环信修改密码. */
		RequestDto requestDto = new RequestDto();
		requestDto.setCmd("huanxinModifyPassword");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userName", MD5.getMD5Code(dto.getId().toString()));
		params.put("userPassword", MD5.getMD5Code(patientDto.getNewPassword()));
		requestDto.setParams(params);
		URLInvoke.post(SysCfg.getString("qujiuyi.chatUrl"), Constants.gson.toJson(requestDto));
		/** step8:返回结果. */
		ResponseDto responseDto = new ResponseDto();
		responseDto.setResultDesc("重置密码成功");
		return responseDto;
	}

	/**
	 * @description 编辑个人信息
	 * @param patientDto
	 * @throws Exception
	 */
	@Override
	public ResponseDto editPersonalInfo(PatientDto patientDto) throws Exception {
		/** step1:空异常处理. */
		if (patientDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		/** step2:获取id. */
		TokenDto token = CacheContainer.getToken(patientDto.getToken());
		PatientDto patient = token != null ? token.getPatient() : new PatientDto();
		patientDto.setId(patient.getId());

		if (Util.isNotEmpty(patientDto.getUid())) {
			patientDto.setGender(IDCard.getGenderByCard(patientDto.getUid()));
			patientDto.setBirthday(IDCard.getBirthdayByCard(patientDto.getUid()));
		}

		/** step3:修改个人信息. */
		patientDto.setUpdateTime(new Date());
		patientDao.editPersonalInfo(patientDto);

		/** step4:更新token. */
		TokenDto tokenDto = new TokenDto();
		PatientDto dto = patientDao.queryPatientById(patientDto);
		dto.setToken(patientDto.getToken());
		dto.setPassword(null);
		dto.setHaveWithdrawalPassword(Util.isNotEmpty(dto.getWithdrawalPassword()));
		dto.setWithdrawalPassword(null);
		tokenDto.setPatient(dto);
		tokenDto.setToken(patientDto.getToken());
		tokenDto.setUpdateTime(System.currentTimeMillis());
		CacheContainer.saveToken(patientDto.getToken(), tokenDto);

		/** step5:返回结果. */
		ResponseDto responseDto = new ResponseDto();
		responseDto.setResultDesc("修改个人信息成功");
		return responseDto;
	}

	/**
	 * @description 查询个人信息
	 * @param patientDto
	 * @throws Exception
	 */
	@Override
	public ResponseDto queryPersonalInfo(PatientDto patientDto) throws Exception {
		/** step1:空异常处理. */
		if (patientDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		/** step2:查询数据. */
		PatientDto dto = null;
		if (patientDto.getDeviceType() == 6) {// 微信用户
			if (Util.isNotEmpty(patientDto.getToken())) {
				TokenDto token = CacheContainer.getToken(patientDto.getToken());
				dto = token != null ? token.getPatient() : null;
			}
			if (dto == null) {
				dto = patientDao.queryPatientByWeixinOpenId(patientDto);
				if (dto == null) {
					ResponseDto responseDto = new ResponseDto();
					responseDto.setResultCode(3);
					responseDto.setResultDesc("您的微信号还末绑定手机号码");
					responseDto.setDetail(dto);
					return responseDto;
				}
				String token = MD5.getMD5Code(Util.getUniqueSn());
				dto.setToken(token);
				dto.setHaveWithdrawalPassword(Util.isNotEmpty(dto.getWithdrawalPassword()));
				dto.setWithdrawalPassword(null);
				TokenDto tokenDto = new TokenDto();
				tokenDto.setToken(token);
				tokenDto.setPatient(dto);
				tokenDto.setUpdateTime(System.currentTimeMillis());
				CacheContainer.saveToken(token, tokenDto);

				/* 保存token到数据库 */
				String md5OfToken = MD5.getMD5Code(token);
				dto.setToken(md5OfToken);// 复用一下token字段
				patientDao.saveToken(dto);
				dto.setToken(token);

				// 同步聊天服务器.
				RequestDto requestDto = new RequestDto();
				requestDto.setCmd("syncUserInfoToServer");
				Map<String, Object> params = new HashMap<String, Object>();
				requestDto.setParams(params);
				params.put("channelId", dto.getWeixinOpenId());
				params.put("userId", dto.getId());
				params.put("token", dto.getToken());
				params.put("deviceType", 6);
				params.put("userType", 2);
				params.put("online", 0);
				URLInvoke.post(SysCfg.getString("qujiuyi.chatUrl"), Constants.gson.toJson(requestDto));
			}
		} else {
			TokenDto token = CacheContainer.getToken(patientDto.getToken());
			dto = token != null ? token.getPatient() : null;
		}

		/** step3:返回结果. */
		ResponseDto responseDto = new ResponseDto();
		responseDto.setResultDesc("查询个人信息成功");
		responseDto.setDetail(dto);
		return responseDto;
	}

	/**
	 * @description 判断手机号是否已注册
	 * @param patientDto
	 * @throws Exception
	 */
	@Override
	public ResponseDto isRegisted(PatientDto patientDto) throws Exception {
		/** 空异常处理. */
		if (patientDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		PatientDto dto = patientDao.queryPatientByPhone(patientDto);

		ResponseDto responseDto = new ResponseDto();
		Map<String, Object> detail = new HashMap<String, Object>();
		responseDto.setDetail(detail);
		if (dto != null) {
			detail.put("isRegisted", true);
		} else {
			detail.put("isRegisted", false);
		}

		return responseDto;
	}

	/**
	 * @description 同步登录信息到服务端
	 * @param patientDto
	 * @throws Exception
	 */
	@Override
	public ResponseDto syncLoginInfoToServer(PatientDto patientDto) throws Exception {
		/** step1:空异常处理. */
		if (patientDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		/** step2:校验患者id. */
		if (patientDto.getId() == null) {
			throw new BusinessException("患者id不能为空");
		}

		/** step3:校验是否为本人操作. */
		TokenDto token = CacheContainer.getToken(patientDto.getToken());
		if (patientDto.getId() == null || (token != null && token.getPatient() != null && !patientDto.getId().equals(token.getPatient().getId()))) {
			throw new BusinessException("非本人操作");
		}

		/** step4:校验通道id. */
		/*
		 * if (!Util.isNotEmpty(patientDto.getChannelId())) { throw new
		 * BusinessException("通道ID不能为空"); }
		 */

		/** step5:校验设备类型. */
		if (patientDto.getDeviceType() == null) {
			throw new BusinessException("设备类型未知");
		}

		/** step6:同步用户信息到聊天服务器. */
		RequestDto requestDto = new RequestDto();
		requestDto.setCmd("syncUserInfoToServer");
		Map<String, Object> params = new HashMap<String, Object>();
		requestDto.setParams(params);
		// params.put("channelId", patientDto.getChannelId());
		params.put("userId", patientDto.getId());
		params.put("token", patientDto.getToken());
		params.put("deviceType", patientDto.getDeviceType());
		params.put("userType", 2);
		params.put("online", 0);
		String result = URLInvoke.post(SysCfg.getString("qujiuyi.chatUrl"), Constants.gson.toJson(requestDto));

		/** step7:校验同步结果. */
		if (!Util.isNotEmpty(result)) {
			throw new BusinessException("同步失败");
		}
		/** step8:返回结果. */
		ResponseDto responseDto = Constants.gson.fromJson(result, ResponseDto.class);
		return responseDto;
	}

	/**
	 * @description 获取短信验证码
	 * @param patientDto
	 * @throws Exception
	 */
	@Override
	public ResponseDto getVerifyCode(PatientDto patientDto) throws Exception {
		/** step1:空异常处理. */
		if (patientDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		/** step2:手机号不为空校验. */
		if (!Util.isNotEmpty(patientDto.getPhone())) {
			throw new BusinessException("手机号必须填写");
		}

		/** step3:发送验证码. */
		CommonResult sms = SmsService.instance().sendCode(patientDto.getPhone());

		/** step4:返回结果. */
		ResponseDto responseDto = new ResponseDto();

		if (sms.isSuccess()) {
			responseDto.setResultDesc("验证码发送成功");
		} else {
			responseDto.setResultDesc(sms.getResultDesc());
		}

		return responseDto;
	}

	/**
	 * @description 绑定微信号
	 * @param patientDto
	 * @throws Exception
	 */
	@Override
	public ResponseDto bindWeixin(PatientDto patientDto) throws Exception {
		/** step1:空异常处理. */
		if (patientDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		/** step2:账号不为空校验. */
		if (!Util.isNotEmpty(patientDto.getPhone())) {
			throw new BusinessException("账号不能为空");
		}

		/** step3:密码不为空校验. */
		if (!Util.isNotEmpty(patientDto.getPassword())) {
			throw new BusinessException("密码不能为空");
		}

		/** step4:微信号不为空校验. */
		if (!Util.isNotEmpty(patientDto.getWeixinOpenId())) {
			throw new BusinessException("微信号不能为空");
		}

		/** step5:校验账号是否存在. */
		PatientDto dto = patientDao.queryPatientByPhone(patientDto);
		if (dto == null) {
			throw new BusinessException("账号或密码错误");
		}

		/** step6:校验码密码是否正确. */
		if (!patientDto.getPassword().equals(dto.getPassword())) {
			throw new BusinessException("账号或密码错误");
		}

		/** step7:校验是否已绑定微信号. */
		if (Util.isNotEmpty(dto.getWeixinOpenId())) {
			throw new BusinessException("该账号已绑定微信号");
		}

		/** step8:绑定微信. */
		dto.setWeixinOpenId(patientDto.getWeixinOpenId());
		patientDao.bindWeixin(dto);

		/** step9:返回结果. */
		ResponseDto responseDto = new ResponseDto();
		responseDto.setResultDesc("绑定成功");
		return responseDto;
	}

	/**
	 * @description 设置提现密码
	 * @param patientDto
	 * @return
	 * @throws Exception
	 */
	@Override
	public ResponseDto setWithdrawalPassword(PatientDto patientDto) throws Exception {
		/** step1:空异常处理. */
		if (patientDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		/** step2:获取用户. */
		TokenDto token = CacheContainer.getToken(patientDto.getToken());
		PatientDto patient = token != null ? token.getPatient() : new PatientDto();

		/** step3:验证码校验. */
		CommonResult sms = SmsService.instance().checkCode(patientDto.getPhone(), patientDto.getVerificationCode());
		if (!sms.isSuccess()) {
			throw new BusinessException("验证码校验失败");
		}

		/** step4:设置提现密码. */
		patientDto.setId(patient.getId());
		patientDao.setWithdrawalPassword(patientDto);
		patient.setHaveWithdrawalPassword(true);

		/** step5:返回结果. */
		ResponseDto responseDto = new ResponseDto();
		responseDto.setResultDesc("设置提现密码成功");
		return responseDto;
	}

	/**
	 * @description 校验验证码
	 * @param patientDto
	 * @return
	 * @throws Exception
	 */
	@Override
	public ResponseDto checkVerificationCode(PatientDto patientDto) throws Exception {
		if (patientDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}
		ResponseDto responseDto = new ResponseDto();
		CommonResult sms = SmsService.instance().checkCode(patientDto.getPhone(), patientDto.getVerificationCode());
		if (sms.isSuccess()) {
			responseDto.setResultDesc("校验成功");
			String accessToken = MD5.getMD5Code(Util.getUniqueSn());
			BaseDto baseDto = new BaseDto();
			baseDto.setAccessToken(accessToken);
			baseDto.setCurrentTimes(System.currentTimeMillis());
			baseDto.setCachPhone(patientDto.getPhone());
			CacheContainer.putAccessToken(accessToken, baseDto);// 存储访问accessToken
			Map<String, Object> dataMap = new HashMap<String, Object>();
			dataMap.put("accessToken", accessToken);
			responseDto.setDetail(dataMap);
		} else {
			responseDto.setResultCode(1);
			responseDto.setResultDesc("校验失败");
		}

		return responseDto;
	}

	/**
	 * 根据token到数据库查询患者信息
	 */
	@Override
	public PatientDto queryPatientByToken(String token) {
		return patientDao.queryPatientByToken(token);
	}

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
	@Override
	public ResponseDto removeWeixinBind(PatientDto patientDto) throws Exception {
		if (patientDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		/** step2:获取用户. */
		TokenDto token = CacheContainer.getToken(patientDto.getToken());
		PatientDto patient = token != null ? token.getPatient() : new PatientDto();
		patientDto.setId(patient.getId());

		patientDao.removeWinxinBind(patientDto);

		// 通知微信服务器
		RequestDto requestDto = new RequestDto();
		requestDto.setCmd("unbindWeixin");
		Map<String, Object> params = new HashMap<String, Object>();
		requestDto.setParams(params);
		params.put("patientId", patient.getId());
		URLInvoke.post(SysCfg.getString("qujiuyi.chatUrl"), Constants.gson.toJson(requestDto));

		/** step5:返回结果. */
		ResponseDto responseDto = new ResponseDto();
		responseDto.setResultDesc("解绑成功");
		return responseDto;
	}

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
	@Override
	public PatientDto queryPatient(PatientDto patientDto) throws Exception {
		return patientDao.queryPatientById(patientDto);
	}
}