package sensor.implementation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import jssc.SerialPort;
import jssc.SerialPortException;
import sensor.SensorStreamReader;
import sensor.listener.QuaternionListener;
import sensor.listener.VectorListener;
import sensor.math.Quaternion4f;
import sensor.math.Vector3s;

public class BasicReader implements SensorStreamReader, Runnable {

	// this is how it work:
	// every 5000 packet we expect to receive 0xff for four time (0xffffffff)
	// then we expect to find, in loop:
	// 1 byte describing what message is coming: "a", "m", "g" are 3 int each message, while "q" is 4 float.
	// then read remaning byte.
	// Please be nice leave upper-case letter for double (not implemented), lets try to keep this clean.
	
	private final static byte TAP_VALUE[] = {(byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff,(byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff};
	private final static int TAP_FREQUENCY = 1000;
	
	private final Logger log = Logger.getLogger( BasicReader.class.getName() );
	
	private final ArrayList<VectorListener> vectorListeners = new ArrayList<>();
	private final ArrayList<QuaternionListener> quaternionListeners = new ArrayList<>();
	
	private SerialPort serialPort;
	Thread myself = new Thread(this);
	
	private int readed=0;
	
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
				
				serialPort.setParams(SerialPort.BAUDRATE_115200, 
		                SerialPort.DATABITS_8,
		                SerialPort.STOPBITS_1,
		                SerialPort.PARITY_NONE);
				
			} catch (Exception e) {
				e.printStackTrace();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				}
			}
		}
		log.info("Serial port to be opened");
		
		try {
			log.info("Waiting for sync message");
			//wait for sync message 0xffffffff, if not already received.
			int lostBytes = waitSyncornized();
			log.info("got sync message after "+lostBytes+" bytes");
			
			int packetRecivedFromLastTap = 0;
			
			int packetRecived = 0;
			boolean outOfSync = false;
			long start_MS = System.currentTimeMillis();
			long packetCount = 0;
			while ( serialPort.isOpened() ){
			
				packetRecivedFromLastTap++;
				//there should be a new synchronization message  
				if (packetRecivedFromLastTap >= TAP_FREQUENCY || outOfSync){
					log.info("Waiting for sync message");
					lostBytes = waitSyncornized();
					log.info("got sync message after "+lostBytes+" bytes");
					packetRecivedFromLastTap = 0;
					outOfSync = false;
				}
				
				packetRecived++;
				packetCount++;
				
				//log.info("waiting type");
				//what kind of packet expect?
				byte packetType = getBytes(1)[0];
				
				//log.info("got type "+packetType);
				switch(packetType){
				case 'a':
					Vector3s a = Vector3s.parse( getBytes(6) );
					for (VectorListener listener:vectorListeners){
						listener.event( VectorListener.EventType.acce, a, packetCount);
					}
					break;
				case 'm':
					Vector3s m = Vector3s.parse( getBytes(6) );
					for (VectorListener listener:vectorListeners){
						listener.event(VectorListener.EventType.magne, m, packetCount);
					}
					break;
				case 'g':
					Vector3s g = Vector3s.parse( getBytes(6) );
					for (VectorListener listener:vectorListeners){
						listener.event( VectorListener.EventType.gyro, g, packetCount);
					}
					break;
				case 'q':
					Quaternion4f q = Quaternion4f.parse( getBytes(6) );
					for (QuaternionListener listener:quaternionListeners){
						listener.event(q);
					}
					break;
				default:
					outOfSync=true;//error! wait next sync message
					System.err.println("Error, unexpected packet type "+packetType);
					//byte[] readBytes = getBytes();
					//if (readBytes!=null){
					//	System.err.println("Buffer is 0x"+bytesToHex(readBytes) );
					//}
					continue;
				}
				long actualTimeMS= System.currentTimeMillis();
				if (actualTimeMS-start_MS >= 1000){
					//almost every seconds, wedon'tcaretoo much about precision, see getBytes that just sum up xD
					start_MS = actualTimeMS;
					log.info("Bytes/s "+readed +" packet/s "+packetRecived);
					packetRecived=readed = 0;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private byte[] getBytes(int size) throws SerialPortException, IOException {
		readed += size;
		
		int size_buffer = serialPort.getInputBufferBytesCount();
		if (size_buffer > 300){
			log.severe("buffer grossi: "+size_buffer);
		}
		
		while(serialPort.getInputBufferBytesCount()<size){
			try {
				Thread.sleep(0, 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		byte[] readBytes = serialPort.readBytes(size);
		//System.out.println(bytesToHex(readBytes));
		return readBytes;
	}
	/*
	private void getBytes(byte ris[]) throws SerialPortException, IOException {
		
		int size_buffer = serialPort.getInputBufferBytesCount();
		if (size_buffer > 300){
			log.severe("buffer grossi: "+size_buffer);
		}
		return serialPort.readBytes(ris.length);
	}*/

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

	private int waitSyncornized() throws SerialPortException, IOException {
		byte tapValueFound = 0;
		byte ris[];
		int lostByte = 0;
		while(tapValueFound < TAP_VALUE.length){
			ris = getBytes(1);
			//System.out.println("GOT: "+bytesToHex(ris) );
			if (ris[0] == TAP_VALUE[tapValueFound]){
				tapValueFound++;
			}else{
				lostByte+=tapValueFound+1;
				tapValueFound=0;//start the counting again
			}
		}
		return lostByte;
	}

	@Override
	public void addListener(VectorListener l) {
		vectorListeners.add(l);
	}

}
