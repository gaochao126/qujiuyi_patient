package com.jiuyi.qujiuyi.dao.appointment;

import com.jiuyi.qujiuyi.dto.appointment.AppointmentDetailDto;

/**
 * @description 医生预约按排详情dao
 * @author zhb
 * @createTime 2015年7月17日
 */
public interface AppointmentDetailDao {
    /**
     * @description 获取医生预约按排详情
     * @param appointmentDetailDto
     * @return
     * @throws Exception
     */
    public AppointmentDetailDto getAppointmentDetail(AppointmentDetailDto appointmentDetailDto) throws Exception;
}