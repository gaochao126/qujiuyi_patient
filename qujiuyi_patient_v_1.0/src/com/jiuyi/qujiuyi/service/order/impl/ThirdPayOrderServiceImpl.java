package com.jiuyi.qujiuyi.service.order.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jiuyi.qujiuyi.common.dict.CacheContainer;
import com.jiuyi.qujiuyi.common.dict.Constants;
import com.jiuyi.qujiuyi.common.util.HttpsUtil;
import com.jiuyi.qujiuyi.common.util.IDCard;
import com.jiuyi.qujiuyi.common.util.MD5;
import com.jiuyi.qujiuyi.common.util.SysCfg;
import com.jiuyi.qujiuyi.common.util.URLInvoke;
import com.jiuyi.qujiuyi.common.util.Util;
import com.jiuyi.qujiuyi.common.util.WxRefundSSL;
import com.jiuyi.qujiuyi.dao.consult.ConsultDao;
import com.jiuyi.qujiuyi.dao.coupon.CouponDao;
import com.jiuyi.qujiuyi.dao.detail.PatientAccountDetailDao;
import com.jiuyi.qujiuyi.dao.doctor.DoctorDao;
import com.jiuyi.qujiuyi.dao.doctor.PersonalDoctorDao;
import com.jiuyi.qujiuyi.dao.order.ThirdPayOrderDao;
import com.jiuyi.qujiuyi.dao.patient.PatientDao;
import com.jiuyi.qujiuyi.dao.prescription.PrescriptionDao;
import com.jiuyi.qujiuyi.dao.prescription.PrescriptionDetailDao;
import com.jiuyi.qujiuyi.dao.refund.RefundDao;
import com.jiuyi.qujiuyi.dao.register.PatientRegisterDao;
import com.jiuyi.qujiuyi.dao.register.RegisterPlanDao;
import com.jiuyi.qujiuyi.dao.relative.PatientRelativeDao;
import com.jiuyi.qujiuyi.dao.service.ServiceDao;
import com.jiuyi.qujiuyi.daoyao.mdeicine.YaoMedicineDao;
import com.jiuyi.qujiuyi.dto.common.RequestDto;
import com.jiuyi.qujiuyi.dto.common.ResponseDto;
import com.jiuyi.qujiuyi.dto.common.TokenDto;
import com.jiuyi.qujiuyi.dto.consult.ConsultDto;
import com.jiuyi.qujiuyi.dto.coupon.CouponDto;
import com.jiuyi.qujiuyi.dto.detail.PatientAccountDetailDto;
import com.jiuyi.qujiuyi.dto.doctor.DoctorDto;
import com.jiuyi.qujiuyi.dto.doctor.PersonalDoctorDto;
import com.jiuyi.qujiuyi.dto.medicine.YaoMedicineFormatDto;
import com.jiuyi.qujiuyi.dto.order.ThirdPayOrderDto;
import com.jiuyi.qujiuyi.dto.patient.PatientDto;
import com.jiuyi.qujiuyi.dto.perscription.PrescriptionDetailDto;
import com.jiuyi.qujiuyi.dto.perscription.PrescriptionDto;
import com.jiuyi.qujiuyi.dto.refund.RefundDto;
import com.jiuyi.qujiuyi.dto.register.PatientRegisterDto;
import com.jiuyi.qujiuyi.dto.register.RegisterPlanDto;
import com.jiuyi.qujiuyi.dto.relative.PatientRelativeDto;
import com.jiuyi.qujiuyi.dto.service.ServiceDto;
import com.jiuyi.qujiuyi.service.BaseService;
import com.jiuyi.qujiuyi.service.BusinessException;
import com.jiuyi.qujiuyi.service.order.ThirdPayOrderService;
import com.jiuyi.qujiuyi.service.prescription.impl.PrescriptionStatus;
import com.qujiuyi.util.sms.SmsService;

/**
 * @description 第三方支付订单业务层实现
 * @author zhb
 * @createTime 2015年8月21日
 */
@Service
public class ThirdPayOrderServiceImpl implements ThirdPayOrderService {
	private final static Logger logger = Logger.getLogger(ThirdPayOrderServiceImpl.class);

	@Autowired
	private RegisterPlanDao registerPlanDao;

	@Autowired
	private ThirdPayOrderDao thirdPayOrderDao;

	@Autowired
	private DoctorDao doctorDao;

	@Autowired
	private PatientRegisterDao patientRegisterDao;

	@Autowired
	private PatientRelativeDao patientRelativeDao;

	@Autowired
	private CouponDao couponDao;

	@Autowired
	private PatientDao patientDao;

	@Autowired
	private ServiceDao serviceDao;

	@Autowired
	private ConsultDao consultDao;

	@Autowired
	private PersonalDoctorDao personalDoctorDao;

	@Autowired
	private PatientAccountDetailDao patientAccountDetailDao;

	@Autowired
	private RefundDao refundDao;

	@Autowired
	private PrescriptionDao prescriptionDao;

	@Autowired
	private PrescriptionDetailDao prescriptionDetailDao;

	@Autowired
	private YaoMedicineDao yaoMedicineDao;

	/**
	 * @description 创建微信支付订单
	 * @param thirdPayOrderDto
	 * @return
	 * @throws Exception
	 */
	@Override
	public ResponseDto createWeixinPayOrder(ThirdPayOrderDto thirdPayOrderDto) throws Exception {
		if (thirdPayOrderDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		thirdPayOrderDto.setOutTradeNo(Util.getUniqueSn());

		if (thirdPayOrderDto.getOrderType() != null && thirdPayOrderDto.getOrderType() == 0) {// 余额充值
			return createRechargeOrder(thirdPayOrderDto);
		} else if (thirdPayOrderDto.getOrderType() != null && thirdPayOrderDto.getOrderType() == 1) {// 购买挂号服务
			return createRegisterOrderPlus(thirdPayOrderDto);
		} else if (thirdPayOrderDto.getOrderType() != null && thirdPayOrderDto.getOrderType() == 2) {// 购买图文咨询服务
			return createConsultOrder(thirdPayOrderDto);
		} else if (thirdPayOrderDto.getOrderType() != null && thirdPayOrderDto.getOrderType() == 3) {// 购买一元诊服务
			return createYiyuanyizhenOrder(thirdPayOrderDto);
		} else if (thirdPayOrderDto.getOrderType() != null && thirdPayOrderDto.getOrderType() == 4) {// 购买私人医生服务
			return createPersonalDoctorOrder(thirdPayOrderDto);
		} else if (thirdPayOrderDto.getOrderType() != null && thirdPayOrderDto.getOrderType() == 5) {// 购买处方
			return createPrescriptionOrder(thirdPayOrderDto);
		} else {
			throw new BusinessException("未知订单类型");
		}
	}

	/**
	 * @description 创建充值订单
	 * @param thirdPayOrderDto
	 * @return
	 * @throws Exception
	 */
	private ResponseDto createRechargeOrder(ThirdPayOrderDto thirdPayOrderDto) throws Exception {
		if (thirdPayOrderDto.getPayAmount() == null) {
			throw new BusinessException("支付金额不能为空");
		}

		/** 获取用户. */
		TokenDto token = CacheContainer.getToken(thirdPayOrderDto.getToken());
		PatientDto patient = token != null ? token.getPatient() : new PatientDto();
		if (patient.getId() == null) {
			throw new BusinessException("登录异常");
		}
		thirdPayOrderDto.setPatientId(patient.getId());

		/** 设置微信预支付ID. */
		setWeixinPrepayid(thirdPayOrderDto, "791余额充值");

		/** 保存订单. */
		thirdPayOrderDto.setCreateTime(new Date());
		thirdPayOrderDto.setPayStatus(0);
		thirdPayOrderDto.setTotalAmount(thirdPayOrderDto.getPayAmount());
		thirdPayOrderDto.setPayType(1);
		saveThirdPayOrder(thirdPayOrderDto);

		/** 返回结果. */
		ResponseDto responseDto = new ResponseDto();
		Map<String, Object> detail = new HashMap<String, Object>();
		detail.put("prepayId", thirdPayOrderDto.getPrepayId());
		if (Util.isNotEmpty(thirdPayOrderDto.getPayParams())) {
			detail.put("prepayId", thirdPayOrderDto.getPrepayId());
			detail.put("outTradeNo", thirdPayOrderDto.getOutTradeNo());
			detail.put("paySign", MD5.getMD5Code(thirdPayOrderDto.getPayParams().replace("?", thirdPayOrderDto.getPrepayId()) + "&key=" + SysCfg.getString("weixin.pay.key")));
		}
		responseDto.setDetail(detail);
		return responseDto;
	}

	/**
	 * @description 创建挂号订单
	 * @param thirdPayOrderDto
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	private ResponseDto createRegisterOrder(ThirdPayOrderDto thirdPayOrderDto) throws Exception {
		if (thirdPayOrderDto.getServiceId() == null) {
			throw new BusinessException("服务ID不能为空");
		}

		if (thirdPayOrderDto.getPatientRelativeId() == null) {
			throw new BusinessException("常用就诊人id不能为空");
		}

		RegisterPlanDto registerPlanDto = new RegisterPlanDto();
		registerPlanDto.setId(Integer.parseInt(thirdPayOrderDto.getServiceId()));
		registerPlanDto = registerPlanDao.getRegisterPlanById(registerPlanDto);
		if (registerPlanDto == null) {
			throw new BusinessException("服务不存在");
		}

		// 判断是否已经存在该挂号
		List<ThirdPayOrderDto> registerOrder = thirdPayOrderDao.queryThirdPayOrderByPlanIdAndRelativeId(thirdPayOrderDto);
		if (registerOrder != null && !registerOrder.isEmpty() && registerOrder.size() > 0) {
			throw new BusinessException("您已存在该挂号，请查看订单");
		}

		if (registerPlanDto.getStatus() == null || registerPlanDto.getStatus() != 1) {
			throw new BusinessException("此服务已关闭");
		}

		if (registerPlanDto.getRegisterCount() == null || registerPlanDto.getRegisterCount() < 1) {
			throw new BusinessException("服务已售完");
		}

		if (registerPlanDto.getPrice() == null) {
			throw new BusinessException("服务价格未知");
		}

		if (!registerPlanDto.getPrice().equals(thirdPayOrderDto.getServicePrice())) {
			throw new BusinessException("服务价格已变,请刷新后再试");
		}

		/** 获取用户. */
		TokenDto token = CacheContainer.getToken(thirdPayOrderDto.getToken());
		PatientDto patient = token != null ? token.getPatient() : new PatientDto();
		if (patient.getId() == null) {
			throw new BusinessException("登录异常");
		}

		/** 获取优惠券. */
		CouponDto couponDto = null;
		if (thirdPayOrderDto.getCouponId() != null) {
			CouponDto queryCouponDto = new CouponDto();
			queryCouponDto.setId(thirdPayOrderDto.getCouponId());
			couponDto = couponDao.queryCouponsById(queryCouponDto);
			if (couponDto == null) {
				throw new BusinessException("优惠券不存在");
			}
			if (couponDto != null && couponDto.getStatus() != 0) {
				throw new BusinessException("优惠券已被使用");
			}
			if (couponDto != null && couponDto.getStatus() == 0 && couponDto.getExpireTime() != null && couponDto.getExpireTime().getTime() < System.currentTimeMillis()) {
				throw new BusinessException("优惠券已过期");
			}
			if (couponDto.getAmount().doubleValue() >= registerPlanDto.getPrice()) {
				// 更新优惠券使用状态
				couponDto.setStatus(1);
				couponDao.updateCouponStatus(couponDto);

				// 保存订单
				thirdPayOrderDto.setCreateTime(new Date());
				thirdPayOrderDto.setPayTime(new Date());
				thirdPayOrderDto.setPayStatus(1);
				thirdPayOrderDto.setPatientId(patient.getId());
				thirdPayOrderDto.setDoctorId(registerPlanDto.getDoctorId());
				thirdPayOrderDto.setTotalAmount(registerPlanDto.getPrice());
				thirdPayOrderDto.setUseBalance(null);
				thirdPayOrderDto.setPayType(1);
				saveThirdPayOrder(thirdPayOrderDto);

				// 创建挂号记录
				createRegister(thirdPayOrderDto);

				registerPlanDao.updateAlreadyRegisterCountUp(registerPlanDto);
				// 保存收支明细
				PatientAccountDetailDto patientAccountDetailDto = new PatientAccountDetailDto();
				patientAccountDetailDto.setPatientId(thirdPayOrderDto.getPatientId());
				patientAccountDetailDto.setType(thirdPayOrderDto.getOrderType());
				patientAccountDetailDto.setTransactionNum(thirdPayOrderDto.getOutTradeNo());
				patientAccountDetailDto.setAmount(thirdPayOrderDto.getTotalAmount());
				patientAccountDetailDto.setCreateTime(new Date());
				patientAccountDetailDao.savePatientAccountDetail(patientAccountDetailDto);

				ResponseDto responseDto = new ResponseDto();
				Map<String, Object> detail = new HashMap<String, Object>();
				detail.put("payStatus", "1");
				detail.put("outTradeNo", thirdPayOrderDto.getOutTradeNo());
				responseDto.setResultDesc("您已成功使用优惠券支付挂号订单");
				responseDto.setDetail(detail);
				return responseDto;
			}
		}

		/** 余额判断. */
		patient = patientDao.queryPatientById(patient);
		patient.setBalance(patient.getBalance() == null ? 0 : patient.getBalance());
		if (thirdPayOrderDto.getUseBalance() != null) {
			if (thirdPayOrderDto.getUseBalance() > patient.getBalance()) {
				throw new BusinessException("余额不足");
			}
			if ((couponDto != null ? couponDto.getAmount().doubleValue() : 0) + thirdPayOrderDto.getUseBalance() >= registerPlanDto.getPrice()) {
				// 更新优惠券使用状态
				if (couponDto != null) {
					couponDto.setStatus(1);
					couponDao.updateCouponStatus(couponDto);
				}

				// 更新个人余额
				patient.setBalance(patient.getBalance() - (registerPlanDto.getPrice() - (couponDto != null ? couponDto.getAmount().doubleValue() : 0)));
				patientDao.updateBalance(patient);
				CacheContainer.getToken(thirdPayOrderDto.getToken()).getPatient().setBalance(patient.getBalance());

				// 保存订单
				thirdPayOrderDto.setCreateTime(new Date());
				thirdPayOrderDto.setPayTime(new Date());
				thirdPayOrderDto.setPayStatus(1);
				thirdPayOrderDto.setPatientId(patient.getId());
				thirdPayOrderDto.setDoctorId(registerPlanDto.getDoctorId());
				thirdPayOrderDto.setTotalAmount(registerPlanDto.getPrice());
				thirdPayOrderDto.setUseBalance(registerPlanDto.getPrice() - (couponDto != null ? couponDto.getAmount().doubleValue() : 0));
				thirdPayOrderDto.setPayType(1);
				saveThirdPayOrder(thirdPayOrderDto);

				// 创建挂号记录
				createRegister(thirdPayOrderDto);

				registerPlanDao.updateAlreadyRegisterCountUp(registerPlanDto);
				// 保存收支明细
				PatientAccountDetailDto patientAccountDetailDto = new PatientAccountDetailDto();
				patientAccountDetailDto.setPatientId(thirdPayOrderDto.getPatientId());
				patientAccountDetailDto.setType(thirdPayOrderDto.getOrderType());
				patientAccountDetailDto.setTransactionNum(thirdPayOrderDto.getOutTradeNo());
				patientAccountDetailDto.setAmount(thirdPayOrderDto.getTotalAmount());
				patientAccountDetailDto.setCreateTime(new Date());
				patientAccountDetailDao.savePatientAccountDetail(patientAccountDetailDto);

				ResponseDto responseDto = new ResponseDto();
				Map<String, Object> detail = new HashMap<String, Object>();
				detail.put("payStatus", "1");
				detail.put("outTradeNo", thirdPayOrderDto.getOutTradeNo());
				responseDto.setResultDesc("您已使用优惠券和余额成功支付挂号订单");
				responseDto.setDetail(detail);
				return responseDto;
			}
		}

		/** 设置微信预支付ID. */
		thirdPayOrderDto.setPayAmount(registerPlanDto.getPrice() - (couponDto != null ? couponDto.getAmount().doubleValue() : 0) - (thirdPayOrderDto.getUseBalance() != null ? thirdPayOrderDto.getUseBalance() : 0));
		setWeixinPrepayid(thirdPayOrderDto, "791门诊预约服务");

		/** 保存订单. */
		thirdPayOrderDto.setCreateTime(new Date());
		thirdPayOrderDto.setPayStatus(0);
		thirdPayOrderDto.setTotalAmount(registerPlanDto.getPrice());
		thirdPayOrderDto.setPatientId(patient.getId());
		thirdPayOrderDto.setDoctorId(registerPlanDto.getDoctorId());
		thirdPayOrderDto.setPayType(1);
		saveThirdPayOrder(thirdPayOrderDto);

		// 更新挂号成功后已挂号数量
		registerPlanDao.updateAlreadyRegisterCountUp(registerPlanDto);

		/** 返回结果. */
		ResponseDto responseDto = new ResponseDto();
		Map<String, Object> detail = new HashMap<String, Object>();
		detail.put("prepayId", thirdPayOrderDto.getPrepayId());
		detail.put("outTradeNo", thirdPayOrderDto.getOutTradeNo());
		if (Util.isNotEmpty(thirdPayOrderDto.getPayParams())) {
			detail.put("paySign", MD5.getMD5Code(thirdPayOrderDto.getPayParams().replace("?", thirdPayOrderDto.getPrepayId()) + "&key=" + SysCfg.getString("weixin.pay.key")));
		}
		detail.put("payStatus", "0");
		responseDto.setResultDesc("创建成功，请在15分钟之内支付");
		responseDto.setDetail(detail);
		return responseDto;
	}

	/**
	 * 
	 * @number @description 创建挂号订单，院方获取数据
	 * 
	 * @param thirdPayOrderDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2016年1月23日
	 */
	public ResponseDto createRegisterOrderPlus(ThirdPayOrderDto thirdPayOrderDto) throws Exception {
		if (thirdPayOrderDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}
		if (thirdPayOrderDto.getServiceId() == null) {
			throw new BusinessException("号源ID不能为空");
		}

		if (thirdPayOrderDto.getPatientRelativeId() == null) {
			throw new BusinessException("常用就诊人id不能为空");
		}

		if (thirdPayOrderDto.getHospitalId() == null) {
			throw new BusinessException("医院id不能为空");
		}

		if (thirdPayOrderDto.getDoctorId() == null) {
			throw new BusinessException("请输入医生id");
		}
		if (thirdPayOrderDto.getPayMode() == null) {
			throw new BusinessException("请指定支付方式");
		}

		// 根据号源ID查询号源详情
		BaseService bservice = new BaseService();
		bservice.getParams().put("hospitalId", thirdPayOrderDto.getHospitalId().toString());
		bservice.getParams().put("doctorId", thirdPayOrderDto.getDoctorId().toString());
		bservice.getParams().put("numSourceId", thirdPayOrderDto.getServiceId());
		bservice.packageData("getNumSource", RegisterPlanDto.class, SysCfg.getString("register.plus.url"));
		if (!bservice.isSuccess()) {
			return new ResponseDto(Integer.parseInt(bservice.getCode()), bservice.getDesc(), null);
		}
		@SuppressWarnings("unchecked")
		List<RegisterPlanDto> registerPlan = (List<RegisterPlanDto>) bservice.getDataList();
		if (registerPlan == null || registerPlan.isEmpty()) {
			throw new BusinessException("号源不存在");
		}

		thirdPayOrderDto.setVisitCost(registerPlan.get(0).getVisitCost());
		thirdPayOrderDto.setTimeRange(registerPlan.get(0).getTimeRange());
		thirdPayOrderDto.setScheduleDate(registerPlan.get(0).getScheduleDate());
		thirdPayOrderDto.setNumSourceId(thirdPayOrderDto.getServiceId());
		thirdPayOrderDto.setStartTime(registerPlan.get(0).getStartTime());
		thirdPayOrderDto.setEndTime(registerPlan.get(0).getEndTime());

		/** 获取用户. */
		TokenDto token = CacheContainer.getToken(thirdPayOrderDto.getToken());
		PatientDto patient = token != null ? token.getPatient() : new PatientDto();
		if (patient.getId() == null) {
			throw new BusinessException("登录异常");
		}
		thirdPayOrderDto.setPatientId(patient.getId());

		/** 获取常用就诊人信息. */
		PatientRelativeDto ratientRelativeDto = new PatientRelativeDto();
		ratientRelativeDto.setId(thirdPayOrderDto.getPatientRelativeId());
		ratientRelativeDto = patientRelativeDao.getPatientRelativeById(ratientRelativeDto);
		if (ratientRelativeDto == null) {
			throw new BusinessException("就诊人不存在");
		}

		/** 查询患者挂号记录，判断是否已挂过此号 */
		PatientRegisterDto patientRegisterDto = new PatientRegisterDto();
		patientRegisterDto.setPatientId(patient.getId());
		patientRegisterDto.setNumSourceId(thirdPayOrderDto.getServiceId());
		patientRegisterDto.setCertificateNumber(ratientRelativeDto.getCertificateNumber());
		patientRegisterDto.setStatus(0);
		PatientRegisterDto patientReg = patientRegisterDao.queryRegisterDetailPlus(patientRegisterDto);
		if (patientReg != null) {
			throw new BusinessException("该就诊人已挂该号源，请更换就诊人");
		}

		/**
		 * 免费
		 */
		if (registerPlan.get(0).getVisitCost() == null || registerPlan.get(0).getVisitCost().equals(new BigDecimal(0))) {
			// 保存订单
			thirdPayOrderDto.setCreateTime(new Date());
			thirdPayOrderDto.setPayTime(new Date());
			thirdPayOrderDto.setPayStatus(1);
			thirdPayOrderDto.setDoctorId(thirdPayOrderDto.getDoctorId());
			thirdPayOrderDto.setTotalAmount(thirdPayOrderDto.getVisitCost().doubleValue());
			thirdPayOrderDto.setUseBalance(null);
			saveThirdPayOrder(thirdPayOrderDto);
			thirdPayOrderDto.setPayType(3);
			// 创建挂号记录
			createRegister(thirdPayOrderDto);

			// 请求院方接口，修改患者挂号记录
			BaseService commitOrder = new BaseService();
			commitOrder.getParams().put("hospitalId", thirdPayOrderDto.getHospitalId().toString());
			commitOrder.getParams().put("numSourceId", thirdPayOrderDto.getServiceId());
			commitOrder.getParams().put("payMode", thirdPayOrderDto.getPayMode().toString());
			commitOrder.getParams().put("userName", thirdPayOrderDto.getPatientName());
			commitOrder.getParams().put("userCardType", "1");// 1身份证
			commitOrder.getParams().put("userCardId", thirdPayOrderDto.getCertificateNumber());
			commitOrder.getParams().put("userSex", thirdPayOrderDto.getGender().toString());
			commitOrder.getParams().put("userBirthday", Util.DateToStr(IDCard.getBirthdayByCard(thirdPayOrderDto.getCertificateNumber())));
			commitOrder.getParams().put("userPhone", thirdPayOrderDto.getPatientPhone());

			commitOrder.packageData("commitOrder", PatientRegisterDto.class, SysCfg.getString("register.plus.url"));
			if (!commitOrder.isSuccess()) {
				throw new BusinessException(commitOrder.getDesc());
			}

			PatientRegisterDto patientRegister = (PatientRegisterDto) commitOrder.getDataObj();
			patientRegister.setOutTradeNo(thirdPayOrderDto.getOutTradeNo());
			logger.info("ThirdPayOrder.register.paymode=2###visitNo:" + patientRegister.getVisitNo() + ";billNo:" + patientRegister.getBillNo());
			patientRegisterDao.updatePatientRegister(patientRegister);

			// 短信提示
			String name = thirdPayOrderDto.getPatientName();
			String date = new SimpleDateFormat("MM月dd日").format(thirdPayOrderDto.getScheduleDate()) + Util.getReange(registerPlan.get(0).getTimeRange());// 获得日期
			String doctor = thirdPayOrderDto.getDepartmentName() + thirdPayOrderDto.getDoctorName() + thirdPayOrderDto.getDoctorTitleName();
			String hospital = thirdPayOrderDto.getHospitalName();
			String param = String.format("#name#=%s&#doctor#=%s&#date#=%s&#hospital#=%s", name + "\n", doctor + "\n", date + "\n", hospital + "\n");
			SmsService.instance().sendSms(thirdPayOrderDto.getPatientPhone(), "9822", param);

			// 终端提示
			RequestDto requestDto = new RequestDto();
			requestDto.setCmd("sendSystemMsg");
			Map<String, Object> params = new HashMap<String, Object>();
			Map<String, String> content = new HashMap<String, String>();
			content.put("cmd", "createWeixinPayOrder");
			content.put("message", Util.getRegisterSms(name, doctor, date, hospital));
			requestDto.setParams(params);
			params.put("target", patient.getId());
			params.put("targetType", 1);
			params.put("summary", "恭喜您挂号成功");
			params.put("content", Constants.gson.toJson(content));
			params.put("weixinMsg", Util.getRegisterSms(name, doctor, date, hospital));
			URLInvoke.post(SysCfg.getString("qujiuyi.chatUrl"), Constants.gson.toJson(requestDto));

			ResponseDto responseDto = new ResponseDto();
			responseDto.setResultDesc("挂号成功，到院支付");
			patientRegister.setPayStatus(1);
			responseDto.setDetail(patientRegister);
			return responseDto;
		}

		/**
		 * 到院支付.
		 * 
		 * 到院支付只存患者挂号记录，不产生订单信息
		 * 
		 */
		if (thirdPayOrderDto.getPayMode() == 2) {
			// 创建挂号记录
			createRegister(thirdPayOrderDto);

			// 请求院方接口，修改患者挂号记录
			BaseService commitOrder = new BaseService();
			commitOrder.getParams().put("hospitalId", thirdPayOrderDto.getHospitalId().toString());
			commitOrder.getParams().put("numSourceId", thirdPayOrderDto.getServiceId());
			commitOrder.getParams().put("payMode", thirdPayOrderDto.getPayMode().toString());
			commitOrder.getParams().put("userName", thirdPayOrderDto.getPatientName());
			commitOrder.getParams().put("userCardType", "1");// 1身份证
			commitOrder.getParams().put("userCardId", thirdPayOrderDto.getCertificateNumber());
			commitOrder.getParams().put("userSex", thirdPayOrderDto.getGender().toString());
			commitOrder.getParams().put("userBirthday", Util.DateToStr(IDCard.getBirthdayByCard(thirdPayOrderDto.getCertificateNumber())));
			commitOrder.getParams().put("userPhone", thirdPayOrderDto.getPatientPhone());

			commitOrder.packageData("commitOrder", PatientRegisterDto.class, SysCfg.getString("register.plus.url"));
			if (!commitOrder.isSuccess()) {
				throw new BusinessException(commitOrder.getDesc());
			}

			PatientRegisterDto patientRegister = (PatientRegisterDto) commitOrder.getDataObj();
			patientRegister.setOutTradeNo(thirdPayOrderDto.getOutTradeNo());
			logger.info("ThirdPayOrder.register.paymode=2###visitNo:" + patientRegister.getVisitNo() + ";billNo:" + patientRegister.getBillNo());
			patientRegisterDao.updatePatientRegister(patientRegister);

			// 短信提示
			String name = thirdPayOrderDto.getPatientName();
			String date = new SimpleDateFormat("MM月dd日").format(thirdPayOrderDto.getScheduleDate()) + Util.getReange(registerPlan.get(0).getTimeRange());// 获得日期
			String doctor = thirdPayOrderDto.getDepartmentName() + thirdPayOrderDto.getDoctorName() + thirdPayOrderDto.getDoctorTitleName();
			String hospital = thirdPayOrderDto.getHospitalName();
			String param = String.format("#name#=%s&#doctor#=%s&#date#=%s&#hospital#=%s", name + "\n", doctor + "\n", date + "\n", hospital + "\n");
			SmsService.instance().sendSms(thirdPayOrderDto.getPatientPhone(), "9822", param);

			// 终端提示
			RequestDto requestDto = new RequestDto();
			requestDto.setCmd("sendSystemMsg");
			Map<String, Object> params = new HashMap<String, Object>();
			Map<String, String> content = new HashMap<String, String>();
			content.put("cmd", "createWeixinPayOrder");
			content.put("message", Util.getRegisterSms(name, doctor, date, hospital));
			requestDto.setParams(params);
			params.put("target", patient.getId());
			params.put("targetType", 1);
			params.put("summary", "恭喜您挂号成功");
			params.put("content", Constants.gson.toJson(content));
			params.put("weixinMsg", Util.getRegisterSms(name, doctor, date, hospital));
			URLInvoke.post(SysCfg.getString("qujiuyi.chatUrl"), Constants.gson.toJson(requestDto));

			ResponseDto responseDto = new ResponseDto();
			responseDto.setResultDesc("挂号成功，到院支付");
			patientRegister.setPayStatus(1);
			responseDto.setDetail(patientRegister);
			return responseDto;
		}
		/** 获取优惠券. */
		CouponDto couponDto = null;
		if (thirdPayOrderDto.getCouponId() != null) {
			CouponDto queryCouponDto = new CouponDto();
			queryCouponDto.setId(thirdPayOrderDto.getCouponId());
			couponDto = couponDao.queryCouponsById(queryCouponDto);
			if (couponDto == null) {
				throw new BusinessException("优惠券不存在");
			}
			if (couponDto != null && couponDto.getStatus() != 0) {
				throw new BusinessException("优惠券已被使用");
			}
			if (couponDto != null && couponDto.getStatus() == 0 && couponDto.getExpireTime() != null && couponDto.getExpireTime().getTime() < System.currentTimeMillis()) {
				throw new BusinessException("优惠券已过期");
			}
			if (couponDto.getAmount().doubleValue() >= thirdPayOrderDto.getVisitCost().doubleValue()) {
				// 更新优惠券使用状态
				couponDto.setStatus(1);
				couponDao.updateCouponStatus(couponDto);
				// 保存订单
				thirdPayOrderDto.setCreateTime(new Date());
				thirdPayOrderDto.setPayTime(new Date());
				thirdPayOrderDto.setPayStatus(1);
				thirdPayOrderDto.setDoctorId(thirdPayOrderDto.getDoctorId());
				thirdPayOrderDto.setTotalAmount(thirdPayOrderDto.getVisitCost().doubleValue());
				thirdPayOrderDto.setUseBalance(null);
				thirdPayOrderDto.setPayType(1);
				saveThirdPayOrder(thirdPayOrderDto);
				// 创建挂号记录
				createRegister(thirdPayOrderDto);
				// 保存收支明细
				PatientAccountDetailDto patientAccountDetailDto = new PatientAccountDetailDto();
				patientAccountDetailDto.setPatientId(thirdPayOrderDto.getPatientId());
				patientAccountDetailDto.setType(thirdPayOrderDto.getOrderType());
				patientAccountDetailDto.setTransactionNum(thirdPayOrderDto.getOutTradeNo());
				patientAccountDetailDto.setAmount(thirdPayOrderDto.getTotalAmount());
				patientAccountDetailDto.setCreateTime(new Date());
				patientAccountDetailDao.savePatientAccountDetail(patientAccountDetailDto);

				// 请求院方接口，修改患者挂号记录
				BaseService commitOrder = new BaseService();
				commitOrder.getParams().put("hospitalId", thirdPayOrderDto.getHospitalId().toString());
				commitOrder.getParams().put("numSourceId", thirdPayOrderDto.getServiceId());
				commitOrder.getParams().put("payMode", thirdPayOrderDto.getPayMode().toString());
				commitOrder.getParams().put("userName", thirdPayOrderDto.getPatientName());
				commitOrder.getParams().put("userCardType", "1");// 1身份证
				commitOrder.getParams().put("userCardId", thirdPayOrderDto.getCertificateNumber());
				commitOrder.getParams().put("userSex", thirdPayOrderDto.getGender().toString());
				commitOrder.getParams().put("userBirthday", Util.DateToStr(IDCard.getBirthdayByCard(thirdPayOrderDto.getCertificateNumber())));
				commitOrder.getParams().put("userPhone", thirdPayOrderDto.getPatientPhone());
				commitOrder.packageData("commitOrder", PatientRegisterDto.class, SysCfg.getString("register.plus.url"));
				if (!commitOrder.isSuccess()) {
					throw new BusinessException(commitOrder.getDesc());
				}
				PatientRegisterDto patientRegister = (PatientRegisterDto) commitOrder.getDataObj();
				patientRegister.setOutTradeNo(thirdPayOrderDto.getOutTradeNo());
				logger.info("ThirdPayOrder.register###visitNo:" + patientRegister.getVisitNo() + ";billNo:" + patientRegister.getBillNo());
				patientRegisterDao.updatePatientRegister(patientRegister);

				// 短信提示
				String name = thirdPayOrderDto.getPatientName();
				String date = new SimpleDateFormat("MM月dd日").format(thirdPayOrderDto.getScheduleDate()) + Util.getReange(registerPlan.get(0).getTimeRange());// 获得日期
				String doctor = thirdPayOrderDto.getDepartmentName() + thirdPayOrderDto.getDoctorName() + thirdPayOrderDto.getDoctorTitleName();
				String hospital = thirdPayOrderDto.getHospitalName();
				String param = String.format("#name#=%s&#doctor#=%s&#date#=%s&#hospital#=%s", name + "\n", doctor + "\n", date + "\n", hospital + "\n");
				SmsService.instance().sendSms(thirdPayOrderDto.getPatientPhone(), "9822", param);

				// 终端提示
				RequestDto requestDto = new RequestDto();
				requestDto.setCmd("sendSystemMsg");
				Map<String, Object> params = new HashMap<String, Object>();
				Map<String, String> content = new HashMap<String, String>();
				content.put("cmd", "createWeixinPayOrder");
				content.put("message", Util.getRegisterSms(name, doctor, date, hospital));
				requestDto.setParams(params);
				params.put("target", patient.getId());
				params.put("targetType", 1);
				params.put("summary", "恭喜您挂号成功");
				params.put("content", Constants.gson.toJson(content));
				params.put("weixinMsg", Util.getRegisterSms(name, doctor, date, hospital));
				URLInvoke.post(SysCfg.getString("qujiuyi.chatUrl"), Constants.gson.toJson(requestDto));

				ResponseDto responseDto = new ResponseDto();
				Map<String, Object> detail = new HashMap<String, Object>();
				detail.put("payStatus", "1");
				detail.put("outTradeNo", thirdPayOrderDto.getOutTradeNo());
				detail.put("visitNo", patientRegister.getVisitNo());
				detail.put("billNo", patientRegister.getBillNo());
				responseDto.setResultDesc("您已成功使用优惠券支付挂号订单");
				responseDto.setDetail(detail);
				return responseDto;
			}
		}

		/** 设置微信预支付ID. */
		thirdPayOrderDto.setPayAmount(thirdPayOrderDto.getVisitCost().doubleValue() - (couponDto != null ? couponDto.getAmount().doubleValue() : 0));
		logger.info("==================================================visitCost:" + thirdPayOrderDto.getVisitCost().doubleValue());
		if (couponDto != null) {
			logger.info("==================================================couponAmount:" + couponDto.getAmount());
		}

		logger.info("==================================================payAmount:" + thirdPayOrderDto.getPayAmount());
		setWeixinPrepayid(thirdPayOrderDto, "791门诊预约服务");

		/** 保存订单. */
		thirdPayOrderDto.setCreateTime(new Date());
		thirdPayOrderDto.setPayStatus(0);
		thirdPayOrderDto.setTotalAmount(thirdPayOrderDto.getVisitCost().doubleValue());
		thirdPayOrderDto.setPatientId(patient.getId());
		thirdPayOrderDto.setDoctorId(thirdPayOrderDto.getDoctorId());
		thirdPayOrderDto.setPayType(1);
		saveThirdPayOrder(thirdPayOrderDto);

		/** 返回结果. */
		ResponseDto responseDto = new ResponseDto();
		Map<String, Object> detail = new HashMap<String, Object>();
		detail.put("prepayId", thirdPayOrderDto.getPrepayId());
		detail.put("outTradeNo", thirdPayOrderDto.getOutTradeNo());
		if (Util.isNotEmpty(thirdPayOrderDto.getPayParams())) {
			detail.put("paySign", MD5.getMD5Code(thirdPayOrderDto.getPayParams().replace("?", thirdPayOrderDto.getPrepayId()) + "&key=" + SysCfg.getString("weixin.pay.key")));
		}
		detail.put("payStatus", "0");
		responseDto.setResultDesc("创建成功，请在15分钟之内支付");
		responseDto.setDetail(detail);
		return responseDto;
	}

	/**
	 * @description 创建咨询订单
	 * @param thirdPayOrderDto
	 * @return
	 * @throws Exception
	 */
	private ResponseDto createConsultOrder(ThirdPayOrderDto thirdPayOrderDto) throws Exception {
		if (thirdPayOrderDto.getServiceId() == null) {
			throw new BusinessException("服务ID不能为空");
		}

		TokenDto token = CacheContainer.getToken(thirdPayOrderDto.getToken());
		PatientDto patient = token != null ? token.getPatient() : new PatientDto();
		if (patient.getId() == null) {
			throw new BusinessException("登录异常");
		}

		// 获取咨询记录
		ConsultDto consultDto = new ConsultDto();
		consultDto.setId(thirdPayOrderDto.getServiceId());
		consultDto = consultDao.queryConsultById(consultDto);
		if (consultDto == null) {
			throw new BusinessException("找不到咨询记录");
		}

		// 判判图文咨询是否已支付
		if (consultDto.getPayStatus() != null && consultDto.getPayStatus() == 1) {
			throw new BusinessException("此服务您已支付");
		}

		// 判断服务是否已开通
		ServiceDto serviceDto = new ServiceDto();
		serviceDto.setDoctorId(consultDto.getDoctorId());
		List<ServiceDto> list = serviceDao.queryConsultServiceByDoctorId(serviceDto);
		serviceDto = list != null && !list.isEmpty() ? list.get(0) : null;
		if (serviceDto == null || serviceDto.getStatus() != 1) {
			throw new BusinessException("此服务暂未开通");
		}

		CouponDto couponDto = null;
		if (thirdPayOrderDto.getCouponId() != null) {
			CouponDto queryCouponDto = new CouponDto();
			queryCouponDto.setId(thirdPayOrderDto.getCouponId());
			couponDto = couponDao.queryCouponsById(queryCouponDto);
			if (couponDto == null) {
				throw new BusinessException("优惠券不存在");
			}
			if (couponDto != null && couponDto.getStatus() != 0) {
				throw new BusinessException("优惠券已被使用");
			}
			if (couponDto != null && couponDto.getStatus() == 0 && couponDto.getExpireTime() != null && couponDto.getExpireTime().getTime() < System.currentTimeMillis()) {
				throw new BusinessException("优惠券已过期");
			}
			if (couponDto.getAmount().doubleValue() >= serviceDto.getPrice()) {
				// 更新优惠券使用状态
				couponDto.setStatus(1);
				couponDao.updateCouponStatus(couponDto);

				// 保存订单
				thirdPayOrderDto.setCreateTime(new Date());
				thirdPayOrderDto.setPayTime(new Date());
				thirdPayOrderDto.setPayStatus(1);
				thirdPayOrderDto.setPatientId(patient.getId());
				thirdPayOrderDto.setDoctorId(serviceDto.getDoctorId());
				thirdPayOrderDto.setTotalAmount(serviceDto.getPrice().doubleValue());
				thirdPayOrderDto.setUseBalance(null);
				thirdPayOrderDto.setPayType(1);
				saveThirdPayOrder(thirdPayOrderDto);

				// 更新图文咨询状态
				consultDto.setPayStatus(Constants.OrderSatus.ORDER_STATUS_1);
				consultDto.setOutTradeNo(thirdPayOrderDto.getOutTradeNo());
				consultDao.updatePayStatus(consultDto);

				// 保存收支明细
				PatientAccountDetailDto patientAccountDetailDto = new PatientAccountDetailDto();
				patientAccountDetailDto.setPatientId(thirdPayOrderDto.getPatientId());
				patientAccountDetailDto.setType(thirdPayOrderDto.getOrderType());
				patientAccountDetailDto.setTransactionNum(thirdPayOrderDto.getOutTradeNo());
				patientAccountDetailDto.setAmount(thirdPayOrderDto.getTotalAmount());
				patientAccountDetailDto.setCreateTime(new Date());
				patientAccountDetailDao.savePatientAccountDetail(patientAccountDetailDto);

				// 通知医生就诊
				RequestDto requestDto = new RequestDto();
				requestDto.setCmd("consultRequest");
				requestDto.setToken(thirdPayOrderDto.getToken());
				Map<String, Object> params = new HashMap<String, Object>();
				requestDto.setParams(params);
				params.put("sender", thirdPayOrderDto.getPatientId());
				params.put("receiver", thirdPayOrderDto.getDoctorId());
				params.put("serviceId", thirdPayOrderDto.getServiceId());
				URLInvoke.post(SysCfg.getString("qujiuyi.chatUrl"), Constants.gson.toJson(requestDto));

				ResponseDto responseDto = new ResponseDto();
				Map<String, Object> detail = new HashMap<String, Object>();
				detail.put("payStatus", "1");
				detail.put("outTradeNo", thirdPayOrderDto.getOutTradeNo());
				detail.put("serviceId", thirdPayOrderDto.getServiceId());
				responseDto.setResultDesc("创建成功");
				responseDto.setDetail(detail);
				return responseDto;
			}
		}

		/** 设置微信预支付ID. */
		thirdPayOrderDto.setPayAmount(serviceDto.getPrice() - (couponDto != null ? couponDto.getAmount().doubleValue() : 0));
		setWeixinPrepayid(thirdPayOrderDto, "791图文咨询服务");

		/** 保存订单. */
		thirdPayOrderDto.setCreateTime(new Date());
		thirdPayOrderDto.setPayStatus(0);
		thirdPayOrderDto.setTotalAmount(serviceDto.getPrice().doubleValue());
		thirdPayOrderDto.setPatientId(patient.getId());
		thirdPayOrderDto.setDoctorId(serviceDto.getDoctorId());
		thirdPayOrderDto.setPayType(1);
		saveThirdPayOrder(thirdPayOrderDto);

		// 返回结果
		ResponseDto responseDto = new ResponseDto();
		Map<String, Object> detail = new HashMap<String, Object>();
		detail.put("prepayId", thirdPayOrderDto.getPrepayId());
		detail.put("outTradeNo", thirdPayOrderDto.getOutTradeNo());
		detail.put("payStatus", "0");
		detail.put("serviceId", thirdPayOrderDto.getServiceId());
		if (Util.isNotEmpty(thirdPayOrderDto.getPayParams())) {
			detail.put("paySign", MD5.getMD5Code(thirdPayOrderDto.getPayParams().replace("?", thirdPayOrderDto.getPrepayId()) + "&key=" + SysCfg.getString("weixin.pay.key")));
		}
		responseDto.setResultDesc("创建成功");
		responseDto.setDetail(detail);
		return responseDto;
	}

	/**
	 * @description 创建一元义诊订单
	 * @param thirdPayOrderDto
	 * @return
	 * @throws Exception
	 */
	private ResponseDto createYiyuanyizhenOrder(ThirdPayOrderDto thirdPayOrderDto) throws Exception {
		if (thirdPayOrderDto.getServiceId() == null) {
			throw new BusinessException("服务ID不能为空");
		}

		TokenDto token = CacheContainer.getToken(thirdPayOrderDto.getToken());
		PatientDto patient = token != null ? token.getPatient() : new PatientDto();
		if (patient.getId() == null) {
			throw new BusinessException("登录异常");
		}

		// 获取咨询记录
		ConsultDto consultDto = new ConsultDto();
		consultDto.setId(thirdPayOrderDto.getServiceId());
		consultDto = consultDao.queryConsultById(consultDto);
		if (consultDto == null) {
			throw new BusinessException("找不到咨询记录");
		}

		// 判判图文咨询是否已支付
		if (consultDto.getPayStatus() != null && consultDto.getPayStatus() == 1) {
			throw new BusinessException("此服务您已支付");
		}

		// 判断服务是否已开通
		ServiceDto serviceDto = new ServiceDto();
		serviceDto = new ServiceDto();
		serviceDto.setDoctorId(consultDto.getDoctorId());
		List<ServiceDto> list = serviceDao.queryConsultServiceByDoctorId(serviceDto);
		serviceDto = list != null && !list.isEmpty() ? list.get(0) : null;
		if (serviceDto == null || serviceDto.getStatus() != 1) {
			throw new BusinessException("此服务暂未开通");
		}

		// 判断一元义诊服务是否已开启
		DoctorDto doctorDto = new DoctorDto();
		doctorDto.setId(consultDto.getDoctorId());
		doctorDto = doctorDao.queryOneYuanDoctorById(doctorDto);
		if (doctorDto == null) {
			throw new BusinessException("该医生未开启一元义诊服务");
		}

		// 判断一元义诊名额是否已满
		if (doctorDto.getYiyuanyizhenNumber() == null || doctorDto.getYiyuanyizhenNumber() == 0) {
			throw new BusinessException("一元义诊名额已使用完");
		}

		CouponDto couponDto = null;
		if (thirdPayOrderDto.getCouponId() != null) {
			CouponDto queryCouponDto = new CouponDto();
			queryCouponDto.setId(thirdPayOrderDto.getCouponId());
			couponDto = couponDao.queryCouponsById(queryCouponDto);
			if (couponDto == null) {
				throw new BusinessException("优惠券不存在");
			}
			if (couponDto != null && couponDto.getStatus() != 0) {
				throw new BusinessException("优惠券已被使用");
			}
			if (couponDto != null && couponDto.getStatus() == 0 && couponDto.getExpireTime() != null && couponDto.getExpireTime().getTime() < System.currentTimeMillis()) {
				throw new BusinessException("优惠券已过期");
			}
			if (couponDto.getAmount().doubleValue() >= serviceDto.getPrice()) {
				// 更新优惠券使用状态
				couponDto.setStatus(1);
				couponDao.updateCouponStatus(couponDto);

				// 保存订单
				thirdPayOrderDto.setCreateTime(new Date());
				thirdPayOrderDto.setPayTime(new Date());
				thirdPayOrderDto.setPayStatus(1);
				thirdPayOrderDto.setPatientId(patient.getId());
				thirdPayOrderDto.setDoctorId(serviceDto.getDoctorId());
				thirdPayOrderDto.setTotalAmount(serviceDto.getPrice().doubleValue());
				thirdPayOrderDto.setUseBalance(null);
				thirdPayOrderDto.setPayType(1);
				saveThirdPayOrder(thirdPayOrderDto);

				// 更新图文咨询状态
				consultDto.setPayStatus(Constants.OrderSatus.ORDER_STATUS_1);
				consultDao.updatePayStatus(consultDto);

				// 保存收支明细
				PatientAccountDetailDto patientAccountDetailDto = new PatientAccountDetailDto();
				patientAccountDetailDto.setPatientId(thirdPayOrderDto.getPatientId());
				patientAccountDetailDto.setType(thirdPayOrderDto.getOrderType());
				patientAccountDetailDto.setTransactionNum(thirdPayOrderDto.getOutTradeNo());
				patientAccountDetailDto.setAmount(thirdPayOrderDto.getTotalAmount());
				patientAccountDetailDto.setCreateTime(new Date());
				patientAccountDetailDao.savePatientAccountDetail(patientAccountDetailDto);

				// 通知医生就诊
				RequestDto requestDto = new RequestDto();
				requestDto.setCmd("consultRequest");
				requestDto.setToken(thirdPayOrderDto.getToken());
				Map<String, Object> params = new HashMap<String, Object>();
				requestDto.setParams(params);
				params.put("sender", thirdPayOrderDto.getPatientId());
				params.put("receiver", thirdPayOrderDto.getDoctorId());
				params.put("serviceId", thirdPayOrderDto.getServiceId());
				URLInvoke.post(SysCfg.getString("qujiuyi.chatUrl"), Constants.gson.toJson(requestDto));

				ResponseDto responseDto = new ResponseDto();
				Map<String, Object> detail = new HashMap<String, Object>();
				detail.put("payStatus", "1");
				detail.put("outTradeNo", thirdPayOrderDto.getOutTradeNo());
				responseDto.setResultDesc("创建成功");
				responseDto.setDetail(detail);
				return responseDto;
			}
		}

		patient = patientDao.queryPatientById(patient);
		patient.setBalance(patient.getBalance() == null ? 0 : patient.getBalance());
		if (thirdPayOrderDto.getUseBalance() != null) {
			if (thirdPayOrderDto.getUseBalance() > patient.getBalance()) {
				throw new BusinessException("余额不足");
			}
			if ((couponDto != null ? couponDto.getAmount().doubleValue() : 0) + thirdPayOrderDto.getUseBalance() >= serviceDto.getPrice()) {
				// 更新优惠券使用状态
				if (couponDto != null) {
					couponDto.setStatus(1);
					couponDao.updateCouponStatus(couponDto);
				}

				// 更新个人余额
				patient.setBalance(patient.getBalance() - (serviceDto.getPrice() - (couponDto != null ? couponDto.getAmount().doubleValue() : 0)));
				patientDao.updateBalance(patient);
				CacheContainer.getToken(thirdPayOrderDto.getToken()).getPatient().setBalance(patient.getBalance());

				// 保存订单
				thirdPayOrderDto.setCreateTime(new Date());
				thirdPayOrderDto.setPayTime(new Date());
				thirdPayOrderDto.setPayStatus(1);
				thirdPayOrderDto.setPatientId(patient.getId());
				thirdPayOrderDto.setDoctorId(serviceDto.getDoctorId());
				thirdPayOrderDto.setTotalAmount(serviceDto.getPrice().doubleValue());
				thirdPayOrderDto.setUseBalance(serviceDto.getPrice() - (couponDto != null ? couponDto.getAmount().doubleValue() : 0));
				thirdPayOrderDto.setPayType(1);
				saveThirdPayOrder(thirdPayOrderDto);

				// 更新图文咨询状态
				consultDto.setPayStatus(Constants.OrderSatus.ORDER_STATUS_1);
				consultDao.updatePayStatus(consultDto);

				// 保存收支明细
				PatientAccountDetailDto patientAccountDetailDto = new PatientAccountDetailDto();
				patientAccountDetailDto.setPatientId(thirdPayOrderDto.getPatientId());
				patientAccountDetailDto.setType(thirdPayOrderDto.getOrderType());
				patientAccountDetailDto.setTransactionNum(thirdPayOrderDto.getOutTradeNo());
				patientAccountDetailDto.setAmount(thirdPayOrderDto.getTotalAmount());
				patientAccountDetailDto.setCreateTime(new Date());
				patientAccountDetailDao.savePatientAccountDetail(patientAccountDetailDto);

				// 通知医生就诊
				RequestDto requestDto = new RequestDto();
				requestDto.setCmd("consultRequest");
				requestDto.setToken(thirdPayOrderDto.getToken());
				Map<String, Object> params = new HashMap<String, Object>();
				requestDto.setParams(params);
				params.put("sender", thirdPayOrderDto.getPatientId());
				params.put("receiver", thirdPayOrderDto.getDoctorId());
				params.put("serviceId", thirdPayOrderDto.getServiceId());
				URLInvoke.post(SysCfg.getString("qujiuyi.chatUrl"), Constants.gson.toJson(requestDto));

				ResponseDto responseDto = new ResponseDto();
				Map<String, Object> detail = new HashMap<String, Object>();
				detail.put("payStatus", "1");
				detail.put("outTradeNo", thirdPayOrderDto.getOutTradeNo());
				responseDto.setResultDesc("创建成功");
				responseDto.setDetail(detail);
				return responseDto;
			}
		}

		/** 设置微信预支付ID. */
		thirdPayOrderDto.setPayAmount(serviceDto.getPrice() - (couponDto != null ? couponDto.getAmount().doubleValue() : 0) - (thirdPayOrderDto.getUseBalance() != null ? thirdPayOrderDto.getUseBalance() : 0));
		setWeixinPrepayid(thirdPayOrderDto, "791图文咨询服务");

		/** 保存订单. */
		thirdPayOrderDto.setCreateTime(new Date());
		thirdPayOrderDto.setPayStatus(0);
		thirdPayOrderDto.setTotalAmount(serviceDto.getPrice().doubleValue());
		thirdPayOrderDto.setPatientId(patient.getId());
		thirdPayOrderDto.setDoctorId(serviceDto.getDoctorId());
		thirdPayOrderDao.createWeixinPayOrder(thirdPayOrderDto);
		thirdPayOrderDto.setPayType(1);
		saveThirdPayOrder(thirdPayOrderDto);

		// 返回结果
		ResponseDto responseDto = new ResponseDto();
		Map<String, Object> detail = new HashMap<String, Object>();
		detail.put("prepayId", thirdPayOrderDto.getPrepayId());
		detail.put("outTradeNo", thirdPayOrderDto.getOutTradeNo());
		if (Util.isNotEmpty(thirdPayOrderDto.getPayParams())) {
			detail.put("paySign", MD5.getMD5Code(thirdPayOrderDto.getPayParams().replace("?", thirdPayOrderDto.getPrepayId()) + "&key=" + SysCfg.getString("weixin.pay.key")));
		}
		responseDto.setResultDesc("创建成功");
		responseDto.setDetail(detail);
		return responseDto;
	}

	/**
	 * @description 创建私人医生订单
	 * @param thirdPayOrderDto
	 * @return
	 * @throws Exception
	 */
	private ResponseDto createPersonalDoctorOrder(ThirdPayOrderDto thirdPayOrderDto) throws Exception {
		if (thirdPayOrderDto.getServiceId() == null) {
			throw new BusinessException("服务ID不能为空");
		}

		TokenDto token = CacheContainer.getToken(thirdPayOrderDto.getToken());
		PatientDto patient = token != null ? token.getPatient() : new PatientDto();
		if (patient.getId() == null) {
			throw new BusinessException("登录异常");
		}
		if (patient.getHeadPortrait() == null) {
			patient.setHeadPortrait("");
		}
		if (patient.getName() == null) {
			patient.setName("");
		}

		// 判断服务是否已开通
		ServiceDto serviceDto = new ServiceDto();
		serviceDto.setDoctorId(thirdPayOrderDto.getDoctorId());
		serviceDto.setId(Integer.parseInt(thirdPayOrderDto.getServiceId()));
		serviceDto = serviceDao.queryPersonalDoctorServiceById(serviceDto);
		if (serviceDto == null || serviceDto.getStatus() != 1) {
			throw new BusinessException("此服务暂未开通");
		}
		// 判断是否已购买
		PersonalDoctorDto personalDoctorDto = new PersonalDoctorDto();
		personalDoctorDto.setPatientId(patient.getId());
		personalDoctorDto.setDoctorId(serviceDto.getDoctorId());
		List<PersonalDoctorDto> list = personalDoctorDao.queryPersonalDoctorByPatientIdAndDoctorId(personalDoctorDto);
		if (list != null && !list.isEmpty()) {
			throw new BusinessException("此服务您已购买过了");
		}

		CouponDto couponDto = null;
		if (thirdPayOrderDto.getCouponId() != null) {
			CouponDto queryCouponDto = new CouponDto();
			queryCouponDto.setId(thirdPayOrderDto.getCouponId());
			couponDto = couponDao.queryCouponsById(queryCouponDto);
			if (couponDto == null) {
				throw new BusinessException("优惠券不存在");
			}
			if (couponDto != null && couponDto.getStatus() != 0) {
				throw new BusinessException("优惠券已被使用");
			}
			if (couponDto != null && couponDto.getStatus() == 0 && couponDto.getExpireTime() != null && couponDto.getExpireTime().getTime() < System.currentTimeMillis()) {
				throw new BusinessException("优惠券已过期");
			}
			if (couponDto.getAmount().doubleValue() >= serviceDto.getPrice()) {
				// 更新优惠券使用状态
				couponDto.setStatus(1);
				couponDao.updateCouponStatus(couponDto);

				// 保存订单
				thirdPayOrderDto.setCreateTime(new Date());
				thirdPayOrderDto.setPayTime(new Date());
				thirdPayOrderDto.setPayStatus(1);
				thirdPayOrderDto.setPatientId(patient.getId());
				thirdPayOrderDto.setDoctorId(serviceDto.getDoctorId());
				thirdPayOrderDto.setTotalAmount(serviceDto.getPrice().doubleValue());
				thirdPayOrderDto.setUseBalance(null);
				thirdPayOrderDto.setPayType(1);
				saveThirdPayOrder(thirdPayOrderDto);

				// 新增私人医生记录
				long createTime = System.currentTimeMillis();
				Date expirationTime = null;
				if (serviceDto.getType() != null && serviceDto.getType() == 1) {
					expirationTime = new Date(createTime + 7 * 24 * 60 * 60 * 1000L);
				} else if (serviceDto.getType() != null && serviceDto.getType() == 2) {
					expirationTime = new Date(createTime + 30 * 24 * 60 * 60 * 1000L);
				}
				personalDoctorDto = new PersonalDoctorDto();
				personalDoctorDto.setId(Util.getUniqueSn());
				personalDoctorDto.setPatientId(thirdPayOrderDto.getPatientId());
				personalDoctorDto.setDoctorId(thirdPayOrderDto.getDoctorId());
				personalDoctorDto.setCreateTime(new Date(createTime));
				personalDoctorDto.setExpirationTime(expirationTime);
				personalDoctorDao.createPersonalDoctor(personalDoctorDto);

				// 更新医生即将到账
				DoctorDto updateDoctorDto = new DoctorDto();
				updateDoctorDto.setId(thirdPayOrderDto.getDoctorId());
				updateDoctorDto.setAccountComing(thirdPayOrderDto.getTotalAmount());
				int updates = doctorDao.upadateDoctorAccountComming(updateDoctorDto);
				if (updates != 1) {
					throw new BusinessException("数据有误");
				}

				// 保存收支明细
				PatientAccountDetailDto patientAccountDetailDto = new PatientAccountDetailDto();
				patientAccountDetailDto.setPatientId(thirdPayOrderDto.getPatientId());
				patientAccountDetailDto.setType(thirdPayOrderDto.getOrderType());
				patientAccountDetailDto.setTransactionNum(thirdPayOrderDto.getOutTradeNo());
				patientAccountDetailDto.setAmount(thirdPayOrderDto.getTotalAmount());
				patientAccountDetailDto.setCreateTime(new Date());
				patientAccountDetailDao.savePatientAccountDetail(patientAccountDetailDto);

				// 通知医生
				RequestDto requestDto = new RequestDto();
				requestDto.setCmd("personalDoctorRequest");
				requestDto.setToken(thirdPayOrderDto.getToken());
				Map<String, Object> params = new HashMap<String, Object>();
				requestDto.setParams(params);
				params.put("sender", thirdPayOrderDto.getPatientId());
				params.put("receiver", thirdPayOrderDto.getDoctorId());
				params.put("senderHead", patient.getHeadPortrait());
				params.put("senderName", patient.getName());
				URLInvoke.post(SysCfg.getString("qujiuyi.chatUrl"), Constants.gson.toJson(requestDto));

				ResponseDto responseDto = new ResponseDto();
				Map<String, Object> detail = new HashMap<String, Object>();
				detail.put("payStatus", "1");
				detail.put("outTradeNo", thirdPayOrderDto.getOutTradeNo());
				responseDto.setResultDesc("创建成功");
				responseDto.setDetail(detail);
				return responseDto;
			}
		}

		patient = patientDao.queryPatientById(patient);
		patient.setBalance(patient.getBalance() == null ? 0 : patient.getBalance());
		if (thirdPayOrderDto.getUseBalance() != null) {
			if (thirdPayOrderDto.getUseBalance() > patient.getBalance()) {
				throw new BusinessException("余额不足");
			}
			if ((couponDto != null ? couponDto.getAmount().doubleValue() : 0) + thirdPayOrderDto.getUseBalance() >= serviceDto.getPrice()) {
				// 更新优惠券使用状态
				if (couponDto != null) {
					couponDto.setStatus(1);
					couponDao.updateCouponStatus(couponDto);
				}

				// 更新个人余额
				patient.setBalance(patient.getBalance() - (serviceDto.getPrice() - (couponDto != null ? couponDto.getAmount().doubleValue() : 0)));
				patientDao.updateBalance(patient);
				CacheContainer.getToken(thirdPayOrderDto.getToken()).getPatient().setBalance(patient.getBalance());

				// 保存订单
				thirdPayOrderDto.setCreateTime(new Date());
				thirdPayOrderDto.setPayTime(new Date());
				thirdPayOrderDto.setPayStatus(1);
				thirdPayOrderDto.setPatientId(patient.getId());
				thirdPayOrderDto.setDoctorId(serviceDto.getDoctorId());
				thirdPayOrderDto.setTotalAmount(serviceDto.getPrice().doubleValue());
				thirdPayOrderDto.setUseBalance(serviceDto.getPrice() - (couponDto != null ? couponDto.getAmount().doubleValue() : 0));
				saveThirdPayOrder(thirdPayOrderDto);

				// 新增私人医生记录
				long createTime = System.currentTimeMillis();
				Date expirationTime = null;
				if (serviceDto.getType() != null && serviceDto.getType() == 1) {
					expirationTime = new Date(createTime + 7 * 24 * 60 * 60 * 1000L);
				} else if (serviceDto.getType() != null && serviceDto.getType() == 2) {
					expirationTime = new Date(createTime + 30 * 24 * 60 * 60 * 1000L);
				}
				personalDoctorDto = new PersonalDoctorDto();
				personalDoctorDto.setId(Util.getUniqueSn());
				personalDoctorDto.setPatientId(thirdPayOrderDto.getPatientId());
				personalDoctorDto.setDoctorId(thirdPayOrderDto.getDoctorId());
				personalDoctorDto.setCreateTime(new Date(createTime));
				personalDoctorDto.setExpirationTime(expirationTime);
				personalDoctorDao.createPersonalDoctor(personalDoctorDto);

				// 更新医生即将到账
				DoctorDto updateDoctorDto = new DoctorDto();
				updateDoctorDto.setId(thirdPayOrderDto.getDoctorId());
				updateDoctorDto.setAccountComing(thirdPayOrderDto.getTotalAmount());
				int updates = doctorDao.upadateDoctorAccountComming(updateDoctorDto);
				if (updates != 1) {
					throw new BusinessException("数据有误");
				}

				// 保存收支明细
				PatientAccountDetailDto patientAccountDetailDto = new PatientAccountDetailDto();
				patientAccountDetailDto.setPatientId(thirdPayOrderDto.getPatientId());
				patientAccountDetailDto.setType(thirdPayOrderDto.getOrderType());
				patientAccountDetailDto.setTransactionNum(thirdPayOrderDto.getOutTradeNo());
				patientAccountDetailDto.setAmount(thirdPayOrderDto.getTotalAmount());
				patientAccountDetailDto.setCreateTime(new Date());
				patientAccountDetailDao.savePatientAccountDetail(patientAccountDetailDto);

				// 通知医生
				RequestDto requestDto = new RequestDto();
				requestDto.setCmd("personalDoctorRequest");
				requestDto.setToken(thirdPayOrderDto.getToken());
				Map<String, Object> params = new HashMap<String, Object>();
				requestDto.setParams(params);
				params.put("sender", thirdPayOrderDto.getPatientId());
				params.put("receiver", thirdPayOrderDto.getDoctorId());
				params.put("senderHead", patient.getHeadPortrait());
				params.put("senderName", patient.getName());
				URLInvoke.post(SysCfg.getString("qujiuyi.chatUrl"), Constants.gson.toJson(requestDto));

				ResponseDto responseDto = new ResponseDto();
				Map<String, Object> detail = new HashMap<String, Object>();
				detail.put("payStatus", "1");
				detail.put("outTradeNo", thirdPayOrderDto.getOutTradeNo());
				responseDto.setResultDesc("创建成功");
				responseDto.setDetail(detail);
				return responseDto;
			}
		}

		/** 设置微信预支付ID. */
		thirdPayOrderDto.setPayAmount(serviceDto.getPrice() - (couponDto != null ? couponDto.getAmount().doubleValue() : 0) - (thirdPayOrderDto.getUseBalance() != null ? thirdPayOrderDto.getUseBalance() : 0));
		setWeixinPrepayid(thirdPayOrderDto, "791私人医生服务");

		/** 保存订单. */
		thirdPayOrderDto.setCreateTime(new Date());
		thirdPayOrderDto.setPayStatus(0);
		thirdPayOrderDto.setTotalAmount(serviceDto.getPrice().doubleValue());
		thirdPayOrderDto.setPatientId(patient.getId());
		thirdPayOrderDto.setDoctorId(serviceDto.getDoctorId());
		thirdPayOrderDao.createWeixinPayOrder(thirdPayOrderDto);
		thirdPayOrderDto.setPayType(1);
		saveThirdPayOrder(thirdPayOrderDto);

		// 返回结果
		ResponseDto responseDto = new ResponseDto();
		Map<String, Object> detail = new HashMap<String, Object>();
		detail.put("prepayId", thirdPayOrderDto.getPrepayId());
		detail.put("outTradeNo", thirdPayOrderDto.getOutTradeNo());
		if (Util.isNotEmpty(thirdPayOrderDto.getPayParams())) {
			detail.put("paySign", MD5.getMD5Code(thirdPayOrderDto.getPayParams().replace("?", thirdPayOrderDto.getPrepayId()) + "&key=" + SysCfg.getString("weixin.pay.key")));
		}
		responseDto.setResultDesc("创建成功");
		responseDto.setDetail(detail);
		return responseDto;
	}

	/**
	 * @description 创建处方订单
	 * @param thirdPayOrderDto
	 * @return
	 * @throws Exception
	 */
	private ResponseDto createPrescriptionOrder(ThirdPayOrderDto thirdPayOrderDto) throws Exception {
		if (thirdPayOrderDto.getServiceId() == null) {
			throw new BusinessException("处方ID不能为空");
		}

		TokenDto token = CacheContainer.getToken(thirdPayOrderDto.getToken());
		PatientDto patient = token != null ? token.getPatient() : new PatientDto();
		if (patient.getId() == null) {
			throw new BusinessException("登录异常");
		}
		if (patient.getHeadPortrait() == null) {
			patient.setHeadPortrait("");
		}
		if (patient.getName() == null) {
			patient.setName("");
		}

		// 查询处方
		PrescriptionDto prescriptionDto = new PrescriptionDto();
		prescriptionDto.setId(thirdPayOrderDto.getServiceId());
		PrescriptionDto prescription = prescriptionDao.queryPrescriptionById(prescriptionDto);

		// 判断处方是否为可待支付状态
		if (PrescriptionStatus.PRESCRIBED.ordinal() != prescription.getStatus()) {
			throw new BusinessException("处方状态不可支付");
		}

		// 查询处方清单
		PrescriptionDetailDto prescriptionDetailDto = new PrescriptionDetailDto();
		prescriptionDetailDto.setPrescriptionId(thirdPayOrderDto.getServiceId());
		List<PrescriptionDetailDto> detail = prescriptionDetailDao.queryPrescriptionDetailByPrescriptionId(prescriptionDetailDto);

		// 查询处方清单中的药品集合，计算价格
		List<String> formatIds = new ArrayList<String>();
		for (int i = 0; i < detail.size(); i++) {
			formatIds.add(detail.get(i).getFormatId());
		}

		List<YaoMedicineFormatDto> formatList = new ArrayList<YaoMedicineFormatDto>();
		if (formatIds != null && !formatIds.isEmpty() && formatIds.size() > 0) {
			// 查询规格集合
			formatList = yaoMedicineDao.queryFormatListByFormatIds(formatIds);
		}

		// 计算处方总价格
		BigDecimal totalAmount = new BigDecimal(0);

		// for (int j = 0; j < formatList.size(); j++) {
		// totalAmount = totalAmount.add(formatList.get(j).getProd_price());
		// }

		for (int n = 0; n < detail.size(); n++) {
			BigDecimal amount = new BigDecimal(0);
			for (int m = 0; m < formatList.size(); m++) {
				if (detail.get(n).getFormatId().equals(formatList.get(m).getFormat_id())) {
					amount = formatList.get(m).getProd_price().multiply(new BigDecimal(detail.get(n).getNumber()));
					break;
				}
			}
			totalAmount = totalAmount.add(amount);
		}

		// 判断价格
		if (!prescription.getPrice().equals(totalAmount)) {
			throw new BusinessException("处方总价已经有变动，请重新请医生开取处方");
		}

		CouponDto couponDto = null;
		if (thirdPayOrderDto.getCouponId() != null) {
			CouponDto queryCouponDto = new CouponDto();
			queryCouponDto.setId(thirdPayOrderDto.getCouponId());
			couponDto = couponDao.queryCouponsById(queryCouponDto);
			if (couponDto == null) {
				throw new BusinessException("优惠券不存在");
			}
			if (couponDto != null && couponDto.getStatus() != 0) {
				throw new BusinessException("优惠券已被使用");
			}
			if (couponDto != null && couponDto.getStatus() == 0 && couponDto.getExpireTime() != null && couponDto.getExpireTime().getTime() < System.currentTimeMillis()) {
				throw new BusinessException("优惠券已过期");
			}

			if (couponDto.getAmount().doubleValue() >= totalAmount.doubleValue()) {
				// 更新优惠券使用状态
				couponDto.setStatus(1);
				couponDao.updateCouponStatus(couponDto);

				// 保存订单
				thirdPayOrderDto.setCreateTime(new Date());
				thirdPayOrderDto.setPayTime(new Date());
				thirdPayOrderDto.setPayStatus(1);
				thirdPayOrderDto.setPatientId(patient.getId());
				thirdPayOrderDto.setDoctorId(prescription.getDoctorId());
				thirdPayOrderDto.setTotalAmount(totalAmount.doubleValue());
				thirdPayOrderDto.setUseBalance(null);
				thirdPayOrderDto.setPayType(1);
				saveThirdPayOrder(thirdPayOrderDto);

				// 保存收支明细
				PatientAccountDetailDto patientAccountDetailDto = new PatientAccountDetailDto();
				patientAccountDetailDto.setPatientId(thirdPayOrderDto.getPatientId());
				patientAccountDetailDto.setType(thirdPayOrderDto.getOrderType());
				patientAccountDetailDto.setTransactionNum(thirdPayOrderDto.getOutTradeNo());
				patientAccountDetailDto.setAmount(thirdPayOrderDto.getTotalAmount());
				patientAccountDetailDto.setCreateTime(new Date());
				patientAccountDetailDao.savePatientAccountDetail(patientAccountDetailDto);

				ResponseDto responseDto = new ResponseDto();
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("payStatus", "1");
				map.put("outTradeNo", thirdPayOrderDto.getOutTradeNo());
				responseDto.setResultDesc("创建成功");
				responseDto.setDetail(map);
				return responseDto;
			}
		}

		/** 设置微信预支付ID. */
		thirdPayOrderDto.setPayAmount(totalAmount.doubleValue() - (couponDto != null ? couponDto.getAmount().doubleValue() : 0));
		setWeixinPrepayid(thirdPayOrderDto, "791处方服务");

		/** 保存订单. */
		thirdPayOrderDto.setCreateTime(new Date());
		thirdPayOrderDto.setPayStatus(0);
		thirdPayOrderDto.setTotalAmount(totalAmount.doubleValue());
		thirdPayOrderDto.setPatientId(patient.getId());
		thirdPayOrderDto.setDoctorId(prescription.getDoctorId());
		thirdPayOrderDao.createWeixinPayOrder(thirdPayOrderDto);
		thirdPayOrderDto.setPayType(1);
		saveThirdPayOrder(thirdPayOrderDto);

		// 返回结果
		ResponseDto responseDto = new ResponseDto();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("prepayId", thirdPayOrderDto.getPrepayId());
		map.put("outTradeNo", thirdPayOrderDto.getOutTradeNo());
		if (Util.isNotEmpty(thirdPayOrderDto.getPayParams())) {
			map.put("paySign", MD5.getMD5Code(thirdPayOrderDto.getPayParams().replace("?", thirdPayOrderDto.getPrepayId()) + "&key=" + SysCfg.getString("weixin.pay.key")));
		}
		responseDto.setResultDesc("创建成功");
		responseDto.setDetail(map);
		return responseDto;
	}

	/**
	 * @description 根据微信支付返回的XML更新微信订单状态
	 * @param respXml
	 * @throws Exception
	 */
	@Override
	public void updateWeixinOrderByRespXml(String respXml) throws Exception {
		/** 解析数据. */
		Document doc = DocumentHelper.parseText(respXml);
		List<?> elements = doc.getRootElement().elements();
		Map<String, String> respMap = new HashMap<String, String>();
		for (Object et : elements) {
			respMap.put(((Element) et).getName(), ((Element) et).getText());
		}

		/** 支付失败时什么也不做. */
		if (respMap == null || !"SUCCESS".equals(respMap.get("return_code"))) {
			return;
		}

		/** 获取订单详情. */
		ThirdPayOrderDto thirdPayOrderDto = new ThirdPayOrderDto();
		thirdPayOrderDto.setOutTradeNo(respMap.get("out_trade_no"));
		thirdPayOrderDto = thirdPayOrderDao.getWeixinOrderByOutTradeNo(thirdPayOrderDto);
		if (thirdPayOrderDto == null) {
			return;
		}

		// 根据用户ID得到用户
		PatientDto dto = CacheContainer.byIdGetPatient(thirdPayOrderDto.getPatientId());
		synchronized (dto) {
			if (thirdPayOrderDto.getPayStatus() != null && thirdPayOrderDto.getPayStatus() == 1) {
				return;
			}

			/** 更新订单. */
			thirdPayOrderDto.setTransactionId(respMap.get("transaction_id"));
			thirdPayOrderDto.setPayTime(new Date());
			thirdPayOrderDto.setPayBank(respMap.get("bank_type"));
			thirdPayOrderDto.setPayStatus(1);
			thirdPayOrderDao.updateWeixinOrder(thirdPayOrderDto);

			if (thirdPayOrderDto.getOrderType() != null && thirdPayOrderDto.getOrderType() == 0) {// 余额充值
				PatientDto patient = new PatientDto();
				patient.setId(thirdPayOrderDto.getPatientId());
				patient = patientDao.queryPatientById(patient);
				patient.setBalance(patient.getBalance() == null ? 0 : patient.getBalance());
				patient.setBalance(patient.getBalance() + thirdPayOrderDto.getPayAmount());
				patientDao.updateBalance(patient);
				CacheContainer.updateTokenByPatient(patient);

				// 保存收支明细
				PatientAccountDetailDto patientAccountDetailDto = new PatientAccountDetailDto();
				patientAccountDetailDto.setPatientId(thirdPayOrderDto.getPatientId());
				patientAccountDetailDto.setType(thirdPayOrderDto.getOrderType());
				patientAccountDetailDto.setTransactionNum(thirdPayOrderDto.getOutTradeNo());
				patientAccountDetailDto.setAmount(thirdPayOrderDto.getTotalAmount());
				patientAccountDetailDto.setCreateTime(new Date());
				patientAccountDetailDao.savePatientAccountDetail(patientAccountDetailDto);
			} else if (thirdPayOrderDto.getOrderType() != null && thirdPayOrderDto.getOrderType() == 1) {// 挂号
				thirdPayOrderDto.setVisitCost(new BigDecimal(thirdPayOrderDto.getTotalAmount()));// 设置挂号金额
				thirdPayOrderDto.setNumSourceId(thirdPayOrderDto.getServiceId());// 设置号源ID

				// 创建挂号记录
				createRegister(thirdPayOrderDto);

				// 更新优惠卷状态
				if (thirdPayOrderDto.getCouponId() != null) {
					CouponDto couponDto = new CouponDto();
					couponDto.setId(thirdPayOrderDto.getCouponId());
					couponDto.setStatus(1);
					couponDao.updateCouponStatus(couponDto);
				}

				// 更新余额
				PatientDto patient = new PatientDto();
				patient.setId(thirdPayOrderDto.getPatientId());
				patient = patientDao.queryPatientById(patient);

				// 保存收支明细
				PatientAccountDetailDto patientAccountDetailDto = new PatientAccountDetailDto();
				patientAccountDetailDto.setPatientId(thirdPayOrderDto.getPatientId());
				patientAccountDetailDto.setType(thirdPayOrderDto.getOrderType());
				patientAccountDetailDto.setTransactionNum(thirdPayOrderDto.getOutTradeNo());
				patientAccountDetailDto.setAmount(thirdPayOrderDto.getTotalAmount());
				patientAccountDetailDto.setCreateTime(new Date());
				patientAccountDetailDao.savePatientAccountDetail(patientAccountDetailDto);

				// 查询患者挂号记录
				PatientRegisterDto patientRegisterDto = new PatientRegisterDto();
				patientRegisterDto.setOutTradeNo(thirdPayOrderDto.getOutTradeNo());
				PatientRegisterDto Register = patientRegisterDao.queryRegisterDetailPlus(patientRegisterDto);

				// 请求院方接口，修改患者挂号记录
				BaseService commitOrder = new BaseService();
				commitOrder.getParams().put("hospitalId", thirdPayOrderDto.getHospitalId().toString());
				commitOrder.getParams().put("numSourceId", thirdPayOrderDto.getServiceId());
				commitOrder.getParams().put("payMode", thirdPayOrderDto.getPayMode().toString());
				commitOrder.getParams().put("userName", Register.getPatientName());
				commitOrder.getParams().put("userCardType", "1");// 1身份证
				commitOrder.getParams().put("userCardId", Register.getCertificateNumber());
				commitOrder.getParams().put("userSex", Register.getGender().toString());
				commitOrder.getParams().put("userBirthday", Util.DateToStr(IDCard.getBirthdayByCard(Register.getCertificateNumber())));
				commitOrder.getParams().put("userPhone", Register.getPatientPhone());
				commitOrder.packageData("commitOrder", PatientRegisterDto.class, SysCfg.getString("register.plus.url"));

				// 挂号失败
				if (!commitOrder.isSuccess()) {
					// 退款
					refund(thirdPayOrderDto);
					// 挂号失败，修改挂号计划为挂号失败，并设置为隐藏状态，该条挂号记录在数据相当于脏数据
					PatientRegisterDto patientReg = new PatientRegisterDto();
					patientReg.setOutTradeNo(thirdPayOrderDto.getOutTradeNo());
					patientRegisterDao.updatePatientRegisterFail(patientReg);
					// 终端提示
					RequestDto requestDto = new RequestDto();
					requestDto.setCmd("sendSystemMsg");
					Map<String, Object> params = new HashMap<String, Object>();
					Map<String, String> content = new HashMap<String, String>();
					content.put("cmd", "createWeixinPayOrder");
					content.put("message", "【791去就医】由于" + commitOrder.getDesc() + "，您的预约挂号已失效，挂号费用已原路返回。为您带来的不便，敬请谅解。");
					requestDto.setParams(params);
					params.put("target", thirdPayOrderDto.getPatientId());
					params.put("targetType", 1);
					params.put("summary", "挂号失败");
					params.put("content", Constants.gson.toJson(content));
					params.put("weixinMsg", "【791去就医】由于" + commitOrder.getDesc() + "，您的预约挂号已失效，挂号费用已原路返回。为您带来的不便，敬请谅解。");
					URLInvoke.post(SysCfg.getString("qujiuyi.chatUrl"), Constants.gson.toJson(requestDto));
					return;
				}

				PatientRegisterDto patientRegister = (PatientRegisterDto) commitOrder.getDataObj();
				logger.info("ThirdPayOrderServieImpl.updateWeixinOrderByRespXml###success  result#visitNo:" + patientRegister.getVisitNo() + ";billNo:" + patientRegister.getBillNo() + ";orderNo:" + thirdPayOrderDto.getOutTradeNo());

				patientRegister.setOutTradeNo(thirdPayOrderDto.getOutTradeNo());
				patientRegisterDao.updatePatientRegister(patientRegister);

				// 短信提示
				String name = thirdPayOrderDto.getPatientName();
				String date = new SimpleDateFormat("MM月dd日").format(Register.getScheduleDate()) + Util.getReange(Register.getTimeRange());// 获得日期
				String doctor = Register.getDepartmentName() + Register.getDoctorName() + Register.getDoctorTitleName();
				String hospital = Register.getHospitalName();
				String param = String.format("#name#=%s&#doctor#=%s&#date#=%s&#hospital#=%s", name + "\n", doctor + "\n", date + "\n", hospital + "\n");
				SmsService.instance().sendSms(Register.getPatientPhone(), "9822", param);

				// 终端提示
				RequestDto requestDto = new RequestDto();
				requestDto.setCmd("sendSystemMsg");
				Map<String, Object> params = new HashMap<String, Object>();
				Map<String, String> content = new HashMap<String, String>();
				content.put("cmd", "createWeixinPayOrder");
				content.put("message", Util.getRegisterSms(name, doctor, date, hospital));
				requestDto.setParams(params);
				params.put("target", patient.getId());
				params.put("targetType", 1);
				params.put("summary", "恭喜您挂号成功");
				params.put("content", Constants.gson.toJson(content));
				params.put("weixinMsg", Util.getRegisterSms(name, doctor, date, hospital));
				URLInvoke.post(SysCfg.getString("qujiuyi.chatUrl"), Constants.gson.toJson(requestDto));

			} else if (thirdPayOrderDto.getOrderType() != null && thirdPayOrderDto.getOrderType() == 2) {// 图文咨询
				// 获取咨询记录
				ConsultDto consultDto = new ConsultDto();
				consultDto.setId(thirdPayOrderDto.getServiceId());
				consultDto = consultDao.queryConsultById(consultDto);

				// 更新图文咨询状态
				consultDto.setPayStatus(Constants.OrderSatus.ORDER_STATUS_1);
				consultDto.setOutTradeNo(thirdPayOrderDto.getOutTradeNo());
				consultDao.updatePayStatus(consultDto);

				// 更新优惠卷状态
				if (thirdPayOrderDto.getCouponId() != null) {
					CouponDto couponDto = new CouponDto();
					couponDto.setId(thirdPayOrderDto.getCouponId());
					couponDto.setStatus(1);
					couponDao.updateCouponStatus(couponDto);
				}

				// 保存收支明细
				PatientAccountDetailDto patientAccountDetailDto = new PatientAccountDetailDto();
				patientAccountDetailDto.setPatientId(thirdPayOrderDto.getPatientId());
				patientAccountDetailDto.setType(thirdPayOrderDto.getOrderType());
				patientAccountDetailDto.setTransactionNum(thirdPayOrderDto.getOutTradeNo());
				patientAccountDetailDto.setAmount(thirdPayOrderDto.getTotalAmount());
				patientAccountDetailDto.setCreateTime(new Date());
				patientAccountDetailDao.savePatientAccountDetail(patientAccountDetailDto);

				// 通知医生就诊
				RequestDto requestDto = new RequestDto();
				requestDto.setCmd("consultRequest");
				requestDto.setToken(thirdPayOrderDto.getToken());
				Map<String, Object> params = new HashMap<String, Object>();
				requestDto.setParams(params);
				params.put("sender", thirdPayOrderDto.getPatientId());
				params.put("receiver", thirdPayOrderDto.getDoctorId());
				params.put("serviceId", thirdPayOrderDto.getServiceId());
				URLInvoke.post(SysCfg.getString("qujiuyi.chatUrl"), Constants.gson.toJson(requestDto));
			} else if (thirdPayOrderDto.getOrderType() != null && thirdPayOrderDto.getOrderType() == 3) {// 一元义诊
				// 获取咨询记录
				ConsultDto consultDto = new ConsultDto();
				consultDto.setId(thirdPayOrderDto.getServiceId());
				consultDto = consultDao.queryConsultById(consultDto);

				// 更新图文咨询状态
				consultDto.setPayStatus(Constants.OrderSatus.ORDER_STATUS_1);
				consultDao.updatePayStatus(consultDto);

				// 更新优惠卷状态
				if (thirdPayOrderDto.getCouponId() != null) {
					CouponDto couponDto = new CouponDto();
					couponDto.setId(thirdPayOrderDto.getCouponId());
					couponDto.setStatus(1);
					couponDao.updateCouponStatus(couponDto);
				}

				// 更新余额
				if (thirdPayOrderDto.getUseBalance() != null) {
					PatientDto patient = new PatientDto();
					patient.setId(thirdPayOrderDto.getPatientId());
					patient = patientDao.queryPatientById(patient);
					patient.setBalance(patient.getBalance() == null ? 0 : patient.getBalance());
					patient.setBalance(patient.getBalance() - thirdPayOrderDto.getUseBalance());
					patientDao.updateBalance(patient);
					CacheContainer.updateTokenByPatient(patient);
				}

				// 保存收支明细
				PatientAccountDetailDto patientAccountDetailDto = new PatientAccountDetailDto();
				patientAccountDetailDto.setPatientId(thirdPayOrderDto.getPatientId());
				patientAccountDetailDto.setType(thirdPayOrderDto.getOrderType());
				patientAccountDetailDto.setTransactionNum(thirdPayOrderDto.getOutTradeNo());
				patientAccountDetailDto.setAmount(thirdPayOrderDto.getTotalAmount());
				patientAccountDetailDto.setCreateTime(new Date());
				patientAccountDetailDao.savePatientAccountDetail(patientAccountDetailDto);

				// 通知医生就诊
				RequestDto requestDto = new RequestDto();
				requestDto.setCmd("consultRequest");
				requestDto.setToken(thirdPayOrderDto.getToken());
				Map<String, Object> params = new HashMap<String, Object>();
				requestDto.setParams(params);
				params.put("sender", thirdPayOrderDto.getPatientId());
				params.put("receiver", thirdPayOrderDto.getDoctorId());
				params.put("serviceId", thirdPayOrderDto.getServiceId());
				URLInvoke.post(SysCfg.getString("qujiuyi.chatUrl"), Constants.gson.toJson(requestDto));
			} else if (thirdPayOrderDto.getOrderType() != null && thirdPayOrderDto.getOrderType() == 4) {// 私人医生
				ServiceDto serviceDto = new ServiceDto();
				serviceDto.setDoctorId(thirdPayOrderDto.getDoctorId());
				serviceDto.setId(Integer.parseInt(thirdPayOrderDto.getServiceId()));
				serviceDto = serviceDao.queryPersonalDoctorServiceById(serviceDto);

				// 新增私人医生记录
				long createTime = System.currentTimeMillis();
				Date expirationTime = null;
				if (serviceDto.getType() != null && serviceDto.getType() == 1) {
					expirationTime = new Date(createTime + 7 * 24 * 60 * 60 * 1000L);
				} else if (serviceDto.getType() != null && serviceDto.getType() == 2) {
					expirationTime = new Date(createTime + 30 * 24 * 60 * 60 * 1000L);
				}
				PersonalDoctorDto personalDoctorDto = new PersonalDoctorDto();
				personalDoctorDto.setId(Util.getUniqueSn());
				personalDoctorDto.setPatientId(thirdPayOrderDto.getPatientId());
				personalDoctorDto.setDoctorId(thirdPayOrderDto.getDoctorId());
				personalDoctorDto.setCreateTime(new Date(createTime));
				personalDoctorDto.setExpirationTime(expirationTime);
				personalDoctorDao.createPersonalDoctor(personalDoctorDto);

				// 更新优惠卷状态
				if (thirdPayOrderDto.getCouponId() != null) {
					CouponDto couponDto = new CouponDto();
					couponDto.setId(thirdPayOrderDto.getCouponId());
					couponDto.setStatus(1);
					couponDao.updateCouponStatus(couponDto);
				}

				// 更新余额
				if (thirdPayOrderDto.getUseBalance() != null) {
					PatientDto patient = new PatientDto();
					patient.setId(thirdPayOrderDto.getPatientId());
					patient = patientDao.queryPatientById(patient);
					patient.setBalance(patient.getBalance() == null ? 0 : patient.getBalance());
					patient.setBalance(patient.getBalance() - thirdPayOrderDto.getUseBalance());
					patientDao.updateBalance(patient);
					CacheContainer.updateTokenByPatient(patient);
				}

				// 更新医生即将到账
				DoctorDto updateDoctorDto = new DoctorDto();
				updateDoctorDto.setId(thirdPayOrderDto.getDoctorId());
				updateDoctorDto.setAccountComing(thirdPayOrderDto.getTotalAmount());
				int updates = doctorDao.upadateDoctorAccountComming(updateDoctorDto);
				if (updates != 1) {
					throw new BusinessException("数据有误");
				}

				// 保存收支明细
				PatientAccountDetailDto patientAccountDetailDto = new PatientAccountDetailDto();
				patientAccountDetailDto.setPatientId(thirdPayOrderDto.getPatientId());
				patientAccountDetailDto.setType(thirdPayOrderDto.getOrderType());
				patientAccountDetailDto.setTransactionNum(thirdPayOrderDto.getOutTradeNo());
				patientAccountDetailDto.setAmount(thirdPayOrderDto.getTotalAmount());
				patientAccountDetailDto.setCreateTime(new Date());
				patientAccountDetailDao.savePatientAccountDetail(patientAccountDetailDto);

				// 通知医生
				PatientDto patientDto = new PatientDto();
				patientDto.setId(thirdPayOrderDto.getPatientId());
				PatientDto patient = patientDao.queryPatientById(patientDto);
				if (patient.getHeadPortrait() == null) {
					patient.setHeadPortrait("");
				}
				if (patient.getName() == null) {
					patient.setName("");
				}
				RequestDto requestDto = new RequestDto();
				requestDto.setCmd("personalDoctorRequest");
				requestDto.setToken(thirdPayOrderDto.getToken());
				Map<String, Object> params = new HashMap<String, Object>();
				requestDto.setParams(params);
				params.put("sender", thirdPayOrderDto.getPatientId());
				params.put("receiver", thirdPayOrderDto.getDoctorId());
				params.put("senderHead", patient.getHeadPortrait());
				params.put("senderName", patient.getName());
				URLInvoke.post(SysCfg.getString("qujiuyi.chatUrl"), Constants.gson.toJson(requestDto));
			}
		}
	}

	/**
	 * @description 获取微信预支付ID
	 * @param thirdPayOrderDto
	 * @return
	 * @throws DocumentException
	 * @throws BusinessException
	 */
	public void setWeixinPrepayid(ThirdPayOrderDto thirdPayOrderDto, String desc) throws Exception {
		Map<String, String> reqMap = new HashMap<String, String>();
		thirdPayOrderDto.setOutTradeNo(thirdPayOrderDto.getOutTradeNo());
		reqMap.put("nonce_str", MD5.getMD5Code(Util.getUniqueSn()));// 随机字符串
		reqMap.put("body", desc);// 商品描述
		reqMap.put("out_trade_no", thirdPayOrderDto.getOutTradeNo()); // 商户订单号
		reqMap.put("total_fee", (int) (thirdPayOrderDto.getPayAmount() * 100) + "");// 总金额
		reqMap.put("spbill_create_ip", thirdPayOrderDto.getIpAddr()); // 终端IP
		reqMap.put("notify_url", SysCfg.getString("weixin.pay.notify"));// 通知地址
		if (Util.isNotEmpty(thirdPayOrderDto.getWeixinOpenid())) {// 微信公众号支付
			reqMap.put("appid", SysCfg.getString("weixin.appid")); // 公众账号ID
			reqMap.put("mch_id", SysCfg.getString("weixin.mch_id"));// 商户号
			reqMap.put("trade_type", "JSAPI");// 交易类型
			reqMap.put("openid", thirdPayOrderDto.getWeixinOpenid()); // 用户标识
		} else { // 微信APP支付
			reqMap.put("appid", SysCfg.getString("app.appid")); // 公众账号ID
			reqMap.put("mch_id", SysCfg.getString("app.mch_id"));// 商户号
			reqMap.put("trade_type", "APP");// 交易类型
		}

		/** reqMap的key值排序. */
		List<String> mapKeys = new ArrayList<String>();
		for (String key : reqMap.keySet()) {
			mapKeys.add(key);
		}
		Collections.sort(mapKeys);

		/** 生成签名. */
		StringBuffer sb = new StringBuffer();
		for (String key : mapKeys) {
			sb.append(key + "=" + reqMap.get(key) + "&");
		}
		sb.append("key=" + SysCfg.getString("weixin.pay.key"));
		String sign = MD5.getMD5Code(sb.toString()).toUpperCase();

		/** 生成请求xml. */
		String reqXml = "<xml>?</xml>";
		sb = new StringBuffer();
		for (String key : mapKeys) {
			sb.append("<" + key + ">" + reqMap.get(key) + "</" + key + ">\n");
		}
		sb.append("<sign>" + sign + "</sign>");
		reqXml = reqXml.replace("?", sb.toString());

		/** 请求下服务器,并解析响应结果. */
		String respXml = HttpsUtil.post(SysCfg.getString("weixin.unifiedorder"), reqXml);
		Document doc = DocumentHelper.parseText(respXml);
		List<?> elements = doc.getRootElement().elements();
		Map<String, String> respMap = new HashMap<String, String>();
		for (Object et : elements) {
			respMap.put(((Element) et).getName(), ((Element) et).getText());
		}
		if (!Util.isNotEmpty(respMap.get("prepay_id"))) {
			throw new BusinessException(respMap.get("return_msg"));
		}
		thirdPayOrderDto.setPrepayId(respMap.get("prepay_id"));
	}

	/**
	 * @description 保存第三方支付订单
	 * @param thirdPayOrderDto
	 * @throws Exception
	 */
	public void saveThirdPayOrder(ThirdPayOrderDto thirdPayOrderDto) throws Exception {
		if (Util.isNotEmpty(thirdPayOrderDto.getWeixinOpenid())) {// 微信客户端
			thirdPayOrderDto.setClientType(0);
		} else { // 微信APP客户端
			thirdPayOrderDto.setClientType(1);
		}
		thirdPayOrderDto.setDisplayStatus(1);
		thirdPayOrderDao.createWeixinPayOrder(thirdPayOrderDto);
	}

	/**
	 * @description 创建挂号记录
	 * @param thirdPayOrderDto
	 * @throws Exception
	 */
	private void createRegister(ThirdPayOrderDto thirdPayOrderDto) throws Exception {
		/** 获取线下医生详情. */
		DoctorDto doctorDto = new DoctorDto();
		doctorDto.setId(thirdPayOrderDto.getDoctorId());
		doctorDto = doctorDao.getHospitalDoctorDetail(doctorDto);
		if (doctorDto == null) {
			return;
		}

		/** 获取常用就诊人信息. */
		PatientRelativeDto ratientRelativeDto = new PatientRelativeDto();
		ratientRelativeDto.setId(thirdPayOrderDto.getPatientRelativeId());
		ratientRelativeDto = patientRelativeDao.getPatientRelativeById(ratientRelativeDto);
		ratientRelativeDto = ratientRelativeDto != null ? ratientRelativeDto : new PatientRelativeDto();

		/** 创建挂号记录. */
		PatientRegisterDto patientRegisterDto = new PatientRegisterDto();
		patientRegisterDto.setPatientId(thirdPayOrderDto.getPatientId());
		patientRegisterDto.setPatientName(ratientRelativeDto.getName());
		patientRegisterDto.setGender(ratientRelativeDto.getGender());
		patientRegisterDto.setCertificateNumber(ratientRelativeDto.getCertificateNumber());
		patientRegisterDto.setPatientAge(Util.getAge(IDCard.getBirthdayByCard(ratientRelativeDto.getCertificateNumber())));
		patientRegisterDto.setRelativeId(ratientRelativeDto.getId());
		patientRegisterDto.setHospitalId(doctorDto.getHospitalId());
		patientRegisterDto.setHospitalName(doctorDto.getHospitalName());
		patientRegisterDto.setDepartmentId(doctorDto.getDepartmentId());
		patientRegisterDto.setDepartmentName(doctorDto.getDepartmentName());
		patientRegisterDto.setDoctorId(doctorDto.getId());
		patientRegisterDto.setDoctorName(doctorDto.getName());
		patientRegisterDto.setRegisterTime(new Date());
		patientRegisterDto.setStatus(0);
		patientRegisterDto.setDoctorTitleName(doctorDto.getTitleName());
		patientRegisterDto.setOutTradeNo(thirdPayOrderDto.getOutTradeNo());
		patientRegisterDto.setPatientPhone(ratientRelativeDto.getPhone());
		patientRegisterDto.setDisplayStatus(1);// 显示状态
		patientRegisterDto.setNumSourceId(thirdPayOrderDto.getNumSourceId());
		patientRegisterDto.setScheduleDate(thirdPayOrderDto.getScheduleDate());
		patientRegisterDto.setVisitCost(thirdPayOrderDto.getVisitCost());
		patientRegisterDto.setTimeRange(thirdPayOrderDto.getTimeRange());
		patientRegisterDto.setPayMode(thirdPayOrderDto.getPayMode());
		patientRegisterDto.setStartTime(thirdPayOrderDto.getStartTime());
		patientRegisterDto.setEndTime(thirdPayOrderDto.getEndTime());
		thirdPayOrderDto.setHospitalName(doctorDto.getHospitalName());
		thirdPayOrderDto.setDepartmentName(doctorDto.getDepartmentName());
		thirdPayOrderDto.setDoctorName(doctorDto.getName());
		thirdPayOrderDto.setDoctorTitleName(doctorDto.getTitleName());
		thirdPayOrderDto.setCertificateNumber(ratientRelativeDto.getCertificateNumber());
		thirdPayOrderDto.setGender(ratientRelativeDto.getGender());
		thirdPayOrderDto.setPatientPhone(ratientRelativeDto.getPhone());
		thirdPayOrderDto.setBirthday(IDCard.getBirthdayByCard(ratientRelativeDto.getCertificateNumber()));
		thirdPayOrderDto.setPatientName(ratientRelativeDto.getName());
		patientRegisterDao.createRegister(patientRegisterDto);

	}

	/**
	 * @description 根据订单号查询订单详情
	 * @param thirdPayOrderDto
	 * @return
	 * @throws Exception
	 */
	@Override
	public ResponseDto queryOrderDetailByOutTradeNo(ThirdPayOrderDto thirdPayOrderDto) throws Exception {
		if (thirdPayOrderDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		// 获取用户
		TokenDto token = CacheContainer.getToken(thirdPayOrderDto.getToken());
		PatientDto patient = token != null ? token.getPatient() : new PatientDto();

		// 获取订单记录
		thirdPayOrderDto.setPatientId(patient.getId());
		thirdPayOrderDto = thirdPayOrderDao.queryOrderDetailByOutTradeNo(thirdPayOrderDto);

		if (thirdPayOrderDto == null) {
			throw new BusinessException("订单不存在");
		}

		if (thirdPayOrderDto.getOrderType() != null && thirdPayOrderDto.getOrderType() == 1) {// 挂号
			PatientRegisterDto patientRegisterDto = new PatientRegisterDto();
			patientRegisterDto.setOutTradeNo(thirdPayOrderDto.getOutTradeNo());
			patientRegisterDto = patientRegisterDao.queryRegisterDetail(patientRegisterDto);
			if (patientRegisterDto != null) {
				thirdPayOrderDto.setHospitalName(patientRegisterDto.getHospitalName());
				thirdPayOrderDto.setDepartmentName(patientRegisterDto.getDepartmentName());
				thirdPayOrderDto.setDoctorName(patientRegisterDto.getDoctorName());
				thirdPayOrderDto.setDoctorTitleName(patientRegisterDto.getDoctorTitleName());
				thirdPayOrderDto.setSeeDoctorDate(patientRegisterDto.getSeeDoctorDate());
				thirdPayOrderDto.setTimeZone(patientRegisterDto.getTimeZone());
				thirdPayOrderDto.setPatientName(patientRegisterDto.getPatientName());
				thirdPayOrderDto.setPatientPhone(patientRegisterDto.getPatientPhone());
				thirdPayOrderDto.setPatientCertificateNumber(patientRegisterDto.getCertificateNumber());
			}
		} else if (thirdPayOrderDto.getOrderType() != null && (thirdPayOrderDto.getOrderType() == 2 || thirdPayOrderDto.getOrderType() == 3 || thirdPayOrderDto.getOrderType() == 4)) {
			DoctorDto doctor = new DoctorDto();
			doctor.setId(thirdPayOrderDto.getDoctorId());
			doctor = doctorDao.queryDoctorInfo(doctor);
			thirdPayOrderDto.setHospitalName(doctor.getHospitalName());
			thirdPayOrderDto.setDepartmentName(doctor.getDepartmentName());
			thirdPayOrderDto.setDoctorName(doctor.getName());
			thirdPayOrderDto.setDoctorTitleName(doctor.getTitleName());
		}

		ResponseDto responseDto = new ResponseDto();
		responseDto.setResultDesc("查询成功");
		responseDto.setDetail(thirdPayOrderDto);
		return responseDto;
	}

	/**
	 * 
	 * @number
	 * @description 处理过期的挂号订单
	 * 
	 * @throws Exception
	 * @Date 2015年12月1日
	 */
	@Override
	public void handleThirdPayOrder() throws Exception {
		ThirdPayOrderDto thirdPayOrderDto = new ThirdPayOrderDto();
		// 更改过期订单状态
		thirdPayOrderDao.updateThirdPayOrderDisplayStatus(thirdPayOrderDto);
	}

	/**
	 * 
	 * @number @description 退款申请
	 * 
	 * @param thirdPayOrderDto
	 * @return
	 * @throws Exception
	 *
	 * @Date 2015年12月15日
	 */
	@Override
	public ResponseDto requestRefund(ThirdPayOrderDto thirdPayOrderDto) throws Exception {
		/** 退款处理 */
		refund(thirdPayOrderDto);
		ResponseDto responseDto = new ResponseDto();
		responseDto.setResultDesc("退款成功");
		return responseDto;
	}

	/**
	 * 
	 * @number @description 退款处理
	 * 
	 * @param thirdPayOrderDto
	 * @throws Exception
	 *
	 * @Date 2015年12月15日
	 */
	@Override
	public void refund(ThirdPayOrderDto thirdPayOrderDto) throws Exception {
		/** step1: 空判断 */
		if (thirdPayOrderDto == null) {
			throw new BusinessException(Constants.DATA_ERROR);
		}

		/** step2: 获取用户 */
		PatientDto patientDto = new PatientDto();
		patientDto.setId(thirdPayOrderDto.getPatientId());
		PatientDto patient = patientDao.queryPatientById(patientDto);
		if (patient.getId() == null) {
			throw new BusinessException("登录异常");
		}

		/** step3:校验订单号 */
		if (!Util.isNotEmpty(thirdPayOrderDto.getOutTradeNo())) {
			throw new BusinessException("订单号不能为空");
		}

		/** step4:查询订单 */
		ThirdPayOrderDto order = thirdPayOrderDao.queryOrderDetailByOutTradeNo(thirdPayOrderDto);
		if (order == null) {
			throw new BusinessException("订单不存在");
		}

		if (order.getPayStatus() != 1) {
			throw new BusinessException("该订单未支付");
		}

		/** 校验订单是否是免费订单，如果订单属于免费订单，直接更改订单状态为退款成功状态. */
		if (order.getPayType() == 3) {
			// 退款成功后，更新订单状态为退款成功
			thirdPayOrderDto.setPayStatus(3);
			thirdPayOrderDao.updatePayStatusByOutTradeNoAndPatientId(thirdPayOrderDto);
			return;
		}
		// 添加退款申请记录
		String outRefundNo = Util.getUniqueSn();
		RefundDto refundDto = new RefundDto();
		refundDto.setId(outRefundNo);
		refundDto.setOutTradeNo(order.getOutTradeNo());
		refundDto.setStatus(0);// 设置退款状态为申请退款中
		refundDto.setOrderType(order.getOrderType());
		refundDao.insertRefund(refundDto);

		// 更改订单状态为申请退款
		thirdPayOrderDto.setPayStatus(2);// 设置订单状态为申请退款
		thirdPayOrderDao.updatePayStatusByOutTradeNoAndPatientId(thirdPayOrderDto);

		/** step6:校验是否使用优惠券 */
		if (order.getCouponId() != null) {
			CouponDto couponDto = new CouponDto();
			couponDto.setId(order.getCouponId());
			// 查询优惠券
			CouponDto cou = couponDao.queryCouponsById(couponDto);
			// 判断优惠券是否过期
			if (cou.getExpireTime().before(new Date())) {
				couponDto.setStatus(3);// 过期
			} else {
				couponDto.setStatus(0);// 未使用
			}
			couponDao.updateCouponStatus(couponDto);
		}

		/** step7:校验是否使用现金 */
		if (order.getPayAmount() != null) {
			Map<String, String> reqMap = new HashMap<String, String>();
			reqMap.put("nonce_str", MD5.getMD5Code(Util.getUniqueSn()));// 随机字符串
			reqMap.put("out_trade_no", thirdPayOrderDto.getOutTradeNo()); // 商户订单号
			reqMap.put("out_refund_no", refundDto.getId());// 商户退款编号
			reqMap.put("total_fee", (int) (order.getPayAmount() * 100) + "");// 总金额
			reqMap.put("refund_fee", (int) (order.getPayAmount() * 100) + "");// 退款金额
			if (order.getClientType() == 0) {// 微信公众号支付
				logger.info("=================================微信退款=================================");
				reqMap.put("mch_id", SysCfg.getString("weixin.mch_id"));// 商户号
				reqMap.put("op_user_id", SysCfg.getString("weixin.mch_id"));// 操作员
				reqMap.put("appid", SysCfg.getString("weixin.appid")); // 公众账号ID
			} else { // 微信APP支付
				logger.info("=================================App退款=================================");
				reqMap.put("mch_id", SysCfg.getString("app.mch_id"));// 商户号
				reqMap.put("op_user_id", SysCfg.getString("app.mch_id"));// 操作员
				reqMap.put("appid", SysCfg.getString("app.appid")); // 公众账号ID
			}

			/** reqMap的key值排序. */
			List<String> mapKeys = new ArrayList<String>();
			for (String key : reqMap.keySet()) {
				mapKeys.add(key);
			}
			Collections.sort(mapKeys);

			/** 生成签名. */
			StringBuffer sb = new StringBuffer();
			for (String key : mapKeys) {
				sb.append(key + "=" + reqMap.get(key) + "&");
			}
			sb.append("key=" + SysCfg.getString("weixin.pay.key"));
			String sign = MD5.getMD5Code(sb.toString()).toUpperCase();

			/** 生成请求xml. */
			String reqXml = "<xml>?</xml>";
			sb = new StringBuffer();
			for (String key : mapKeys) {
				sb.append("<" + key + ">" + reqMap.get(key) + "</" + key + ">\n");
			}
			sb.append("<sign>" + sign + "</sign>");
			reqXml = reqXml.replace("?", sb.toString());
			/** 请求下服务器,并解析响应结果. */
			logger.info("请求数据========================" + reqXml);
			logger.info("查看证书========================" + reqMap.get("mch_id"));
			String respXml = WxRefundSSL.post(reqXml, reqMap.get("mch_id"), order.getClientType());
			logger.info("返回结果respXml:" + respXml);

			Document doc = DocumentHelper.parseText(respXml);
			List<?> elements = doc.getRootElement().elements();
			Map<String, String> respMap = new HashMap<String, String>();
			for (Object et : elements) {
				respMap.put(((Element) et).getName(), ((Element) et).getText());
			}

			// 退款失败
			if (!respMap.get("return_code").equals("SUCCESS")) {
				throw new BusinessException(respMap.get("return_msg"));
			}
			if (!respMap.get("result_code").equals("SUCCESS")) {
				throw new BusinessException("退款失败，失败原因：" + respMap.get("err_code_des"));
			}
		}
		// 退款成功后，更新退款记录状态0.申请退款，1.退款成功，2.退款失败
		refundDto.setEndTime(new Date());
		refundDto.setStatus(1);// 设置退款成功状态
		refundDao.updateRefund(refundDto);

		// 退款成功后，更新订单状态为退款成功
		thirdPayOrderDto.setPayStatus(3);
		thirdPayOrderDao.updatePayStatusByOutTradeNoAndPatientId(thirdPayOrderDto);
	}

	/**
	 * 
	 * @number @description 定时请求15分钟内订单支付情况
	 * 
	 * @throws Exception
	 *
	 * @Date 2016年1月28日
	 */
	// @Override
	@Override
	public void reqWinxinOrderQuery() throws Exception {
		/** step1:查询未过期，并且未付款的订单 */
		ThirdPayOrderDto thirdPayOrderDto = new ThirdPayOrderDto();
		List<ThirdPayOrderDto> orders = thirdPayOrderDao.queryThirdOrder(thirdPayOrderDto);

		if (orders == null || orders.isEmpty()) {
			logger.info("ThirdPayOrderServiceImpl.reqWinxinOrderQuery###没有可处理订单");
			return;
		}

		for (int i = 0; i < orders.size(); i++) {
			Map<String, String> reqMap = new HashMap<String, String>();
			reqMap.put("nonce_str", MD5.getMD5Code(Util.getUniqueSn()));// 随机字符串
			reqMap.put("out_trade_no", orders.get(i).getOutTradeNo()); // 商户订单号
			if (orders.get(i).getClientType() == 0) {// 微信公众号支付
				reqMap.put("mch_id", SysCfg.getString("weixin.mch_id"));// 商户号
				reqMap.put("appid", SysCfg.getString("weixin.appid")); // 公众账号ID
			} else { // 微信APP支付
				reqMap.put("mch_id", SysCfg.getString("app.mch_id"));// 商户号
				reqMap.put("appid", SysCfg.getString("app.appid")); // 公众账号ID
			}

			/** reqMap的key值排序. */
			List<String> mapKeys = new ArrayList<String>();
			for (String key : reqMap.keySet()) {
				mapKeys.add(key);
			}
			Collections.sort(mapKeys);

			/** 生成签名. */
			StringBuffer sb = new StringBuffer();
			for (String key : mapKeys) {
				sb.append(key + "=" + reqMap.get(key) + "&");
			}
			sb.append("key=" + SysCfg.getString("weixin.pay.key"));
			String sign = MD5.getMD5Code(sb.toString()).toUpperCase();

			/** 生成请求xml. */
			String reqXml = "<xml>?</xml>";
			sb = new StringBuffer();
			for (String key : mapKeys) {
				sb.append("<" + key + ">" + reqMap.get(key) + "</" + key + ">\n");
			}
			sb.append("<sign>" + sign + "</sign>");
			reqXml = reqXml.replace("?", sb.toString());
			logger.info("请求微信服务器订单查询数据req------------>>>>>>>>:" + reqXml);
			/** 请求下服务器,并解析响应结果. */
			String respXml = HttpsUtil.post(SysCfg.getString("weixin.order.query"), reqXml);
			logger.info("请求服务器后响应结果respXml--------------------->>>>>>>>>>:" + respXml);
			Document doc = DocumentHelper.parseText(respXml);
			List<?> elements = doc.getRootElement().elements();
			Map<String, String> respMap = new HashMap<String, String>();
			for (Object et : elements) {
				respMap.put(((Element) et).getName(), ((Element) et).getText());
			}

			// 判断结果
			if (respMap.get("return_code").equals("SUCCESS")) {
				if (respMap.get("result_code").equals("SUCCESS") && respMap.get("trade_state").equals("SUCCESS")) {
					logger.info("ThirdPayOrderServiceImpl.refund==>return_code:" + respMap.get("return_code"));
					// 成功后查询订单，判断是否支付成功
					thirdPayOrderDto.setOutTradeNo(orders.get(i).getOutTradeNo());
					ThirdPayOrderDto order = thirdPayOrderDao.queryOrderDetailByOutTradeNo(thirdPayOrderDto);

					// 根据用户ID得到用户
					PatientDto patient = CacheContainer.byIdGetPatient(thirdPayOrderDto.getPatientId());
					synchronized (patient) {
						if (order.getPayStatus() == 0 && order.getDisplayStatus() == 1) {
							/** 更新订单. */
							order.setTransactionId(respMap.get("transaction_id"));
							order.setPayTime(new Date());
							order.setPayBank(respMap.get("bank_type"));
							order.setPayStatus(1);
							thirdPayOrderDao.updateWeixinOrder(order);

							order.setVisitCost(new BigDecimal(order.getTotalAmount()));// 设置挂号金额
							order.setNumSourceId(order.getServiceId());// 设置号源ID

							// 创建挂号记录
							createRegister(order);
							// 更新优惠卷状态
							if (order.getCouponId() != null) {
								CouponDto couponDto = new CouponDto();
								couponDto.setId(order.getCouponId());
								couponDto.setStatus(1);
								couponDao.updateCouponStatus(couponDto);
							}

							// 保存收支明细
							PatientAccountDetailDto patientAccountDetailDto = new PatientAccountDetailDto();
							patientAccountDetailDto.setPatientId(order.getPatientId());
							patientAccountDetailDto.setType(order.getOrderType());
							patientAccountDetailDto.setTransactionNum(order.getOutTradeNo());
							patientAccountDetailDto.setAmount(order.getTotalAmount());
							patientAccountDetailDto.setCreateTime(new Date());
							patientAccountDetailDao.savePatientAccountDetail(patientAccountDetailDto);

							// 请求院方接口，修改患者挂号记录
							BaseService commitOrder = new BaseService();
							commitOrder.getParams().put("hospitalId", order.getHospitalId().toString());
							commitOrder.getParams().put("numSourceId", order.getServiceId());
							commitOrder.getParams().put("payMode", order.getPayMode().toString());
							commitOrder.getParams().put("userName", order.getPatientName());
							commitOrder.getParams().put("userCardType", "1");// 1身份证
							commitOrder.getParams().put("userCardId", order.getCertificateNumber());
							commitOrder.getParams().put("userSex", order.getGender().toString());
							commitOrder.getParams().put("userBirthday", Util.DateToStr(IDCard.getBirthdayByCard(order.getCertificateNumber())));
							commitOrder.getParams().put("userPhone", order.getPatientPhone());

							commitOrder.packageData("commitOrder", PatientRegisterDto.class, SysCfg.getString("register.plus.url"));
							// 挂号失败
							if (!commitOrder.isSuccess()) {
								// 退款
								refund(thirdPayOrderDto);
								// 挂号失败，修改挂号计划为挂号失败，并设置为隐藏状态，该条挂号记录在数据相当于脏数据
								PatientRegisterDto patientReg = new PatientRegisterDto();
								patientReg.setOutTradeNo(thirdPayOrderDto.getOutTradeNo());
								patientRegisterDao.updatePatientRegisterFail(patientReg);
								// 终端提示
								RequestDto requestDto = new RequestDto();
								requestDto.setCmd("sendSystemMsg");
								Map<String, Object> params = new HashMap<String, Object>();
								Map<String, String> content = new HashMap<String, String>();
								content.put("cmd", "createWeixinPayOrder");
								content.put("message", "【791去就医】由于" + commitOrder.getDesc() + "，您的预约挂号已失效，挂号费用已原路返回。为您带来的不便，敬请谅解。");
								requestDto.setParams(params);
								params.put("target", order.getPatientId());
								params.put("targetType", 1);
								params.put("summary", "挂号失败");
								params.put("content", Constants.gson.toJson(content));
								params.put("weixinMsg", "【791去就医】由于" + commitOrder.getDesc() + "，您的预约挂号已失效，挂号费用已原路返回。为您带来的不便，敬请谅解。");
								URLInvoke.post(SysCfg.getString("qujiuyi.chatUrl"), Constants.gson.toJson(requestDto));
								return;
							}
							PatientRegisterDto patientRegister = (PatientRegisterDto) commitOrder.getDataObj();
							logger.info("ThirdPayOrderServieImpl.updateWeixinOrderByRespXml###success  result#visitNo:" + patientRegister.getVisitNo() + ";billNo:" + patientRegister.getBillNo() + ";orderNo:" + order.getOutTradeNo());
							patientRegister.setOutTradeNo(order.getOutTradeNo());
							patientRegisterDao.updatePatientRegister(patientRegister);

							// 短信提示
							String name = order.getPatientName();
							String date = new SimpleDateFormat("MM月dd日").format(order.getScheduleDate()) + Util.getReange(order.getTimeRange());// 获得日期
							String doctor = order.getDepartmentName() + order.getDoctorName() + order.getDoctorTitleName();
							String hospital = order.getHospitalName();
							String param = String.format("#name#=%s&#doctor#=%s&#date#=%s&#hospital#=%s", name + "\n", doctor + "\n", date + "\n", hospital + "\n");
							SmsService.instance().sendSms(order.getPatientPhone(), "9822", param);

							// 终端提示
							RequestDto requestDto = new RequestDto();
							requestDto.setCmd("sendSystemMsg");
							Map<String, Object> params = new HashMap<String, Object>();
							Map<String, String> content = new HashMap<String, String>();
							content.put("cmd", "createWeixinPayOrder");
							content.put("message", Util.getRegisterSms(name, doctor, date, hospital));
							requestDto.setParams(params);
							params.put("target", order.getPatientId());
							params.put("targetType", 1);
							params.put("summary", "恭喜您挂号成功");
							params.put("content", Constants.gson.toJson(content));
							params.put("weixinMsg", Util.getRegisterSms(name, doctor, date, hospital));
							URLInvoke.post(SysCfg.getString("qujiuyi.chatUrl"), Constants.gson.toJson(requestDto));
						}
					}
				}
				if (respMap.get("result_code").equals("SUCCESS") && respMap.get("trade_state").equals("NOTPAY")) {
					logger.info("ThirdOrderPayServiceImpl.reqWinxinOrderQuery订单未付款，订单号：" + respMap.get("out_trade_no") + "：描述：" + respMap.get("trade_state_desc"));
				}
				if (respMap.get("result_code").equals("SUCCESS") && respMap.get("trade_state").equals("REFUND")) {
					logger.info("ThirdOrderPayServiceImpl.reqWinxinOrderQuery订单转入退款，订单号：" + respMap.get("out_trade_no") + "：描述：" + respMap.get("trade_state_desc"));
				}
				if (respMap.get("result_code").equals("SUCCESS") && respMap.get("trade_state").equals("CLOSED")) {
					logger.info("ThirdOrderPayServiceImpl.reqWinxinOrderQuery订单交易已关闭，订单号：" + respMap.get("out_trade_no") + "：描述：" + respMap.get("trade_state_desc"));
				}
				if (respMap.get("result_code").equals("SUCCESS") && respMap.get("trade_state").equals("USERPAYING")) {
					logger.info("ThirdOrderPayServiceImpl.reqWinxinOrderQuery订单支付中....，订单号：" + respMap.get("out_trade_no") + "：描述：" + respMap.get("trade_state_desc"));
				}
				if (respMap.get("result_code").equals("SUCCESS") && respMap.get("trade_state").equals("PAYERROR")) {
					logger.info("ThirdOrderPayServiceImpl.reqWinxinOrderQuery支付失败....，订单号：" + respMap.get("out_trade_no") + "：描述：" + respMap.get("trade_state_desc"));
					// 终端提示
					RequestDto requestDto = new RequestDto();
					requestDto.setCmd("sendSystemMsg");
					Map<String, Object> params = new HashMap<String, Object>();
					Map<String, String> content = new HashMap<String, String>();
					content.put("cmd", "createWeixinPayOrder");
					content.put("message", "您的订单支付失败，" + respMap.get("trade_state_desc"));
					requestDto.setParams(params);
					params.put("target", orders.get(i).getPatientId());
					params.put("targetType", 1);
					params.put("summary", "挂号失败");
					params.put("content", Constants.gson.toJson(content));
					params.put("weixinMsg", "您的订单支付失败，" + respMap.get("trade_state_desc"));
					URLInvoke.post(SysCfg.getString("qujiuyi.chatUrl"), Constants.gson.toJson(requestDto));
				}
			}
		}
	}

	/**
	 * 
	 * @number @description 删除未付款的订单（脏数据）
	 * 
	 * @throws Exception
	 *
	 * @Date 2016年3月2日
	 */
	@Override
	public void deleteNoPayOrder() throws Exception {
		thirdPayOrderDao.deleteNoPayOrder();
	}
}