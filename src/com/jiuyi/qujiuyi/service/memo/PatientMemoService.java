package com.jiuyi.qujiuyi.service.memo;

import com.jiuyi.qujiuyi.dto.common.ResponseDto;
import com.jiuyi.qujiuyi.dto.memo.PatientMemoDto;

/**
 * @description 常用就诊人业务层接口
 * @author zhb
 * @createTime 2015年4月29日
 */
public interface PatientMemoService {
    /**
     * @description 新增常用就诊人
     * @param ratientMemoDto
     * @return
     * @throws Exception
     */
    public ResponseDto addMemo(PatientMemoDto ratientMemoDto) throws Exception;

    /**
     * @description 删除常用就诊人
     * @param ratientMemoDto
     * @return
     * @throws Exception
     */
    public ResponseDto delMemo(PatientMemoDto ratientMemoDto) throws Exception;

    /**
     * @description 修改常用就诊人
     * @param ratientMemoDto
     * @return
     * @throws Exception
     */
    public ResponseDto modMemo(PatientMemoDto ratientMemoDto) throws Exception;

    /**
     * @description 查询常用就诊人
     * @param ratientMemoDto
     * @return
     * @throws Exception
     */
    public ResponseDto queryMemos(PatientMemoDto ratientMemoDto) throws Exception;
}