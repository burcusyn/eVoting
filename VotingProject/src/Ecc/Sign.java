package Ecc;

import java.math.BigInteger;
import java.util.Random;

import hash.Hash;
import hash.IHash;;

public class Sign implements Isign{
	
	private BigInteger pr;		//private key of CA
    private BigInteger n;     	//the order of the curve
    private Curve curve;    	//the elliptic curve
    private Point G;        	//the generator point, an elliptic curve domain parameter
	
	public Sign(BigInteger privateKey) {
        curve = new Curve("P-192");
        n = curve.getN();
        G = curve.getG();
        this.pr = privateKey;
    }
	
	/*
	 * public String siginingCertificate(String certificate, BigInteger privateKey);
	 * 
	 * 
	 * */
	
	
	//Signing certificate using CA�s private key 
    //Returns signature in hex string representation
    public String signingMessage(String message) throws Exception{
        Point signPoint = signatureGeneration(message);
        //System.out.println("x:" + signPoint.getX()+ ", Len:" + signPoint.getX().bitLength());
        //System.out.println("y:" + signPoint.getY()+ ", Len:" + signPoint.getY().bitLength());
        String signPointString = signPoint.toHexString();
        return signPointString;
    }

	
	//For generating a signature using private key on the certificate
    //Returns signature in point representation
    private Point signatureGeneration(String m){ 
        BigInteger e, k, r, s = BigInteger.ZERO;
 /////////////// Hash       
        IHash hashing = new Hash();
        e = new BigInteger( hashing.sha3(m, 224), 16);
        //e = new BigInteger("7e16b5527c77ea58bac36dddda6f5b444f32e81b", 16);
        Point x1y1 = new Point();
        do{
            k = randomBigInteger(n.subtract(BigInteger.ONE));
            x1y1 = G.multiplication(k);
            r = x1y1.getX().mod(n);
            if (! (r.compareTo(BigInteger.ZERO) == 0)){
                if (k.gcd(n).compareTo(BigInteger.ONE) == 0){
                    BigInteger temp = k.modInverse(n);
                    s = (temp.multiply((pr.multiply(r)).add(e))).mod(n);
                }
            }
        } while ((r.compareTo(BigInteger.ZERO) == 0) || (s.compareTo(BigInteger.ZERO) == 0));
        Point signature = new Point();
        signature.setX(r);
        signature.setY(s);
        return signature;
    }
    
    //BigInteger random generator in closed set [1, n]
    private BigInteger randomBigInteger(BigInteger n) {
        Random rnd = new Random();
        int maxNumBitLength = n.bitLength();
        BigInteger aRandomBigInt;
        do {
            aRandomBigInt = new BigInteger(maxNumBitLength, rnd);
            // compare random number less than given number
        } while (aRandomBigInt.compareTo(n) > 0); 
        return aRandomBigInt;
    }
}
