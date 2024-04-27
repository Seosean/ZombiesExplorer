package com.seosean.zombiesexplorer.utils;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public abstract class DelayedTask implements Runnable {

    private int delay;
    private int ticks;
    private boolean isCancelled = false;

    public DelayedTask(){
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event){
        if(event.phase != Phase.START) {
            return;
        }

        if (this.isCancelled) {
            MinecraftForge.EVENT_BUS.unregister(this);
            return;
        }

        if(delay <= 0){
            if (ticks == 0) {
                MinecraftForge.EVENT_BUS.unregister(this);
            } else {
                delay = ticks;
            }
            this.run();
        }

        delay--;
    }

    public DelayedTask runTaskLater(int delay) {
        this.isCancelled = false;
        this.delay = delay;
        return this;
    }

    public DelayedTask runTaskTimer(int delay, int ticks) {
        this.isCancelled = false;
        this.delay = delay;
        this.ticks = ticks;
        return this;
    }

    public void cancel() {
        this.isCancelled = true;
        this.ticks = 0;
    }
}
