package com.jiuyi.qujiuyi.common.util;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

@SuppressWarnings("deprecation")
public class URLInvoke {
	private final static Logger logger = Logger.getLogger(URLInvoke.class);

    private static ThreadSafeClientConnManager cm = null;
    static {
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            X509TrustManager tm = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            ctx.init(null, new TrustManager[] { tm }, null);
            SSLSocketFactory ssf = new SSLSocketFactory(ctx, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            SchemeRegistry schemeRegistry = new SchemeRegistry();
            schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
            schemeRegistry.register(new Scheme("https", 443, ssf));
            cm = new ThreadSafeClientConnManager(schemeRegistry);
            cm.setMaxTotal(400);
            cm.setDefaultMaxPerRoute(40);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @description 获取 HTTP Client
     * @return
     */
    private static HttpClient getHttpClient() {
        HttpParams params = new BasicHttpParams();
        params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
        return new DefaultHttpClient(cm, params);
    }

    /**
     * @description HTTP POST
     * @param url
     * @param msg
     * @return
     */
    public static String post(String url, String msg) {
        logger.info("URLInvoke.post#request message : " + msg);
        String content = null;
        HttpClient httpClient = getHttpClient();
        HttpPost postMethod = new HttpPost(url);
        try {
            // 从接过过来的代码转换为UTF-8的编码
            HttpEntity stringEntity = new StringEntity(msg, "text/plain", "UTF-8");
            postMethod.setEntity(stringEntity);
            HttpResponse response = httpClient.execute(postMethod);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                // 使用EntityUtils的toString方法，传递默认编码，在EntityUtils中的默认编码是ISO-8859-1
                content = EntityUtils.toString(entity, "UTF-8");
            }
        } catch (Exception e) {
            logger.error("URLInvoke.post#request error", e);
        } finally {
            postMethod.abort();
        }
        logger.info("URLInvoke.post#response message : " + content);
        return content;
    }
}