package eu.claymc.proxysystem.timeout;

public class SimpleTimeoutManager implements ITimeoutManager {

    private long timeout = System.currentTimeMillis();


    @Override
    public void timeout(long length) {
        this.timeout = length;
    }

    @Override
    public long getTimeoutLength() {
        return this.timeout;
    }

    @Override
    public boolean isActive() {
        return System.currentTimeMillis() - timeout < 0;
    }
}
