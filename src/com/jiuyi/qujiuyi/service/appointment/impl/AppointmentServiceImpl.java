package com.jiuyi.qujiuyi.service.appointment.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jiuyi.qujiuyi.common.dict.CacheContainer;
import com.jiuyi.qujiuyi.common.dict.Constants;
import com.jiuyi.qujiuyi.common.util.IDCard;
import com.jiuyi.qujiuyi.common.util.Util;
import com.jiuyi.qujiuyi.dao.appointment.AppointmentCountDao;
import com.jiuyi.qujiuyi.dao.appointment.AppointmentDao;
import com.jiuyi.qujiuyi.dao.appointment.AppointmentDetailDao;
import com.jiuyi.qujiuyi.dao.service.ServiceDao;
import com.jiuyi.qujiuyi.dto.appointment.AppointmentCountDto;
import com.jiuyi.qujiuyi.dto.appointment.AppointmentDetailDto;
import com.jiuyi.qujiuyi.dto.appointment.AppointmentDto;
import com.jiuyi.qujiuyi.dto.common.ResponseDto;
import com.jiuyi.qujiuyi.dto.common.TokenDto;
import com.jiuyi.qujiuyi.dto.patient.PatientDto;
import com.jiuyi.qujiuyi.dto.service.ServiceDto;
import com.jiuyi.qujiuyi.service.BusinessException;
import com.jiuyi.qujiuyi.service.appointment.AppointmentService;

/**
 * @description 预约业务层实现
 * @author zhb
 * @createTime 2015年7月13日
 */
@Service
public class AppointmentServiceImpl implements AppointmentService {
    @Autowired
    private AppointmentDao appointmentDao;

    @Autowired
    private AppointmentCountDao appointmentCountDao;

    @Autowired
    private ServiceDao serviceDao;

    @Autowired
    private AppointmentDetailDao appointmentDetailDao;

    /**
     * @description 患者预约
     * @param appointmentDto
     * @return
     * @throws Exception
     */
    public ResponseDto createPatientAppointment(AppointmentDto appointmentDto) throws Exception {
        if (appointmentDto == null) {
            throw new BusinessException(Constants.DATA_ERROR);
        }

        TokenDto token = CacheContainer.getToken(appointmentDto.getToken());
        PatientDto patient = token != null ? token.getPatient() : null;
        if (patient == null || patient.getId() == null) {
            throw new BusinessException("患者不存在");
        } else {
            appointmentDto.setPatientId(patient.getId());
        }

        if (appointmentDto.getDoctorId() == null) {
            throw new BusinessException("医生id不能为空");
        }

        if (!Util.isNotEmpty(appointmentDto.getName())) {
            throw new BusinessException("患者姓名不能为空");
        }

        if (!Util.isNotEmpty(appointmentDto.getPhone())) {
            throw new BusinessException("患者电话不能为空");
        }

        if (!Util.isNotEmpty(appointmentDto.getUid())) {
            throw new BusinessException("患者身份证号不能为空");
        }

        // 校验身份证
        try {
            IDCard.IDCardValidate(appointmentDto.getUid());
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }

        if (!Util.isNotEmpty(appointmentDto.getSymptoms())) {
            throw new BusinessException("患者症状不能为空");
        }

        if (appointmentDto.getAppointmentDate() == null) {
            throw new BusinessException("就诊日期不能为空");
        }

        if (appointmentDto.getTimeZone() == null) {
            throw new BusinessException("就诊时间段不能为空");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        long time = appointmentDto.getAppointmentDate().getTime() - sdf.parse(sdf.format(new Date())).getTime();
        long day = time / (24 * 60 * 60 * 1000);
        if (!(day >= 0 && day < 14)) {
            throw new BusinessException("预约时间不在有效范围内");
        }

        // 获取医生预约服务信息
        ServiceDto serviceDto = new ServiceDto();
        serviceDto.setDoctorId(appointmentDto.getDoctorId());
        List<ServiceDto> list = serviceDao.queryAppointmentServiceByDoctorId(serviceDto);
        serviceDto = list != null && !list.isEmpty() ? list.get(0) : null;
        if (serviceDto == null) {
            throw new BusinessException("医生此服务未开通");
        }

        // 获取医预约按排详情
        AppointmentDetailDto appointmentDetailDto = new AppointmentDetailDto();
        appointmentDetailDto.setDoctorId(appointmentDto.getDoctorId());
        appointmentDetailDto.setWeekday(Util.getDayOfWeek(appointmentDto.getAppointmentDate()));
        appointmentDetailDto.setTimeZone(appointmentDto.getTimeZone());
        appointmentDetailDto = appointmentDetailDao.getAppointmentDetail(appointmentDetailDto);
        if (appointmentDetailDto == null || appointmentDetailDto.getNumber() == null || appointmentDetailDto.getNumber() <= 0) {
            throw new BusinessException("医生在您选择的时间段无加号按排");
        }

        // 获取预约次数
        AppointmentCountDto appointmentCountDto = new AppointmentCountDto();
        appointmentCountDto.setDoctorId(appointmentDto.getDoctorId());
        appointmentCountDto.setDate(appointmentDto.getAppointmentDate());
        appointmentCountDto.setTimeZone(appointmentDto.getTimeZone());
        appointmentCountDto = appointmentCountDao.getAppointmentCount(appointmentCountDto);
        int appointmentCount = appointmentCountDto == null ? 0 : appointmentCountDto.getUsedNumber();
        if (appointmentCount >= appointmentDetailDto.getNumber()) {
            throw new BusinessException("加号名额已满");
        }

        // 保存预约记录
        appointmentDto.setCreateDate(new Date());
        appointmentDto.setStatus(0);
        appointmentDto.setVisitStatus(0);
        if (serviceDto.getPrice() != null && serviceDto.getPrice() > 0) {
            appointmentDto.setPayStatus(0);
        } else {
            appointmentDto.setPayStatus(1);
        }
        appointmentDao.patientAppointment(appointmentDto);

        // 更新预约次数表
        if (appointmentCountDto == null && appointmentDto.getPayStatus() == 1) {
            AppointmentCountDto insertAppointmentCountDto = new AppointmentCountDto();
            insertAppointmentCountDto.setDoctorId(appointmentDto.getDoctorId());
            insertAppointmentCountDto.setTimeZone(appointmentDto.getTimeZone());
            insertAppointmentCountDto.setUsedNumber(1);
            appointmentCountDao.insertAppointmentCount(insertAppointmentCountDto);
        } else if (appointmentCountDto != null && appointmentDto.getPayStatus() == 1) {
            appointmentCountDao.updateAppointmentCount(appointmentCountDto);
        }

        ResponseDto responseDto = new ResponseDto();
        responseDto.setResultDesc("创建成功");
        return responseDto;
    }

    /**
     * @description 获取预约记录(加号)
     * @param appointmentDto
     * @return
     * @throws Exception
     */
    public ResponseDto getAppointmentList(AppointmentDto appointmentDto) throws Exception {
        if (appointmentDto == null) {
            throw new BusinessException(Constants.DATA_ERROR);
        }

        TokenDto token = CacheContainer.getToken(appointmentDto.getToken());
        PatientDto patient = token != null ? token.getPatient() : null;
        if (patient == null || patient.getId() == null) {
            throw new BusinessException("患者不存在");
        } else {
            appointmentDto.setPatientId(patient.getId());
        }

        List<AppointmentDto> list = appointmentDao.getAppointmentList(appointmentDto);

        ResponseDto responseDto = new ResponseDto();
        responseDto.setResultDesc("获取成功");
        Map<String, Object> detail = new HashMap<String, Object>();
        detail.put("list", list);
        detail.put("page", appointmentDto.getPage());
        responseDto.setDetail(detail);
        return responseDto;
    }
}