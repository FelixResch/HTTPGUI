package at.resch.html.test.auth;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

import at.resch.html.server.Session;

public class UserServices {

	public static BigInteger[] generateKeys(String password) {
		long begin = System.currentTimeMillis();
		BigInteger[] primes = splitPass(password);
		BigInteger[] keys = generateRsa(primes);
		BigInteger m = BigInteger.valueOf(7);
		BigInteger c = m.modPow(keys[1], keys[0]);
		m = c.modPow(keys[2], keys[0]);
		if (!m.equals(BigInteger.valueOf(7))) {
			//System.out.println("[WARN] Key not valid!");
			return null;
		}
		long end = System.currentTimeMillis() ;
		Session.logger.info("Key generation took " + (end - begin) + " ms");
		return keys;
	}

	
	private static BigInteger[] splitPass(String password) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			byte[] hash = md.digest(password.getBytes());
//			for(int i = 0; i < hash.length; i++) {
//				System.out.print((int) (hash[i] & 0xff) + " ");
//			}
//			System.out.println();
			byte[] no1 = new byte[(int) Math.ceil((double) hash.length / 2)], no2 = new byte[(int) Math
					.floor((double) hash.length / 2)];
			System.arraycopy(hash, 0, no1, 0, no1.length);
			no1 = reverseArray(no1);
			System.arraycopy(hash, no1.length, no2, 0, no2.length);
			no2 = reverseArray(no2);
			BigInteger[] ret = new BigInteger[] { new BigInteger(no1),
					new BigInteger(no2) };
			for (int i = 0; i < ret.length; i++) {
				ret[i] = ret[i].abs();
				ret[i] = findPrime(ret[i]);
			}
			return ret;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static byte[] reverseArray(byte[] in) {
		byte[] out = new byte[in.length];
		for(int i = 0; i < in.length; i++) {
			out[in.length - i - 1] = in[i];
		}
		return out;
	}

	private static BigInteger findPrime(BigInteger number) {
		boolean prime = false;
		Random r = new Random();
		while (!prime) {
			prime = true;
			for (int j = 0; j < 100; j++) {
				BigInteger a = BigInteger.valueOf(number.compareTo(BigInteger
						.valueOf(Integer.MAX_VALUE)) == -1 ? (int) number
						.intValue() - 1 : r.nextInt(Integer.MAX_VALUE));
				BigInteger res = a.modPow(number, number);
				if(!a.equals(res)) {
					prime = false;
					number = number.add(BigInteger.ONE);
					break;
				}
			}
		}
		return number;
	}
	
	private static BigInteger[] generateRsa(BigInteger[] primes) {
		BigInteger p = primes[0];
		//System.out.println("p: " + p.toString());
		BigInteger q = primes[1];
		//System.out.println("q: " + q.toString());
		BigInteger N = p.multiply(q);
		//System.out.println("N: " + N.toString());
		BigInteger R = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
		//System.out.println("R: " + R.toString());
		BigInteger e = BigInteger.valueOf(65537);
		//System.out.println("e: " + e.toString());
		BigInteger d = e.modInverse(R);
		//System.out.println("d: " + d.toString());
		return new BigInteger[] {N, e, d};
	}
	
	public static String decrypt(String s, BigInteger[] keys) {
		String ret = "";
		String[] blocks = s.split("0x");
		int blocks_count = blocks.length;
		for(int i = 0; i < blocks_count; i++) {
			if(blocks[i].length() == 0)
				continue;
			//Console.Write("Block(" + i + "): " + blocks[i]);
			BigInteger val = new BigInteger(blocks[i]);
			BigInteger dec = val.modPow(keys[2], keys[0]);
			String var = new String(reverseArray(dec.toByteArray()));
			//Console.WriteLine(" => " + var);
			ret += var;
		}
		return ret;
	}
	
	public static String encrypt(String message, BigInteger[] keys) {
		String ret = "";
		int bytelen = (int)Math.ceil(keys[0].bitLength() / 8);
		byte[] bytes = message.getBytes();
		int blocks = (int)Math.ceil((double)bytes.length / (double)bytelen);
		//System.out.println("Encrypting with " + blocks + " Blocks");
		for(int i = 0; i < blocks; i++) {
			byte[] block = new byte[bytelen];
			System.arraycopy(bytes, i * bytelen, block, 0, (i == blocks - 1 ? bytes.length - i * bytelen : bytelen));
			block = reverseArray(block);
			BigInteger val = new BigInteger(block);
			BigInteger enc = val.modPow(keys[1], keys[0]);
			String var = "0x" + enc.toString();
			ret += var;
		}
		return ret;
	}
	
	public static void main(String[] args) {
		generateKeys("test");
	}
}
