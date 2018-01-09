package Nodes;

import ScriptClasses.PublicStaticFinalConstants;
import org.osbot.rs07.api.Magic;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.ui.Spells;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.script.MethodProvider;


public class SplashNode implements ExecutableNode, Comparable<ExecutableNode>{

    private final int BASE_STARTING_KEY = 1;
    private int currentKey = BASE_STARTING_KEY;
    private static SplashNode splashNodeSingleton = null;

    private SplashNode(){

    }

    public static SplashNode getStunNodeInstance() {
        if(splashNodeSingleton == null){
            //PublicStaticFinalConstants.hostScriptReference.log("creating new splashNodeSingleton");
            splashNodeSingleton = new SplashNode();
        }

        return splashNodeSingleton;
    }

    @Override
    public int executeNodeAction() throws InterruptedException {
        Magic m = PublicStaticFinalConstants.hostScriptReference.getMagic();
        NPC npc = PublicStaticFinalConstants.hostScriptReference.getNpcs().closest(PublicStaticFinalConstants.targetNPC);
        if(PublicStaticFinalConstants.canCast()){
            waitForMagicTab();
            if(!m.castSpellOnEntity(PublicStaticFinalConstants.splashingSpell, npc)){
                PublicStaticFinalConstants.hostScriptReference.stop();
            }

            if(AlchErrorNode.getAlchErrorNodeInstance().getKey() <= 0){ //alch error is next, let next node handle moving mouse
                return (int) PublicStaticFinalConstants.randomNormalDist(50, 5);
            }
            if(m.hoverSpell(Spells.NormalSpells.HIGH_LEVEL_ALCHEMY)){ //set up mouse on alch
                return (int) PublicStaticFinalConstants.randomNormalDist(50, 5);
            }
        }
        else{
            PublicStaticFinalConstants.hostScriptReference.stop();
        }
        return 0;

    }

    private void waitForMagicTab() throws InterruptedException {
        for(int i = 0; i < 5; i++){ //wait until magic tab reopens from alching
            if(!(PublicStaticFinalConstants.hostScriptReference.getTabs().getOpen() == Tab.MAGIC)){
                MethodProvider.sleep(PublicStaticFinalConstants.RS_GAME_TICK_MS); //rs game tick ms
            }
            else{
                break;
            }
        }
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
