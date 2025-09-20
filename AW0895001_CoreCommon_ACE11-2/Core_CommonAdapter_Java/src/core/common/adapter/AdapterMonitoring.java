package core.common.adapter;

//import java.net.Inet4Address;

public class AdapterMonitoring {
    public static String ReturnMyIP() {
	try {
	    //String myIp = Inet4Address.getLocalHost().getHostAddress();
	    String myIp = System.getenv("GET_POD_IP");
	    return myIp;
	} catch (Exception e) {
	    return null;
	}
    }

}
