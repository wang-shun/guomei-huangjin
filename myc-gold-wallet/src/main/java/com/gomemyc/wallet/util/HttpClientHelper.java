package com.gomemyc.wallet.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientHelper {
	

	private final Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * http->post 发送请求，获取结果
	 * 
	 * @param url
	 *            请求地址
	 * @param msg
	 *            请求消息
	 * @param contentType
	 *            编码方式
	 * @return 请求返回结果
	 */
	public String postMsg(String url, String msg, String contentType) {
		String result = null;
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(url);
		// 设置建立连接和传输数据的超时时间
		RequestConfig requestConfig = RequestConfig.custom()
				.setConnectTimeout(1000).setSocketTimeout(5000).build();
		httppost.setConfig(requestConfig);
		// 设置数据类型和编码方式
		StringEntity entity = new StringEntity(msg, ContentType.create(
				"application/json", Consts.UTF_8));
		entity.setChunked(true);
		httppost.setEntity(entity);
		try {
			CloseableHttpResponse response = httpclient.execute(httppost);
			HttpEntity responseEntity = response.getEntity();
			if (response.getStatusLine().getStatusCode() != 200) {
				return null;
			}
			ContentType contentTypeRes = ContentType
					.getOrDefault(responseEntity);
			Charset charset = contentTypeRes.getCharset();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					responseEntity.getContent(), charset));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			reader.close();
			result = sb.toString();
		} catch (MalformedURLException e) {
			log.info(e.getMessage());
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
		} finally {
			if (httpclient != null) {
				try {
					httpclient.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

}
