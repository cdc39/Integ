package orm.integ.utils;

import java.util.Calendar;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;

public class IdGenerator { 

	private static int[] factors = new int[]{100, 1000, 10000, 100000, 1000000, 10000000, 100000000};
	private static final Random idRand = new Random(); 
	static {
		idRand.setSeed(System.currentTimeMillis());
	}
	
	private IdGenerator() {
	}
	
	private static char getRandomChar() {
		int r = idRand.nextInt(62);
		char ch ;
		if (r<10) {
			ch = (char) (r+48);
		}
		else if (r<36) {
			ch = (char) (r+55);
		}
		else {
			ch = (char) (r+61);
		}
		return ch;
	}
	
	private static String replaceNotGoodChar(String uuid, char ch) {
		char newCh;
		newCh = getRandomChar();
		return uuid.replace(ch, newCh);
	}
	
	 static String intTo62Str(int num) {
		if (num<=0) {
			return "0";
		}
		String s = "";
		int b;
		char c;
		while (num>0) {
			b = num%62;
			c = intToChar(b);
			s = c+s;
			num = num/62;
		}
		return s;
	}
	static char intToChar(int b) {
		if (b<10) {
			return (char) (b+48);
		}
		else if (b<36) {
			return (char) (b-10+65);
		}
		else if (b<62) {
			return (char) (b-36+97);
		}
		else {
			return '0';
		}
	}
	
	public static String createHistorySerialNumber(boolean simple){
		if (simple) {
			return createHistorySerialNumber(true, 5);
		}
		else {
			return createHistorySerialNumber(false, 3);
		}
	}
	
	public static String createHistorySerialNumber(boolean dateOnly, int randomLength) {
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR)-2000;
		String yearStr = intTo62Str(year);
		if (yearStr.length()==1) {
			yearStr = "0"+yearStr;
		}
		char monthChar = intToChar(now.get(Calendar.MONTH)+1);
		char dateChar = intToChar(now.get(Calendar.DATE));
		char hourChar = intToChar(now.get(Calendar.HOUR_OF_DAY));
		char minChar = intToChar(now.get(Calendar.MINUTE));
		char secChar = intToChar(now.get(Calendar.SECOND));
		String msChar = intTo62Str(now.get(Calendar.MILLISECOND));
		String randomStr = createRandomStr(randomLength, false);
		if (dateOnly) {
			return yearStr+monthChar+dateChar+randomStr;
		}
		else {
			return yearStr+monthChar+dateChar+hourChar+minChar+secChar+msChar+randomStr;
		}
	}
	
	public static String createRandomStr32(boolean compress) {
		if (compress) {
			String str = Base64UUIDGenerate.uuid();
			str = replaceNotGoodChar(str, '-');
			str = replaceNotGoodChar(str, '_');
			return str;
		}
		else {
			String str = UUID.randomUUID().toString();
			String id = str.replaceAll("-", "");
			return id;
		}
	}
	
	public static String createRandomStr(int length, boolean compress) {
		int len = 0;
		String str = "";
		while (len<length) {
			str = str + createRandomStr32(compress);
			len+=32;
		}
		return str.substring(0, length);
	}

	public static int createFixedNumber(int length) {
		int factor = factors[length-3];
		int newId = idRand.nextInt(9*factor)+factor;
		return newId;
	}
	
}

class Base64UUIDGenerate {
	
	public static void main(String[] args) {
		for (int i=0; i<10; i++) {
			String uuid = uuid();
			System.out.println(uuid);
		}
	}

	public static String uuid() {
		UUID uuid = UUID.randomUUID();
		return toBase64UUID(uuid);
	}

	public static String UUID2Base64UUID(String uuidString) {
		UUID uuid = UUID.fromString(uuidString);
		return toBase64UUID(uuid);
	}

	public static String base64UUID2UUID(String base64uuid) {
		if (base64uuid.length() != 22) {
			throw new IllegalArgumentException("Invalid base64uuid!");
		}

		byte[] byUuid = Base64.decodeBase64(base64uuid + "==");
		long most = bytes2long(byUuid, 0);
		long least = bytes2long(byUuid, 8);
		UUID uuid = new UUID(most, least);
		return uuid.toString().toUpperCase();
	}

	private static String toBase64UUID(UUID uuid) {
		byte[] byUuid = new byte[16];
		long least = uuid.getLeastSignificantBits();
		long most = uuid.getMostSignificantBits();
		long2bytes(most, byUuid, 0);
		long2bytes(least, byUuid, 8);
		String compressUUID = Base64.encodeBase64URLSafeString(byUuid);
		return compressUUID;
	}

	private static void long2bytes(long value, byte[] bytes, int offset) {
		for (int i = 7; i > -1; i--) {
			bytes[offset++] = (byte) ((value >> 8 * i) & 0xFF);
		}
	}

	private static long bytes2long(byte[] bytes, int offset) {
		long value = 0;
		for (int i = 7; i > -1; i--) {
			value |= (((long) bytes[offset++]) & 0xFF) << 8 * i;
		}
		return value;
	}
	
}
