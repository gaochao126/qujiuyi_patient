package com.jiuyi.qujiuyi.dao.appointment;

import com.jiuyi.qujiuyi.dto.appointment.AppointmentCountDto;

/**
 * @description 患者预约次数dao层接口
 * @author zhb
 * @createTime 2015年7月16日
 */
public interface AppointmentCountDao {
    /**
     * @description 获取预约次数
     * @param appointmentCountDto
     * @return
     * @throws Exception
     */
    public AppointmentCountDto getAppointmentCount(AppointmentCountDto appointmentCountDto) throws Exception;

    /**
     * @description 插入预约次数
     * @param appointmentCountDto
     * @throws Exception
     */
    public void insertAppointmentCount(AppointmentCountDto appointmentCountDto) throws Exception;

    /**
     * @description 更新预约次数
     * @param appointmentCountDto
     * @throws Exception
     */
    public void updateAppointmentCount(AppointmentCountDto appointmentCountDto) throws Exception;
}