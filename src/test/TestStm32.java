package test;

import filter.AccelerometerFilter;
import filter.GyroscopeFilter;
import filter.MagnetometerFilter;
import sensor.SensorStreamReader;
import sensor.implementation.BasicReader;

public class TestStm32 {

	public static void main(String args[]) {
		
		SensorStreamReader listener = new BasicReader();

		try {
			
			
			GyroscopeFilter gyroFilter = new GyroscopeFilter();
			AccelerometerFilter acceFilter = new AccelerometerFilter();
			MagnetometerFilter magneFilter = new MagnetometerFilter();

			listener.addAccelerometerListener(acceFilter);

			listener.addGyroscopeListener( gyroFilter );

			listener.addMagnetometerListener(magneFilter);

			listener.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
