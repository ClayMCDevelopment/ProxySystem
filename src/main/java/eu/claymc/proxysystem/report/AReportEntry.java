package eu.claymc.proxysystem.report;

import eu.thesimplecloud.api.player.IOfflineCloudPlayer;

public abstract class AReportEntry {

    private int reportId;
    private int priority;
    private boolean closed;

    private IOfflineCloudPlayer reporter;
    private IOfflineCloudPlayer suspect;

    private String reason;

    private long timestamp;

    public AReportEntry reportId(int reportId) {
        this.reportId = reportId;
        return this;
    }

    public AReportEntry priority(int priority){
        this.priority = priority;
        return this;
    }

    public AReportEntry reporter(IOfflineCloudPlayer reporter) {
        this.reporter = reporter;
        return this;
    }

    public AReportEntry suspect(IOfflineCloudPlayer suspect) {
        this.suspect = suspect;
        return this;
    }

    public AReportEntry reason(String reason) {
        this.reason = reason;
        return this;
    }

    public AReportEntry timestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public AReportEntry closed(boolean closed){
        this.closed = closed;
        return this;
    }


    public int reportId() {
        return this.reportId;
    }


    public int priority(){
        return this.priority;
    }

    public IOfflineCloudPlayer reporter() {
        return this.reporter;
    }

    public IOfflineCloudPlayer suspect() {
        return this.suspect;
    }

    public String reason() {
        return this.reason;
    }

    public long timestamp() {
        return this.timestamp;
    }

    public boolean closed(){
        return this.closed;
    }

    public abstract void commit();


}
