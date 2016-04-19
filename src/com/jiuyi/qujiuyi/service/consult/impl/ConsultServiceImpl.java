package com.jiuyi.qujiuyi.service.consult.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jiuyi.qujiuyi.common.dict.CacheContainer;
import com.jiuyi.qujiuyi.common.dict.Constants;
import com.jiuyi.qujiuyi.common.util.MD5;
import com.jiuyi.qujiuyi.common.util.SysCfg;
import com.jiuyi.qujiuyi.common.util.URLInvoke;
import com.jiuyi.qujiuyi.common.util.Util;
import com.jiuyi.qujiuyi.dao.consult.ConsultDao;
import com.jiuyi.qujiuyi.dao.doctor.DoctorDao;
import com.jiuyi.qujiuyi.dao.doctor.PersonalDoctorDao;
import com.jiuyi.qujiuyi.dao.order.ThirdPayOrderDao;
import com.jiuyi.qujiuyi.dao.service.ServiceDao;
import com.jiuyi.qujiuyi.dto.chat.ChatDto;
import com.jiuyi.qujiuyi.dto.common.RequestDto;
import com.jiuyi.qujiuyi.dto.common.ResponseDto;
import com.jiuyi.qujiuyi.dto.common.TokenDto;
import com.jiuyi.qujiuyi.dto.consult.ConsultDto;
import com.jiuyi.qujiuyi.dto.doctor.DoctorDto;
import com.jiuyi.qujiuyi.dto.doctor.PersonalDoctorDto;
import com.jiuyi.qujiuyi.dto.order.ThirdPayOrderDto;
import com.jiuyi.qujiuyi.dto.patient.PatientDto;
import com.jiuyi.qujiuyi.dto.service.ServiceDto;
import com.jiuyi.qujiuyi.service.BusinessException;
import com.jiuyi.qujiuyi.service.consult.ConsultService;
import com.jiuyi.qujiuyi.service.order.ThirdPayOrderService;
import com.qujiuyi.util.sms.SmsService;

/**
 * @description 患者咨询业务层实现
 * @author zhb
 * @createTime 2015年4月28日
 */
@Service
public class ConsultServiceImpl implements ConsultService {
	private final Logger logger = Logger.getLogger(ConsultServiceImpl.class);
	@Autowired
	private ConsultDao consultDao;

	@Autowired
	private PersonalDoctorDao personalDoctorDao;

	@Autowired
	private ServiceDao serviceDao;

	@Autowired
	private DoctorDao doctorDao;

	@Autowired
	private ThirdPayOrderDao thirdPayOrderDao;

	@Autowired
	private ThirdPayOrderService thirdPayOrderService;

	/**
	 * @description 获取我的图文咨询
	 * @param consultDto
	 * @return
	 * @throws Exception
	 */
	@Override
	public ResponseDto queryMyConsult(ConsultDto consultDto) throws Exception {
		/** step1:空异常处理. */
		if (consultDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		/** step2:获取用户. */
		TokenDto token = CacheContainer.getToken(consultDto.getToken());
		PatientDto patient = token != null ? token.getPatient() : new PatientDto();
		consultDto.setPatientId(patient.getId());

		/** step3:查询数据库. */
		List<ConsultDto> list = consultDao.queryMyConsult(consultDto);
		if (list != null && !list.isEmpty()) {
			for (ConsultDto dto : list) {
				if (Util.isNotEmpty(dto.getDoctorHead()) && !dto.getDoctorHead().startsWith("http")) {
					dto.setDoctorHead(SysCfg.getString("doctor.head.path") + dto.getDoctorHead());
				}
				if (dto.getDoctorId() != null) {
					dto.setMd5Id(MD5.getMD5Code(MD5.getMD5Code(dto.getDoctorId().toString())));
				}
			}
		}

		/** step4:返回结果. */
		ResponseDto responseDto = new ResponseDto();
		responseDto.setResultDesc("获取成功");
		Map<String, Object> detail = new HashMap<String, Object>();
		responseDto.setDetail(detail);
		detail.put("page", consultDto.getPage());
		detail.put("list", list);
		return responseDto;
	}

	/**
	 * @description 创建咨询
	 * @param consultDto
	 * @throws Exception
	 */
	@Override
	public ResponseDto createMyConsult(ConsultDto consultDto) throws Exception {
		/** step1:空异常处理. */
		if (consultDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		TokenDto token = CacheContainer.getToken(consultDto.getToken());
		PatientDto patient = token != null ? token.getPatient() : new PatientDto();
		consultDto.setPatientId(patient.getId());

		/** step4:判断医生id不为空. */
		if (consultDto.getDoctorId() == null) {
			throw new BusinessException("未选择医生");
		}

		/** step5:校验症状描述. */
		if (!Util.isNotEmpty(consultDto.getSymptoms())) {
			throw new BusinessException("症状描述不能为空");
		}

		/** step6:校验年龄. */
		if (consultDto.getAge() == null) {
			throw new BusinessException("年龄不能为空");
		}

		if (consultDto.getAge() > 100) {
			throw new BusinessException("请输入正确年龄");
		}
		/** step7:校验性别 */
		if (consultDto.getGender() == null) {
			throw new BusinessException("性别不能为空");
		}

		/** step8:咨询人姓名不能为空. */
		// if(!Util.isNotEmpty(consultDto.getConsultName())){
		// throw new BusinessException("咨询人名称不能为空");
		// }

		consultDto.setPayStatus(Constants.OrderSatus.ORDER_STATUS_0);
		/** step8:校验是否咨询本人. */
		DoctorDto queryDoctorDto = new DoctorDto();
		queryDoctorDto.setDoctorPhone(patient.getPhone());
		List<DoctorDto> doctorList = doctorDao.queryDoctorByPhone(queryDoctorDto);
		if (doctorList != null && !doctorList.isEmpty() && doctorList.get(0).getId().equals(consultDto.getDoctorId())) {
			throw new BusinessException("您不能享用自己提供的服务");
		}

		String id = Util.getUniqueSn();
		synchronized (patient) {
			/** step9:校验是否正在咨询此医生. */
			List<ConsultDto> consults = consultDao.queryMyConsultNoEnd(consultDto);
			if (consults != null && !consults.isEmpty()) {
				throw new BusinessException("你与该医生存在未结束的服务");
			}

			/** step10:判断医生是否为私人医生. */
			consultDto.setType(1);
			PersonalDoctorDto personalDoctorDto = new PersonalDoctorDto();
			personalDoctorDto.setPatientId(consultDto.getPatientId());
			personalDoctorDto.setDoctorId(consultDto.getDoctorId());
			List<PersonalDoctorDto> list = personalDoctorDao.queryPersonalDoctorByPatientIdAndDoctorId(personalDoctorDto);
			if (list != null && !list.isEmpty()) {
				consultDto.setType(2);
				consultDto.setPayStatus(Constants.OrderSatus.ORDER_STATUS_1);
			} else {
				consultDto.setPayStatus(Constants.OrderSatus.ORDER_STATUS_0);
			}

			/** step11:判断图文咨询是否免费. */
			if (consultDto.getPayStatus() != null && consultDto.getPayStatus() == Constants.OrderSatus.ORDER_STATUS_0) {
				ServiceDto serviceDto = new ServiceDto();
				serviceDto.setDoctorId(consultDto.getDoctorId());
				List<ServiceDto> serivceList = serviceDao.queryConsultServiceByDoctorId(serviceDto);
				if (serivceList == null || serivceList.isEmpty()) {
					throw new BusinessException("该服务价格未设定");
				}
				if (serivceList.get(0).getPrice() != null && serivceList.get(0).getPrice() == 0) {
					consultDto.setPayStatus(Constants.OrderSatus.ORDER_STATUS_1);
				} else {
					consultDto.setPayStatus(Constants.OrderSatus.ORDER_STATUS_0);
				}
			}

			/** step12:设置医生接受状态和咨询状态. */
			if (consultDto.getPayStatus() != null && consultDto.getPayStatus() == Constants.OrderSatus.ORDER_STATUS_1 && consultDto.getDeviceType() != null && consultDto.getDeviceType() == 1) {
				consultDto.setAcceptStatus(1);
				consultDto.setConsultStatus(1);
				// 同步图文咨询
				RequestDto requestDto = new RequestDto();
				requestDto.setCmd("syncConsultToChatServer");
				requestDto.setToken(consultDto.getToken());
				Map<String, Object> params = new HashMap<String, Object>();
				requestDto.setParams(params);
				params.put("id", id);
				URLInvoke.post(SysCfg.getString("qujiuyi.chatUrl"), Constants.gson.toJson(requestDto));
			} else {
				consultDto.setAcceptStatus(0);
				consultDto.setConsultStatus(0);
			}
			consultDto.setDisplayStatus(1);// 设置咨询显示状态

			/** step13:创建问诊记录. */
			consultDto.setCreateTime(new Date());
			consultDto.setId(id);
			consultDao.createMyConsult(consultDto);
		}

		/** step14:通知医生就诊. */
		if (consultDto.getPayStatus() != null && consultDto.getPayStatus() == 1 && !(consultDto.getDeviceType() != null && consultDto.getDeviceType() == 1)) {
			RequestDto requestDto = new RequestDto();
			requestDto.setCmd("consultRequest");
			requestDto.setToken(consultDto.getToken());
			Map<String, Object> params = new HashMap<String, Object>();
			requestDto.setParams(params);
			params.put("sender", consultDto.getPatientId());
			params.put("receiver", consultDto.getDoctorId());
			params.put("serviceId", consultDto.getId());
			URLInvoke.post(SysCfg.getString("qujiuyi.chatUrl"), Constants.gson.toJson(requestDto));
		}

		/** step15:返回结果. */
		ResponseDto responseDto = new ResponseDto();
		responseDto.setResultDesc("创建成功");
		Map<String, Object> detail = new HashMap<String, Object>();
		responseDto.setDetail(detail);
		detail.put("id", id);
		detail.put("payStatus", consultDto.getPayStatus());
		return responseDto;
	}

	/**
	 * @description 创建免费咨询
	 * @param consultDto
	 * @throws Exception
	 */
	@Override
	public ResponseDto createFreeConsult(ConsultDto consultDto) throws Exception {
		if (consultDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		TokenDto token = CacheContainer.getToken(consultDto.getToken());
		PatientDto patient = token != null ? token.getPatient() : new PatientDto();
		consultDto.setPatientId(patient.getId());

		ConsultDto queryDto = new ConsultDto();
		queryDto.setPatientId(patient.getId());
		queryDto.setIsCurDate(0);
		queryDto.setType(0);
		List<ConsultDto> consultList = consultDao.queryMyConsult(queryDto);
		if (consultList != null && consultList.size() >= 3) {
			throw new BusinessException("每日免费咨询上线3个，您已达到上限");
		}

		if (consultDto.getDoctorId() != null) {
			throw new BusinessException("免费咨询无需指定医生");
		}

		if (consultDto.getPatientRelativeId() != null) {
			throw new BusinessException("免费咨询无需指定常用就诊人");
		}

		if (consultDto.getAge() == null) {
			throw new BusinessException("免费咨询需填写年龄");
		}
		if (consultDto.getAge() > 100) {
			throw new BusinessException("请输入正确的年龄");
		}

		if (consultDto.getGender() == null) {
			throw new BusinessException("免费咨询需填写性别");
		}

		if (!Util.isNotEmpty(consultDto.getSymptoms())) {
			throw new BusinessException("症状描述必填");
		}

		if (consultDto.getSymptoms().length() > 200) {
			throw new BusinessException("您输入的症状描述过长");
		}

		if (!Util.isNotEmpty(consultDto.getConsultName())) {
			throw new BusinessException("咨询人姓名不能空");
		}

		consultDto.setAcceptStatus(0);
		consultDto.setConsultStatus(0);
		consultDto.setPayStatus(1);
		consultDto.setDisplayStatus(1);

		consultDto.setPatientId(patient.getId());
		String id = Util.getUniqueSn();
		consultDto.setCreateTime(new Date());
		consultDto.setId(id);
		consultDto.setType(0);
		consultDao.createMyConsult(consultDto);

		// 同步图文咨询
		RequestDto requestDto = new RequestDto();
		requestDto.setCmd("syncConsultToChatServer");
		requestDto.setToken(consultDto.getToken());
		Map<String, Object> params = new HashMap<String, Object>();
		requestDto.setParams(params);
		params.put("id", id);
		URLInvoke.post(SysCfg.getString("qujiuyi.chatUrl"), Constants.gson.toJson(requestDto));

		ResponseDto responseDto = new ResponseDto();
		responseDto.setResultDesc("创建成功");
		Map<String, Object> detail = new HashMap<String, Object>();
		responseDto.setDetail(detail);
		detail.put("id", id);
		detail.put("payStatus", consultDto.getPayStatus());
		detail.put("remainder", 2 - consultList.size());
		return responseDto;
	}

	/**
	 * @description 评价咨询
	 * @param consultDto
	 * @throws Exception
	 */
	@Override
	public ResponseDto evaluateMyConsult(ConsultDto consultDto) throws Exception {
		/** step1:空异常处理. */
		if (consultDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		/** step2:获取患者信息. */
		TokenDto token = CacheContainer.getToken(consultDto.getToken());
		PatientDto patientDto = token.getPatient();

		/** step3:校验咨询id. */
		if (!Util.isNotEmpty(consultDto.getId())) {
			throw new BusinessException("咨询id不能为空");
		}

		// 如果是咨询，判断医生是否回复，如果没有回复，不可评价
		ChatDto chatDto = new ChatDto();
		chatDto.setServiceId(consultDto.getId());
		List<ChatDto> chats = consultDao.queryChatBySendTypeAndServiceId(chatDto);
		if (chats == null || chats.isEmpty() || chats.size() == 0) {
			throw new BusinessException("您不可评价医生未回复的咨询");
		}

		/** step3:校验咨询评价内容. */
		if (!Util.isNotEmpty(consultDto.getEvaluation().trim()) || consultDto.getEvaluation().trim().length() < 7) {
			throw new BusinessException("评价内容不得少于7个字");
		}

		/** step4:校验满意度. */
		if (consultDto.getSatisfaction() == null) {
			throw new BusinessException("满意度必选");
		}

		/** step5:根据id获取咨询记录. */
		ConsultDto dto = consultDao.queryConsultById(consultDto);

		/** step6:判断咨询记录是否存在. */
		if (dto == null) {
			throw new BusinessException("该记录不存在");
		}

		/** step7:判断咨询记录是否是自己的. */
		if (patientDto != null && patientDto.getId() != null && !patientDto.getId().equals(dto.getPatientId())) {
			throw new BusinessException("不能评价他人的咨询");
		}

		/** step8:判断咨询是否已开始. */
		if (dto.getConsultStatus() == null || dto.getConsultStatus() == 0) {
			throw new BusinessException("不能评价还未开始的咨询");
		}

		/** step9:判断咨询是否已结束. */
		if (dto.getConsultStatus() == null || dto.getConsultStatus() != 2) {
			throw new BusinessException("不能评价未结束的咨询");
		}

		/** step10:判断咨询是否已评价. */
		if (Util.isNotEmpty(dto.getEvaluation())) {
			throw new BusinessException("不能对已评价过的咨询进行再次评价");
		}

		/** step11:保存评价信息. */
		consultDto.setEvaluateTime(new Date());
		consultDao.evaluateMyConsult(consultDto);

		/** step12:返回结果. */
		ResponseDto responseDto = new ResponseDto();
		responseDto.setResultDesc("评价成功");
		return responseDto;
	}

	/**
	 * @description 判断是否正在咨询此医生
	 * @param consultDto
	 * @throws Exception
	 */
	@Override
	public ResponseDto isConsultingWithTheDoctor(ConsultDto consultDto) throws Exception {
		/** step1:空异常处理. */
		if (consultDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		/** step2:校验是否为本人操作. */
		TokenDto token = CacheContainer.getToken(consultDto.getToken());
		if (consultDto.getPatientId() == null || (token != null && token.getPatient() != null && !consultDto.getPatientId().equals(token.getPatient().getId()))) {
			throw new BusinessException("非本人操作");
		}

		/** step3:校验医生id. */
		if (consultDto.getDoctorId() == null) {
			throw new BusinessException("医生id不能为空");
		}

		/** step4:查询数据. */
		List<ConsultDto> list = consultDao.queryMyConsulting(consultDto);

		/** step5:返回结果. */
		ResponseDto responseDto = new ResponseDto();
		responseDto.setResultDesc("成功");
		Map<String, Object> detail = new HashMap<String, Object>();
		detail.put("isConsultingWithTheDoctor", list != null && !list.isEmpty());
		detail.put("cosultStatus", list != null && !list.isEmpty() ? list.get(0).getConsultStatus() : null);
		detail.put("consultId", list != null && !list.isEmpty() ? list.get(0).getId() : null);
		detail.put("symptoms", list != null && !list.isEmpty() ? list.get(0).getSymptoms() : null);
		responseDto.setDetail(detail);
		return responseDto;
	}

	/**
	 * @description 根据医生id获取评价列表
	 * @param consultDto
	 * @throws Exception
	 */
	@Override
	public ResponseDto queryEvaluationsByDoctorId(ConsultDto consultDto) throws Exception {
		/** step1:空异常处理. */
		if (consultDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		/** step2:校验医生id. */
		if (consultDto.getDoctorId() == null) {
			throw new BusinessException("医生id不能为空");
		}

		/** step3:查询数据. */
		List<ConsultDto> list = consultDao.queryEvaluationsByDoctorId(consultDto);

		/** step4:返回结果. */
		ResponseDto responseDto = new ResponseDto();
		responseDto.setResultDesc("成功");
		Map<String, Object> detail = new HashMap<String, Object>();
		detail.put("list", list == null ? new ArrayList<ConsultDto>() : list);
		detail.put("page", consultDto.getPage());
		responseDto.setDetail(detail);
		return responseDto;
	}

	/**
	 * @description 获取咨询详情
	 * @param consultDto
	 * @throws Exception
	 */
	@Override
	public ResponseDto queryConsultDetail(ConsultDto consultDto) throws Exception {
		/** step1:空异常处理. */
		if (consultDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		/** step2:获取数据. */
		ConsultDto dto = consultDao.queryConsultById(consultDto);

		/** step3:校验数据是否存在. */
		if (dto == null) {
			throw new BusinessException("数据不存在");
		}
		/** step4:校验数据是否属于用户. */
		TokenDto token = CacheContainer.getToken(consultDto.getToken());
		PatientDto patient = token != null ? token.getPatient() : null;
		if (dto.getPatientId() == null || patient == null || !dto.getPatientId().equals(patient.getId())) {
			throw new BusinessException("数据不存在");
		}

		if (Util.isNotEmpty(dto.getDoctorHead()) && !dto.getDoctorHead().startsWith("http")) {
			dto.setDoctorHead(SysCfg.getString("doctor.head.path") + dto.getDoctorHead());
		}

		/** step4:返回结果. */
		ResponseDto responseDto = new ResponseDto();
		responseDto.setResultDesc("获取成功");
		responseDto.setDetail(dto);
		return responseDto;
	}

	/**
	 * @description 删除图文咨询
	 * @param consultDto
	 * @return
	 * @throws Exception
	 */
	@Override
	public ResponseDto deleteConsult(ConsultDto consultDto) throws Exception {
		/** step1:空异常处理. */
		if (consultDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		/** step2:判断图文咨询ID是否为空. */
		if (consultDto.getId() == null) {
			throw new BusinessException("图文咨询ID不能为空");
		}

		/** step3:获取图文咨询. */
		ConsultDto dto = consultDao.queryConsultById(consultDto);

		/** step4:获取用户. */
		TokenDto token = CacheContainer.getToken(consultDto.getToken());
		PatientDto patient = token != null ? token.getPatient() : null;

		/** step5:判断图文咨询是否存在. */
		if (dto == null) {
			throw new BusinessException("图文咨询不存在");
		}

		/** step6:判断有无删除权限. */
		if (patient != null && patient.getId() != null && !patient.getId().equals(dto.getPatientId())) {
			throw new BusinessException("无权删除");
		}

		/** step7:判断图文咨询是否已结束. */
		if (dto.getConsultStatus() != null && dto.getConsultStatus() != 2) {
			throw new BusinessException("不能删除还未结束的图文咨询");
		}

		/** step8:删除图文咨询. */
		consultDao.deleteConsult(consultDto);

		/** step9:返回结果. */
		ResponseDto responseDto = new ResponseDto();
		responseDto.setResultDesc("删除问诊成功");
		return responseDto;
	}

	/**
	 * @description 取消图文咨询
	 * @param consultDto
	 * @return
	 * @throws Exception
	 */
	@Override
	public ResponseDto cancelConsult(ConsultDto consultDto) throws Exception {
		/** step1:空异常处理. */
		if (consultDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		/** step2:判断图文咨询ID是否为空. */
		if (consultDto.getId() == null) {
			throw new BusinessException("图文咨询ID不能为空");
		}

		/** step3:获取图文咨询. */
		ConsultDto dto = consultDao.queryConsultById(consultDto);
		/** step4:获取用户. */
		TokenDto token = CacheContainer.getToken(consultDto.getToken());
		PatientDto patient = token != null ? token.getPatient() : null;

		/** step5:判断图文咨询是否存在. */
		if (dto == null) {
			throw new BusinessException("图文咨询不存在");
		}

		/** step6:判断有无取消权限. */
		if (patient != null && patient.getId() != null && !patient.getId().equals(dto.getPatientId())) {
			throw new BusinessException("无权取消");
		}

		/** step7:判断有无付费. */
		if (dto.getPayStatus() == null || dto.getPayStatus() == 0) {
			throw new BusinessException("该咨询还没付费");
		}

		/** step8:判断付费是否已进行. */
		if (dto.getConsultStatus() != null && dto.getConsultStatus() != 0) {
			throw new BusinessException("只能取消待接处理的咨询服务");
		}

		/** step9:取消咨询. */
		consultDao.cancelConsult(consultDto);

		/** step10:根据图文咨询获取订单. */
		if (!dto.isFree()) {// 免费咨询没有退款流程
			// 查询咨询记录
			ThirdPayOrderDto thirdPayOrderDto = new ThirdPayOrderDto();
			thirdPayOrderDto.setOutTradeNo(dto.getOutTradeNo());
			ThirdPayOrderDto order = thirdPayOrderDao.queryOrderDetailByOutTradeNo(thirdPayOrderDto);
			if (order != null) {
				order.setToken(consultDto.getToken());
				thirdPayOrderService.refund(order);
			}
		}
		/** step13:返回结果. */
		ResponseDto responseDto = new ResponseDto();
		responseDto.setResultDesc("取消问诊成功");
		return responseDto;
	}

	/**
	 * @description 获取当前图文咨询
	 * @param consultDto
	 * @return
	 * @throws Exception
	 */
	@Override
	public ResponseDto getCurrentConsult(ConsultDto consultDto) throws Exception {
		/** step1:空异常处理. */
		if (consultDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		/** step2:查询数据. */
		TokenDto token = CacheContainer.getToken(consultDto.getToken());
		PatientDto patient = token != null ? token.getPatient() : null;
		consultDto.setPatientId(patient != null ? patient.getId() : null);
		List<ConsultDto> list = consultDao.queryMyConsulting(consultDto);

		/** step3:返回结果. */
		ResponseDto responseDto = new ResponseDto();
		responseDto.setResultDesc("获取成功");
		responseDto.setDetail(list != null && !list.isEmpty() ? list.get(0) : null);
		return responseDto;
	}

	/**
	 * 
	 * @number
	 * @description 查询用户离线消息条数
	 * 
	 * @param chatDto
	 * @return
	 * @throws Exception
	 * @Date 2015年11月12日
	 */
	@Override
	public ResponseDto queryNoReadMessageCount(ChatDto chatDto) throws Exception {
		/** 空值判断. */
		if (chatDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		/** 获取用户. */
		TokenDto token = CacheContainer.getToken(chatDto.getToken());
		PatientDto patient = token != null ? token.getPatient() : null;

		/** 获取用户ID. */
		if (patient == null) {
			throw new BusinessException("未登录");
		}
		chatDto.setReceiver(patient.getId());

		ResponseDto responseDto = new ResponseDto();
		List<ChatDto> chats = consultDao.queryNoReadMessageCount(chatDto);
		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put("list", chats);
		responseDto.setResultDesc("查询成功");
		responseDto.setDetail(dataMap);
		return responseDto;
	}

	/**
	 * @description 更新咨询状态
	 * @param consultDto
	 * @throws Exception
	 */
	@Override
	public void updateConsultStatus(ConsultDto consultDto) throws Exception {
		consultDto.setConsultStatus(1);
		// 查询医生接收患者未评价
		List<ConsultDto> consult = consultDao.queryConsultAlreadyExpired(consultDto);

		// 更改已经过期的咨询为已结束状态
		consultDao.updateConsultStatus(consultDto);

		// 更改过期咨询的未读消息为已读
		consultDao.updateReadStatusByBatchServiceId(consultDto);

		if (consult != null && !consult.isEmpty() && consult.size() > 0) {
			// 同步聊天服务器
			RequestDto requestDto = new RequestDto();
			requestDto.setCmd("autoStopConsult");
			Map<String, Object> params = new HashMap<String, Object>();
			requestDto.setParams(params);
			params.put("consults", consult);
			URLInvoke.post(SysCfg.getString("qujiuyi.chatUrl"), Constants.gson.toJson(requestDto));
		}
	}

	/**
	 * 48小时内医生未接受的免费咨询自动消除
	 * 
	 * @param consultDto
	 * @throws Exception
	 */
	@Override
	public void updateFreeConsultNoRecive(ConsultDto consultDto) throws Exception {
		// 删除医生未接受的免费咨询
		consultDao.deleteFreeConsult(consultDto);
	}

	/**
	 * 
	 * @number @description 查询免费咨询当天剩余次数
	 * 
	 * @param consultDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2015年12月17日
	 */
	@Override
	public ResponseDto queryFreeConsultNoUseCount(ConsultDto consultDto) throws Exception {
		if (consultDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		TokenDto token = CacheContainer.getToken(consultDto.getToken());
		PatientDto patient = token != null ? token.getPatient() : new PatientDto();
		consultDto.setPatientId(patient.getId());

		ConsultDto queryDto = new ConsultDto();
		queryDto.setPatientId(patient.getId());
		queryDto.setType(0);
		queryDto.setIsCurDate(0);
		List<ConsultDto> consultList = consultDao.queryMyConsult(queryDto);

		ResponseDto responseDto = new ResponseDto();
		responseDto.setResultDesc("免费咨询剩余次数");
		Map<String, Object> detail = new HashMap<String, Object>();
		responseDto.setDetail(detail);
		detail.put("remainder", 3 - consultList.size());
		return responseDto;
	}

	/**
	 * 
	 * @number @description 处理12小时医生未接受，咨询未开始的咨询
	 * 
	 * @param consultDto
	 * @throws Exception
	 *
	 * @Date 2016年1月29日
	 */
	@Override
	public void handleConsultDoctorNoAccept() throws Exception {
		ConsultDto consultDto = new ConsultDto();
		List<ConsultDto> consults = consultDao.queryConsultDoctorNoAccept(consultDto);
		if (consults == null || consults.isEmpty()) {
			logger.info("ConsultServiceImpl.handleConsultDoctorNoAccept###--------------->>>>>>>>>暂无需处理的退款咨询");
			return;
		}

		// 循环符合条件的咨询订单退款
		for (int i = 0; i < consults.size(); i++) {
			ThirdPayOrderDto thirdPayOrderDto = new ThirdPayOrderDto();
			thirdPayOrderDto.setOutTradeNo(consults.get(i).getOutTradeNo());
			logger.info("图文咨询过期退款处理订单号：" + thirdPayOrderDto.getOutTradeNo());
			thirdPayOrderDto = thirdPayOrderDao.queryOrderDetailByOutTradeNo(thirdPayOrderDto);
			thirdPayOrderService.refund(thirdPayOrderDto);

			// 退款成功后更改咨询状态
			consultDao.updateConsultRefundSuccess(consults.get(i));

			// 短信提示
			String name = consults.get(i).getDoctorName();

			String param = String.format("#name#=%s", name + "\n");
			SmsService.instance().sendSms(consults.get(i).getPatientPhone(), "10024", param);
		}
	}
}