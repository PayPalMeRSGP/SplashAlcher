package ScriptClasses;

import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.api.ui.Spells;
import org.osbot.rs07.input.mouse.WidgetDestination;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.script.Script;

import java.awt.Point;
import java.util.List;
import java.util.Random;

public class Statics {
    private Statics(){} //meant to be a constant provider, no constructor

    public static long randomNormalDist(double mean, double stddev){
        return (long) Math.abs(new Random().nextGaussian() * stddev + mean); //abs in case we get a negative number
    }

    public static void shortRandomSleep() throws InterruptedException {
        MethodProvider.sleep(randomNormalDist(300,100));
    }


}
