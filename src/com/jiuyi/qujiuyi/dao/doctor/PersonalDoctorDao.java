package com.jiuyi.qujiuyi.dao.doctor;

import java.util.List;

import com.jiuyi.qujiuyi.dto.doctor.DoctorDto;
import com.jiuyi.qujiuyi.dto.doctor.PersonalDoctorDto;

/**
 * @description 私人医生dao层务接口
 * @author zhb
 * @createTime 2015年4月24日
 */
public interface PersonalDoctorDao {
    /**
     * @description 获取私人医生
     * @param personalDoctorDto
     * @return
     * @throws Exception
     */
    public List<DoctorDto> queryPersonalDoctors(PersonalDoctorDto personalDoctorDto) throws Exception;

    /**
     * @description 创建私人医生
     * @param personalDoctorDto
     * @return
     * @throws Exception
     */
    public void createPersonalDoctor(PersonalDoctorDto personalDoctorDto) throws Exception;

    /**
     * @description 根据患者和医生获取私人医生记录
     * @param personalDoctorDto
     * @return
     * @throws Exception
     */
    public List<PersonalDoctorDto> queryPersonalDoctorByPatientIdAndDoctorId(PersonalDoctorDto personalDoctorDto) throws Exception;
}