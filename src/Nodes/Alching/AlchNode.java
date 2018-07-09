package Nodes.Alching;

import MarkovChain.Edge;
import MarkovChain.ExecutableNode;
import Nodes.Splashing.AbstractSplashNode;
import Nodes.Splashing.HoverSplashingNPC;
import Nodes.Splashing.RegularSplash;
import ScriptClasses.MainScript;
import ScriptClasses.Statics;
import org.osbot.rs07.api.Magic;
import org.osbot.rs07.api.ui.Spells;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.utility.ConditionalSleep;

import java.util.Arrays;
import java.util.List;


public class AlchNode implements ExecutableNode {

    private final static String NODE_STATUS = "Alching";
    private static final List<Edge> EDGES = Arrays.asList(new Edge(RegularSplash.class, 25), new Edge(HoverSplashingNPC.class, 75));
    private int alchingItemID; //target npc and alch item ids are calculated at script start.
    private Script script;

    public AlchNode(int alchingItemID, Script script){
        this.alchingItemID = alchingItemID;
        this.script = script;
    }

    @Override
    public boolean canExecute() {
        boolean canExecute = new ConditionalSleep(2000) {
            @Override
            public boolean condition() throws InterruptedException {
                return script.getMagic().canCast(Spells.NormalSpells.HIGH_LEVEL_ALCHEMY);
            }
        }.sleep();
        if(!canExecute){
            script.log("Stopping reason: ran out high alch spell casts");
            script.stop(false);
        }
        return canExecute;
    }

    @Override
    public int executeNode() throws InterruptedException {
        if(script instanceof MainScript){
            ((MainScript) script).setScriptStatus(NODE_STATUS);
        }
        Magic magic = script.getMagic();
        magic.hoverSpell(Spells.NormalSpells.HIGH_LEVEL_ALCHEMY);
        if(script.myPlayer().isAnimating())
            MethodProvider.sleep(Statics.randomNormalDist(300, 100));
        if(magic.castSpell(Spells.NormalSpells.HIGH_LEVEL_ALCHEMY)){
            if(magic.isSpellSelected() && magic.getSelectedSpellName().equals("High Level Alchemy")){
                if(script.getInventory().contains(alchingItemID)){
                    if(script.getInventory().interact("Cast", alchingItemID)){
                        /*
                        Successor nodes handle sleeping.
                        Wanted splashNode/alchNode to handle hovering over respective spell.
                        IMO alchNode should not handle hovering over splashing spell, vice versa for splashNode.
                        Hovering should happen immediately after successful alch/splash. Therefore wait before next node execute should be 0.
                        */
                        return 0;
                    }
                } else {
                    script.log("Stopping reason: ran out high alch items");
                    script.stop(true);
                }
            }
        }
        return (int) Statics.randomNormalDist(1000, 300);
    }

    @Override
    public List<Edge> getAdjacentNodes() {
        return EDGES;
    }

    @Override
    public boolean isJumping() {
        return false;
    }

    @Override
    public Class<? extends ExecutableNode> setJumpTarget() {
        return null;
    }

    @Override
    public void logNode() {

    }
}
