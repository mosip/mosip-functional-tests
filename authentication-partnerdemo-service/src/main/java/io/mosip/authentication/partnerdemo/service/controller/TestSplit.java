package io.mosip.authentication.partnerdemo.service.controller;

import java.util.Arrays;
import java.util.stream.IntStream;

public class TestSplit {

	public static void main(String[] args) {

		String str = "123456";
		String sep = "4";

		byte[] strBytes = str.getBytes();

		byte[] sepBytes = sep.getBytes();

		byte[][] splits = splitAtFirstOccurance(strBytes, sepBytes);

		System.out.println(new String(splits[0]) + " : " + new String(splits[1]));
	}

	private static byte[][] splitAtFirstOccurance(byte[] strBytes, byte[] sepBytes) {
		int index = findIndex(strBytes, sepBytes);
		if (index >= 0) {
			byte[] bytes1 = new byte[index];
			byte[] bytes2 = new byte[strBytes.length - (bytes1.length + sepBytes.length)];
			System.arraycopy(strBytes, 0, bytes1, 0, bytes1.length);
			System.arraycopy(strBytes, (bytes1.length + sepBytes.length), bytes2, 0, bytes2.length);
			return new byte[][] { bytes1, bytes2 };
		} else {
			return new byte[][] { strBytes, new byte[0] };
		}
	}

	private static int findIndex(byte arr[], byte[] subarr) {
		int len = arr.length;
		int subArrayLen = subarr.length;
		return IntStream.range(0, len).filter(currentIndex -> {
			if ((currentIndex + subArrayLen) <= len) {
				byte[] sArray = new byte[subArrayLen];
				System.arraycopy(arr, currentIndex, sArray, 0, subArrayLen);
				return Arrays.equals(sArray, subarr);
			}
			return false;
		}).findFirst() // first occurence
				.orElse(-1); // No element found
	}

}
