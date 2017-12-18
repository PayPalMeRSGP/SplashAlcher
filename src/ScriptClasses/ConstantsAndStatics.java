package ScriptClasses;

import Nodes.AlchNode;
import Nodes.StunNode;
import org.osbot.rs07.api.Mouse;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.script.Script;

import java.awt.Point;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class ConstantsAndStatics {

    public static final int RS_GAME_TICK_MS = 603;
    public static final double BETWEEN_ALCH_STDDEV_MS = 20;
    public static final double BETWEEN_ALCH_MEAN_MS = 215;

    public static final Point STUN_UPPER_LEFT_BOUND = new Point(613, 417);
    public static final Point STUN_LOWER_RIGHT_BOUND = new Point(626, 431);
    public static final Point EARTH_BLAST_UPPER_LEFT_BOUND = new Point(686,322);
    public static final Point EARTH_BLAST_LOWER_RIGHT_BOUND = new Point(700,336);
    public static final Point ALCH_NOTHING_UPPER_LEFT_BOUNDS = new Point(721,319);
    public static final Point ALCH_NOTHING_LOWER_RIGHT_BOUNDS = new Point(725,337);

    public static final int DEBUG_NPC_ID = 2838;
    public static final String DEBUG_ITEM = "Magic longbow";

    public static Script hostScriptReference;

    private ConstantsAndStatics(){} //meant to be a constant provider, no constructor

    public static void setHostScriptReference(Script ref){
        hostScriptReference = ref;
    }

    public static long randomNormalDist(double mean, double stddev){
        long debug = (long) ((new Random().nextGaussian() * stddev + mean));
        return Math.abs(debug); //in case we get a negative number
    }



    public static boolean hoverOverStun(Script hostScriptReference){
        return hoverOverArea(STUN_UPPER_LEFT_BOUND, STUN_LOWER_RIGHT_BOUND, hostScriptReference);
    }

    private static boolean hoverOverArea(Point upperLeftBound, Point lowerRightBound, Script hostScriptReference){
        int randX = ThreadLocalRandom.current().nextInt(upperLeftBound.x, lowerRightBound.x);
        int randY = ThreadLocalRandom.current().nextInt(upperLeftBound.y, lowerRightBound.y);
        return !hostScriptReference.getMouse().move(randX, randY);
    }
}
