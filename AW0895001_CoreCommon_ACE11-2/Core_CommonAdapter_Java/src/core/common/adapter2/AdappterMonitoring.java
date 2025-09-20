package core.common.adapter2;

import java.net.Inet4Address;

public class AdappterMonitoring {
    public static String ReturnMyIP() {
	try {
	    String myIp = Inet4Address.getLocalHost().getHostAddress();
	    return myIp;
	} catch (Exception e) {
	    return null;
	}
    }

}
