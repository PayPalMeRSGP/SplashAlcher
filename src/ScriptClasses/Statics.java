package ScriptClasses;

import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.api.ui.Spells;
import org.osbot.rs07.input.mouse.WidgetDestination;
import org.osbot.rs07.script.Script;

import java.awt.Point;
import java.util.List;
import java.util.Random;

public class Statics {
    public static final String SCRIPT_NAME = "Splash_Alcher";
    public static final int RS_GAME_TICK_MS = 603;

    public static Script hostScriptReference;

    private Statics(){} //meant to be a constant provider, no constructor

    public static long randomNormalDist(double mean, double stddev){
        return (long) Math.abs(new Random().nextGaussian() * stddev + mean); //abs in case we get a negative number
    }

    //for errors
    public static void throwIllegalStateException(String msg){
        hostScriptReference.stop(false);
        throw new IllegalStateException(msg);
    }

    public static void setHostScriptReference(Script ref){
        hostScriptReference = ref;
    }

}
