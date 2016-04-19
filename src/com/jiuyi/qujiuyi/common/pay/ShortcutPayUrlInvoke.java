package com.jiuyi.qujiuyi.common.pay;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

/**
 * @description 快捷支付工具类
 * @author zhb
 * @createTime 2015年5月6日
 */
public class ShortcutPayUrlInvoke {
    public static String sendMsg(String xml, String mac) throws Exception {
        HttpClient httpClient = new HttpClient();

        // 设置请求地址
        PostMethod postMethod = new PostMethod(ShortcutPayCfig.getString("APP_SERVER_URL"));
        // 设置用户浏览器为MicrosoftIE6.0
        postMethod.setRequestHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)");
        // 设置编码
        postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
        // 设置请求参数
        postMethod.addParameter("xml", xml);
        postMethod.addParameter("mac", mac);

        String respXml = "";
        // 获取执行结果
        int status = httpClient.executeMethod(postMethod);
        if (status == 200) {
            String content = new String(postMethod.getResponseBody(), "UTF-8");
            String[] respMsg = content.split("&");
            respXml = respMsg[0].substring(4);
        }

        return respXml;
    }
}