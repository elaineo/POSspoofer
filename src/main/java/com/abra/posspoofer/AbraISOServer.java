package com.abra.posspoofer;

import org.jpos.iso.ISOMsg; 
import org.jpos.iso.ISOServer; 
import org.jpos.iso.ISOField; 
import org.jpos.iso.ISOSource;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.ISOChannel;
import org.jpos.iso.ISOException;
import org.jpos.iso.ServerChannel; 
import org.jpos.iso.ISORequestListener; 
import org.jpos.iso.channel.NACChannel; 
import org.jpos.iso.packager.GenericPackager;
import org.jpos.util.Logger; 
import org.jpos.util.SimpleLogListener; 

public class AbraISOServer implements ISORequestListener { 
	public static final String UB_SERVER = "202.14.87.10";
	public static final Integer UB_PORT = 8012;

	
    public boolean process (ISOSource source, ISOMsg m) { 
        try { 
        	System.out.println("something received");
            m = (ISOMsg) m.clone (); 
            m.setResponseMTI(); 
            m.set (new ISOField(39, "99")); 
            source.send(m); 
        } catch (Exception e) { 
            return false; 
        } 
        return true; 
    } 

    public static void main(String args[]) throws ISOException { 
    	
    	GenericPackager packager = new GenericPackager("ub_request.xml");
    
    	// (Unionbank Debit Message specs)
		ISOMsg isoMsg = new ISOMsg();
		isoMsg.setPackager(packager);
		isoMsg.setMTI("0200");
		isoMsg.set(2,"4404520100000034");
		isoMsg.set(3,"001000");
		isoMsg.set(4, "10000");
		isoMsg.set(7, "0217221800");
		isoMsg.set(11, "123456");
		isoMsg.set(12, "221800");
		isoMsg.set(13, "0216");
		isoMsg.set(22,"021");
		isoMsg.set(24,"827");
		isoMsg.set(25,"00");
		//isoMsg.set(35, "0");
		
		isoMsg.set(41, "ELAINEOU");
		isoMsg.set(42, "ABRAGLOBAL01234");
		
		
		
		/*
		byte[] pin = "374181".getBytes();
		BitSet pinb = ISOUtil.int2BitSet(374181, 0);
		isoMsg.set("52", ISOUtil.bitSet2byte(pinb));
 		*/	
		// print the DE list
		//logISOMsg(isoMsg);
 
		// Get and print the output result
		byte[] data = isoMsg.pack();
		System.out.println("RESULT : " + new String(data));
		
		// Create TPDU header
		byte[] isoHeader = ISOUtil.hex2byte("6001208100");
    	
        try { 
            Logger logger = new Logger(); 
            logger.addListener (new SimpleLogListener (System.out)); 

            NACChannel clientSideChannel = new NACChannel(UB_SERVER, UB_PORT, packager, isoHeader);
            clientSideChannel.setLogger(logger, null); 
            
            ISOServer server = new ISOServer(UB_PORT, (ServerChannel) clientSideChannel, null);

            server.setLogger (logger, "iso-server"); 
            server.addISORequestListener (new AbraISOServer()); 
            
            new Thread (server).start();
            
            clientSideChannel.connect();
            clientSideChannel.send(data);
        } catch (Exception e) { 
            e.printStackTrace(); 
        } 
    } 
} 