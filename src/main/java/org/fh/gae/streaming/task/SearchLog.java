package org.fh.gae.streaming.task;

public class SearchLog extends GaeLog {
    private String sid;

    private long bid;

    private long ts;

    public SearchLog() {
        super(LogType.SEARCH_LOG);
    }

    public SearchLog(String sid, long bid, long ts) {
        this();

        this.sid = sid;
        this.bid = bid;
        this.ts = ts;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public long getBid() {
        return bid;
    }

    public void setBid(long bid) {
        this.bid = bid;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SearchLog{");
        sb.append("sid='").append(sid).append('\'');
        sb.append(", bid=").append(bid);
        sb.append(", ts=").append(ts);
        sb.append('}');
        return sb.toString();
    }
}
