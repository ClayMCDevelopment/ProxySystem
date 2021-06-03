package eu.claymc.proxysystem.timeout;

public interface ITimeoutManager {

    void timeout(long length);
    long getTimeoutLength();
    boolean isActive();




}
