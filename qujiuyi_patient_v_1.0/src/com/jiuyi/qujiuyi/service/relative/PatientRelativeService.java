package com.jiuyi.qujiuyi.service.relative;

import com.jiuyi.qujiuyi.dto.common.ResponseDto;
import com.jiuyi.qujiuyi.dto.relative.PatientRelativeDto;

/**
 * @description 常用就诊人业务层接口
 * @author zhb
 * @createTime 2015年4月29日
 */
public interface PatientRelativeService {
    /**
     * @description 新增常用就诊人
     * @param ratientRelativeDto
     * @return
     * @throws Exception
     */
    public ResponseDto addRelative(PatientRelativeDto ratientRelativeDto) throws Exception;

    /**
     * @description 删除常用就诊人
     * @param ratientRelativeDto
     * @return
     * @throws Exception
     */
    public ResponseDto delRelative(PatientRelativeDto ratientRelativeDto) throws Exception;

    /**
     * @description 修改常用就诊人
     * @param ratientRelativeDto
     * @return
     * @throws Exception
     */
    public ResponseDto modRelative(PatientRelativeDto ratientRelativeDto) throws Exception;

    /**
     * @description 查询常用就诊人
     * @param ratientRelativeDto
     * @return
     * @throws Exception
     */
    public ResponseDto queryRelatives(PatientRelativeDto ratientRelativeDto) throws Exception;
}