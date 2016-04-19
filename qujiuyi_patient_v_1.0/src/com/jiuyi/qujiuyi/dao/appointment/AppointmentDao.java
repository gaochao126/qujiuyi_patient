package com.jiuyi.qujiuyi.dao.appointment;

import java.util.List;

import com.jiuyi.qujiuyi.dto.appointment.AppointmentDto;

/**
 * @description 预约dao层接口
 * @author zhb
 * @createTime 2015年7月13日
 */
public interface AppointmentDao {
    /**
     * @description 患者预约
     * @param appointmentDto
     * @throws Exception
     */
    public void patientAppointment(AppointmentDto appointmentDto) throws Exception;

    /**
     * @description 根据id获取预约记录
     * @param appointmentDto
     * @return
     * @throws Exception
     */
    public AppointmentDto getAppointmentById(AppointmentDto appointmentDto) throws Exception;

    /**
     * @description 更新支付状态
     * @param appointmentDto
     * @throws Exception
     */
    public void updatePayStatus(AppointmentDto appointmentDto) throws Exception;

    /**
     * @description 获取预约记录(加号)
     * @param appointmentDto
     * @return
     * @throws Exception
     */
    public List<AppointmentDto> getAppointmentList(AppointmentDto appointmentDto) throws Exception;
}