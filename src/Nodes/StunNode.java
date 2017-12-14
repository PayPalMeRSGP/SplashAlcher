package Nodes;

import ScriptClasses.ConstantsAndStatics;
import org.osbot.rs07.api.Magic;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.ui.Spells;
import org.osbot.rs07.script.MethodProvider;

public class StunNode implements ExecutableNode, Comparable<ExecutableNode>{

    private final int BASE_STARTING_KEY = 1;
    private int currentKey = BASE_STARTING_KEY;


    private static StunNode stunNodeSingleton = null;

    private StunNode(){

    }

    public static StunNode getStunNodeInstance() {
        if(stunNodeSingleton == null){
            ConstantsAndStatics.hostScriptReference.log("creating new stunNodeSingleton");
            stunNodeSingleton = new StunNode();
        }
        else{
            ConstantsAndStatics.hostScriptReference.log("using old stunNodeSingleton");
        }

        return stunNodeSingleton;
    }

    @Override
    public int executeNodeAction() throws InterruptedException {
        Magic m = ConstantsAndStatics.hostScriptReference.getMagic();
        NPC npc = ConstantsAndStatics.hostScriptReference.getNpcs().closest(ConstantsAndStatics.DEBUG_NPC);
        if(m.canCast(Spells.NormalSpells.STUN)){
            m.castSpellOnEntity(Spells.NormalSpells.STUN, npc);
            if(m.hoverSpell(Spells.NormalSpells.HIGH_LEVEL_ALCHEMY)){

                return (int) ConstantsAndStatics.randomNormalDist(50, 5);

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
        return "Type: Stun, CurrentKey: " + currentKey;
    }
}
