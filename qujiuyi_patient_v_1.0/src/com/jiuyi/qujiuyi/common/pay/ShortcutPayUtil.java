package com.jiuyi.qujiuyi.common.pay;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.des.Constants;
import com.des.CryptoUtils;

/**
 * @description 支付工具类
 * @author zhb
 * @createTime 2015年5月6日
 */
public class ShortcutPayUtil {

    /**
     * @description 支付
     * @param reqBean
     * @param shortcutPayType
     * @return
     * @throws Exception
     */
    public static ShortcutRespBean execute(ShortcutReqBean reqBean, ShortcutPayEnum shortcutPayType) throws Exception {
        Long timestamp = System.currentTimeMillis();
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        df.setTimeZone(TimeZone.getTimeZone(Constants.DATE_TIMEZONE));
        String currTime = df.format(new Date(timestamp));
        if (reqBean.getCustId() != null && !"".equals(reqBean.getCustId())) {
            reqBean.setCustId(CryptoUtils.encryptKeyData(reqBean.getCustId(), timestamp, ShortcutPayCfig.getString("keys")));
        }
        if (reqBean.getCardNo() != null && !"".equals(reqBean.getCardNo())) {
            reqBean.setCardNo(CryptoUtils.encryptKeyData(reqBean.getCardNo(), timestamp, ShortcutPayCfig.getString("keys")));
        }
        String xml = ShortcutPayXmlUtil.getRequestXml(shortcutPayType.name(), currTime, reqBean);
        String mac = ShortcutPayMac.getMsgMac(xml);

        String respXml = ShortcutPayUrlInvoke.sendMsg(xml, mac);

        ShortcutRespBean respBean = new ShortcutRespBean();
        respBean.setRespXml(respXml);
        ShortcutPayXmlUtil.getRespBeanFromXml(respXml, "head", respBean);
        ShortcutPayXmlUtil.getRespBeanFromXml(respXml, "body", respBean);
        return respBean;
    }
}