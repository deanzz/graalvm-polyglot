package graal.polyglot;

import java.util.concurrent.Callable;

public class Param {
    public int               id    = 42;
    public String            text  = "42";
    public int[]             arr   = new int[]{1, 42, 3};
    public Callable<Integer> ret42 = () -> 42;
}
