package org.imec.ivlab.core.util;

import java.util.Random;

public class RandomGenerator {

	private static char[] _base62chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

	private static char[] _base52chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

	private static char[] _base10numbers = "0123456789".toCharArray();

	private static Random _random = new Random();

	public static String getBase62(int length) {
        StringBuilder sb = new StringBuilder(length);

		for (int i = 0; i < length; i++)
			sb.append(_base62chars[_random.nextInt(62)]);

		return sb.toString();
	}


	public static String getBase52(int length) {
		StringBuilder sb = new StringBuilder(length);

		for (int i = 0; i < length; i++)
			sb.append(_base62chars[_random.nextInt(52)]);

		return sb.toString();
	}

	public static String getBase36(int length) {
		StringBuilder sb = new StringBuilder(length);

		for (int i = 0; i < length; i++)
			sb.append(_base62chars[_random.nextInt(36)]);

		return sb.toString();
	}

	public static String getBaseNumber(int length) {
		StringBuilder sb = new StringBuilder(length);

		for (int i = 0; i < length; i++)
			sb.append(_base10numbers[_random.nextInt(10)]);

		return sb.toString();
	}

}