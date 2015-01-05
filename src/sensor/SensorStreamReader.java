package sensor;

import sensor.listener.AccelerometerListener;
import sensor.listener.GyroscopeListener;
import sensor.listener.MagnetometerListener;


public interface SensorStreamReader {

	public void start() throws Exception;

	void addAccelerometerListener(AccelerometerListener l);
	void addMagnetometerListener(MagnetometerListener l);
	void addGyroscopeListener(GyroscopeListener l);

}
