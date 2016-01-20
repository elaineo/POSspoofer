package com.abra.posspoofer;
 
import java.io.IOException;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.channel.NACChannel;
import org.jpos.iso.ISOHeader;
import org.jpos.iso.packager.GenericPackager;
 
public class BuildISOMessage {
 
	public static void main(String[] args) throws IOException, ISOException {
		// Create Packager based on XML that contain DE type
		GenericPackager packager = new GenericPackager("ub_request.xml");
 
		// Create ISO Message 
		// (Unionbank Debit Message specs)
		ISOMsg isoMsg = new ISOMsg();
		isoMsg.setPackager(packager);
		isoMsg.setMTI("0200");
		isoMsg.set(3, "000001");
		isoMsg.set(4, "10000");
		isoMsg.set(7, "0120221800");
		isoMsg.set(11, "123456");
		isoMsg.set(12, "221800");
		isoMsg.set(22, "021");
		isoMsg.set(24, "827");
		isoMsg.set(25, "00");
		isoMsg.set(35, "");
		isoMsg.set(41, "ELAINEOU");
		isoMsg.set(42, "ABRAGLOBAL01234");
 
		// print the DE list
		logISOMsg(isoMsg);
 
		// Get and print the output result
		byte[] data = isoMsg.pack();
		System.out.println("RESULT : " + new String(data));
		
		// Create TPDU header
		byte[] isoHeader = ISOUtil.hex2byte("6001208100");
		
		// Send to endpoint
		// test: 203.131.75.230:8012
		NACChannel nc = new NACChannel("203.131.75.230", 8012, packager, isoHeader);
		nc.connect();
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