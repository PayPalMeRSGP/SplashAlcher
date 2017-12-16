package Nodes;

import ScriptClasses.ConstantsAndStatics;
import org.osbot.rs07.api.Magic;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.ui.Spells;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.script.MethodProvider;


public class StunNode implements ExecutableNode, Comparable<ExecutableNode>{

    private final int BASE_STARTING_KEY = 1;
    private int currentKey = BASE_STARTING_KEY;
    private static StunNode stunNodeSingleton = null;

    private int stunsLeft = 0;

    private StunNode(){

    }

    public static StunNode getStunNodeInstance() {
        if(stunNodeSingleton == null){
            //ConstantsAndStatics.hostScriptReference.log("creating new stunNodeSingleton");
            stunNodeSingleton = new StunNode();
        }
        else{
            //ConstantsAndStatics.hostScriptReference.log("using old stunNodeSingleton");
        }

        return stunNodeSingleton;
    }

    @Override
    public int executeNodeAction() throws InterruptedException {
        Magic m = ConstantsAndStatics.hostScriptReference.getMagic();
        NPC npc = ConstantsAndStatics.hostScriptReference.getNpcs().closest(ConstantsAndStatics.DEBUG_NPC);
        if(canCastStun()){
            m.castSpellOnEntity(Spells.NormalSpells.STUN, npc);
            if(AlchErrorNode.getAlchErrorNodeInstance().getKey() <= 0){ //alch error is next, let next node handle moving mouse
                return (int) ConstantsAndStatics.randomNormalDist(50, 5);
            }
            if(m.hoverSpell(Spells.NormalSpells.HIGH_LEVEL_ALCHEMY)){ //set up mouse on alch
                return (int) ConstantsAndStatics.randomNormalDist(50, 5);
            }
        }
        return 0;

    }

    private boolean canCastStun() throws InterruptedException {
        Magic m = ConstantsAndStatics.hostScriptReference.getMagic();
        boolean magicTabOpen = false;
        for(int i = 0; i < 5; i++){ //wait until magic tab reopens from alching
            if(!(ConstantsAndStatics.hostScriptReference.getTabs().getOpen() == Tab.MAGIC)){
                MethodProvider.sleep(603); //rs game tick ms
            }
            else{
                magicTabOpen = true;
                break;
            }
        }

        if(magicTabOpen){
            stunsLeft--;
            if(stunsLeft <= 0){
                if(m.canCast(Spells.NormalSpells.STUN)){
                    stunsLeft = 100;
                    return true;
                }
                return false;

            }
            return true;
        }
        return false;
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
    public int compareTo(ExecutableNode o) {
        return this.getKey() - o.getKey();
    }

    @Override
    public String toString(){
        return "Type: Stun, CurrentKey: " + currentKey;
    }
}
