package com.gomemyc.wallet.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Utils {
	
	/**
	 * 生成字符串的md5码
	 * 
	 * @param str 字符串
	 * @return md5码
	 */
	public static String md5(String str) {
		MessageDigest alga = null;
		try {
			alga = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		alga.update(str.getBytes());
		return byteTohex(alga.digest());
	}
	
	/**
	 * 将二进制比特数组转化为字符串
	 * 
	 * @param b
	 * @return
	 */
	private static String byteTohex(byte[] b) {
		String hs = "";
		String stmp = "";
		for (byte element : b) {
			stmp = (Integer.toHexString(element & 0XFF));
			if (stmp.length() == 1) {
				hs = hs + "0" + stmp;
			} else {
				hs = hs + stmp;
			}
		}
		return hs;
	}
	
	public static String md5File(String fileUrl) {
		try {
			return DigestUtils.md5Hex(new FileInputStream(fileUrl));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
