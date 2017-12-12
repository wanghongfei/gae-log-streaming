package org.fh.gae.streaming.task.log;

public class ChargeLog extends GaeLog {
    private long exposeTs;

    private SearchLog searchLog;

    public ChargeLog() {
        super(LogType.CHARGE_LOG);
    }


    public long getExposeTs() {
        return exposeTs;
    }

    public void setExposeTs(long exposeTs) {
        this.exposeTs = exposeTs;
    }

    public SearchLog getSearchLog() {
        return searchLog;
    }

    public void setSearchLog(SearchLog searchLog) {
        this.searchLog = searchLog;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(60);

        sb.append(getLogType().code()).append('\t');
        sb.append(exposeTs).append('\t');
        sb.append(searchLog.toString());

        return sb.toString();
    }
}
