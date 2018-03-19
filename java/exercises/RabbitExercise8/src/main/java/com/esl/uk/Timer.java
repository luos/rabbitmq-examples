package com.esl.uk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Timer {

    private String ref;
    private String phase;
    private long   start_time;
    private long   stop_time;
    private Logger logger;

    public Timer(String ref){
        this.ref         = ref;
        this.phase       = Helper.IDLE;
        this.start_time  = 0;
        this.logger      = LoggerFactory.getLogger(Timer.class);
    }

    public void start(){
        this.phase      = Helper.RUNNING;
        this.start_time = now();
        this.stop_time  = 0;
    }

    public void stop(){
        this.phase = Helper.STOPPED;
        this.stop_time  = now();
    }

    public void result(){
        logger.info("REFERENCE : {} \nPHASE : {} \nDURATION: {}", this.ref,
                this.phase, (this.start_time - this.stop_time) / 1e6 );
    }

    private long now(){
        return System.nanoTime();
    }
}
