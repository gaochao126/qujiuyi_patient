package com.jiuyi.qujiuyi.dao.doctor;

import java.util.List;

import com.jiuyi.qujiuyi.dto.doctor.DoctorDto;

/**
 * @description 医生dao层接口
 * @author zhb
 * @createTime 2015年4月9日
 */
public interface DoctorDao {
    /**
     * @description 根据科室id获取医生列表
     * @param doctorDto
     * @return
     * @throws Exception
     */
    public List<DoctorDto> queryDoctorListByDepartmentId(DoctorDto doctorDto) throws Exception;

    /**
     * @description 根据科室id获取医生列表
     * @param doctorDto
     * @return
     * @throws Exception
     */
    public DoctorDto queryDoctorById(DoctorDto doctorDto) throws Exception;

    /**
     * @description 根据科室随机获取一个医生
     * @param doctorDto
     * @return
     * @throws Exception
     */
    public List<DoctorDto> getRandomDoctorByDepartment(DoctorDto doctorDto) throws Exception;

    /**
     * @description 获取医生信息
     * @param doctorDto
     * @return
     * @throws Exception
     */
    public DoctorDto queryDoctorInfo(DoctorDto doctorDto) throws Exception;

    /**
     * @description 获取1元医生列表
     * @param doctorDto
     * @return
     * @throws Exception
     */
    public List<DoctorDto> queryOneYuanDoctorList(DoctorDto doctorDto) throws Exception;

    /**
     * @description 根据医生id获取一元义诊医生
     * @param doctorDto
     * @return
     * @throws Exception
     */
    public DoctorDto queryOneYuanDoctorById(DoctorDto doctorDto) throws Exception;

    /**
     * @description 根据医生id更新一元义诊名额
     * @param doctorDto
     * @throws Exception
     */
    public void updateOneYuanNumberById(DoctorDto doctorDto) throws Exception;

    /**
     * @description 搜索医生
     * @param doctorDto
     * @return
     * @throws Exception
     */
    public List<DoctorDto> searchDoctors(DoctorDto doctorDto) throws Exception;

    /**
     * @description 根据手机号码查询医生
     * @param doctorDto
     * @return
     * @throws Exception
     */
    public List<DoctorDto> queryDoctorByPhone(DoctorDto doctorDto) throws Exception;

    /**
     * @description 更新医生即将到账
     * @param doctorDto
     * @throws Exception
     */
    public int upadateDoctorAccountComming(DoctorDto doctorDto) throws Exception;

    /**
     * @description 获取医院医生列表
     * @param doctorDto
     * @return
     * @throws Exception
     */
    public List<DoctorDto> getHospitalDoctors(DoctorDto doctorDto) throws Exception;

    /**
     * @description 获取线下医生详情
     * @param doctorDto
     * @return
     * @throws Exception
     */
    public DoctorDto getHospitalDoctorDetail(DoctorDto doctorDto) throws Exception;

    /**
     * @description 获取名医推荐
     * @param doctorDto
     * @return
     * @throws Exception
     */
    public List<DoctorDto> getFamousDoctors(DoctorDto doctorDto) throws Exception;

    /**
     * @description 根据条件搜索医生
     * @param doctorDto
     * @return
     * @throws Exception
     */
    public List<DoctorDto> queryDoctorByConditions(DoctorDto doctorDto) throws Exception;

	/**
	 * 
	 * @number
	 * @description 查询线下医生挂号计划总数
	 * 
	 * @param doctorDto
	 * @return
	 * @throws Exception
	 * @Date 2015年11月11日
	 */
	public Integer queryOfflineDoctorRegisterPlanCount(DoctorDto doctorDto) throws Exception;
}