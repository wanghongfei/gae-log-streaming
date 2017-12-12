package org.fh.gae.streaming;

import org.fh.gae.streaming.task.ChargingTask;

public class LogStreamingApp {
    public static void main(String[] args) throws Exception {
        new ChargingTask().run("gae-charging");
    }
}
