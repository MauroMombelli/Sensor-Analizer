package sensor.listener;

import sensor.math.Vector3s;

public interface VectorListener {

	public static enum EventType{
		gyro, acce, magne
	}
	void event(EventType t, Vector3s a, long packetNumber);

}
