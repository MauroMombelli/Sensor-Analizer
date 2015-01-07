package sensor.math;

public class Vector3s {
	
	short arr[] = new short[3];
	
	public static Vector3s parse(byte[] buffer) {
		Vector3s ris = new Vector3s();
		for (int i=0; i < 3; i++){
			//ris.arr[i] |= buffer[i*2+1] & 0xFF;
			//ris.arr[i] |= buffer[i*2]<<8;
			ris.arr[i]=(short)( ((buffer[i*2+1] & 0xFF)<<8) | (buffer[i*2] & 0xFF) );
		}
		return ris;
	}

	@Override
	public String toString(){
		StringBuilder s = new StringBuilder(20);
		s.append("(");
		for (int i=0; i < 3; i++){
			s.append( arr[i] );
			s.append( ";");
		}
		s.append(")");
		return s.toString();
	}

	public Number getX() {
		return arr[0];
	}
	
	public Number getY() {
		return arr[1];
	}
	
	public Number getZ() {
		return arr[2];
	}
}
