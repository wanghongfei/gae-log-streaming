package org.fh.gae.streaming.task;

import java.io.Serializable;

public class ExposeLog extends GaeLog {
    private long ts;

    public ExposeLog() {
        super(LogType.EXPOSE_LOG);
    }

    public ExposeLog(long ts) {
        this();

        this.ts = ts;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ExposeLog{");
        sb.append("ts=").append(ts);
        sb.append('}');
        return sb.toString();
    }
}
