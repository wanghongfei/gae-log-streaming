package org.fh.gae.streaming.task.log;

import java.io.Serializable;

public class GaeLog implements Serializable {
    private LogType logType;

    protected GaeLog(LogType type) {
        this.logType = type;
    }

    public LogType getLogType() {
        return logType;
    }
}
