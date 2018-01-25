package Nodes;

import ScriptClasses.MainScript;
import ScriptClasses.PublicStaticFinalConstants;
import org.osbot.rs07.api.Magic;
import org.osbot.rs07.api.Mouse;
import org.osbot.rs07.api.ui.Spells;
import org.osbot.rs07.script.MethodProvider;


import java.util.concurrent.ThreadLocalRandom;


public class AlchErrorNode implements ExecutableNode{

    private final static String NODE_STATUS = "Alching Antiban";

    private static AlchErrorNode alchErrorNodeSingleton;

    private AlchErrorNode(){}

    public static AlchErrorNode getAlchErrorNodeInstance() {
        if(alchErrorNodeSingleton == null){
            alchErrorNodeSingleton = new AlchErrorNode();
        }
        return alchErrorNodeSingleton;
    }

    @Override
    public int executeNodeAction() throws InterruptedException {
        Magic m = PublicStaticFinalConstants.hostScriptReference.getMagic();
        int randomNum = ThreadLocalRandom.current().nextInt(0, 2);

        if(randomNum == 0){
            PublicStaticFinalConstants.hostScriptReference.log("committing alchNothing artificial error");
            alchNothing();
        }
        else{
            PublicStaticFinalConstants.hostScriptReference.log("committing misclickEarthBlast artificial error");
            misclickEarthBlast();
        }

        return (int) PublicStaticFinalConstants.randomNormalDist(400, 50);
    }

    private void misclickEarthBlast() throws InterruptedException {
        Mouse mouse = PublicStaticFinalConstants.hostScriptReference.getMouse();
        int randX = ThreadLocalRandom.current().nextInt(PublicStaticFinalConstants.EARTH_BLAST_UPPER_LEFT_BOUND.x, PublicStaticFinalConstants.EARTH_BLAST_LOWER_RIGHT_BOUND.x);
        int randY = ThreadLocalRandom.current().nextInt(PublicStaticFinalConstants.EARTH_BLAST_UPPER_LEFT_BOUND.y, PublicStaticFinalConstants.EARTH_BLAST_LOWER_RIGHT_BOUND.y);
        if(mouse.move(randX, randY)){
            MethodProvider.sleep(PublicStaticFinalConstants.randomNormalDist(30, 5)); //pause before clicking on earth blast
            if(mouse.click(randX, randY, false)){ //select
                if(PublicStaticFinalConstants.hostScriptReference.getMagic().isSpellSelected()){
                    MethodProvider.sleep(PublicStaticFinalConstants.randomNormalDist(400, 50)); //pause to emulate player realizing he has not clicked on alch
                    mouse.click(randX, randY, false); //deselect
                }
            }
        }

    }

    private void alchNothing() throws InterruptedException {
        setScriptStatus();
        Mouse mouse = PublicStaticFinalConstants.hostScriptReference.getMouse();
        Magic m = PublicStaticFinalConstants.hostScriptReference.getMagic();
        int randX = ThreadLocalRandom.current().nextInt(PublicStaticFinalConstants.ALCH_NOTHING_UPPER_LEFT_BOUND.x, PublicStaticFinalConstants.ALCH_NOTHING_LOWER_RIGHT_BOUND.x);
        int randY = ThreadLocalRandom.current().nextInt(PublicStaticFinalConstants.ALCH_NOTHING_UPPER_LEFT_BOUND.y, PublicStaticFinalConstants.ALCH_NOTHING_LOWER_RIGHT_BOUND.y);
        if(mouse.move(randX, randY)){
            MethodProvider.sleep(PublicStaticFinalConstants.randomNormalDist(100, 10));
            if(m.canCast(Spells.NormalSpells.HIGH_LEVEL_ALCHEMY)){
                m.castSpell(Spells.NormalSpells.HIGH_LEVEL_ALCHEMY);
                MethodProvider.sleep(PublicStaticFinalConstants.randomNormalDist(150, 10));
                mouse.click(randX, randY, false); //deselect
                MethodProvider.sleep(PublicStaticFinalConstants.randomNormalDist(2500, 500)); //pause to emulate player realizing that he just alched nothing
                m.open(); //reopen magic tab
            }
        }
    }

    private void setScriptStatus(){
        if(PublicStaticFinalConstants.hostScriptReference instanceof MainScript){
            ((MainScript) PublicStaticFinalConstants.hostScriptReference).setScriptStatus(NODE_STATUS);
        }
    }

    @Override
    public String getStatus() {
        return "Alching Antiban";
    }

}
