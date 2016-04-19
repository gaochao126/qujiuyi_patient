/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package com.jiuyi.qujiuyi.common.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * This example demonstrates how to create secure connections with a custom SSL
 * context.
 */
public class WxRefundSSL {

	private static SSLConnectionSocketFactory sslsf;

	public final static String post(String entity, String mch_id, Integer clientType) throws Exception {
		try {
			KeyStore keyStore = KeyStore.getInstance("PKCS12");
			// FileInputStream instream = new FileInputStream(new
			// File("D:\\apiclient_cert.p12"));

			FileInputStream instream = null;

			if (clientType == 0) {
				instream = new FileInputStream(new File(SysCfg.getString("apiclient.ssl")));
			} else {
				instream = new FileInputStream(new File(SysCfg.getString("apiclient.app.ssl")));
			}

			try {
				keyStore.load(instream, mch_id.toCharArray());
			} finally {
				instream.close();
			}

			SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, mch_id.toCharArray()).build();

			sslsf = new SSLConnectionSocketFactory(sslcontext, new String[] { "TLSv1" }, null, SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
		String result = "";
		try {
			HttpPost post = new HttpPost(SysCfg.getString("weixin.refund"));
			post.setEntity(new StringEntity(entity));
			CloseableHttpResponse response = httpclient.execute(post);
			try {
				HttpEntity resp = response.getEntity();
				if (resp != null) {
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resp.getContent()));
					String line = null;
					while ((line = bufferedReader.readLine()) != null) {
						result += line;
					}
				}
				EntityUtils.consume(resp);
			} finally {
				response.close();
			}
		} finally {
			httpclient.close();
		}
		return result;
	}

	public final static void main(String[] args) throws Exception {
		String str = "<xml><appid>wx9b6be7d304a361e8</appid><mch_id>1267256901</mch_id><nonce_str>8c1a1b53b2f35007d5606614ff5f0bad</nonce_str>"+
   "<op_user_id>1267256901</op_user_id><out_refund_no>145638606217036</out_refund_no><out_trade_no>145638592631522</out_trade_no>"+
   "<refund_fee>300</refund_fee><total_fee>300</total_fee><sign>8c1a1b53b2f35007d5606614ff5f0bad</sign></xml>";
		String res = post(str, "1267256901", 1);
		System.out.println(res);
	}
}
