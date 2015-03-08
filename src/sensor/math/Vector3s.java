package sensor.math;

import java.util.Arrays;

public class Vector3s {
	
	short arr[] = new short[3];
	
	public Vector3s(){
	}
	
	public Vector3s(short x, short y, short z){
		arr[0] = x;
		arr[1] = y;
		arr[2] = z;
	}
	
	public static Vector3s parse(byte[] buffer, boolean MSB) {
		Vector3s ris = new Vector3s();
		for (int i=0; i < 3; i++){
			//ris.arr[i] |= buffer[i*2+1] & 0xFF;
			//ris.arr[i] |= buffer[i*2]<<8;
			ris.arr[i]=(short)( ((buffer[i*2+(MSB?1:0)] & 0xFF)<<8) | (buffer[i*2+(MSB?0:1)] & 0xFF) );
		}
		return ris;
	}

	@Override
	public String toString(){
		return Arrays.toString(arr);
	}

	public short getX() {
		return arr[0];
	}
	
	public short getY() {
		return arr[1];
	}
	
	public short getZ() {
		return arr[2];
	}

	public void setX(short x) {
		arr[0] = x;
	}
	public void setY(short y) {
		arr[1] = y;
	}
	public void setZ(short z) {
		arr[2] = z;
	}

	public void sub(Vector3s gyroZero) {
		for (int i=0; i < 3; i++)
			arr[i] -= gyroZero.arr[i]; 
	}
	
}
