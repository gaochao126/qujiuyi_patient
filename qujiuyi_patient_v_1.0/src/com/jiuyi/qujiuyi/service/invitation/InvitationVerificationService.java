package com.jiuyi.qujiuyi.service.invitation;

import com.jiuyi.qujiuyi.dto.common.ResponseDto;
import com.jiuyi.qujiuyi.dto.invitation.InvitationVerificationDto;

/**
 * @description 邀请验证业务层接口
 * @author zhb
 * @createTime 2015年5月27日
 */
public interface InvitationVerificationService {
    /**
     * @description 邀请验证
     * @param patientDto
     * @throws Exception
     */
    public ResponseDto invitationVerification(InvitationVerificationDto invitationVerificationDto) throws Exception;
}