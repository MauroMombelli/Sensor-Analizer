package sensor.listener;

import sensor.math.Vector3s;

public interface MagnetometerListener {

	void event(Vector3s a, long packetNumber);
}
