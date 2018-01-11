package Nodes;

import ScriptClasses.PublicStaticFinalConstants;
import org.osbot.rs07.api.Magic;
import org.osbot.rs07.api.ui.Spells;
import org.osbot.rs07.script.MethodProvider;

public class AlchNode implements ExecutableNode, Comparable<ExecutableNode> {

    private final int BASE_STARTING_KEY = 0;
    private int currentKey = BASE_STARTING_KEY;
    private static AlchNode alchNodeSingleton;

    private AlchNode(){

    }

    public static AlchNode getAlchNodeInstance() {
        if(alchNodeSingleton == null){
            alchNodeSingleton = new AlchNode();
        }
        return alchNodeSingleton;
    }

    @Override
    public int executeNodeAction() throws InterruptedException {
        Magic m = PublicStaticFinalConstants.hostScriptReference.getMagic();

        if(!m.castSpell(Spells.NormalSpells.HIGH_LEVEL_ALCHEMY)){
            PublicStaticFinalConstants.hostScriptReference.log("client error: could not find high alch");
        }
        MethodProvider.sleep(PublicStaticFinalConstants.randomNormalDist(PublicStaticFinalConstants.BETWEEN_ALCH_MEAN_MS, PublicStaticFinalConstants.BETWEEN_ALCH_STDDEV_MS));
        if(m.isSpellSelected()){
            PublicStaticFinalConstants.hostScriptReference.getInventory().interact("Cast", PublicStaticFinalConstants.targetItem);
        }

        if(PublicStaticFinalConstants.hoverOverSplashSpell()){
            return (int) PublicStaticFinalConstants.randomNormalDist(100, 10);
        }


        return 0;
    }

    @Override
    public void increaseKey() {
        this.currentKey++;
    }

    @Override
    public void attemptDecreaseKey() {
        if (!(currentKey < 0)){
            this.currentKey--;
        }
    }

    @Override
    public void resetKey() {
        this.currentKey = this.BASE_STARTING_KEY;
    }

    @Override
    public void setKey(int key) {
        this.currentKey = key;
    }

    @Override
    public int getKey() {
        return this.currentKey;
    }

    @Override
    public String getStatus() {
        return "Alching";
    }

    @Override
    public int compareTo(ExecutableNode o) {
        int diff = this.getKey() - o.getKey();
        if(diff == 0){
            if(o instanceof AlchErrorNode){
                return 1;/*if tie, give priority to the other node, which is an error node (hopefully)
                           because if a tie exists between an alch error or alch node. We do alch error first then an successful alch to
                           emulate a player making a mistake then correcting it.
                          */
            }
        }
        return diff;

    }

    @Override
    public String toString(){
        return "Type: Alch, CurrentKey: " + currentKey;
    }
}
