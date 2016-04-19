package com.jiuyi.qujiuyi.service.register.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jiuyi.qujiuyi.common.dict.CacheContainer;
import com.jiuyi.qujiuyi.common.dict.Constants;
import com.jiuyi.qujiuyi.common.util.SysCfg;
import com.jiuyi.qujiuyi.common.util.URLInvoke;
import com.jiuyi.qujiuyi.common.util.Util;
import com.jiuyi.qujiuyi.dao.order.ThirdPayOrderDao;
import com.jiuyi.qujiuyi.dao.patient.PatientDao;
import com.jiuyi.qujiuyi.dao.register.PatientRegisterDao;
import com.jiuyi.qujiuyi.dao.register.RegisterPlanDao;
import com.jiuyi.qujiuyi.dao.relative.PatientRelativeDao;
import com.jiuyi.qujiuyi.dto.common.RequestDto;
import com.jiuyi.qujiuyi.dto.common.ResponseDto;
import com.jiuyi.qujiuyi.dto.common.TokenDto;
import com.jiuyi.qujiuyi.dto.order.ThirdPayOrderDto;
import com.jiuyi.qujiuyi.dto.patient.PatientDto;
import com.jiuyi.qujiuyi.dto.register.PatientRegisterDto;
import com.jiuyi.qujiuyi.dto.relative.PatientRelativeDto;
import com.jiuyi.qujiuyi.service.BaseService;
import com.jiuyi.qujiuyi.service.BusinessException;
import com.jiuyi.qujiuyi.service.order.ThirdPayOrderService;
import com.jiuyi.qujiuyi.service.register.PatientRegisterService;
import com.qujiuyi.util.sms.SmsService;

/**
 * @description 患者挂号业务层实现
 * @author zhb
 * @createTime 2015年8月13日
 */
@Service
public class PatientRegisterServiceImpl implements PatientRegisterService {
	@Autowired
	private PatientRegisterDao patientRegisterDao;

	@Autowired
	private RegisterPlanDao registerPlanDao;

	@Autowired
	private ThirdPayOrderDao thirdPayOrderDao;

	@Autowired
	private ThirdPayOrderService thirdPayOrderService;

	@Autowired
	private PatientRelativeDao patientRelativeDao;

	@Autowired
	private PatientDao patientDao;

	/**
	 * @description 创建挂号记录
	 * @param patientRegisterDto
	 * @return
	 * @throws Exception
	 */
	@Override
	public ResponseDto createRegister(PatientRegisterDto patientRegisterDto) throws Exception {
		if (patientRegisterDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		patientRegisterDto.setStatus(RegisterStatus.NOREGISTER.getIntValue());
		patientRegisterDto.setRegisterTime(new Date());
		patientRegisterDao.createRegister(patientRegisterDto);

		ResponseDto responseDto = new ResponseDto();
		responseDto.setResultDesc("预约成功");
		return responseDto;
	}

	/**
	 * @description 获取患者挂号
	 * @param patientRegisterDto
	 * @return
	 * @throws Exception
	 */
	@Override
	public ResponseDto getPatientRegisterList(PatientRegisterDto patientRegisterDto) throws Exception {
		/** step1:空异常处理. */
		if (patientRegisterDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		/** step2:获取用户. */
		TokenDto token = CacheContainer.getToken(patientRegisterDto.getToken());
		PatientDto patient = token != null ? token.getPatient() : new PatientDto();
		patientRegisterDto.setPatientId(patient.getId());

		List<PatientRegisterDto> list = patientRegisterDao.getPatientRegisterList(patientRegisterDto);

		ResponseDto responseDto = new ResponseDto();
		responseDto.setResultDesc("获取成功");
		Map<String, Object> detail = new HashMap<String, Object>();
		detail.put("page", patientRegisterDto.getPage());
		detail.put("list", list);
		responseDto.setDetail(detail);
		return responseDto;
	}

	/**
	 * @description 删除挂号
	 * @param patientRegisterDto
	 * @return
	 * @throws Exception
	 */
	@Override
	public ResponseDto delRegister(PatientRegisterDto patientRegisterDto) throws Exception {
		/** step1:空异常处理. */
		if (patientRegisterDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		/** step2:获取用户. */
		TokenDto token = CacheContainer.getToken(patientRegisterDto.getToken());
		PatientDto patient = token != null ? token.getPatient() : new PatientDto();

		patientRegisterDto.setPatientId(patient.getId());
		patientRegisterDao.delRegister(patientRegisterDto);

		ResponseDto responseDto = new ResponseDto();
		responseDto.setResultDesc("删除成功");
		return responseDto;
	}

	/**
	 * @description 取消挂号
	 * @param patientRegisterDto
	 * @return
	 * @throws Exception
	 */
	@Override
	public ResponseDto cancelRegister(PatientRegisterDto patientRegisterDto) throws Exception {
		if (patientRegisterDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		// 获取用户信息
		TokenDto token = CacheContainer.getToken(patientRegisterDto.getToken());
		PatientDto patient = token != null ? token.getPatient() : new PatientDto();
		patientRegisterDto.setPatientId(patient.getId());

		// 获取挂号记录
		PatientRegisterDto registerDetail = patientRegisterDao.queryRegisterDetail(patientRegisterDto);
		if (registerDetail == null) {
			throw new BusinessException("挂号数据不存在");
		}

		if (registerDetail.getStatus() != null && registerDetail.getStatus() == RegisterStatus.REGISTED.getIntValue()) {
			throw new BusinessException("不能取消已就诊的挂号");
		}

		if (registerDetail.getStatus() != null && registerDetail.getStatus() == RegisterStatus.CANCLE.getIntValue()) {
			throw new BusinessException("不能重复取消挂号");
		}

		// 获取挂号订单
		ThirdPayOrderDto thirdPayOrderDto = new ThirdPayOrderDto();
		thirdPayOrderDto.setOutTradeNo(registerDetail.getOutTradeNo());
		thirdPayOrderDto = thirdPayOrderDao.getWeixinOrderByOutTradeNo(thirdPayOrderDto);
		if (thirdPayOrderDto == null) {
			throw new BusinessException("订单不存在");
		}

		thirdPayOrderDto.setToken(patientRegisterDto.getToken());
		// 退款处理
		thirdPayOrderService.refund(thirdPayOrderDto);

		// 更新挂号记录状态
		patientRegisterDao.cancelRegister(patientRegisterDto);

		// 更改订单为隐藏状态
		thirdPayOrderDao.updateThirdPayOrderDisplayStatusByOrderNo(thirdPayOrderDto);

		// 取消挂号后更新已挂号数量
		registerPlanDao.updateAlreadyRegisterCountDown(patientRegisterDto);

		ResponseDto responseDto = new ResponseDto();
		responseDto.setResultDesc("取消成功");
		return responseDto;
	}

	/**
	 * 
	 * @number @description 处理患者过期的挂号处理
	 * 
	 * @throws Exception
	 *
	 * @Date 2015年12月16日
	 */
	@Override
	public void handleExpiredRegister() throws Exception {

		PatientRegisterDto patientRegisterDto = new PatientRegisterDto();
		Date now = new Date();
		// 获得当前时间
		int date = Util.timeAmPmNight(now);
		patientRegisterDto.setRegisterTime(Util.nowFormatDate(new Date()));
		// 上午
		if (date == 0) {
			patientRegisterDto.setTimeZone(0);
		}
		// 下午
		if (date == 1) {
			patientRegisterDto.setTimeZone(1);
		}
		// 晚上
		if (date == 2) {
			patientRegisterDto.setTimeZone(2);
		}

		patientRegisterDao.updateRegisterStatus(patientRegisterDto);
	}

	/**
	 * 
	 * @number @description 取消挂号（院方读取数据）
	 * 
	 * @param patientRegisterDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2016年1月23日
	 */
	@Override
	public ResponseDto cancelRegisterPlus(PatientRegisterDto patientRegisterDto) throws Exception {
		if (patientRegisterDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		// 获取用户信息
		TokenDto token = CacheContainer.getToken(patientRegisterDto.getToken());
		PatientDto patient = token != null ? token.getPatient() : new PatientDto();
		patientRegisterDto.setPatientId(patient.getId());
		// 获取挂号记录
		PatientRegisterDto registerDetail = patientRegisterDao.queryRegisterDetailPlus(patientRegisterDto);
		if (registerDetail == null) {
			throw new BusinessException("挂号数据不存在");
		}

		if (registerDetail.getStatus() != null && registerDetail.getStatus() == RegisterStatus.REGISTED.getIntValue()) {
			throw new BusinessException("不能取消已就诊的挂号");
		}

		if (registerDetail.getStatus() != null && registerDetail.getStatus() == RegisterStatus.CANCLE.getIntValue()) {
			throw new BusinessException("不能重复取消挂号");
		}

		Date currentDate = Util.nowFormatDate(new Date());
		if (registerDetail.getScheduleDate().equals(currentDate)) {
			throw new BusinessException("很抱歉取消挂号至少提前一天申请");
		}
		if (registerDetail.getScheduleDate().before(currentDate)) {
			throw new BusinessException("挂号已过期，无法取消");
		}
		patientRegisterDto.setStatus(RegisterStatus.CANCLE.getIntValue());
		// 到院支付挂号
		if (registerDetail.getPayMode() == RegisterStatus.TOHOSPITAL.getIntValue()) {
			// 更新挂号记录状态
			patientRegisterDao.cancelRegister(patientRegisterDto);

			// 请求院方接口
			BaseService cancelOrder = new BaseService();
			cancelOrder.getParams().put("hospitalId", registerDetail.getHospitalId().toString());
			cancelOrder.getParams().put("numSourceId", registerDetail.getNumSourceId());
			cancelOrder.getParams().put("visitNo", registerDetail.getVisitNo());

			cancelOrder.packageData("cancelOrder", ResponseDto.class, SysCfg.getString("register.plus.url"));

			if (!cancelOrder.isSuccess()) {
				throw new BusinessException(cancelOrder.getDesc());
			}

			ResponseDto responseDto = new ResponseDto();
			responseDto.setResultDesc("取消成功，挂号类型（到院支付）");
			return responseDto;
		}

		// 获取挂号订单
		ThirdPayOrderDto thirdPayOrderDto = new ThirdPayOrderDto();
		thirdPayOrderDto.setOutTradeNo(registerDetail.getOutTradeNo());
		thirdPayOrderDto = thirdPayOrderDao.getWeixinOrderByOutTradeNo(thirdPayOrderDto);
		if (thirdPayOrderDto == null) {
			throw new BusinessException("订单不存在");
		}

		thirdPayOrderDto.setToken(patientRegisterDto.getToken());

		// 退款处理
		thirdPayOrderService.refund(thirdPayOrderDto);

		// 更新挂号记录状态
		patientRegisterDao.cancelRegister(patientRegisterDto);

		// 请求院方接口
		BaseService cancelOrder = new BaseService();
		cancelOrder.getParams().put("hospitalId", registerDetail.getHospitalId().toString());
		cancelOrder.getParams().put("numSourceId", registerDetail.getNumSourceId());
		cancelOrder.getParams().put("visitNo", registerDetail.getVisitNo());
		cancelOrder.packageData("cancelOrder", ResponseDto.class, SysCfg.getString("register.plus.url"));

		if (!cancelOrder.isSuccess()) {
			throw new BusinessException(cancelOrder.getDesc());
		}

		ResponseDto responseDto = new ResponseDto();
		responseDto.setResultDesc("取消成功，挂号类型（网上支付）");
		return responseDto;
	}

	/**
	 * 停诊取消挂号
	 * 
	 * @param patientRegisterDto
	 * @return
	 * @throws Exception
	 */
	public ResponseDto stopRegister(PatientRegisterDto patientRegisterDto) throws Exception {
		if (patientRegisterDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		// 获取挂号记录
		PatientRegisterDto registerDetail = patientRegisterDao.queryRegisterDetailPlus(patientRegisterDto);
		if (registerDetail == null) {
			throw new BusinessException("挂号数据不存在");
		}

		if (registerDetail.getStatus() != null && registerDetail.getStatus() == RegisterStatus.REGISTED.getIntValue()) {
			throw new BusinessException("不能取消已就诊的挂号");
		}

		if (registerDetail.getStatus() != null && registerDetail.getStatus() == RegisterStatus.CANCLE.getIntValue()) {
			throw new BusinessException("不能重复取消挂号");
		}

		Date currentDate = Util.nowFormatDate(new Date());
		if (registerDetail.getScheduleDate().before(currentDate)) {
			throw new BusinessException("挂号已过期，无法取消");
		}

		// 查询用户
		PatientDto patientDto = new PatientDto();
		patientDto.setId(registerDetail.getPatientId());
		PatientDto patient = patientDao.queryPatientById(patientDto);

		patientRegisterDto.setStatus(RegisterStatus.STOPREGISTER.getIntValue());
		// 到院支付挂号
		if (registerDetail.getPayMode() == RegisterStatus.TOHOSPITAL.getIntValue()) {
			// 更新挂号记录状态
			patientRegisterDao.cancelRegister(patientRegisterDto);

			// 请求院方接口
			BaseService cancelOrder = new BaseService();
			cancelOrder.getParams().put("hospitalId", registerDetail.getHospitalId().toString());
			cancelOrder.getParams().put("numSourceId", registerDetail.getNumSourceId());
			cancelOrder.getParams().put("visitNo", registerDetail.getVisitNo());

			cancelOrder.packageData("cancelOrder", ResponseDto.class, SysCfg.getString("register.plus.url"));
			if (!cancelOrder.isSuccess()) {
				throw new BusinessException(cancelOrder.getDesc());
			}

			// 短信提示
			String patientName = patient.getName();
			String doctorName = registerDetail.getDoctorName();
			String visitNo = registerDetail.getVisitNo();
			String param = String.format("#patientName#=%s&#doctorName#=%s&#visitNo#=%s", patientName + "\n", doctorName + "\n", visitNo + "\n");
			SmsService.instance().sendSms(registerDetail.getPatientPhone(), "11859", param);

			// 终端提示
			RequestDto requestDto = new RequestDto();
			requestDto.setCmd("sendSystemMsg");
			Map<String, Object> params = new HashMap<String, Object>();
			Map<String, String> content = new HashMap<String, String>();
			content.put("cmd", "stopRegister");
			content.put("message", Util.stopRegister(patientName, doctorName, visitNo));
			requestDto.setParams(params);
			params.put("target", patient.getId());
			params.put("targetType", 1);
			params.put("summary", "医生停诊处理");
			params.put("content", Constants.gson.toJson(content));
			params.put("weixinMsg", Util.stopRegister(patientName, doctorName, visitNo));
			URLInvoke.post(SysCfg.getString("qujiuyi.chatUrl"), Constants.gson.toJson(requestDto));

			ResponseDto responseDto = new ResponseDto();
			responseDto.setResultDesc("取消成功，挂号类型（到院支付）");
			return responseDto;
		}

		// 获取挂号订单
		ThirdPayOrderDto thirdPayOrderDto = new ThirdPayOrderDto();
		thirdPayOrderDto.setOutTradeNo(registerDetail.getOutTradeNo());
		thirdPayOrderDto = thirdPayOrderDao.getWeixinOrderByOutTradeNo(thirdPayOrderDto);
		if (thirdPayOrderDto == null) {
			throw new BusinessException("订单不存在");
		}

		thirdPayOrderDto.setToken(patientRegisterDto.getToken());

		// 退款处理
		thirdPayOrderService.refund(thirdPayOrderDto);

		// 更新挂号记录状态
		patientRegisterDao.cancelRegister(patientRegisterDto);

		// 请求院方接口
		BaseService cancelOrder = new BaseService();
		cancelOrder.getParams().put("hospitalId", registerDetail.getHospitalId().toString());
		cancelOrder.getParams().put("numSourceId", registerDetail.getNumSourceId());
		cancelOrder.getParams().put("visitNo", registerDetail.getVisitNo());
		cancelOrder.packageData("cancelOrder", ResponseDto.class, SysCfg.getString("register.plus.url"));
		if (!cancelOrder.isSuccess()) {
			throw new BusinessException(cancelOrder.getDesc());
		}
		// 短信提示
		String patientName = patient.getName();
		String doctorName = registerDetail.getDoctorName();
		String visitNo = registerDetail.getVisitNo();
		String param = String.format("#patientName#=%s&#doctorName#=%s&#visitNo#=%s", patientName + "\n", doctorName + "\n", visitNo + "\n");
		SmsService.instance().sendSms(registerDetail.getPatientPhone(), "11859", param);

		// 终端提示
		RequestDto requestDto = new RequestDto();
		requestDto.setCmd("sendSystemMsg");
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, String> content = new HashMap<String, String>();
		content.put("cmd", "stopRegister");
		content.put("message", Util.stopRegister(patientName, doctorName, visitNo));
		requestDto.setParams(params);
		params.put("target", patient.getId());
		params.put("targetType", 1);
		params.put("summary", "医生停诊处理");
		params.put("content", Constants.gson.toJson(content));
		params.put("weixinMsg", Util.stopRegister(patientName, doctorName, visitNo));
		URLInvoke.post(SysCfg.getString("qujiuyi.chatUrl"), Constants.gson.toJson(requestDto));
		ResponseDto responseDto = new ResponseDto();
		responseDto.setResultDesc("取消成功，挂号类型（网上支付）");
		return responseDto;
	}

	/**
	 * @description 获取患者挂号 （院方读取数据）
	 * @param patientRegisterDto
	 * @return
	 * @throws Exception
	 */
	@Override
	public ResponseDto getPatientRegisterListPlus(PatientRegisterDto patientRegisterDto) throws Exception {
		/** step1:空异常处理. */
		if (patientRegisterDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		/** step2:获取用户. */
		TokenDto token = CacheContainer.getToken(patientRegisterDto.getToken());
		PatientDto patient = token != null ? token.getPatient() : new PatientDto();
		patientRegisterDto.setPatientId(patient.getId());

		List<PatientRegisterDto> list = patientRegisterDao.getPatientRegisterListPlus(patientRegisterDto);
		// 循环挂号计划列表，判断挂号是否可以取号，是否可以取消
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getStatus() == RegisterStatus.NOREGISTER.getIntValue()) {
				if (list.get(i).getScheduleDate().equals(Util.nowFormatDate(new Date()))) {
					if (new Date().after(list.get(i).getStartTime()) && new Date().before(list.get(i).getEndTime())) {
						list.get(i).setIsFetchNumber(RegisterStatus.YESFETCHNUMBER.getIntValue());
					} else {
						list.get(i).setIsFetchNumber(RegisterStatus.NOFETCHNUMBER.getIntValue());
					}
					list.get(i).setIsCancle(RegisterStatus.NOCANCLE.getIntValue());
				} else {
					list.get(i).setIsFetchNumber(RegisterStatus.NOFETCHNUMBER.getIntValue());
					if (Util.nowFormatDate(new Date()).after(list.get(i).getScheduleDate())) {
						list.get(i).setIsCancle(RegisterStatus.NOCANCLE.getIntValue());
					} else {
						list.get(i).setIsCancle(RegisterStatus.YESCANCLE.getIntValue());
					}
				}
			} else {
				list.get(i).setIsFetchNumber(RegisterStatus.NOFETCHNUMBER.getIntValue());
				list.get(i).setIsCancle(RegisterStatus.NOCANCLE.getIntValue());
			}
			list.get(i).setDoctorHead(SysCfg.getString("doctor.head.path") + list.get(i).getDoctorHead());
		}

		ResponseDto responseDto = new ResponseDto();
		responseDto.setResultDesc("获取成功");
		Map<String, Object> detail = new HashMap<String, Object>();
		detail.put("page", patientRegisterDto.getPage());
		detail.put("list", list);
		responseDto.setDetail(detail);
		return responseDto;
	}

	/**
	 * 
	 * @number @description 取号
	 * 
	 * @param patientRegisterDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2016年1月23日
	 */
	@Override
	public ResponseDto fetchNumber(PatientRegisterDto patientRegisterDto) throws Exception {
		if (patientRegisterDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}
		if (patientRegisterDto.getId() == null) {
			throw new BusinessException("挂号记录id不能为空");
		}

		PatientRegisterDto register = patientRegisterDao.queryRegisterDetailPlus(patientRegisterDto);

		if (register.getStatus() == RegisterStatus.OUTDATE.getIntValue()) {
			throw new BusinessException("该号源已过期");
		}
		if (register.getStatus() == RegisterStatus.CANCLE.getIntValue()) {
			throw new BusinessException("该号源已取消");
		}
		if (register.getStatus() == RegisterStatus.REGISTED.getIntValue()) {
			throw new BusinessException("该号源已取号");
		}

		if (register.getStatus() == RegisterStatus.NOREGISTER.getIntValue()) {
			if (register.getScheduleDate().equals(Util.nowFormatDate(new Date()))) {
				if (new Date().after(register.getStartTime()) && new Date().before(register.getEndTime())) {
					register.setIsFetchNumber(RegisterStatus.YESFETCHNUMBER.getIntValue());
				} else {
					throw new BusinessException("请在就诊时间段取号");
				}
			} else {
				throw new BusinessException("请在就诊当天具体时间段取号");
			}
		}

		BaseService fetchNumber = new BaseService();
		fetchNumber.getParams().put("hospitalId", register.getHospitalId().toString());
		fetchNumber.getParams().put("numSourceId", register.getNumSourceId());
		fetchNumber.getParams().put("visitNo", register.getVisitNo());
		fetchNumber.packageData("fetchNumber", ResponseDto.class, SysCfg.getString("register.plus.url"));

		if (!fetchNumber.isSuccess()) {
			throw new BusinessException(fetchNumber.getDesc());
		}

		patientRegisterDto.setCheckInDate(new Date());
		patientRegisterDao.fetchNumber(patientRegisterDto);
		ResponseDto responseDto = new ResponseDto();

		responseDto.setResultDesc("取号成功");
		return responseDto;
	}

	/**
	 * 
	 * @number @description 排队信息查询
	 * 
	 * @param patientRegisterDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2016年1月23日
	 */
	@Override
	public ResponseDto getLineInfo(PatientRegisterDto patientRegisterDto) throws Exception {
		if (patientRegisterDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}
		if (patientRegisterDto.getHospitalId() == null) {
			throw new BusinessException("医院id不能为空");
		}
		if (!Util.isNotEmpty(patientRegisterDto.getNumSourceId())) {
			throw new BusinessException("号源id不能为空");
		}
		if (!Util.isNotEmpty(patientRegisterDto.getVisitNo())) {
			throw new BusinessException("就诊号不能为空");
		}

		BaseService getLineInfo = new BaseService();
		getLineInfo.getParams().put("hospitalId", patientRegisterDto.getHospitalId().toString());
		getLineInfo.getParams().put("numSourceId", patientRegisterDto.getNumSourceId());
		getLineInfo.getParams().put("visitNo", patientRegisterDto.getVisitNo());
		getLineInfo.packageData("getLineInfo", ResponseDto.class, SysCfg.getString("register.plus.url"));
		if (!getLineInfo.isSuccess()) {
			throw new BusinessException(getLineInfo.getDesc());
		}
		ResponseDto responseDto = new ResponseDto();
		responseDto.setResultDesc("排队信息查询成功");
		return responseDto;
	}

	/**
	 * 
	 * @number @description 判断是否已挂过指定号源
	 * 
	 * @param patientRegisterDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2016年3月8日
	 */
	@Override
	public ResponseDto checkIsRegister(PatientRegisterDto patientRegisterDto) throws Exception {
		if (patientRegisterDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}
		if (!Util.isNotEmpty(patientRegisterDto.getNumSourceId())) {
			throw new BusinessException("号源ID不能为空");
		}
		if (patientRegisterDto.getRelativeId() == null) {
			throw new BusinessException("常用就诊人不能为空");
		}
		if (patientRegisterDto.getHospitalId() == null) {
			throw new BusinessException("医院id不能为空");
		}

		/** 获取用户. */
		TokenDto token = CacheContainer.getToken(patientRegisterDto.getToken());
		PatientDto patient = token != null ? token.getPatient() : new PatientDto();
		if (patient.getId() == null) {
			throw new BusinessException("登录异常");
		}
		patientRegisterDto.setPatientId(patient.getId());

		/** 获取常用就诊人信息. */
		PatientRelativeDto ratientRelativeDto = new PatientRelativeDto();
		ratientRelativeDto.setId(patientRegisterDto.getRelativeId());
		ratientRelativeDto = patientRelativeDao.getPatientRelativeById(ratientRelativeDto);
		if (ratientRelativeDto == null) {
			throw new BusinessException("就诊人不存在");
		}

		/** 查询患者挂号记录，判断是否已挂过此号 */
		patientRegisterDto.setPatientId(patient.getId());
		patientRegisterDto.setNumSourceId(patientRegisterDto.getNumSourceId());
		patientRegisterDto.setCertificateNumber(ratientRelativeDto.getCertificateNumber());
		patientRegisterDto.setStatus(RegisterStatus.NOREGISTER.getIntValue());
		PatientRegisterDto patientReg = patientRegisterDao.queryRegisterDetailPlus(patientRegisterDto);
		ResponseDto responseDto = new ResponseDto();
		Map<String, Object> map = new HashMap<String, Object>();
		if (patientReg != null) {
			map.put("isRegister", "true");
		} else {
			map.put("isRegister", "false");
		}
		responseDto.setDetail(map);
		responseDto.setResultDesc("成功");
		return responseDto;
	}

	/**
	 * 
	 * @number @description 根据就诊号查询患者挂号计划
	 * 
	 * @param patientRegisterDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2016年3月11日
	 */
	@Override
	public ResponseDto queryRegisterPlanDetail(PatientRegisterDto patientRegisterDto) throws Exception {
		if (patientRegisterDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		TokenDto token = CacheContainer.getToken(patientRegisterDto.getToken());
		PatientDto patient = token != null ? token.getPatient() : new PatientDto();
		if (patient.getId() == null) {
			throw new BusinessException("登录异常");
		}
		patientRegisterDto.setPatientId(patient.getId());

		PatientRegisterDto patientReg = patientRegisterDao.queryRegisterDetailPlus(patientRegisterDto);
		// 循环挂号计划列表，判断挂号是否可以取号，是否可以取消
		if (patientReg.getStatus() == RegisterStatus.NOREGISTER.getIntValue()) {
			if (patientReg.getScheduleDate().equals(Util.nowFormatDate(new Date()))) {
				if (new Date().after(patientReg.getStartTime()) && new Date().before(patientReg.getEndTime())) {
					patientReg.setIsFetchNumber(RegisterStatus.YESFETCHNUMBER.getIntValue());
				} else {
					patientReg.setIsFetchNumber(RegisterStatus.NOFETCHNUMBER.getIntValue());
				}
				patientReg.setIsCancle(RegisterStatus.NOCANCLE.getIntValue());
			} else {
				patientReg.setIsFetchNumber(RegisterStatus.NOFETCHNUMBER.getIntValue());
				if (Util.nowFormatDate(new Date()).after(patientReg.getScheduleDate())) {
					patientReg.setIsCancle(RegisterStatus.NOCANCLE.getIntValue());
				} else {
					patientReg.setIsCancle(RegisterStatus.YESCANCLE.getIntValue());
				}
			}
		} else {
			patientReg.setIsFetchNumber(RegisterStatus.NOFETCHNUMBER.getIntValue());
			patientReg.setIsCancle(RegisterStatus.NOCANCLE.getIntValue());
		}
		patientReg.setDoctorHead(SysCfg.getString("doctor.head.path") + patientReg.getDoctorHead());

		ResponseDto responseDto = new ResponseDto();
		responseDto.setDetail(patientReg);
		responseDto.setResultDesc("挂号详情");
		return responseDto;

	}
}