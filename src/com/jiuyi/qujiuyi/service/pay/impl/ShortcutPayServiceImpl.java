package com.jiuyi.qujiuyi.service.pay.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jiuyi.qujiuyi.common.dict.CacheContainer;
import com.jiuyi.qujiuyi.common.dict.Constants;
import com.jiuyi.qujiuyi.common.pay.ShortcutPayEnum;
import com.jiuyi.qujiuyi.common.pay.ShortcutPayUtil;
import com.jiuyi.qujiuyi.common.pay.ShortcutReqBean;
import com.jiuyi.qujiuyi.common.pay.ShortcutRespBean;
import com.jiuyi.qujiuyi.common.util.SysCfg;
import com.jiuyi.qujiuyi.common.util.URLInvoke;
import com.jiuyi.qujiuyi.common.util.Util;
import com.jiuyi.qujiuyi.dao.appointment.AppointmentCountDao;
import com.jiuyi.qujiuyi.dao.appointment.AppointmentDao;
import com.jiuyi.qujiuyi.dao.consult.ConsultDao;
import com.jiuyi.qujiuyi.dao.coupon.CouponDao;
import com.jiuyi.qujiuyi.dao.detail.PatientAccountDetailDao;
import com.jiuyi.qujiuyi.dao.doctor.DoctorDao;
import com.jiuyi.qujiuyi.dao.doctor.PersonalDoctorDao;
import com.jiuyi.qujiuyi.dao.order.OrderDao;
import com.jiuyi.qujiuyi.dao.patient.PatientDao;
import com.jiuyi.qujiuyi.dao.pay.BankDao;
import com.jiuyi.qujiuyi.dao.service.ServiceDao;
import com.jiuyi.qujiuyi.dto.appointment.AppointmentCountDto;
import com.jiuyi.qujiuyi.dto.appointment.AppointmentDto;
import com.jiuyi.qujiuyi.dto.common.RequestDto;
import com.jiuyi.qujiuyi.dto.common.ResponseDto;
import com.jiuyi.qujiuyi.dto.common.TokenDto;
import com.jiuyi.qujiuyi.dto.consult.ConsultDto;
import com.jiuyi.qujiuyi.dto.coupon.CouponDto;
import com.jiuyi.qujiuyi.dto.detail.PatientAccountDetailDto;
import com.jiuyi.qujiuyi.dto.doctor.DoctorDto;
import com.jiuyi.qujiuyi.dto.doctor.PersonalDoctorDto;
import com.jiuyi.qujiuyi.dto.order.OrderDto;
import com.jiuyi.qujiuyi.dto.patient.PatientDto;
import com.jiuyi.qujiuyi.dto.pay.BankDto;
import com.jiuyi.qujiuyi.dto.service.ServiceDto;
import com.jiuyi.qujiuyi.service.BusinessException;
import com.jiuyi.qujiuyi.service.pay.ShortcutPayService;

/**
 * @description 快捷支付业务层实现
 * @author zhb
 * @createTime 2015年5月7日
 */
@Service
public class ShortcutPayServiceImpl implements ShortcutPayService {
    private static final String SUCCESS = "C000000000";

    @Autowired
    private BankDao bankDao;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private PersonalDoctorDao personalDoctorDao;

    @Autowired
    private ServiceDao serviceDao;

    @Autowired
    private ConsultDao consultDao;

    @Autowired
    private PatientAccountDetailDao patientAccountDetailDao;

    @Autowired
    private CouponDao couponDao;

    @Autowired
    private PatientDao patientDao;

    @Autowired
    private DoctorDao doctorDao;

    @Autowired
    private AppointmentDao appointmentDao;

    @Autowired
    private AppointmentCountDao appointmentCountDao;

    /**
     * @description 查询银行列表
     * @param bankDto
     * @throws Exception
     */
    public ResponseDto queryBanks(BankDto bankDto) throws Exception {
        List<BankDto> banks = bankDao.queryBanks();
        if (banks != null && !banks.isEmpty()) {
            for (BankDto bank : banks) {
                bank.setLogoUrl(bank.getLogoUrl() != null ? SysCfg.getString("patient.bank.logoUrl.basePath") + bank.getLogoUrl() : null);
            }
        }

        ResponseDto responseDto = new ResponseDto();
        responseDto.setResultDesc("获取银行列表成功");
        Map<String, Object> detail = new HashMap<String, Object>();
        responseDto.setDetail(detail);
        detail.put("list", banks);
        return responseDto;
    }

    /**
     * @description 消费交易QP0001 (商户->快捷支付平台)
     * @param shortcutReqBean
     * @return
     * @throws Exception
     */
    public ResponseDto QP0001(ShortcutReqBean shortcutReqBean) throws Exception {
        /** step1:空异常处理. */
        if (shortcutReqBean == null) {
            throw new BusinessException(Constants.DATA_ERROR);
        }

        /** step2:校验订单是否存在. */
        OrderDto queryOrderDto = new OrderDto();
        queryOrderDto.setOrderNumber(shortcutReqBean.getMerOrderId());
        OrderDto orderDto = orderDao.queryOrderByOrderNumber(queryOrderDto);
        if (orderDto == null) {
            throw new BusinessException("订单不存在");
        }

        /** step3:校验订单是否已支付. */
        if (orderDto.getStatus() == 1) {
            throw new BusinessException("订单已支付");
        }

        /** step4:校验支付金额是正确. */
        Double amount = Util.isNotEmpty(shortcutReqBean.getAmount()) ? Double.parseDouble(shortcutReqBean.getAmount()) : 0;
        if (orderDto.getPayAmount() == null || !orderDto.getPayAmount().equals(amount)) {
            throw new BusinessException("支付金额不对");
        }

        /** step5:如果是私人医生,校验是否已购买过此服务. */
        if (orderDto.getOrderType() == 1 && orderDto.getServiceType() == 2) {
            PersonalDoctorDto personalDoctorDto = new PersonalDoctorDto();
            personalDoctorDto.setPatientId(orderDto.getPatientId());
            personalDoctorDto.setDoctorId(orderDto.getDoctorId());
            List<PersonalDoctorDto> list = personalDoctorDao.queryPersonalDoctorByPatientIdAndDoctorId(personalDoctorDto);
            if (list != null && !list.isEmpty()) {
                throw new BusinessException("此服务您已购买过了");
            }
        }

        /** step6:校验订单. */
        checkOrder(orderDto);

        /** step7:调用支付接口. */
        String token = shortcutReqBean.getToken();
        shortcutReqBean.setToken(null);
        ShortcutRespBean resp = ShortcutPayUtil.execute(shortcutReqBean, ShortcutPayEnum.QP0001);

        /** step8:更新订单支付状态. */
        if (SUCCESS.equals(resp.getRespCode())) {
            orderDto.setStatus(1);
            orderDto.setPayTime(new Date());
            orderDao.updateOrder(orderDto);
        }

        /** step9:更新一元一诊名额. */
        if (SUCCESS.equals(resp.getRespCode())) {
            DoctorDto doctorDto = new DoctorDto();
            doctorDto.setId(orderDto.getDoctorId());
            doctorDao.updateOneYuanNumberById(doctorDto);
        }

        /** step10:如果是购买私人医生服务,则新增私人医生记录. */
        if (SUCCESS.equals(resp.getRespCode()) && orderDto.getOrderType() == 1 && orderDto.getServiceType() == 2) {
            // 获取价格信息
            ServiceDto queryServiceDto = new ServiceDto();
            queryServiceDto.setDoctorId(orderDto.getDoctorId());
            queryServiceDto.setId(orderDto.getPatientId());
            List<ServiceDto> serviceList = serviceDao.queryPersonalDoctorServiceByDoctorId(queryServiceDto);
            Date createTime = new Date();
            Date expirationTime = null;
            if (serviceList != null && !serviceList.isEmpty()) {
                ServiceDto serviceDto = serviceList.get(0);
                if (serviceDto.getType() == 1) {
                    expirationTime = new Date(createTime.getTime() + 7 * 24 * 60 * 60 * 1000);
                } else if (serviceDto.getType() == 2) {
                    expirationTime = new Date(createTime.getTime() + 30 * 24 * 60 * 60 * 1000);
                }
            }
            PersonalDoctorDto personalDoctorDto = new PersonalDoctorDto();
            personalDoctorDto.setId(orderDto.getServiceId());
            personalDoctorDto.setPatientId(orderDto.getPatientId());
            personalDoctorDto.setDoctorId(orderDto.getDoctorId());
            personalDoctorDto.setCreateTime(createTime);
            personalDoctorDto.setExpirationTime(expirationTime);
            personalDoctorDao.createPersonalDoctor(personalDoctorDto);

            // 通知医生
            RequestDto requestDto = new RequestDto();
            requestDto.setCmd("personalDoctorRequest");
            requestDto.setToken(orderDto.getToken());
            Map<String, Object> params = new HashMap<String, Object>();
            requestDto.setParams(params);
            params.put("sender", orderDto.getPatientId());
            params.put("receiver", orderDto.getDoctorId());
            URLInvoke.post(SysCfg.getString("qujiuyi.chatUrl"), Constants.gson.toJson(requestDto));

            // 更新医生即将到账
            DoctorDto updateDoctorDto = new DoctorDto();
            updateDoctorDto.setId(orderDto.getDoctorId());
            updateDoctorDto.setAccountComing(orderDto.getTotalAmount());
            int updates = doctorDao.upadateDoctorAccountComming(updateDoctorDto);
            if (updates != 1) {
                throw new BusinessException("数据有误");
            }
        }

        /** step11:如果是购买图文咨询,则刷新咨询记录的支付状态和通知医生就诊. */
        if (SUCCESS.equals(resp.getRespCode()) && orderDto.getOrderType() == 1 && orderDto.getServiceType() == 1) {
            // 刷新支付状态
            ConsultDto consultDto = new ConsultDto();
            consultDto.setId(orderDto.getServiceId());
            consultDto.setPayStatus(Constants.OrderSatus.ORDER_STATUS_1);
            consultDao.updatePayStatus(consultDto);

            // 通知医生就诊
            RequestDto requestDto = new RequestDto();
            requestDto.setCmd("consultRequest");
            requestDto.setToken(token);
            Map<String, Object> params = new HashMap<String, Object>();
            requestDto.setParams(params);
            params.put("sender", orderDto.getPatientId());
            params.put("receiver", orderDto.getDoctorId());
            params.put("serviceId", orderDto.getServiceId());
            URLInvoke.post(SysCfg.getString("qujiuyi.chatUrl"), Constants.gson.toJson(requestDto));
        }

        /** step12:如果是预约加号,则更新支付状态和更新预约名额. */
        if (SUCCESS.equals(resp.getRespCode()) && orderDto.getOrderType() == 1 && orderDto.getServiceType() == 4) {
            // 刷新支付状态
            AppointmentDto appointmentDto = new AppointmentDto();
            appointmentDto.setId(Integer.parseInt(orderDto.getServiceId()));
            appointmentDto.setPayStatus(Constants.OrderSatus.ORDER_STATUS_1);
            appointmentDao.updatePayStatus(appointmentDto);

            // 更新预约名额
            appointmentDto = appointmentDao.getAppointmentById(appointmentDto);
            AppointmentCountDto appointmentCountDto = new AppointmentCountDto();
            appointmentCountDto.setDoctorId(appointmentDto.getDoctorId());
            appointmentCountDto.setDate(appointmentDto.getAppointmentDate());
            appointmentCountDto.setTimeZone(appointmentDto.getTimeZone());
            appointmentCountDto = appointmentCountDao.getAppointmentCount(appointmentCountDto);
            if (appointmentCountDto == null && appointmentDto.getPayStatus() == 1) {
                AppointmentCountDto insertAppointmentCountDto = new AppointmentCountDto();
                insertAppointmentCountDto.setDoctorId(appointmentDto.getDoctorId());
                insertAppointmentCountDto.setTimeZone(appointmentDto.getTimeZone());
                insertAppointmentCountDto.setUsedNumber(1);
                appointmentCountDao.insertAppointmentCount(insertAppointmentCountDto);
            } else if (appointmentCountDto != null && appointmentDto.getPayStatus() == 1) {
                appointmentCountDao.updateAppointmentCount(appointmentCountDto);
            }
        }

        /** step13:保存消费明细. */
        if (SUCCESS.equals(resp.getRespCode())) {
            PatientAccountDetailDto patientAccountDetailDto = new PatientAccountDetailDto();
            if (orderDto.getOrderType() == 1) {
                patientAccountDetailDto.setAmount((orderDto.getPayAmount() != null ? orderDto.getPayAmount() : 0)
                        + (orderDto.getBalance() != null ? orderDto.getBalance() : 0));
                patientAccountDetailDto.setType(orderDto.getServiceType());
            } else if (orderDto.getOrderType() == 2) {
                patientAccountDetailDto.setAmount(orderDto.getPayAmount());
                patientAccountDetailDto.setType(3);
            }
            patientAccountDetailDto.setPatientId(orderDto.getPatientId());
            patientAccountDetailDto.setTransactionNum(orderDto.getOrderNumber());
            patientAccountDetailDto.setCreateTime(new Date());
            patientAccountDetailDao.savePatientAccountDetail(patientAccountDetailDto);
        }

        /** step14:订单类型为服务购类型时,且余额使用不为空,则更新个人余额. */
        if (SUCCESS.equals(resp.getRespCode()) && orderDto.getOrderType() == 1 && orderDto.getBalance() != null && orderDto.getBalance() > 0) {
            PatientDto queryPatientDto = new PatientDto();
            queryPatientDto.setId(orderDto.getPatientId());
            PatientDto patientDto = patientDao.queryPatientById(queryPatientDto);
            patientDto.setBalance(patientDto.getBalance() - orderDto.getBalance());
            patientDao.updateBalance(patientDto);
            TokenDto tokenDto = CacheContainer.getToken(orderDto.getToken());
            tokenDto.getPatient().setBalance(patientDto.getBalance());
        }

        /** step15:订单类型为服务购类型时,且礼券使用不为空,则更礼券使用状态. */
        if (SUCCESS.equals(resp.getRespCode()) && orderDto.getOrderType() == 1 && orderDto.getCouponId() != null) {
            CouponDto queryCouponDto = new CouponDto();
            queryCouponDto.setId(orderDto.getCouponId());
            CouponDto couponDto = couponDao.queryCouponsById(queryCouponDto);
            if (couponDto != null) {
                couponDto.setStatus(1);
                couponDao.updateCouponStatus(couponDto);
            }
        }

        /** step16:返回结果. */
        ResponseDto responseDto = new ResponseDto();
        responseDto.setResultCode(SUCCESS.equals(resp.getRespCode()) ? 0 : 1);
        responseDto.setResultDesc(resp.getRespMsg());
        Map<String, Object> detail = new HashMap<String, Object>();
        responseDto.setDetail(detail);
        return responseDto;
    }

    /**
     * @description 快捷支付手机动态鉴权QP0002(商户->快捷支付平台)
     * @param shortcutReqBean
     * @return
     * @throws Exception
     */
    public ResponseDto QP0002(ShortcutReqBean shortcutReqBean) throws Exception {
        /** step1:空异常处理. */
        if (shortcutReqBean == null) {
            throw new BusinessException(Constants.DATA_ERROR);
        }

        /** step2:校验订单是否存在. */
        OrderDto queryOrderDto = new OrderDto();
        queryOrderDto.setOrderNumber(shortcutReqBean.getMerOrderId());
        OrderDto orderDto = orderDao.queryOrderByOrderNumber(queryOrderDto);
        if (orderDto == null) {
            throw new BusinessException("订单不存在");
        }

        /** step3:校验订单是否已支付. */
        if (orderDto.getStatus() == 1) {
            throw new BusinessException("订单已支付");
        }

        /** step4:校验支付金额是正确. */
        Double amount = Util.isNotEmpty(shortcutReqBean.getAmount()) ? Double.parseDouble(shortcutReqBean.getAmount()) : 0;
        if (orderDto.getPayAmount() == null || !orderDto.getPayAmount().equals(amount)) {
            throw new BusinessException("支付金额不对");
        }

        /** step5:校验订单. */
        checkOrder(orderDto);

        /** step6:调用支付接口. */
        shortcutReqBean.setToken(null);
        ShortcutRespBean resp = ShortcutPayUtil.execute(shortcutReqBean, ShortcutPayEnum.QP0002);

        /** step7:返回结果. */
        ResponseDto responseDto = new ResponseDto();
        responseDto.setResultCode(SUCCESS.equals(resp.getRespCode()) ? 0 : 1);
        responseDto.setResultDesc(resp.getRespMsg());
        Map<String, Object> detail = new HashMap<String, Object>();
        responseDto.setDetail(detail);
        detail.put("merOrderId", resp.getMerOrderId());
        detail.put("phoneToken", resp.getPhoneToken());
        return responseDto;
    }

    /**
     * @description 关闭快捷支付QP0003（商户->快捷支付平台）
     * @param shortcutReqBean
     * @return
     * @throws Exception
     */
    public ResponseDto QP0003(ShortcutReqBean shortcutReqBean) throws Exception {
        /** step1:空异常处理. */
        if (shortcutReqBean == null) {
            throw new BusinessException(Constants.DATA_ERROR);
        }

        /** step2:调用支付接口. */
        shortcutReqBean.setToken(null);
        shortcutReqBean.setMerOrderId(System.currentTimeMillis() + "");
        ShortcutRespBean resp = ShortcutPayUtil.execute(shortcutReqBean, ShortcutPayEnum.QP0003);

        /** step3:返回结果. */
        ResponseDto responseDto = new ResponseDto();
        responseDto.setResultCode(SUCCESS.equals(resp.getRespCode()) ? 0 : 1);
        responseDto.setResultDesc(resp.getRespMsg());
        Map<String, Object> detail = new HashMap<String, Object>();
        responseDto.setDetail(detail);
        return responseDto;
    }

    /**
     * @description 快捷支付客户卡信息查询QP0004(商户->快捷支付平台)
     * @param shortcutReqBean
     * @return
     * @throws Exception
     */
    public ResponseDto QP0004(ShortcutReqBean shortcutReqBean) throws Exception {
        /** step1:空异常处理. */
        if (shortcutReqBean == null) {
            throw new BusinessException(Constants.DATA_ERROR);
        }

        /** step2:调用支付接口. */
        shortcutReqBean.setToken(null);
        ShortcutRespBean resp = ShortcutPayUtil.execute(shortcutReqBean, ShortcutPayEnum.QP0004);

        /** step3:返回结果. */
        ResponseDto responseDto = new ResponseDto();
        responseDto.setResultCode(SUCCESS.equals(resp.getRespCode()) ? 0 : 1);
        responseDto.setResultDesc(resp.getRespMsg());
        Map<String, Object> detail = new HashMap<String, Object>();
        responseDto.setDetail(detail);
        detail.put("cardNum", resp.getCardNum());
        detail.put("cardInfos", resp.getCardInfos());
        return responseDto;
    }

    /**
     * @description 退货交易QP0005(商户->快捷支付平台，暂无)
     * @param shortcutReqBean
     * @return
     * @throws Exception
     */
    public ResponseDto QP0005(ShortcutReqBean shortcutReqBean) throws Exception {
        return null;
    }

    /**
     * @description 快捷支付交易流水查询QP0006(商户->快捷支付平台)
     * @param shortcutReqBean
     * @return
     * @throws Exception
     */
    public ResponseDto QP0006(ShortcutReqBean shortcutReqBean) throws Exception {
        return null;
    }

    /**
     * @description 快捷支付卡信息查询QP0007(商户->快捷支付平台)
     * @param shortcutReqBean
     * @return
     * @throws Exception
     */
    public ResponseDto QP0007(ShortcutReqBean shortcutReqBean) throws Exception {
        /** step1:空异常处理. */
        if (shortcutReqBean == null) {
            throw new BusinessException(Constants.DATA_ERROR);
        }

        /** step2:调用支付接口. */
        shortcutReqBean.setToken(null);
        ShortcutRespBean resp = ShortcutPayUtil.execute(shortcutReqBean, ShortcutPayEnum.QP0007);

        /** step3:返回结果. */
        ResponseDto responseDto = new ResponseDto();
        responseDto.setResultCode(SUCCESS.equals(resp.getRespCode()) ? 0 : 1);
        responseDto.setResultDesc(resp.getRespMsg());
        Map<String, Object> detail = new HashMap<String, Object>();
        responseDto.setDetail(detail);
        detail.put("cardType", resp.getCardType());
        detail.put("bankNo", resp.getBankNo());
        detail.put("bankNm", resp.getBankNm());
        return responseDto;
    }

    /**
     * @description 一键支付交易QP0008 (商户->快捷支付平台)
     * @param shortcutReqBean
     * @return
     * @throws Exception
     */
    public ResponseDto QP0008(ShortcutReqBean shortcutReqBean) throws Exception {
        return null;
    }

    /**
     * @description 一键支付限额查询QP0009 (商户->快捷支付平台)
     * @param shortcutReqBean
     * @return
     * @throws Exception
     */
    public ResponseDto QP0009(ShortcutReqBean shortcutReqBean) throws Exception {
        return null;
    }

    /**
     * @description 校验订单
     * @param orderDto
     * @throws Exception
     */
    private void checkOrder(OrderDto orderDto) throws Exception {
        ServiceDto serviceDto = null;
        CouponDto couponDto = null;
        if (orderDto != null && orderDto.getOrderType() != null && orderDto.getOrderType() == 1) {
            // 判断一元义诊是否开启
            DoctorDto doctorDto = null;
            if (orderDto.getServiceType() == 3) {
                DoctorDto queryDoctorDto = new DoctorDto();
                queryDoctorDto.setId(orderDto.getDoctorId());
                doctorDto = doctorDao.queryOneYuanDoctorById(queryDoctorDto);
                if (doctorDto == null) {
                    throw new BusinessException("一元义诊服务已关闭");
                }
            }
            // 判断一元义诊名额是否存在
            if (doctorDto != null && (doctorDto.getYiyuanyizhenNumber() == null || doctorDto.getYiyuanyizhenNumber() == 0)) {
                throw new BusinessException("一元义诊名额已使用完");
            }
            // 当为私人医生服务类型时,判断未购买,且没过期
            if (orderDto.getServiceType() == 2) {
                PersonalDoctorDto personalDoctorDto = new PersonalDoctorDto();
                personalDoctorDto.setPatientId(orderDto.getPatientId());
                personalDoctorDto.setDoctorId(orderDto.getDoctorId());
                List<PersonalDoctorDto> list = personalDoctorDao.queryPersonalDoctorByPatientIdAndDoctorId(personalDoctorDto);
                if (list != null && !list.isEmpty()) {
                    throw new BusinessException("此服务您已购买过了");
                }
            }
            // 当为图文咨询服务时,校验图文咨询id是不为空
            if (orderDto.getServiceType() == 1 && !Util.isNotEmpty(orderDto.getServiceId())) {
                throw new BusinessException("咨询id不能为空");
            }
            // 图文咨询系列校验
            if (orderDto.getServiceType() == 1 && Util.isNotEmpty(orderDto.getServiceId())) {
                // 不为空校验
                ConsultDto queryConsultDto = new ConsultDto();
                queryConsultDto.setId(orderDto.getServiceId());
                ConsultDto consultDto = consultDao.queryConsultById(queryConsultDto);
                if (consultDto == null) {
                    throw new BusinessException("咨询记录不存在");
                }
                // 判判图文咨询是否已支付
                if (consultDto.getPayStatus() != null && consultDto.getPayStatus() == 1) {
                    throw new BusinessException("此服务您已购买过了");
                }
            }
            // 判断服务类型是否已开通
            ServiceDto queryserviceDto = new ServiceDto();
            queryserviceDto.setDoctorId(orderDto.getDoctorId());
            if (orderDto.getServiceType() != null && (orderDto.getServiceType() == 1 || orderDto.getServiceType() == 3)) {
                List<ServiceDto> list = serviceDao.queryConsultServiceByDoctorId(queryserviceDto);
                serviceDto = list != null && !list.isEmpty() ? list.get(0) : null;
            } else if (orderDto.getServiceType() != null && orderDto.getServiceType() == 2) {
                queryserviceDto.setId(orderDto.getPriceId());
                List<ServiceDto> list = serviceDao.queryPersonalDoctorServiceByDoctorId(queryserviceDto);
                serviceDto = list != null && !list.isEmpty() ? list.get(0) : null;
            }
            if (serviceDto == null || (serviceDto.getStatus() != 1)) {
                throw new BusinessException("此服务暂未开通");
            }
            // 获取优惠券面值
            if (orderDto.getCouponId() != null) {
                CouponDto queryCouponDto = new CouponDto();
                queryCouponDto.setId(orderDto.getCouponId());
                couponDto = couponDao.queryCouponsById(queryCouponDto);
            }
            // 判断优惠券是否存在
            if (orderDto.getCouponId() != null && couponDto == null) {
                throw new BusinessException("优惠券不存在");
            }
            // 判断优惠券是否已过期
            if (couponDto != null && couponDto.getStatus() == 0 && couponDto.getExpireTime() != null
                    && couponDto.getExpireTime().getTime() < System.currentTimeMillis()) {
                throw new BusinessException("优惠券已过期");
            }
            // 判断支付金额是否正确
            if ((serviceDto != null && serviceDto.getPrice() != null) || doctorDto != null) {
                double payAmount = orderDto.getPayAmount() != null ? orderDto.getPayAmount() : 0;
                double balance = orderDto.getBalance() != null ? orderDto.getBalance() : 0;
                double couponAmount = couponDto != null && couponDto.getAmount() != null ? couponDto.getAmount().doubleValue() : 0;
                int totailAmount = serviceDto != null && serviceDto.getPrice() != null ? serviceDto.getPrice() : 0;
                totailAmount = doctorDto == null ? totailAmount : 1;
                if (payAmount + balance + couponAmount < totailAmount) {
                    throw new BusinessException("支付金额不对");
                }
                if (payAmount + balance > totailAmount) {
                    throw new BusinessException("支付金额不对");
                }
                if (payAmount + balance > 0 && couponAmount > totailAmount) {
                    throw new BusinessException("支付金额不对");
                }
                if (payAmount + balance > 0 && payAmount + balance + couponAmount > totailAmount) {
                    throw new BusinessException("支付金额不对");
                }
            }
        }
    }
}