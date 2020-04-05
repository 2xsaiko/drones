package net.dblsaiko.drones.util;

public interface RcReceiver {

    RcInputState inputs();

    RcAction nextAction();

}
