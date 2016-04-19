package com.jiuyi.qujiuyi.common.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.log4j.Logger;

/**
 * @description HTTPS工具类
 * @author zhb
 * @createTime 2015年5月21日
 */
public class HttpsUtil {
    private final static Logger logger = Logger.getLogger(HttpsUtil.class);

    /**
     * @description HTTPS GET
     * @param httpsUrl
     * @return
     */
    public static String get(String httpsUrl) {
        StringBuffer sb = new StringBuffer();
        HttpsURLConnection conn = null;
        InputStream in = null;
        try {
            // 建立连接
            URL url = new URL(httpsUrl);
            conn = (HttpsURLConnection) url.openConnection();

            // 使用自定义的信任管理器
            TrustManager[] tm = { new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }

                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }
            } };
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, tm, new SecureRandom());
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            conn.setSSLSocketFactory(ssf);
            conn.setDoInput(true);

            // 设置请求方式
            conn.setRequestMethod("GET");

            // 取得输入流
            in = conn.getInputStream();
            sb = new StringBuffer();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = in.read(buffer)) != -1) {
                sb.append(new String(buffer, 0, len, "UTF-8"));
            }
        } catch (Exception e) {
            logger.error(e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (conn != null) {
                    conn.disconnect();
                }
            } catch (Exception e) {
                logger.error(e);
            }
        }
        return sb.toString();
    }

    /**
     * @description HTTPS GET
     * @param httpsUrl
     * @return
     */
    public static String post(String httpsUrl, String reqContent) {
		System.out.println("========");
        StringBuffer sb = new StringBuffer();
        HttpsURLConnection conn = null;
        OutputStream out = null;
        InputStream in = null;
        try {
            // 建立连接
            URL url = new URL(httpsUrl);
            conn = (HttpsURLConnection) url.openConnection();
            // 使用自定义的信任管理器
            TrustManager[] tm = { new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }

                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }
            } };
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, tm, new SecureRandom());
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            conn.setSSLSocketFactory(ssf);
            conn.setDoOutput(true);
            conn.setDoInput(true);

            // 设置请求方式
            conn.setRequestMethod("POST");

            // 发送数据
            out = conn.getOutputStream();
            out.write(reqContent.getBytes("UTF-8"));
            // 取得输入流
            in = conn.getInputStream();
            sb = new StringBuffer();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = in.read(buffer)) != -1) {
                sb.append(new String(buffer, 0, len, "UTF-8"));
            }
        } catch (Exception e) {
            logger.error(e);
        } finally {
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
                if (conn != null) {
                    conn.disconnect();
                }
            } catch (Exception e) {
                logger.error(e);
            }
        }
        return sb.toString();
    }
}