package test;

import sensor.SensorStreamReader;
import sensor.implementation.BasicReader;
import sensor.listener.AccelerometerListener;
import sensor.listener.GyroscopeListener;
import sensor.listener.MagnetometerListener;
import sensor.math.Vector3s;

public class TestStm32 {

	public static void main(String args[]){
		SensorStreamReader listener = new BasicReader();
		
		try {
			listener.start();
			
			listener.addAccelerometerListener(new AccelerometerListener() {
				@Override
				public void event(Vector3s a) {
					System.out.println("Readed accelerometer: "+a);
				}
			});
			
			listener.addGyroscopeListener(new GyroscopeListener() {
				@Override
				public void event(Vector3s a) {
					System.out.println("Readed gyroscope: "+a);
				}
			});
			
			listener.addMagnetometerListener(new MagnetometerListener() {
				@Override
				public void event(Vector3s a) {
					System.out.println("Readed magnetometer: "+a);
				}
			});
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
