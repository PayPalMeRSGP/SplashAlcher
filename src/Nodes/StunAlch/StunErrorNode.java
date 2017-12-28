package Nodes.StunAlch;

import Nodes.ExecutableNode;
import ScriptClasses.ConstantsAndStatics;
import org.osbot.rs07.api.Mouse;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.ui.Spells;
import org.osbot.rs07.input.mouse.EntityDestination;
import org.osbot.rs07.input.mouse.RectangleDestination;
import org.osbot.rs07.script.Script;

import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

//TODO: Implement
public class StunErrorNode implements ExecutableNode, Comparable<ExecutableNode> {

    private final int BASE_STARTING_KEY = 27; //odd numbers only, I subtract 2 at a time so key will not every be 0 causing a tie with an alch or stun node.
    private int currentKey = BASE_STARTING_KEY;

    private static StunErrorNode singleton;

    private StunErrorNode(){}

    public static StunErrorNode getStunErrorNodeInstance(){
        if(singleton == null){
            singleton = new StunErrorNode();
        }
        return singleton;
    }

    @Override
    public int executeNodeAction() {
        ConstantsAndStatics.hostScriptReference.log("committing misclickNPC artificial error");
        misclickNPC();
        return (int) ConstantsAndStatics.randomNormalDist(200,30);
    }

    private void misclickNPC(){
        NPC targetNpc = ConstantsAndStatics.hostScriptReference.getNpcs().closest(ConstantsAndStatics.targetNPC);
        EntityDestination stunTarget = new EntityDestination(ConstantsAndStatics.hostScriptReference.getBot(), targetNpc);
        Rectangle boundingBox = stunTarget.getBoundingBox();
        int MAX_MISCLICK_AMOUNT = 25;
        int misclickBoundX = (int) boundingBox.getX() + MAX_MISCLICK_AMOUNT;
        int misclickBoundY = (int) boundingBox.getY() + MAX_MISCLICK_AMOUNT;

        Script hostScriptRef = ConstantsAndStatics.hostScriptReference;
        Mouse mouse = ConstantsAndStatics.hostScriptReference.getMouse();
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

    @Override
    public void increaseKey() {
        currentKey++;
    }

    @Override
    public void attemptDecreaseKey() {
        int randNum = ThreadLocalRandom.current().nextInt(0, 10);
        if(randNum >= 7){ //70% change to decrement
            this.currentKey -= 2;
        }
    }

    @Override
    public void resetKey() {
        currentKey = BASE_STARTING_KEY;
    }

    @Override
    public void setKey(int key) {
        currentKey = key;
    }

    @Override
    public int getKey() {
        return this.currentKey;
    }

    @Override
    public int compareTo(ExecutableNode o) {
        int diff = this.getKey() - o.getKey();
        if(diff == 0){
            if(o instanceof AlchNode){
                return 1;
            }
            else if(o instanceof StunNode){
                return -1;
            }
        }
        return diff;
    }

    @Override
    public String toString(){
        return "Type: StunErrorNode, CurrentKey: " + currentKey;
    }
}