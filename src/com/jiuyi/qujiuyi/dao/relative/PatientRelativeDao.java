package com.jiuyi.qujiuyi.dao.relative;

import java.util.List;

import com.jiuyi.qujiuyi.dto.relative.PatientRelativeDto;

/**
 * @description 常用就诊人dao层接口
 * @author zhb
 * @createTime 2015年4月29日
 */
public interface PatientRelativeDao {
    /**
     * @description 新增常用就诊人
     * @param ratientRelativeDto
     * @throws Exception
     */
    public void addRelative(PatientRelativeDto ratientRelativeDto) throws Exception;

    /**
     * @description 删除常用就诊人
     * @param ratientRelativeDto
     * @return
     * @throws Exception
     */
    public void delRelative(PatientRelativeDto ratientRelativeDto) throws Exception;

    /**
     * @description 修改常用就诊人
     * @param ratientRelativeDto
     * @throws Exception
     */
    public void modRelative(PatientRelativeDto ratientRelativeDto) throws Exception;

    /**
     * @description 查询常用就诊人
     * @param ratientRelativeDto
     * @return
     * @throws Exception
     */
    public List<PatientRelativeDto> queryRelatives(PatientRelativeDto ratientRelativeDto) throws Exception;

    /**
     * @description 更新就诊人
     * @param ratientRelativeDto
     * @return
     * @throws Exception
     */
    public void updateAllDefultByPatientId(PatientRelativeDto ratientRelativeDto) throws Exception;

    /**
     * @description 根据id获取常用就诊人信息
     * @param ratientRelativeDto
     * @return
     * @throws Exception
     */
    public PatientRelativeDto getPatientRelativeById(PatientRelativeDto ratientRelativeDto) throws Exception;
}