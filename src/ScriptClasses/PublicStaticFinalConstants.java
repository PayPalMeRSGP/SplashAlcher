package ScriptClasses;

import org.osbot.rs07.api.ui.Spells;
import org.osbot.rs07.script.Script;

import java.awt.Point;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class PublicStaticFinalConstants {
    public static final String SCRIPT_NAME = "Splash_Alcher";
    public static final int RS_GAME_TICK_MS = 603;
    public static final double BETWEEN_ALCH_STDDEV_MS = 20;
    public static final double BETWEEN_ALCH_MEAN_MS = 215;

    public static final Point CURSE_UPPER_LEFT_BOUND = new Point(658, 248);
    public static final Point CURSE_LOWER_RIGHT_BOUND = new Point(677, 265);

    public static final Point VULNERABILITY_UPPER_LEFT_BOUND = new Point(586, 391);
    public static final Point VULNERABILITY_LOWER_RIGHT_BOUND = new Point(605, 409);

    public static final Point ENFEEBLE_UPPER_LEFT_BOUND = new Point(682, 391);
    public static final Point ENFEEBLE_LOWER_RIGHT_BOUND = new Point(700, 409);

    public static final Point STUN_UPPER_LEFT_BOUND = new Point(613, 417);
    public static final Point STUN_LOWER_RIGHT_BOUND = new Point(626, 431);

    public static final Point EARTH_BLAST_UPPER_LEFT_BOUND = new Point(686,322);
    public static final Point EARTH_BLAST_LOWER_RIGHT_BOUND = new Point(700,336);

    public static final Point ALCH_NOTHING_UPPER_LEFT_BOUNDS = new Point(721,319);
    public static final Point ALCH_NOTHING_LOWER_RIGHT_BOUNDS = new Point(725,337);

    public static int targetNPC;
    public static int targetItem;
    public static Spells.NormalSpells splashingSpell;
    public static int totalCastableSpells;

    public static Script hostScriptReference;

    private PublicStaticFinalConstants(){} //meant to be a constant provider, no constructor

    public static boolean canCast(){
        return totalCastableSpells-- > 0;
    }

    public static long randomNormalDist(double mean, double stddev){
        return (long) Math.abs(new Random().nextGaussian() * stddev + mean); //in case we get a negative number
    }

    public static boolean hoverOverSplashSpell(){
        switch (splashingSpell){
            case CURSE:
                return hoverOverArea(CURSE_UPPER_LEFT_BOUND, CURSE_LOWER_RIGHT_BOUND);
            case VULNERABILITY:
                return hoverOverArea(VULNERABILITY_UPPER_LEFT_BOUND, VULNERABILITY_LOWER_RIGHT_BOUND);
            case ENFEEBLE:
                return hoverOverArea(ENFEEBLE_UPPER_LEFT_BOUND, ENFEEBLE_LOWER_RIGHT_BOUND);
            case STUN:
                return hoverOverArea(STUN_UPPER_LEFT_BOUND, STUN_LOWER_RIGHT_BOUND);
            default:
                hostScriptReference.log("MAJOR BUG: invalid splashing spell! Stopping Script.");
                hostScriptReference.stop();
                return false;
        }

    }

    private static boolean hoverOverArea(Point upperLeftBound, Point lowerRightBound){
        int randX = ThreadLocalRandom.current().nextInt(upperLeftBound.x, lowerRightBound.x);
        int randY = ThreadLocalRandom.current().nextInt(upperLeftBound.y, lowerRightBound.y);
        return !hostScriptReference.getMouse().move(randX, randY);
    }

    public static void setTargetNPC(int npcID) {
        PublicStaticFinalConstants.targetNPC = npcID;
    }

    public static void setTargetItem(int itemID) {
        PublicStaticFinalConstants.targetItem = itemID;
    }

    public static void setSplashingSpell(Spells.NormalSpells selectedSpell){
        PublicStaticFinalConstants.splashingSpell = selectedSpell;
    }

    public static void setHostScriptReference(Script ref){
        hostScriptReference = ref;
    }

    public static void setTotalCastableSpells(int num){
        totalCastableSpells = num;
    }
}
