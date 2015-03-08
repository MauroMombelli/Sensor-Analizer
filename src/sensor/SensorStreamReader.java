package sensor;

import sensor.listener.QuaternionListener;
import sensor.listener.VectorListener;


public interface SensorStreamReader {

	public void start() throws Exception;

	void addListener(VectorListener l);
	
	void addListener(QuaternionListener l);

}
