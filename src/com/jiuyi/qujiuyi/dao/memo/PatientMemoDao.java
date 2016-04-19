package com.jiuyi.qujiuyi.dao.memo;

import java.util.List;

import com.jiuyi.qujiuyi.dto.memo.PatientMemoDto;

public interface PatientMemoDao {
    /**
     * @description 新增常用就诊人
     * @param ratientMemoDto
     * @throws Exception
     */
    public void addMemo(PatientMemoDto ratientMemoDto) throws Exception;

    /**
     * @description 删除常用就诊人
     * @param ratientMemoDto
     * @throws Exception
     */
    public void delMemo(PatientMemoDto ratientMemoDto) throws Exception;

    /**
     * @description 修改常用就诊人
     * @param ratientMemoDto
     * @throws Exception
     */
    public void modMemo(PatientMemoDto ratientMemoDto) throws Exception;

    /**
     * @description 查询常用就诊人
     * @param ratientMemoDto
     * @return
     * @throws Exception
     */
    public List<PatientMemoDto> queryMemos(PatientMemoDto ratientMemoDto) throws Exception;
}