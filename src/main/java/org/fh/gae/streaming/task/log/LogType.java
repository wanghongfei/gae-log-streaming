package org.fh.gae.streaming.task.log;

public enum LogType {
    SEARCH_LOG(0),
    EXPOSE_LOG(1);

    private int code;

    LogType(int code) {
        this.code = code;
    }

    public static LogType of(int code) {
        switch (code) {
            case 0:
                return SEARCH_LOG;

            case 1:
                return EXPOSE_LOG;
        }

        return null;
    }
}
