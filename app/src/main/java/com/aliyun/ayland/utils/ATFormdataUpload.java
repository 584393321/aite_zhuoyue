package com.aliyun.ayland.utils;

import com.aliyun.ayland.global.ATGlobalApplication;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class ATFormdataUpload {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String filepath="E:\\图二.jpg";
		
		String urlStr = "http://server.atsmartlife.com/uploadpicture";
		
		Map<String, String> textMap = new HashMap<String, String>();
	
		textMap.put("name", "testname");

		Map<String, String> fileMap = new HashMap<String, String>();
		
		fileMap.put("file", filepath);
		
		String ret = formUpload(urlStr, textMap, fileMap);
		
		System.out.println(ret);
	}


	/**
	 * 上传图片
	 * 
	 * @param urlStr
	 * @param textMap
	 * @param fileMap
	 * @return
	 */
	public static String formUpload(String urlStr, Map<String, String> textMap,
                                    Map<String, String> fileMap) {
		String res = "";
		HttpURLConnection conn = null;
		String BOUNDARY = "----WebKitFormBoundaryoOMIZVFTRHCHGBe1"; //boundary就是request头和上传文件内容的分隔符
		try {
			URL url = new URL(urlStr);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(30000);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("User-Agent","Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
			conn.setRequestProperty("Content-Type","multipart/form-data; boundary=" + BOUNDARY);
			OutputStream out = new DataOutputStream(conn.getOutputStream());
			// text
			if (textMap != null) {
				StringBuffer strBuf = new StringBuffer();
				Iterator iter = textMap.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					String inputName = (String) entry.getKey();
					String inputValue = (String) entry.getValue();
					if (inputValue == null) {
						continue;
					}
					strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
					strBuf.append("Content-Disposition: form-data; name=\""+ inputName + "\"\r\n\r\n");
					strBuf.append(inputValue);
				}
				out.write(strBuf.toString().getBytes());
			}
//			Log.d("alarm", "0000000000000000000000000000000");
			// file
			if (fileMap != null) {
				Iterator iter = fileMap.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					String inputName = (String) entry.getKey();
					String inputValue = (String) entry.getValue();
					if (inputValue == null) {
						continue;
					}
					File file = new File(inputValue);
					String filename = ATGlobalApplication.getAccount()+"-"+file.getName();
					
					String contentType = "";/* new MimetypesFileTypeMap()
							.getContentType(file)*/;
					
					if (filename.endsWith(".jpg") ) {
						contentType = "image/jpeg";
					}
					else if(filename.endsWith(".png")){
						contentType = "image/png";
					}
					if (contentType == null || contentType.equals("")) {
						contentType = "application/octet-stream";
					}

					StringBuffer strBuf = new StringBuffer();
					strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
					strBuf.append("Content-Disposition: form-data; name=\""
							+ inputName + "\"; filename=\"" + filename
							+ "\"\r\n");
					strBuf.append("Content-Type:" + contentType + "\r\n\r\n");
					out.write(strBuf.toString().getBytes());

					DataInputStream in = new DataInputStream(
							new FileInputStream(file));
					int bytes = 0;
					byte[] bufferOut = new byte[1024];
					while ((bytes = in.read(bufferOut)) != -1) {
						out.write(bufferOut, 0, bytes);
					}
					in.close();
//					Log.d("alarm", "1111111111111111111111111111111111111111");
				}
			}

			byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
			out.write(endData);
			out.flush();
			out.close();

//			Log.d("alarm", "2222222222222222222222222222222");
			// 读取返回数据
			StringBuffer strBuf = new StringBuffer();
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				strBuf.append(line).append("\n");
			}
			res = strBuf.toString();
			reader.close();
			reader = null;
//			Log.d("alarm", "333333333333333333333333333333==="+res);
		} catch (Exception e) {
//			Log.d("alarm", "44444444444444444444444444444444444");
			e.printStackTrace();
		} finally {
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
		}
		return res;
	}

}
