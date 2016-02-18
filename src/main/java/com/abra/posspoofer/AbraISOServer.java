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

    	byte b[] = new byte[5]; 
    	byte[] isoHeader = ISOUtil.hex2byte("6001208100");
    
        try { 
            Logger logger = new Logger(); 
            logger.addListener (new SimpleLogListener (System.out)); 

            ISOChannel clientSideChannel = new NACChannel(UB_SERVER, UB_PORT, packager, b);

            ISOServer server = new ISOServer(UB_PORT, (ServerChannel) clientSideChannel, null);

            server.setLogger (logger, "iso-server"); 
            server.addISORequestListener (new AbraISOServer()); 
            new Thread (server).start(); 
        } catch (Exception e) { 
            e.printStackTrace(); 
        } 
    } 
} 