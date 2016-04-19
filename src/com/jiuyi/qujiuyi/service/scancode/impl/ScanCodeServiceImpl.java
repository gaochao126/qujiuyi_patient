package com.jiuyi.qujiuyi.service.scancode.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jiuyi.qujiuyi.common.dict.Constants;
import com.jiuyi.qujiuyi.common.util.Util;
import com.jiuyi.qujiuyi.dao.scancode.ScanCodeDao;
import com.jiuyi.qujiuyi.dto.common.ResponseDto;
import com.jiuyi.qujiuyi.dto.scancode.ScanCodeDto;
import com.jiuyi.qujiuyi.service.BusinessException;
import com.jiuyi.qujiuyi.service.scancode.ScanCodeService;
@Service
public class ScanCodeServiceImpl implements ScanCodeService{
	@Autowired
	private ScanCodeDao scanCodeDao;
	/**
	 * 1.扫码，运营人员工作量记录
	 * @param scanCodeDto
	 * @return
	 * @throws Exception
	 */
	public ResponseDto scanCode(ScanCodeDto scanCodeDto)throws Exception{
		if(scanCodeDto==null){
			throw new BusinessException(Constants.DATA_ERROR);
		}
		if(scanCodeDto.getAdminId()==null){
			throw new BusinessException("运营人员id不能为空");
		}
		if(!Util.isNotEmpty(scanCodeDto.getWeixinOpenId())){
			throw new BusinessException("weixinOpenId不能为空");
		}
		List<ScanCodeDto> works = scanCodeDao.queryWorkLoad(scanCodeDto);
		
		if(works ==null || works.isEmpty()){
			scanCodeDao.insertWorkLoad(scanCodeDto);
		}
		
		ResponseDto responseDto = new ResponseDto();
		responseDto.setResultDesc("成功");
		return responseDto;
	}
}
