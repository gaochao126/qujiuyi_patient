package com.jiuyi.qujiuyi.service.appointment;

import com.jiuyi.qujiuyi.dto.appointment.AppointmentDto;
import com.jiuyi.qujiuyi.dto.common.ResponseDto;

/**
 * @description 预约业务层接口
 * @author zhb
 * @createTime 2015年7月13日
 */
public interface AppointmentService {
    /**
     * @description 患者预约(加号)
     * @param appointmentDto
     * @return
     * @throws Exception
     */
    public ResponseDto createPatientAppointment(AppointmentDto appointmentDto) throws Exception;

    /**
     * @description 获取预约记录(加号)
     * @param appointmentDto
     * @return
     * @throws Exception
     */
    public ResponseDto getAppointmentList(AppointmentDto appointmentDto) throws Exception;
}