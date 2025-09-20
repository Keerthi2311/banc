package core.common.util2;

public class SystemUtil {	
    public static String getEnv(String env) {
	try {
		return System.getenv(env);
	} catch (Exception e) {
	    return null;
	}
    }
    
    public static void printLnConsole(String data) {
    	System.out.println(data);
    }    
}
