package com.jiuyi.qujiuyi.service.order.impl;

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
import com.jiuyi.qujiuyi.dao.appointment.AppointmentDao;
import com.jiuyi.qujiuyi.dao.consult.ConsultDao;
import com.jiuyi.qujiuyi.dao.coupon.CouponDao;
import com.jiuyi.qujiuyi.dao.detail.PatientAccountDetailDao;
import com.jiuyi.qujiuyi.dao.doctor.DoctorDao;
import com.jiuyi.qujiuyi.dao.doctor.PersonalDoctorDao;
import com.jiuyi.qujiuyi.dao.order.OrderDao;
import com.jiuyi.qujiuyi.dao.patient.PatientDao;
import com.jiuyi.qujiuyi.dao.service.ServiceDao;
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
import com.jiuyi.qujiuyi.dto.service.ServiceDto;
import com.jiuyi.qujiuyi.service.BusinessException;
import com.jiuyi.qujiuyi.service.order.OrderService;

/**
 * @description 订单业务层实现
 * @author zhb
 * @createTime 2015年5月9日
 */
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderDao orderDao;

    @Autowired
    private ServiceDao serviceDao;

    @Autowired
    private CouponDao couponDao;

    @Autowired
    private PatientDao patientDao;

    @Autowired
    private PersonalDoctorDao personalDoctorDao;

    @Autowired
    private ConsultDao consultDao;

    @Autowired
    private PatientAccountDetailDao patientAccountDetailDao;

    @Autowired
    private DoctorDao doctorDao;

    @Autowired
    private AppointmentDao appointmentDao;

    /**
     * @description 创建订单
     * @param orderDto
     * @return
     * @throws Exception
     */
    public ResponseDto createOrder(OrderDto orderDto) throws Exception {
        if (orderDto == null) {
            throw new BusinessException(Constants.DATA_ERROR);
        }

        TokenDto token = CacheContainer.getToken(orderDto.getToken());
        PatientDto patient = token != null && token.getPatient() != null ? token.getPatient() : new PatientDto();
        if (orderDto.getPatientId() == null || !orderDto.getPatientId().equals(patient.getId())) {
            throw new BusinessException("非本人操作");
        }

        // 设置订单号
        orderDto.setOrderNumber(Util.getUniqueSn());

        if (orderDto.getOrderType() != null && orderDto.getOrderType() == 1) {// 服务购买类型
            DoctorDto queryDoctorDto1 = new DoctorDto();
            queryDoctorDto1.setDoctorPhone(patient.getPhone());
            List<DoctorDto> doctorList = doctorDao.queryDoctorByPhone(queryDoctorDto1);
            if (doctorList != null && !doctorList.isEmpty() && doctorList.get(0).getId().equals(orderDto.getDoctorId())) {
                throw new BusinessException("您不能享用自己提供的服务");
            }
            if (orderDto.getDoctorId() == null) {
                throw new BusinessException("医生ID不能为空");
            }
            if (orderDto.getServiceType() == null) {
                throw new BusinessException("服务类型不能为空");
            }
            if (orderDto.getPriceId() == null) {
                throw new BusinessException("价格ID不能为空");
            }

            switch (orderDto.getServiceType()) {
            case 1:// 图文咨询服务
                return bayConsultService(orderDto);
            case 2:// 私人医生服务
                return bayPersonalDoctorService(orderDto);
            case 3:// 一无义诊服务
                return bayYiyuanyizhenService(orderDto);
            case 4:// 预约加号服务
                return bayJiaHaoService(orderDto);
            default:
                throw new BusinessException("无效的服务类型");
            }
        } else if (orderDto.getOrderType() != null && orderDto.getOrderType() == 2) {// 余客充值
            // 金额有效性判断
            if (orderDto.getPayAmount() == null || orderDto.getPayAmount() <= 0) {
                throw new BusinessException("请有效填写充值金额");
            }
            // 创建订单
            orderDto.setCreateTime(new Date());
            orderDto.setStatus(0);
            orderDto.setTotalAmount(orderDto.getPayAmount());
            orderDao.createOrder(orderDto);
            // 返回结果
            ResponseDto responseDto = new ResponseDto();
            responseDto.setResultDesc("订单创建成功");
            Map<String, Object> detail = new HashMap<String, Object>();
            detail.put("status", "0");
            responseDto.setDetail(detail);
            return responseDto;
        } else {
            throw new BusinessException("订单类型无效");
        }
    }

    /**
     * @description 购买图文咨询服务
     * @param orderDto
     * @return
     * @throws Exception
     */
    private ResponseDto bayConsultService(OrderDto orderDto) throws Exception {
        CouponDto couponDto = null;
        PatientDto patientDto = null;
        ServiceDto serviceDto = null;
        double totailAmount = 0;
        boolean pay = false;

        // 判断服务是否已开通
        serviceDto = new ServiceDto();
        serviceDto.setDoctorId(orderDto.getDoctorId());
        List<ServiceDto> list = serviceDao.queryConsultServiceByDoctorId(serviceDto);
        serviceDto = list != null && !list.isEmpty() ? list.get(0) : null;
        if (serviceDto == null || serviceDto.getStatus() != 1) {
            throw new BusinessException("此服务暂未开通");
        }
        // 判断咨询id
        if (orderDto.getServiceId() == null) {
            throw new BusinessException("咨询id不能为空");
        }
        // 判断咨询记录是否存在
        ConsultDto queryConsultDto = new ConsultDto();
        queryConsultDto.setId(orderDto.getServiceId());
        ConsultDto consultDto = consultDao.queryConsultById(queryConsultDto);
        if (consultDto == null) {
            throw new BusinessException("咨询记录不存在");
        }
        // 判判图文咨询是否已支付
        if (consultDto.getPayStatus() != null && consultDto.getPayStatus() == 1) {
            throw new BusinessException("此服务您已支付");
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
        // 判断优惠券是否已被使用
        if (couponDto != null && couponDto.getStatus() != 0) {
            throw new BusinessException("优惠券已被使用");
        }
        // 判断优惠券是否已过期
        if (couponDto != null && couponDto.getStatus() == 0 && couponDto.getExpireTime() != null
                && couponDto.getExpireTime().getTime() < System.currentTimeMillis()) {
            throw new BusinessException("优惠券已过期");
        }
        // 判断余额是否有效
        if (orderDto.getBalance() != null && orderDto.getBalance() > 0) {
            PatientDto queryPatientDto = new PatientDto();
            queryPatientDto.setId(orderDto.getPatientId());
            patientDto = patientDao.queryPatientById(queryPatientDto);
            if (patientDto.getBalance() == null || orderDto.getBalance() > patientDto.getBalance()) {
                throw new BusinessException("余额不足");
            }
        }
        double payAmount = orderDto.getPayAmount() != null ? orderDto.getPayAmount() : 0;
        double balance = orderDto.getBalance() != null ? orderDto.getBalance() : 0;
        double couponAmount = couponDto != null && couponDto.getAmount() != null ? couponDto.getAmount().doubleValue() : 0;
        totailAmount = serviceDto != null && serviceDto.getPrice() != null ? serviceDto.getPrice() : 0;
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
        if (payAmount == 0 && (balance + couponAmount == totailAmount || (balance == 0 && couponAmount >= totailAmount))) {// 余额+礼券支付类型
            pay = true;
        }

        if (pay) {
            // 刷新支付状态
            consultDto.setId(orderDto.getServiceId());
            consultDto.setPayStatus(Constants.OrderSatus.ORDER_STATUS_1);
            consultDao.updatePayStatus(consultDto);

            // 更新个人余额
            if (orderDto.getPayAmount() != null && orderDto.getPayAmount() > 0) {
                patientDto.setBalance(patientDto.getBalance() - orderDto.getBalance());
                patientDao.updateBalance(patientDto);
                CacheContainer.getToken(orderDto.getToken()).getPatient().setBalance(patientDto.getBalance());
            }

            // 更新礼卷使用状态
            if (orderDto.getCouponId() != null) {
                couponDto.setStatus(1);
                couponDao.updateCouponStatus(couponDto);
            }

            // 保存收支明细
            PatientAccountDetailDto patientAccountDetailDto = new PatientAccountDetailDto();
            patientAccountDetailDto.setPatientId(orderDto.getPatientId());
            patientAccountDetailDto.setType(orderDto.getServiceType());
            patientAccountDetailDto.setTransactionNum(orderDto.getOrderNumber());
            patientAccountDetailDto.setAmount(orderDto.getBalance());
            patientAccountDetailDto.setCreateTime(new Date());
            patientAccountDetailDao.savePatientAccountDetail(patientAccountDetailDto);

            // 通知医生就诊
            RequestDto requestDto = new RequestDto();
            requestDto.setCmd("consultRequest");
            requestDto.setToken(orderDto.getToken());
            Map<String, Object> params = new HashMap<String, Object>();
            requestDto.setParams(params);
            params.put("sender", orderDto.getPatientId());
            params.put("receiver", orderDto.getDoctorId());
            params.put("serviceId", orderDto.getServiceId());
            URLInvoke.post(SysCfg.getString("qujiuyi.chatUrl"), Constants.gson.toJson(requestDto));
        }

        // 创建订单
        orderDto.setCreateTime(new Date());
        orderDto.setStatus(pay ? 1 : 0);
        orderDto.setPayTime(pay ? new Date() : null);
        orderDto.setTotalAmount(totailAmount);
        orderDao.createOrder(orderDto);

        ResponseDto responseDto = new ResponseDto();
        responseDto.setResultDesc("订单创建成功");
        Map<String, Object> detail = new HashMap<String, Object>();
        detail.put("orderNumber", orderDto.getOrderNumber());
        detail.put("status", pay ? "1" : "0");
        responseDto.setDetail(detail);
        return responseDto;
    }

    /**
     * @description 购买一元一诊咨询服务
     * @param orderDto
     * @return
     * @throws Exception
     */
    private ResponseDto bayYiyuanyizhenService(OrderDto orderDto) throws Exception {
        CouponDto couponDto = null;
        PatientDto patientDto = null;
        ServiceDto serviceDto = null;
        double totailAmount = 0;
        boolean pay = false;

        // 判断服务是否已开通
        serviceDto = new ServiceDto();
        serviceDto.setDoctorId(orderDto.getDoctorId());
        List<ServiceDto> list = serviceDao.queryConsultServiceByDoctorId(serviceDto);
        serviceDto = list != null && !list.isEmpty() ? list.get(0) : null;
        if (serviceDto == null || serviceDto.getStatus() != 1) {
            throw new BusinessException("此服务暂未开通");
        }
        // 判断一元义诊服务是否已开始
        DoctorDto doctorDto = new DoctorDto();
        doctorDto.setId(orderDto.getDoctorId());
        doctorDto = doctorDao.queryOneYuanDoctorById(doctorDto);
        if (doctorDto == null) {
            throw new BusinessException("该医生未开启一元义诊服务");
        }
        // 判断一元义诊名额是否已满
        if (doctorDto.getYiyuanyizhenNumber() == null || doctorDto.getYiyuanyizhenNumber() == 0) {
            throw new BusinessException("一元义诊名额已使用完");
        }
        // 判断咨询记录是否存在
        ConsultDto queryConsultDto = new ConsultDto();
        queryConsultDto.setId(orderDto.getServiceId());
        ConsultDto consultDto = consultDao.queryConsultById(queryConsultDto);
        if (consultDto == null) {
            throw new BusinessException("咨询记录不存在");
        }
        // 判判图文咨询是否已支付
        if (consultDto.getPayStatus() != null && consultDto.getPayStatus() == 1) {
            throw new BusinessException("此服务您已支付");
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
        // 判断优惠券是否已被使用
        if (couponDto != null && couponDto.getStatus() != 0) {
            throw new BusinessException("优惠券已被使用");
        }
        // 判断优惠券是否已过期
        if (couponDto != null && couponDto.getStatus() == 0 && couponDto.getExpireTime() != null
                && couponDto.getExpireTime().getTime() < System.currentTimeMillis()) {
            throw new BusinessException("优惠券已过期");
        }
        // 判断余额是否有效
        if (orderDto.getBalance() != null && orderDto.getBalance() > 0) {
            PatientDto queryPatientDto = new PatientDto();
            queryPatientDto.setId(orderDto.getPatientId());
            patientDto = patientDao.queryPatientById(queryPatientDto);
            if (patientDto.getBalance() == null || orderDto.getBalance() > patientDto.getBalance()) {
                throw new BusinessException("余额不足");
            }
        }
        double payAmount = orderDto.getPayAmount() != null ? orderDto.getPayAmount() : 0;
        double balance = orderDto.getBalance() != null ? orderDto.getBalance() : 0;
        double couponAmount = couponDto != null && couponDto.getAmount() != null ? couponDto.getAmount().doubleValue() : 0;
        totailAmount = 1;
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
        if (payAmount == 0 && (balance + couponAmount == totailAmount || (balance == 0 && couponAmount >= totailAmount))) {// 余额+礼券支付类型
            pay = true;
        }
        // 创建订单
        String orderNumber = Util.getOrderNumber();
        orderDto.setOrderNumber(orderNumber);
        orderDto.setCreateTime(new Date());
        orderDto.setStatus(pay ? 1 : 0);
        orderDto.setPayTime(pay ? new Date() : null);
        orderDto.setTotalAmount(totailAmount);
        orderDao.createOrder(orderDto);
        if (pay) {
            // 更新医生一元义诊次数
            DoctorDto doctor = new DoctorDto();
            doctor.setId(orderDto.getDoctorId());
            doctorDao.updateOneYuanNumberById(doctor);

            // 刷新支付状态
            consultDto.setPayStatus(Constants.OrderSatus.ORDER_STATUS_1);
            consultDao.updatePayStatus(consultDto);

            // 更新个人余额
            if (orderDto.getPayAmount() != null && orderDto.getPayAmount() > 0) {
                patientDto.setBalance(patientDto.getBalance() - orderDto.getBalance());
                patientDao.updateBalance(patientDto);
                CacheContainer.getToken(orderDto.getToken()).getPatient().setBalance(patientDto.getBalance());
            }

            // 更新礼卷使用状态
            if (orderDto.getCouponId() != null) {
                couponDto.setStatus(1);
                couponDao.updateCouponStatus(couponDto);
            }

            // 保存收支明细
            PatientAccountDetailDto patientAccountDetailDto = new PatientAccountDetailDto();
            patientAccountDetailDto.setPatientId(orderDto.getPatientId());
            patientAccountDetailDto.setType(orderDto.getServiceType());
            patientAccountDetailDto.setTransactionNum(orderDto.getOrderNumber());
            patientAccountDetailDto.setAmount(orderDto.getBalance());
            patientAccountDetailDto.setCreateTime(new Date());
            patientAccountDetailDao.savePatientAccountDetail(patientAccountDetailDto);

            // 通知医生就诊
            RequestDto requestDto = new RequestDto();
            requestDto.setCmd("consultRequest");
            requestDto.setToken(orderDto.getToken());
            Map<String, Object> params = new HashMap<String, Object>();
            requestDto.setParams(params);
            params.put("sender", orderDto.getPatientId());
            params.put("receiver", orderDto.getDoctorId());
            params.put("serviceId", orderDto.getServiceId());
            URLInvoke.post(SysCfg.getString("qujiuyi.chatUrl"), Constants.gson.toJson(requestDto));
        }

        // 返回结果
        ResponseDto responseDto = new ResponseDto();
        responseDto.setResultDesc("订单创建成功");
        Map<String, Object> detail = new HashMap<String, Object>();
        detail.put("orderNumber", orderDto.getOrderNumber());
        detail.put("status", pay ? "1" : "0");
        responseDto.setDetail(detail);
        return responseDto;
    }

    /**
     * @description 购买私人医生咨询服务
     * @param orderDto
     * @return
     * @throws Exception
     */
    private ResponseDto bayPersonalDoctorService(OrderDto orderDto) throws Exception {
        CouponDto couponDto = null;
        PatientDto patientDto = null;
        ServiceDto serviceDto = null;
        double totailAmount = 0;
        boolean pay = false;

        // 判断服务是否已开通
        serviceDto = new ServiceDto();
        serviceDto.setDoctorId(orderDto.getDoctorId());
        serviceDto.setId(orderDto.getPriceId());
        List<ServiceDto> serviceList = serviceDao.queryPersonalDoctorServiceByDoctorId(serviceDto);
        serviceDto = serviceList != null && !serviceList.isEmpty() ? serviceList.get(0) : null;
        if (serviceDto == null || serviceDto.getStatus() != 1) {
            throw new BusinessException("此服务暂未开通");
        }
        // 判断是否已购买
        PersonalDoctorDto personalDoctorDto = new PersonalDoctorDto();
        personalDoctorDto.setPatientId(orderDto.getPatientId());
        personalDoctorDto.setDoctorId(orderDto.getDoctorId());
        List<PersonalDoctorDto> list = personalDoctorDao.queryPersonalDoctorByPatientIdAndDoctorId(personalDoctorDto);
        if (list != null && !list.isEmpty()) {
            throw new BusinessException("此服务您已购买过了");
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
        // 判断优惠券是否已被使用
        if (couponDto != null && couponDto.getStatus() != 0) {
            throw new BusinessException("优惠券已被使用");
        }
        // 判断优惠券是否已过期
        if (couponDto != null && couponDto.getStatus() == 0 && couponDto.getExpireTime() != null
                && couponDto.getExpireTime().getTime() < System.currentTimeMillis()) {
            throw new BusinessException("优惠券已过期");
        }
        // 判断余额是否有效
        if (orderDto.getBalance() != null && orderDto.getBalance() > 0) {
            PatientDto queryPatientDto = new PatientDto();
            queryPatientDto.setId(orderDto.getPatientId());
            patientDto = patientDao.queryPatientById(queryPatientDto);
            if (patientDto.getBalance() == null || orderDto.getBalance() > patientDto.getBalance()) {
                throw new BusinessException("余额不足");
            }
        }
        // 判断支付金额是否正确
        double payAmount = orderDto.getPayAmount() != null ? orderDto.getPayAmount() : 0;
        double balance = orderDto.getBalance() != null ? orderDto.getBalance() : 0;
        double couponAmount = couponDto != null && couponDto.getAmount()!= null ? couponDto.getAmount().doubleValue() : 0;
        totailAmount = serviceDto != null && serviceDto.getPrice() != null ? serviceDto.getPrice() : 0;
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
        if (payAmount == 0 && (balance + couponAmount == totailAmount || (balance == 0 && couponAmount >= totailAmount))) {// 余额+礼券支付类型
            pay = true;
        }
        // 创建订单
        String orderNumber = Util.getOrderNumber();
        orderDto.setOrderNumber(orderNumber);
        orderDto.setCreateTime(new Date());
        orderDto.setStatus(pay ? 1 : 0);
        orderDto.setPayTime(pay ? new Date() : null);
        orderDto.setTotalAmount(totailAmount);
        orderDto.setServiceId(Util.getUniqueSn());
        orderDao.createOrder(orderDto);

        if (pay) {
            // 新增私人医生记录
            long createTime = System.currentTimeMillis();
            Date expirationTime = null;
            if (serviceDto.getType() != null && serviceDto.getType() == 1) {
                expirationTime = new Date(createTime + 7 * 24 * 60 * 60 * 1000L);
            } else if (serviceDto.getType() != null && serviceDto.getType() == 2) {
                expirationTime = new Date(createTime + 30 * 24 * 60 * 60 * 1000L);
            }
            personalDoctorDto = new PersonalDoctorDto();
            personalDoctorDto.setId(orderDto.getServiceId());
            personalDoctorDto.setPatientId(orderDto.getPatientId());
            personalDoctorDto.setDoctorId(orderDto.getDoctorId());
            personalDoctorDto.setCreateTime(new Date(createTime));
            personalDoctorDto.setExpirationTime(expirationTime);
            personalDoctorDao.createPersonalDoctor(personalDoctorDto);

            // 更新个人余额
            if (orderDto.getPayAmount() != null && orderDto.getPayAmount() > 0) {
                patientDto.setBalance(patientDto.getBalance() - orderDto.getBalance());
                patientDao.updateBalance(patientDto);
                CacheContainer.getToken(orderDto.getToken()).getPatient().setBalance(patientDto.getBalance());
            }

            // 更新礼卷使用状态
            if (orderDto.getCouponId() != null) {
                couponDto.setStatus(1);
                couponDao.updateCouponStatus(couponDto);
            }

            // 更新医生即将到账
            DoctorDto updateDoctorDto = new DoctorDto();
            updateDoctorDto.setId(orderDto.getDoctorId());
            updateDoctorDto.setAccountComing(orderDto.getTotalAmount());
            int updates = doctorDao.upadateDoctorAccountComming(updateDoctorDto);
            if (updates != 1) {
                throw new BusinessException("数据有误");
            }

            // 保存收支明细
            PatientAccountDetailDto patientAccountDetailDto = new PatientAccountDetailDto();
            patientAccountDetailDto.setPatientId(orderDto.getPatientId());
            patientAccountDetailDto.setType(orderDto.getServiceType());
            patientAccountDetailDto.setTransactionNum(orderDto.getOrderNumber());
            patientAccountDetailDto.setAmount(orderDto.getBalance());
            patientAccountDetailDto.setCreateTime(new Date());
            patientAccountDetailDao.savePatientAccountDetail(patientAccountDetailDto);

            // 通知医生
            RequestDto requestDto = new RequestDto();
            requestDto.setCmd("personalDoctorRequest");
            requestDto.setToken(orderDto.getToken());
            Map<String, Object> params = new HashMap<String, Object>();
            requestDto.setParams(params);
            params.put("sender", orderDto.getPatientId());
            params.put("receiver", orderDto.getDoctorId());
            URLInvoke.post(SysCfg.getString("qujiuyi.chatUrl"), Constants.gson.toJson(requestDto));

        }

        ResponseDto responseDto = new ResponseDto();
        responseDto.setResultDesc("订单创建成功");
        Map<String, Object> detail = new HashMap<String, Object>();
        detail.put("orderNumber", orderDto.getOrderNumber());
        detail.put("status", pay ? "1" : "0");
        responseDto.setDetail(detail);
        return responseDto;
    }

    /**
     * @description 购买预约加号服务
     * @param orderDto
     * @return
     * @throws Exception
     */
    private ResponseDto bayJiaHaoService(OrderDto orderDto) throws Exception {
        CouponDto couponDto = null;
        PatientDto patientDto = null;
        ServiceDto serviceDto = null;
        double totailAmount = 0;
        boolean pay = false;

        // 判断服务是否已开通
        serviceDto = new ServiceDto();
        serviceDto.setDoctorId(orderDto.getDoctorId());
        List<ServiceDto> serviceList = serviceDao.queryAppointmentServiceByDoctorId(serviceDto);
        serviceDto = serviceList != null && !serviceList.isEmpty() ? serviceList.get(0) : null;
        if (serviceDto == null || serviceDto.getStatus() != 1) {
            throw new BusinessException("此服务暂未开通");
        }
        // 判断记录时否存在
        if (!Util.isNotEmpty(orderDto.getServiceId())) {
            throw new BusinessException("服务id不能为空");
        }
        AppointmentDto appointmentDto = new AppointmentDto();
        appointmentDto.setId(Integer.parseInt(orderDto.getServiceId()));
        appointmentDto =appointmentDao.getAppointmentById(appointmentDto);
        if (appointmentDto == null) {
            throw new BusinessException("预约记录不存在");
        }
        // 判断是否已支付
        if (appointmentDto.getPayStatus() == 1) {
            throw new BusinessException("该服务已支付");
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
        // 判断优惠券是否已被使用
        if (couponDto != null && couponDto.getStatus() != 0) {
            throw new BusinessException("优惠券已被使用");
        }
        // 判断优惠券是否已过期
        if (couponDto != null && couponDto.getStatus() == 0 && couponDto.getExpireTime() != null
                && couponDto.getExpireTime().getTime() < System.currentTimeMillis()) {
            throw new BusinessException("优惠券已过期");
        }
        // 判断余额是否有效
        if (orderDto.getBalance() != null && orderDto.getBalance() > 0) {
            PatientDto queryPatientDto = new PatientDto();
            queryPatientDto.setId(orderDto.getPatientId());
            patientDto = patientDao.queryPatientById(queryPatientDto);
            if (patientDto.getBalance() == null || orderDto.getBalance() > patientDto.getBalance()) {
                throw new BusinessException("余额不足");
            }
        }
        // 判断支付金额是否正确
        double payAmount = orderDto.getPayAmount() != null ? orderDto.getPayAmount() : 0;
        double balance = orderDto.getBalance() != null ? orderDto.getBalance() : 0;
        double couponAmount = couponDto != null && couponDto.getAmount() != null ? couponDto.getAmount().doubleValue() : 0;
        totailAmount = serviceDto != null && serviceDto.getPrice() != null ? serviceDto.getPrice() : 0;
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
        if (payAmount == 0 && (balance + couponAmount == totailAmount || (balance == 0 && couponAmount >= totailAmount))) {// 余额+礼券支付类型
            pay = true;
        }
        // 创建订单
        String orderNumber = Util.getOrderNumber();
        orderDto.setOrderNumber(orderNumber);
        orderDto.setCreateTime(new Date());
        orderDto.setStatus(pay ? 1 : 0);
        orderDto.setPayTime(pay ? new Date() : null);
        orderDto.setTotalAmount(totailAmount);
        orderDao.createOrder(orderDto);

        if (pay) {
            // 更新个人余额
            if (orderDto.getPayAmount() != null && orderDto.getPayAmount() > 0) {
                patientDto.setBalance(patientDto.getBalance() - orderDto.getBalance());
                patientDao.updateBalance(patientDto);
                CacheContainer.getToken(orderDto.getToken()).getPatient().setBalance(patientDto.getBalance());
            }

            // 保存收支明细
            PatientAccountDetailDto patientAccountDetailDto = new PatientAccountDetailDto();
            patientAccountDetailDto.setPatientId(orderDto.getPatientId());
            patientAccountDetailDto.setType(orderDto.getServiceType());
            patientAccountDetailDto.setTransactionNum(orderDto.getOrderNumber());
            patientAccountDetailDto.setAmount(orderDto.getBalance());
            patientAccountDetailDto.setCreateTime(new Date());
            patientAccountDetailDao.savePatientAccountDetail(patientAccountDetailDto);

            // 更新预约加号支付状态
            appointmentDto.setPayStatus(Constants.OrderSatus.ORDER_STATUS_1);
            appointmentDao.updatePayStatus(appointmentDto);

            // 更新礼卷使用状态
            if (orderDto.getCouponId() != null) {
                couponDto.setStatus(1);
                couponDao.updateCouponStatus(couponDto);
            }
        }

        ResponseDto responseDto = new ResponseDto();
        responseDto.setResultDesc("订单创建成功");
        Map<String, Object> detail = new HashMap<String, Object>();
        detail.put("orderNumber", orderDto.getOrderNumber());
        detail.put("status", pay ? "1" : "0");
        responseDto.setDetail(detail);
        return responseDto;
    }

    /**
     * @description 删除订单
     * @param orderDto
     * @return
     * @throws Exception
     */
    public ResponseDto deleteOrder(OrderDto orderDto) throws Exception {
        /** step1:空异常处理. */
        if (orderDto == null) {
            throw new BusinessException(Constants.DATA_ERROR);
        }

        /** step2:校验是否为本人操作. */
        TokenDto token = CacheContainer.getToken(orderDto.getToken());
        if (orderDto.getPatientId() == null || (token != null && token.getPatient() != null && !orderDto.getPatientId().equals(token.getPatient().getId()))) {
            throw new BusinessException("非本人操作");
        }

        /** step3:校验订单id不为空. */
        if (orderDto.getId() == null) {
            throw new BusinessException("订单id不能为空");
        }

        /** step4:判断订单是否存在. */
        OrderDto dto = orderDao.queryOrderById(orderDto);
        if (dto == null) {
            throw new BusinessException("订单不存在");
        }

        /** step5:删除订单. */
        orderDao.deleteOrder(orderDto);

        /** step6:返回结果. */
        ResponseDto responseDto = new ResponseDto();
        responseDto.setResultDesc("删除成功");
        return responseDto;
    }

    /**
     * @description 查询订单列表
     * @param orderDto
     * @return
     * @throws Exception
     */
    public ResponseDto queryOrderList(OrderDto orderDto) throws Exception {
        /** step1:空异常处理. */
        if (orderDto == null) {
            throw new BusinessException(Constants.DATA_ERROR);
        }

        /** step2:校验是否为本人操作. */
        TokenDto token = CacheContainer.getToken(orderDto.getToken());
        if (orderDto.getPatientId() == null || (token != null && token.getPatient() != null && !orderDto.getPatientId().equals(token.getPatient().getId()))) {
            throw new BusinessException("非本人操作");
        }

        /** step3:创建订单. */
        List<OrderDto> list = orderDao.queryOrderList(orderDto);

        /** step4:返回结果. */
        ResponseDto responseDto = new ResponseDto();
        Map<String, Object> detail = new HashMap<String, Object>();
        detail.put("page", orderDto.getPage());
        detail.put("list", list);
        responseDto.setResultDesc("查询成功");
        responseDto.setDetail(detail);
        return responseDto;
    }

    /**
     * @description 查询订单详情
     * @param orderDto
     * @return
     * @throws Exception
     */
    public ResponseDto queryOrderDetail(OrderDto orderDto) throws Exception {
        /** step1:空异常处理. */
        if (orderDto == null) {
            throw new BusinessException(Constants.DATA_ERROR);
        }

        /** step2:校验订单id. */
        if (orderDto.getId() == null) {
            throw new BusinessException("订单id不能为空");
        }

        /** step3:查询数据. */
        OrderDto dto = orderDao.queryOrderDetail(orderDto);

        /** step4:返回结果. */
        ResponseDto responseDto = new ResponseDto();
        responseDto.setResultDesc("查询成功");
        responseDto.setDetail(dto);
        return responseDto;
    }

    /**
     * @description 判断订单是否有效
     * @param orderDto
     * @return
     * @throws Exception
     */
    public ResponseDto isValidOrder(OrderDto orderDto) throws Exception {
        /** step1:空异常处理. */
        if (orderDto == null) {
            throw new BusinessException(Constants.DATA_ERROR);
        }

        /** step2:校验订单id. */
        if (orderDto.getId() == null) {
            throw new BusinessException("订单id不能为空");
        }

        /** step3:查询数据. */
        OrderDto dto = orderDao.queryOrderDetail(orderDto);

        /** step4:判断订单是否有效. */
        boolean isValidOrder = true;
        ResponseDto responseDto = new ResponseDto();
        Map<String, Object> detail = new HashMap<String, Object>();
        TokenDto token = CacheContainer.getToken(orderDto.getToken());
        PatientDto patient = token != null ? token.getPatient() : null;
        if (patient == null || patient.getId() == null || !patient.getId().equals(dto.getPatientId())) {
            isValidOrder = false;
            detail.put("desc", "已权访问");
        }
        if (isValidOrder && dto.getStatus() != null && dto.getStatus() == Constants.OrderSatus.ORDER_STATUS_1) {
            isValidOrder = false;
            detail.put("desc", "订单已支付过了");
        }
        if (dto.getOrderType() != null && dto.getOrderType() == 1) {
            if (dto.getBalance() != null) {
                PatientDto queryPatientDto = new PatientDto();
                queryPatientDto.setId(dto.getPatientId());
                PatientDto patientDto = patientDao.queryPatientById(queryPatientDto);
                if (patientDto == null || patientDto.getBalance() == null || patientDto.getBalance() < dto.getBalance()) {
                    isValidOrder = false;
                    detail.put("desc", "余额不足");
                }
            }
        }

        /** step5:返回结果. */
        responseDto.setResultDesc("调用成功");
        detail.put("isValidOrder", isValidOrder);
        if (isValidOrder) {
            detail.put("desc", "订单有效");
        }
        responseDto.setDetail(detail);
        return responseDto;
    }
}