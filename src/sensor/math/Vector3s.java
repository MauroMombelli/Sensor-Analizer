package sensor.math;

public class Vector3s {
	
	short arr[] = new short[3];
	
	public static Vector3s parse(byte[] buffer) {
		Vector3s ris = new Vector3s();
		for (int i=0; i < 3; i++){
			ris.arr[i] |= buffer[i*2+1] & 0xFF;
			ris.arr[i] |= buffer[i*2]<<8;
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
}
