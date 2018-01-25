package Nodes;

import ScriptClasses.MainScript;
import ScriptClasses.PublicStaticFinalConstants;
import org.osbot.rs07.api.Mouse;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.ui.Spells;
import org.osbot.rs07.input.mouse.EntityDestination;
import org.osbot.rs07.input.mouse.RectangleDestination;
import org.osbot.rs07.script.Script;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;


public class SplashErrorNode implements ExecutableNode {

    private final static String NODE_STATUS = "Splashing Antiban";

    private static SplashErrorNode singleton;

    private SplashErrorNode(){}

    public static SplashErrorNode getSplashErrorNodeInstance(){
        if(singleton == null){
            singleton = new SplashErrorNode();
        }
        return singleton;
    }

    @Override
    public int executeNodeAction() {
        setScriptStatus();
        misclickNPC();
        return (int) PublicStaticFinalConstants.randomNormalDist(2500,500);
    }

    private void misclickNPC(){
        NPC targetNpc = PublicStaticFinalConstants.hostScriptReference.getNpcs().closest(PublicStaticFinalConstants.targetNPC);
        EntityDestination stunTarget = new EntityDestination(PublicStaticFinalConstants.hostScriptReference.getBot(), targetNpc);
        Rectangle boundingBox = stunTarget.getBoundingBox();
        int MAX_MISCLICK_AMOUNT = 25;
        int misclickBoundX = (int) boundingBox.getX() + MAX_MISCLICK_AMOUNT;
        int misclickBoundY = (int) boundingBox.getY() + MAX_MISCLICK_AMOUNT;

        Script hostScriptRef = PublicStaticFinalConstants.hostScriptReference;
        Mouse mouse = PublicStaticFinalConstants.hostScriptReference.getMouse();
        boolean useVertBoundingBox = ThreadLocalRandom.current().nextBoolean();
        hostScriptRef.getMagic().castSpell(Spells.NormalSpells.STUN);
        if(useVertBoundingBox){
            Rectangle vertMisclickBoundingBox = new Rectangle(misclickBoundX, misclickBoundY, MAX_MISCLICK_AMOUNT, (int) boundingBox.getHeight());
            RectangleDestination mouseRectangleDest = new RectangleDestination(hostScriptRef.getBot(), vertMisclickBoundingBox);
            if(hostScriptRef.getMagic().isSpellSelected()){ //if spell is not selected player moves
                mouse.click(mouseRectangleDest);
            }

        }
        else{
            Rectangle horizMisclickBoundingBox = new Rectangle(misclickBoundX, misclickBoundY, (int) boundingBox.getWidth(), MAX_MISCLICK_AMOUNT);
            RectangleDestination mouseRectangleDest = new RectangleDestination(hostScriptRef.getBot(), horizMisclickBoundingBox);
            if(hostScriptRef.getMagic().isSpellSelected()){
                mouse.click(mouseRectangleDest);
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
        return "Splash Antiban";
    }

}
