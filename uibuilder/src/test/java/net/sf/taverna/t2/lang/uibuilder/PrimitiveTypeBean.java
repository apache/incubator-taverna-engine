package net.sf.taverna.t2.lang.uibuilder;

/**
 * Bean containing all the primitive types in Java (AFAIK)
 * 
 * @author Tom Oinn
 * 
 */
public class PrimitiveTypeBean {

	private int intValue = 1;
	private short shortValue = 2;
	private long longValue = (long) 3.0123;
	private double doubleValue = 4.01234;
	private boolean booleanValue = false;
	private byte byteValue = 5;
	private float floatValue = 6.012345f;
	private char charValue = 'a';

	public PrimitiveTypeBean() {
		//
	}

	public void setIntValue(int intValue) {
		this.intValue = intValue;
	}

	public String toString() {
		return intValue + "," + shortValue + "," + longValue + ","
				+ doubleValue + "," + booleanValue + "," + byteValue + ","
				+ floatValue + "," + charValue;
	}

	public int getIntValue() {
		return intValue;
	}

	public void setShortValue(short shortValue) {
		this.shortValue = shortValue;
		System.out.println(this);
	}

	public short getShortValue() {
		return shortValue;
	}

	public void setLongValue(long longValue) {
		this.longValue = longValue;
		System.out.println(this);
	}

	public long getLongValue() {
		return longValue;
	}

	public void setDoubleValue(double doubleValue) {
		this.doubleValue = doubleValue;
		System.out.println(this);
	}

	public double getDoubleValue() {
		return doubleValue;
	}

	public void setBooleanValue(boolean booleanValue) {
		this.booleanValue = booleanValue;
		System.out.println(this);
	}

	public boolean getBooleanValue() {
		return booleanValue;
	}

	public void setByteValue(byte byteValue) {
		this.byteValue = byteValue;
		System.out.println(this);
	}

	public byte getByteValue() {
		return byteValue;
	}

	public void setFloatValue(float floatValue) {
		this.floatValue = floatValue;
		System.out.println(this);
	}

	public float getFloatValue() {
		return floatValue;
	}

	public void setCharValue(char charValue) {
		this.charValue = charValue;
		System.out.println(this);
	}

	public char getCharValue() {
		return charValue;
	}

}
