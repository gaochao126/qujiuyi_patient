package com.jiuyi.qujiuyi.service.pay;

import com.jiuyi.qujiuyi.common.pay.ShortcutReqBean;
import com.jiuyi.qujiuyi.dto.common.ResponseDto;
import com.jiuyi.qujiuyi.dto.pay.BankDto;

/**
 * @description 快捷支付业务层接口
 * @author zhb
 * @createTime 2015年5月7日
 */
public interface ShortcutPayService {
    /**
     * @description 查询银行列表
     * @param bankDto
     * @throws Exception
     */
    public ResponseDto queryBanks(BankDto bankDto) throws Exception;

    /**
     * @description 消费交易QP0001 (商户->快捷支付平台)
     * @param shortcutReqBean
     * @return
     * @throws Exception
     */
    public ResponseDto QP0001(ShortcutReqBean shortcutReqBean) throws Exception;
    
    /**
     * @description 快捷支付手机动态鉴权QP0002(商户->快捷支付平台)
     * @param shortcutReqBean
     * @return
     * @throws Exception
     */
    public ResponseDto QP0002(ShortcutReqBean shortcutReqBean) throws Exception;
    
    /**
     * @description 关闭快捷支付QP0003（商户->快捷支付平台）
     * @param shortcutReqBean
     * @return
     * @throws Exception
     */
    public ResponseDto QP0003(ShortcutReqBean shortcutReqBean) throws Exception;
    
    /**
     * @description 快捷支付客户卡信息查询QP0004(商户->快捷支付平台)
     * @param shortcutReqBean
     * @return
     * @throws Exception
     */
    public ResponseDto QP0004(ShortcutReqBean shortcutReqBean) throws Exception;
    
    /**
     * @description 退货交易QP0005(商户->快捷支付平台，暂无)
     * @param shortcutReqBean
     * @return
     * @throws Exception
     */
    public ResponseDto QP0005(ShortcutReqBean shortcutReqBean) throws Exception;
    
    /**
     * @description 快捷支付交易流水查询QP0006(商户->快捷支付平台)
     * @param shortcutReqBean
     * @return
     * @throws Exception
     */
    public ResponseDto QP0006(ShortcutReqBean shortcutReqBean) throws Exception;
    
    /**
     * @description 快捷支付卡信息查询QP0007(商户->快捷支付平台)
     * @param shortcutReqBean
     * @return
     * @throws Exception
     */
    public ResponseDto QP0007(ShortcutReqBean shortcutReqBean) throws Exception;
    
    /**
     * @description 一键支付交易QP0008 (商户->快捷支付平台)
     * @param shortcutReqBean
     * @return
     * @throws Exception
     */
    public ResponseDto QP0008(ShortcutReqBean shortcutReqBean) throws Exception;
    
    /**
     * @description 一键支付限额查询QP0009 (商户->快捷支付平台)
     * @param shortcutReqBean
     * @return
     * @throws Exception
     */
    public ResponseDto QP0009(ShortcutReqBean shortcutReqBean) throws Exception;
}