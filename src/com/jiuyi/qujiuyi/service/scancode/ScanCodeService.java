package com.jiuyi.qujiuyi.service.scancode;

import com.jiuyi.qujiuyi.dto.common.ResponseDto;
import com.jiuyi.qujiuyi.dto.scancode.ScanCodeDto;

public interface ScanCodeService {
	/**
	 * 1.扫码，运营人员工作量记录
	 * @param scanCodeDto
	 * @return
	 * @throws Exception
	 */
	public ResponseDto scanCode(ScanCodeDto scanCodeDto)throws Exception;
}
