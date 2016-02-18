package com.abra.posspoofer;
 
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.channel.NACChannel;
import org.jpos.iso.packager.GenericPackager;
import org.jpos.util.Logger;
import org.jpos.util.SimpleLogListener;
 
public class BuildISOMessage {
	
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
 
	public static void main(String[] args) throws IOException, ISOException {
		// Create Packager based on XML that contain DE type
		GenericPackager packager = new GenericPackager("ub_request.xml");
		
		// Create logger to dump channel output
		Logger logger = new Logger();
		logger.addListener(new SimpleLogListener (System.out)); 
		
		HashMap<String, String> transaction = new HashMap<String, String>();
		transaction.put("2","2C04340100000022");
		transaction.put("3","000A00");
		transaction.put("22","021");
		transaction.put("24","827");
		transaction.put("25","00");
		//transaction.put("35","0"); // FIXME
		
		
		Set set = transaction.entrySet();
	    Iterator iterator = set.iterator();
 
		// Create ISO Message 
		// (Unionbank Debit Message specs)
		ISOMsg isoMsg = new ISOMsg();
		isoMsg.setPackager(packager);
		isoMsg.setMTI("0200");
		/*
    	while(iterator.hasNext()) {
    		Map.Entry mentry = (Map.Entry)iterator.next();
    		byte [] bval = ISOUtil.str2bcd("4404520100000034", false);
    		System.out.println(bval);
    		isoMsg.set((String) mentry.getKey(), bval);
    	}
    	*/
		
		//isoMsg.set(4, "10000");
		//isoMsg.set(7, "0217221800");
		//isoMsg.set(11, "123456");
		//isoMsg.set(12, "221800");
		//isoMsg.set(13, "0216");
    	
		isoMsg.set(2,"164404520100000034");
		isoMsg.set(3,"001000");
		isoMsg.set(4, "10000");
		isoMsg.set(7, "0211161200");
		isoMsg.set(11, "000001");
		isoMsg.set(12, "161200");
		isoMsg.set(13, "0210");
		isoMsg.set(22,"021");
		isoMsg.set(24,"827");
		isoMsg.set(25,"00");
		//isoMsg.set(35, "0");
		
		isoMsg.set(41, "ELAINEOU");
		isoMsg.set(42, "ABRAGLOBAL01234");
		
		
		//EncryptedPIN pin = BaseSMAdapter.encryptPIN("blah","blah", true);
		/*
		byte[] pin = "374181".getBytes();
		BitSet pinb = ISOUtil.int2BitSet(374181, 0);
		isoMsg.set("52", ISOUtil.bitSet2byte(pinb));
 		*/	
		// print the DE list
		logISOMsg(isoMsg);
 
		// Get and print the output result
		byte[] data = isoMsg.pack();
		System.out.println("RESULT : " + bytesToHex(data));
		
		// Create TPDU header
		byte[] isoHeader = ISOUtil.hex2byte("6001208100");
		
		// Send to endpoint
		NACChannel nc = new NACChannel("202.14.87.10", 8012, packager, isoHeader);
		nc.setLogger(logger, null); 
		nc.connect();
		nc.send(isoMsg);
		ISOMsg r = nc.receive();
		logISOMsg(r);
		//nc.send(data);
		
	}
 
	private static void logISOMsg(ISOMsg msg) {
		System.out.println("----ISO MESSAGE-----");
		try {
			System.out.println("  MTI : " + msg.getMTI());
			for (int i=1;i<=msg.getMaxField();i++) {
				if (msg.hasField(i)) {
					System.out.println("    Field-"+i+" : "+msg.getString(i));
				}
			}
		} catch (ISOException e) {
			e.printStackTrace();
		} finally {
			System.out.println("--------------------");
		}
 
	}
 
}