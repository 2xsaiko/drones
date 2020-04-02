package drones.util;

public interface RcReceiver {

    RcInputState inputs();

    RcAction nextAction();

}
