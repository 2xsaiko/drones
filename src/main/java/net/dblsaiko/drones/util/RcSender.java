package net.dblsaiko.drones.util;

public interface RcSender {

    void send(RcInputState inputs);

    void send(RcAction action);

}
