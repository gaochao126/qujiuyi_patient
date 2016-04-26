package com.jiuyi.qujiuyi.common.dict;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;

import com.jiuyi.qujiuyi.common.util.MD5;
import com.jiuyi.qujiuyi.common.util.SysCfg;
import com.jiuyi.qujiuyi.common.util.Util;
import com.jiuyi.qujiuyi.dto.BaseDto;
import com.jiuyi.qujiuyi.dto.common.TokenDto;
import com.jiuyi.qujiuyi.dto.department.DepartmentDto;
import com.jiuyi.qujiuyi.dto.patient.PatientDto;
import com.jiuyi.qujiuyi.service.ad.AdService;
import com.jiuyi.qujiuyi.service.address.PatientAddressService;
import com.jiuyi.qujiuyi.service.appointment.AppointmentService;
import com.jiuyi.qujiuyi.service.area.AreaService;
import com.jiuyi.qujiuyi.service.auth.RealNameAuthService;
import com.jiuyi.qujiuyi.service.collect.PatientCollectService;
import com.jiuyi.qujiuyi.service.comment.CommentService;
import com.jiuyi.qujiuyi.service.consult.ConsultService;
import com.jiuyi.qujiuyi.service.coupon.CouponService;
import com.jiuyi.qujiuyi.service.department.DepartmentService;
import com.jiuyi.qujiuyi.service.detail.PatientAccountDetailService;
import com.jiuyi.qujiuyi.service.doctor.DoctorService;
import com.jiuyi.qujiuyi.service.doctor.PersonalDoctorService;
import com.jiuyi.qujiuyi.service.feedback.SuggestionFeedbackService;
import com.jiuyi.qujiuyi.service.guide.GuidingPatientService;
import com.jiuyi.qujiuyi.service.hospital.HospitalService;
import com.jiuyi.qujiuyi.service.invitation.InvitationVerificationService;
import com.jiuyi.qujiuyi.service.lottery.LotteryService;
import com.jiuyi.qujiuyi.service.medicine.MedicineService;
import com.jiuyi.qujiuyi.service.medicine.MedicineTypeService;
import com.jiuyi.qujiuyi.service.memo.PatientMemoService;
import com.jiuyi.qujiuyi.service.order.OrderService;
import com.jiuyi.qujiuyi.service.order.ThirdPayOrderService;
import com.jiuyi.qujiuyi.service.patient.PatientService;
import com.jiuyi.qujiuyi.service.pay.ShortcutPayService;
import com.jiuyi.qujiuyi.service.prescribe.PrescribeService;
import com.jiuyi.qujiuyi.service.prescription.PrescriptionService;
import com.jiuyi.qujiuyi.service.register.PatientRegisterService;
import com.jiuyi.qujiuyi.service.register.RegisterPlanService;
import com.jiuyi.qujiuyi.service.relative.PatientRelativeService;
import com.jiuyi.qujiuyi.service.scancode.ScanCodeService;
import com.jiuyi.qujiuyi.service.syscfg.SysCfgService;
import com.jiuyi.qujiuyi.service.withdrawal.WithdrawalService;

public class CacheContainer {
	private final static Logger logger = Logger.getLogger(CacheContainer.class);

	/** 业务map. */
	public static Map<String, Class<?>> serviceMap;

	/** not authentication map. */
	public static Map<String, Object> notAuthMap;

	/** token map. */
	private static ConcurrentHashMap<String, TokenDto> tokenMap;

	/** 用户id-token */
	private static ConcurrentHashMap<Integer, PatientDto> idPatient;

	/** 注册访问token. */
	private static ConcurrentHashMap<String, BaseDto> accessToken;

	/** 用于缓存推荐科室列表. */
	private static List<DepartmentDto> recommendDepartmentList;

	public static void init() {
		serviceMap = new HashMap<String, Class<?>>();
		notAuthMap = new HashMap<String, Object>();
		tokenMap = new ConcurrentHashMap<String, TokenDto>();
		accessToken = new ConcurrentHashMap<String, BaseDto>();
		idPatient = new ConcurrentHashMap<Integer, PatientDto>();

		/** service interface. */
		serviceMap.put("register", PatientService.class);
		serviceMap.put("signIn", PatientService.class);
		serviceMap.put("modifyPassword", PatientService.class);
		serviceMap.put("resetPassword", PatientService.class);
		serviceMap.put("signOut", PatientService.class);
		serviceMap.put("editPersonalInfo", PatientService.class);
		serviceMap.put("queryPersonalInfo", PatientService.class);
		serviceMap.put("isRegisted", PatientService.class);
		serviceMap.put("syncChannelIdToServer", PatientService.class);
		serviceMap.put("getVerifyCode", PatientService.class);
		serviceMap.put("bindWeixin", PatientService.class);
		serviceMap.put("setWithdrawalPassword", PatientService.class);
		serviceMap.put("checkVerificationCode", PatientService.class);
		serviceMap.put("queryDoctorListByDepartmentId", DoctorService.class);
		serviceMap.put("queryDoctorInfo", DoctorService.class);
		serviceMap.put("queryOneYuanDoctorList", DoctorService.class);
		serviceMap.put("searchDoctors", DoctorService.class);
		serviceMap.put("getHospitalDoctors", DoctorService.class);
		serviceMap.put("getHospitalDoctorDetail", DoctorService.class);
		serviceMap.put("getFamousDoctors", DoctorService.class);
		serviceMap.put("queryDoctorByConditions", DoctorService.class);
		serviceMap.put("queryDepartmentList", DepartmentService.class);
		serviceMap.put("queryRecommendDepartmentList", DepartmentService.class);
		serviceMap.put("collectDoctor", PatientCollectService.class);
		serviceMap.put("queryCollectDoctorList", PatientCollectService.class);
		serviceMap.put("deleteCollectDoctor", PatientCollectService.class);
		serviceMap.put("isCollectedDoctor", PatientCollectService.class);
		serviceMap.put("createRealNameAuth", RealNameAuthService.class);
		serviceMap.put("queryPersonalDoctors", PersonalDoctorService.class);
		serviceMap.put("isMyPersonalDoctor", PersonalDoctorService.class);
		serviceMap.put("queryMyConsult", ConsultService.class);
		serviceMap.put("createMyConsult", ConsultService.class);
		serviceMap.put("evaluateMyConsult", ConsultService.class);
		serviceMap.put("isConsultingWithTheDoctor", ConsultService.class);
		serviceMap.put("queryEvaluationsByDoctorId", ConsultService.class);
		serviceMap.put("queryConsultDetail", ConsultService.class);
		serviceMap.put("deleteConsult", ConsultService.class);
		serviceMap.put("cancelConsult", ConsultService.class);
		serviceMap.put("getCurrentConsult", ConsultService.class);
		serviceMap.put("queryNoReadMessageCount", ConsultService.class);
		serviceMap.put("createFreeConsult", ConsultService.class);
		serviceMap.put("addRelative", PatientRelativeService.class);
		serviceMap.put("delRelative", PatientRelativeService.class);
		serviceMap.put("modRelative", PatientRelativeService.class);
		serviceMap.put("queryRelatives", PatientRelativeService.class);
		serviceMap.put("addMemo", PatientMemoService.class);
		serviceMap.put("delMemo", PatientMemoService.class);
		serviceMap.put("modMemo", PatientMemoService.class);
		serviceMap.put("queryMemos", PatientMemoService.class);
		serviceMap.put("queryBanks", ShortcutPayService.class);
		serviceMap.put("QP0001", ShortcutPayService.class);
		serviceMap.put("QP0002", ShortcutPayService.class);
		serviceMap.put("QP0003", ShortcutPayService.class);
		serviceMap.put("QP0004", ShortcutPayService.class);
		serviceMap.put("QP0005", ShortcutPayService.class);
		serviceMap.put("QP0006", ShortcutPayService.class);
		serviceMap.put("QP0007", ShortcutPayService.class);
		serviceMap.put("QP0008", ShortcutPayService.class);
		serviceMap.put("QP0009", ShortcutPayService.class);
		serviceMap.put("queryVersion", SysCfgService.class);
		serviceMap.put("queryAboutMe", SysCfgService.class);
		serviceMap.put("queryHelpUrl", SysCfgService.class);
		serviceMap.put("createOrder", OrderService.class);
		serviceMap.put("deleteOrder", OrderService.class);
		serviceMap.put("queryOrderList", OrderService.class);
		serviceMap.put("queryOrderDetail", OrderService.class);
		serviceMap.put("isValidOrder", OrderService.class);
		serviceMap.put("queryCouponsByPatientId", CouponService.class);
		serviceMap.put("queryCoupon", CouponService.class);
		serviceMap.put("queryAds", AdService.class);
		serviceMap.put("createSuggestionFeedback", SuggestionFeedbackService.class);
		serviceMap.put("alipayWithdrawal", WithdrawalService.class);
		serviceMap.put("bankWithdrawal", WithdrawalService.class);
		serviceMap.put("queryWithdrawalRules", WithdrawalService.class);
		serviceMap.put("queryPatientAccountDetail", PatientAccountDetailService.class);
		serviceMap.put("invitationVerification", InvitationVerificationService.class);
		serviceMap.put("queryMedicineList", MedicineService.class);
		serviceMap.put("queryMedicineTypeList", MedicineTypeService.class);
		serviceMap.put("createPrescribe", PrescribeService.class);
		serviceMap.put("queryPrescribeDetail", PrescribeService.class);
		serviceMap.put("getPrescribeListByPatientId", PrescribeService.class);
		serviceMap.put("delPrescribe", PrescribeService.class);
		serviceMap.put("createPatientAppointment", AppointmentService.class);
		serviceMap.put("getAppointmentList", AppointmentService.class);
		serviceMap.put("getHospitalList", HospitalService.class);
		serviceMap.put("hospitalDetail", HospitalService.class);
		serviceMap.put("getDoctorRegisterPlan", RegisterPlanService.class);
		serviceMap.put("createRegister", PatientRegisterService.class);
		serviceMap.put("getPatientRegisterList", PatientRegisterService.class);
		serviceMap.put("delRegister", PatientRegisterService.class);
		serviceMap.put("cancelRegister", PatientRegisterService.class);
		serviceMap.put("createWeixinPayOrder", ThirdPayOrderService.class);
		serviceMap.put("queryOrderDetailByOutTradeNo", ThirdPayOrderService.class);
		serviceMap.put("patientCommentDoctorService", CommentService.class);
		serviceMap.put("getMyComments", CommentService.class);
		serviceMap.put("getCommentsByDoctor", CommentService.class);
		serviceMap.put("createPatientAddr", PatientAddressService.class);
		serviceMap.put("deletePatientAddr", PatientAddressService.class);
		serviceMap.put("updatePatientAddr", PatientAddressService.class);
		serviceMap.put("queryPatientAddrList", PatientAddressService.class);
		serviceMap.put("queryPatientAddr", PatientAddressService.class);
		serviceMap.put("getCityList", AreaService.class);
		serviceMap.put("getProvinceList", AreaService.class);
		serviceMap.put("getTownList", AreaService.class);
		serviceMap.put("recivePrescription", PrescriptionService.class);
		serviceMap.put("requestRefund", ThirdPayOrderService.class);
		serviceMap.put("queryPrescriptionList", PrescriptionService.class);
		serviceMap.put("queryPrescriptionDetail", PrescriptionService.class);
		serviceMap.put("queryFreeConsultNoUseCount", ConsultService.class);
		serviceMap.put("detelePrescription", PrescriptionService.class);
		serviceMap.put("queryProvinceCascade", AreaService.class);
		serviceMap.put("queryCityByProvinceCascade", AreaService.class);
		serviceMap.put("queryTownByCityCascade", AreaService.class);
		serviceMap.put("getLotteryLimits", LotteryService.class);
		serviceMap.put("queryNewLotteryResult", LotteryService.class);
		serviceMap.put("queryJoinLottery", LotteryService.class);
		serviceMap.put("queryLotteryResultByLotteryNo", LotteryService.class);
		serviceMap.put("removeWeixinBind", PatientService.class);
		serviceMap.put("exitLottery", LotteryService.class);
		serviceMap.put("queryBodyPart", GuidingPatientService.class);
		serviceMap.put("queryIllness", GuidingPatientService.class);
		serviceMap.put("getDoctorRegisterPlanPlus", RegisterPlanService.class);
		serviceMap.put("getPatientRegisterListPlus", PatientRegisterService.class);
		serviceMap.put("cancelRegisterPlus", PatientRegisterService.class);
		serviceMap.put("getPatientRegisterLisPlust", PatientRegisterService.class);
		serviceMap.put("fetchNumber", PatientRegisterService.class);
		serviceMap.put("getLineInfo", PatientRegisterService.class);
		serviceMap.put("comfirmPrescription", PrescriptionService.class);
		serviceMap.put("checkIsRegister", PatientRegisterService.class);
		serviceMap.put("collectHospital", PatientCollectService.class);
		serviceMap.put("deleteCollectHospital", PatientCollectService.class);
		serviceMap.put("queryCollectHospitalList", PatientCollectService.class);
		serviceMap.put("checkHospitalIsCollect", PatientCollectService.class);
		serviceMap.put("queryRegisterPlanDetail", PatientRegisterService.class);
		serviceMap.put("scanCode", ScanCodeService.class);
		serviceMap.put("stopRegister", PatientRegisterService.class);
		serviceMap.put("onceAgain", PrescriptionService.class);
		serviceMap.put("queryDepart", DepartmentService.class);
		serviceMap.put("hospitalScanCode", ScanCodeService.class);

		/** not authentication map. */
		notAuthMap.put("register", null);
		notAuthMap.put("signIn", null);
		notAuthMap.put("resetPassword", null);
		notAuthMap.put("isRegisted", null);
		notAuthMap.put("queryDepartmentList", null);
		notAuthMap.put("queryDoctorListByDepartmentId", null);
		notAuthMap.put("queryOneYuanDoctorList", null);
		notAuthMap.put("queryRecommendDepartmentList", null);
		notAuthMap.put("searchDoctors", null);
		notAuthMap.put("queryDoctorInfo", null);
		notAuthMap.put("getVerifyCode", null);
		notAuthMap.put("bindWeixin", null);
		notAuthMap.put("queryBanks", null);
		notAuthMap.put("queryVersion", null);
		notAuthMap.put("queryAboutMe", null);
		notAuthMap.put("queryHelpUrl", null);
		notAuthMap.put("queryAds", null);
		notAuthMap.put("queryEvaluationsByDoctorId", null);
		notAuthMap.put("queryWithdrawalRules", null);
		notAuthMap.put("queryMedicineList", null);
		notAuthMap.put("getHospitalList", null);
		notAuthMap.put("getHospitalDoctors", null);
		notAuthMap.put("getDoctorRegisterPlan", null);
		notAuthMap.put("getHospitalDoctorDetail", null);
		notAuthMap.put("checkVerificationCode", null);
		notAuthMap.put("queryMedicineTypeList", null);
		notAuthMap.put("getProvinceList", null);
		notAuthMap.put("getCityList", null);
		notAuthMap.put("getTownList", null);
		notAuthMap.put("getFamousDoctors", null);
		notAuthMap.put("getCommentsByDoctor", null);
		notAuthMap.put("queryDoctorByConditions", null);
		notAuthMap.put("hospitalDetail", null);
		notAuthMap.put("queryProvinceCascade", null);
		notAuthMap.put("queryCityByProvinceCascade", null);
		notAuthMap.put("queryTownByCityCascade", null);
		notAuthMap.put("queryBodyPart", null);
		notAuthMap.put("queryIllness", null);
		notAuthMap.put("getDoctorRegisterPlanPlus", null);
		notAuthMap.put("scanCode", null);
		notAuthMap.put("stopRegister", null);
		notAuthMap.put("queryDepart", null);
		notAuthMap.put("hospitalScanCode", null);

		/** update recommend department list. */
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					logger.info("CacheContainer.init#update recommend department list");
					recommendDepartmentList = Constants.applicationContext.getBean(DepartmentService.class).refreshRecommendDepartmentList();
				} catch (Exception e) {
					logger.error("CacheContainer.init#update recommend department list error", e);
				}
			}
		}, 0, 30 * 60 * 1000);

		/** update token map . */
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				for (String key : tokenMap.keySet()) {
					TokenDto token = tokenMap.get(key);
					if (token == null) {
						continue;
					}
					if (System.currentTimeMillis() - token.getUpdateTime() > 24 * 60 * 60 * 1000) {
						tokenMap.remove(key);
					}
				}

				for (String k : accessToken.keySet()) {
					BaseDto base = accessToken.get(k);
					if (base == null) {
						continue;
					}
					if (System.currentTimeMillis() - base.getCurrentTimes() > 5 * 60 * 1000) {
						accessToken.remove(k);
					}
				}
			}
		}, 0, 60 * 1000);
	}

	/**
	 * @description 获取token,现在内存里面找，如果找不到再到数据库中查找
	 * @param key
	 * @return
	 */
	public static TokenDto getToken(String key) {
		return getToken(key, "1", false);
	}

	/**
	 * @description 获取patient,现在内存里面找，如果找不到再到数据库中查找
	 * @param key
	 * @return
	 * @throws Exception
	 * @throws BeansException
	 */
	public static PatientDto byIdGetPatient(Integer key) throws BeansException, Exception {
		PatientDto patient = idPatient.get(key);
		if (patient != null) {
			return patient;
		}
		PatientDto patientDto = new PatientDto();
		patientDto.setId(key);
		PatientDto dto = Constants.applicationContext.getBean(PatientService.class).queryPatient(patientDto);
		if (dto != null) {
			dto = idPatient.putIfAbsent(key, dto);
		}
		return dto;
	}

	/**
	 * @description 获取token,现在内存里面找，如果找不到再到数据库中查找
	 * @param key
	 * @return
	 */
	public static TokenDto getToken(String key, String deviceType, boolean synToChatServer) {
		TokenDto tokenDto = tokenMap.get(key);
		if (tokenDto != null) {
			return tokenDto;
		}
		String md5OfToken = MD5.getMD5Code(key);
		PatientDto patientDto = Constants.applicationContext.getBean(PatientService.class).queryPatientByToken(md5OfToken);
		if (patientDto != null) {
			tokenDto = new TokenDto();
			tokenDto.setToken(key);
			tokenDto.setUpdateTime(System.currentTimeMillis());
			patientDto.setHaveWithdrawalPassword(Util.isNotEmpty(patientDto.getWithdrawalPassword()));
			patientDto.setWithdrawalPassword(null);
			tokenDto.setPatient(patientDto);
			CacheContainer.saveToken(key, tokenDto);
			tokenDto = tokenMap.put(key, tokenDto);
			if (synToChatServer) {
				patientDto.setDeviceType(Integer.parseInt(deviceType));
				// 同步到聊天服
				try {
					Constants.applicationContext.getBean(PatientService.class).syncLoginInfoToServer(patientDto);
				} catch (Exception e) {
					logger.error("<<CacheContainer>> function getToken syncLoginInfoToServer err", e);
				}
			}
		}
		return tokenDto;
	}

	/**
	 * @description 删除token
	 * @param key
	 */
	public static void removeToken(String key) {
		if (tokenMap.containsKey(key)) {
			tokenMap.remove(key);
		}
	}

	/**
	 * @description 保存token
	 * @param key
	 */
	public static void saveToken(String key, TokenDto tokenDto) {
		tokenDto.getPatient().setMd5Id(MD5.getMD5Code(tokenDto.getPatient().getId().toString()));
		tokenDto.getPatient().setMd5Password(MD5.getMD5Code(tokenDto.getPatient().getPassword()));
		if (Util.isNotEmpty(tokenDto.getPatient().getHeadPortrait()) && !tokenDto.getPatient().getHeadPortrait().startsWith("http")) {
			tokenDto.getPatient().setHeadPortrait(SysCfg.getString("patient.head.virtualUrl") + tokenDto.getPatient().getHeadPortrait());
		}
		tokenDto.getPatient().setPassword(null);
		tokenMap.put(key, tokenDto);
		idPatient.put(tokenDto.getPatient().getId(), tokenDto.getPatient());
	}

	/**
	 * @description 根据患者更新token
	 * @param patient
	 */
	public static void updateTokenByPatient(PatientDto patient) {
		for (String key : tokenMap.keySet()) {
			TokenDto token = tokenMap.get(key);
			if (token != null) {
				PatientDto dto = token.getPatient();
				if (dto != null && dto.getId() != null && dto.getId().equals(patient.getId())) {
					token.setPatient(patient);
					patient.setMd5Id(MD5.getMD5Code(patient.getId().toString()));
					patient.setMd5Password(MD5.getMD5Code(patient.getPassword()));
					patient.setPassword(null);
				}
			}
		}
	}

	/**
	 * @description 获取token
	 * @param key
	 * @return
	 */
	public static BaseDto getAccessToken(String key) {
		return accessToken.get(key);
	}

	/**
	 * @description 删除token
	 * @param key
	 */
	public static void removeAccessToken(String key) {
		if (accessToken.containsKey(key)) {
			accessToken.remove(key);
		}
	}

	/**
	 * @description 添加token
	 * @param key
	 * @param value
	 */
	public static void putAccessToken(String key, BaseDto baseDto) {
		accessToken.put(key, baseDto);
	}

	/**
	 * @description 获取推荐科室列表
	 * @return
	 */
	public static List<DepartmentDto> getRecommendDepartmentList() {
		return recommendDepartmentList;
	}
}