package sensor.implementation;

import java.util.ArrayList;
import java.util.logging.Logger;

import jssc.SerialPort;
import jssc.SerialPortException;
import sensor.SensorStreamReader;
import sensor.listener.AccelerometerListener;
import sensor.listener.GyroscopeListener;
import sensor.listener.MagnetometerListener;
import sensor.listener.QuaternionListener;
import sensor.math.Quaternion4f;
import sensor.math.Vector3s;

public class BasicReader implements SensorStreamReader, Runnable {

	// this is how it work:
	// every 5000 packet we expect to receive 0xff for four time (0xffffffff)
	// then we expect to find, in loop:
	// 1 byte describing what message is coming: "a", "m", "g" are 3 int each message, while "q" is 4 float.
	// then read remaning byte.
	// Please be nice leave upper-case letter for double (not implemented), lets try to keep this clean.
	
	private final static byte TAP_VALUE[] = {(byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff};
	private final static int TAP_FREQUENCY = 5000;
	
	private final Logger log = Logger.getLogger( BasicReader.class.getName() );
	
	private final ArrayList<AccelerometerListener> accelerometerListeners = new ArrayList<>();
	private final ArrayList<MagnetometerListener> magnetometerListeners = new ArrayList<>();
	private final ArrayList<GyroscopeListener> gyroscopeListeners = new ArrayList<>();
	private final ArrayList<QuaternionListener> quaternionListeners = new ArrayList<>();
	
	private SerialPort serialPort;
	Thread myself = new Thread(this);
	
	@Override
	public void start() throws Exception {
		serialPort = new SerialPort("/dev/ttyACM0");
		myself.start();
		myself.setName( getClass().getName() );
	}

	@Override
	public void run() {
		log.info("Waiting for serial port to be open");
		while ( !serialPort.isOpened() ){
			try {
				serialPort.openPort();
				Thread.sleep(200);
			} catch (SerialPortException e) {
				if ( !e.getExceptionType().equals(SerialPortException.TYPE_PORT_NOT_FOUND) ){
					e.printStackTrace();
					return;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		log.info("Serial port to be opened");
		
		try {
			log.info("Waiting for sync message");
			//wait for sync message 0xffffffff, if not already received.
			waitSyncornized();
			log.info("got sync message");
			
			int packetRecived = 0;
			boolean outOfSync = false;
			while ( serialPort.isOpened() ){
				
				packetRecived++;
				//there should be a new synchronization message  
				if (packetRecived >= TAP_FREQUENCY || outOfSync){
					log.info("Waiting for sync message");
					waitSyncornized();
					log.info("got sync message");
					packetRecived = 0;
					outOfSync = false;
				}
				
				log.info("waiting type");
				//what kind of packet expect?
				byte packetType = serialPort.readBytes(1)[0];
				log.info("got type "+packetType);
				switch(packetType){
				case 'a':
					Vector3s a = Vector3s.parse( serialPort.readBytes(6) );
					for (AccelerometerListener listener:accelerometerListeners){
						listener.event(a);
					}
					break;
				case 'm':
					Vector3s m = Vector3s.parse( serialPort.readBytes(6) );
					for (MagnetometerListener listener:magnetometerListeners){
						listener.event(m);
					}
					break;
				case 'g':
					Vector3s g = Vector3s.parse( serialPort.readBytes(6) );
					for (GyroscopeListener listener:gyroscopeListeners){
						listener.event(g);
					}
					break;
				case 'q':
					Quaternion4f q = Quaternion4f.parse( serialPort.readBytes(6) );
					for (QuaternionListener listener:quaternionListeners){
						listener.event(q);
					}
					break;
				default:
					outOfSync=true;//error! wait next sync message
					System.err.println("Error, unexpected packet type "+packetType);
					byte[] readBytes = serialPort.readBytes();
					if (readBytes!=null){
						System.err.println("Buffer is 0x"+bytesToHex(readBytes) );
						
					}
					continue;
				}
			
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}

	private boolean waitSyncornized() throws SerialPortException {
		byte tapValueFound = 0;
		int size_buffer;
		while(tapValueFound < TAP_VALUE.length){
			size_buffer =serialPort.getInputBufferBytesCount(); 
			if (size_buffer > 300){
				log.severe("buffer grossi: "+size_buffer);
			}
			
			if (serialPort.readBytes(1)[0] == TAP_VALUE[tapValueFound]){
				tapValueFound++;
			}else{
				tapValueFound=0;//start the counting again
			}
		}
		return true;
	}

	@Override
	public void addAccelerometerListener(AccelerometerListener l) {
		accelerometerListeners.add(l);
	}

	@Override
	public void addMagnetometerListener(MagnetometerListener l) {
		magnetometerListeners.add(l);
	}

	@Override
	public void addGyroscopeListener(GyroscopeListener l) {
		gyroscopeListeners.add(l);
	}

}
