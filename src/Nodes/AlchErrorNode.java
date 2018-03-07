package Nodes;

import ScriptClasses.MainScript;
import ScriptClasses.Statics;
import org.osbot.rs07.api.Magic;
import org.osbot.rs07.api.Mouse;
import org.osbot.rs07.api.ui.Spells;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.script.Script;


import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;


public class AlchErrorNode implements ExecutableNode{

    private final static String NODE_STATUS = "Alching Antiban";
    private Script hostScriptReference;

    private static final Point ALCH_NOTHING_UPPER_LEFT_BOUND = new Point(721,319);
    private static final Point ALCH_NOTHING_LOWER_RIGHT_BOUND = new Point(725,337);
    
    private static ExecutableNode singleton;

    public static ExecutableNode getInstance(Script hostScriptReference){
        if(singleton == null){
            singleton = new AlchErrorNode(hostScriptReference);
        }
        return singleton;
    }

    public static ExecutableNode getInstance(){
        if(singleton == null){
            Statics.throwIllegalStateException("need to call other overloaded getInstance first to instantiate");
        }
        return singleton;
    }

    private AlchErrorNode(Script hostScriptReference){
        this.hostScriptReference = hostScriptReference;
    }
    
    @Override
    public int executeNodeAction() throws InterruptedException {
        setScriptStatus();
        int randomNum = ThreadLocalRandom.current().nextInt(0, 2);
        if(randomNum == 0){
            alchNothing();
        }
        else{
            misclickEarthBlast();
        }

        return (int) Statics.randomNormalDist(400, 50);
    }

    private void misclickEarthBlast() throws InterruptedException {
        Magic m = hostScriptReference.getMagic();
        if(m.castSpell(Spells.NormalSpells.EARTH_BLAST)){
            MethodProvider.sleep(Statics.randomNormalDist(100, 30)); //pause before clicking on earth blast
            if(m.isSpellSelected()){
                MethodProvider.sleep(Statics.randomNormalDist(1000, 300)); //pause to emulate player realizing he has not clicked on alch
                m.deselectSpell();
            }
        }
    }

    private void alchNothing() throws InterruptedException {
        Mouse mouse = hostScriptReference.getMouse();
        Magic m = hostScriptReference.getMagic();
        /*
        these coordinates form an area where the alching item is not under high-alch spell.
        If you double click in this area you end up alching nothing.
         */
        int randX = ThreadLocalRandom.current().nextInt(ALCH_NOTHING_UPPER_LEFT_BOUND.x, ALCH_NOTHING_LOWER_RIGHT_BOUND.x);
        int randY = ThreadLocalRandom.current().nextInt(ALCH_NOTHING_UPPER_LEFT_BOUND.y, ALCH_NOTHING_LOWER_RIGHT_BOUND.y);
        if(mouse.move(randX, randY)){
            MethodProvider.sleep(Statics.randomNormalDist(100, 10));
            if(m.canCast(Spells.NormalSpells.HIGH_LEVEL_ALCHEMY)){
                m.castSpell(Spells.NormalSpells.HIGH_LEVEL_ALCHEMY);
                MethodProvider.sleep(Statics.randomNormalDist(150, 10));
                if(m.isSpellSelected()){
                    mouse.click(randX, randY, false); //deselect
                }
                MethodProvider.sleep(Statics.randomNormalDist(2500, 500)); //pause to emulate player realizing that he just alched nothing
                m.open();
            }
        }
    }

    private void setScriptStatus(){
        if(hostScriptReference instanceof MainScript){
            ((MainScript) hostScriptReference).setScriptStatus(NODE_STATUS);
        }
    }
}
