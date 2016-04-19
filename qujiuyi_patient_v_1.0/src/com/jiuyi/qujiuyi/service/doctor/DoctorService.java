package com.jiuyi.qujiuyi.service.doctor;

import com.jiuyi.qujiuyi.dto.common.ResponseDto;
import com.jiuyi.qujiuyi.dto.doctor.DoctorDto;

/**
 * @description 医生业务接口
 * @author zhb
 * @createTime 2015年4月8日
 */
public interface DoctorService {
    /**
     * @description 根据科室id获取医生列表
     * @param doctorDto
     * @return
     * @throws Exception
     */
    public ResponseDto queryDoctorListByDepartmentId(DoctorDto doctorDto) throws Exception;

    /**
     * @description 获取医生信息
     * @param doctorDto
     * @return
     * @throws Exception
     */
    public ResponseDto queryDoctorInfo(DoctorDto doctorDto) throws Exception;

    /**
     * @description 获取1元医生列表
     * @param doctorDto
     * @return
     * @throws Exception
     */
    public ResponseDto queryOneYuanDoctorList(DoctorDto doctorDto) throws Exception;

    /**
     * @description 搜索医生
     * @param doctorDto
     * @return
     * @throws Exception
     */
    public ResponseDto searchDoctors(DoctorDto doctorDto) throws Exception;

    /**
     * @description 获取医院医生列表
     * @param doctorDto
     * @return
     * @throws Exception
     */
    public ResponseDto getHospitalDoctors(DoctorDto doctorDto) throws Exception;

    /**
     * @description 获取医院医生详情
     * @param doctorDto
     * @return
     * @throws Exception
     */
    public ResponseDto getHospitalDoctorDetail(DoctorDto doctorDto) throws Exception;

    /**
     * @description 获取名医推荐
     * @param doctorDto
     * @return
     * @throws Exception
     */
    public ResponseDto getFamousDoctors(DoctorDto doctorDto) throws Exception;

    /**
     * @description 根据条件搜索医生
     * @param doctorDto
     * @return
     * @throws Exception
     */
    public ResponseDto queryDoctorByConditions(DoctorDto doctorDto) throws Exception;
}