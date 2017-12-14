package Nodes;

import ScriptClasses.ConstantsAndStatics;
import org.osbot.rs07.api.Magic;
import org.osbot.rs07.api.model.NPC;
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
            ConstantsAndStatics.hostScriptReference.log("creating new alchNodeSingleton");
            alchNodeSingleton = new AlchNode();
        }
        else{
            ConstantsAndStatics.hostScriptReference.log("using old new alchNodeSingleton");
        }

        return alchNodeSingleton;
    }



    @Override
    public int executeNodeAction() throws InterruptedException {
        Magic m = ConstantsAndStatics.hostScriptReference.getMagic();
        if(m.canCast(Spells.NormalSpells.HIGH_LEVEL_ALCHEMY)){
            m.castSpell(Spells.NormalSpells.HIGH_LEVEL_ALCHEMY);
            MethodProvider.sleep(ConstantsAndStatics.randomNormalDist(ConstantsAndStatics.BETWEEN_ALCH_MEAN_MS, ConstantsAndStatics.BETWEEN_ALCH_STDDEV_MS));
            if(m.isSpellSelected()){
                ConstantsAndStatics.hostScriptReference.getInventory().interact("Cast","Magic longbow");
            }
            MethodProvider.sleep(ConstantsAndStatics.randomNormalDist(ConstantsAndStatics.RS_GAME_TICK_MS, 80));
            if(ConstantsAndStatics.hoverOverStun(ConstantsAndStatics.hostScriptReference)){
                return (int) ConstantsAndStatics.randomNormalDist(30, 3);
            }
        }

        return 0;
    }

    @Override
    public void increaseKey() {
        this.currentKey++;
    }

    @Override
    public void decreaseKey() {
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
    public int compareTo(ExecutableNode o) {
        return o.getKey() - this.getKey();
    }

    @Override
    public String toString(){
        return "Type: Alch, CurrentKey: " + currentKey;
    }
}
