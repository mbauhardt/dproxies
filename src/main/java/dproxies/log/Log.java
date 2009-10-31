package dproxies.log;

public interface Log {

    void debug(String message);
    
    void info(String message);
    
    void error(String message);
    
    void warn(String message);
    
}
