package Nodes.Splashing;

import GUI.SwingGUI;
import MarkovChain.Edge;
import MarkovChain.ExecutableNode;
import Nodes.Alching.AlchErrorNode;
import Nodes.Alching.AlchNode;
import ScriptClasses.Statics;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.api.ui.Spells;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.utility.ConditionalSleep;

import java.util.Arrays;
import java.util.List;


public abstract class AbstractSplashNode implements ExecutableNode {
    Script script;
    static final int ALCH_ANIMATION = 713;
    private Spells.NormalSpells splashingSpell;
    private SwingGUI.SplashingSpellTypes splashingSpellType;
    private int targetNPCID; //target npc and alch item ids are calculated at script start.
    NPC targetNPC;
    private boolean splashOnly;

    private static final List<Edge> SPLASH_ALCH_EDGES = Arrays.asList(new Edge(AlchNode.class, 98), new Edge(AlchErrorNode.class, 2));
    private static final List<Edge> SPLASH_ONLY_EDGES = Arrays.asList(new Edge(RegularSplash.class, 25), new Edge(HoverSplashingNPC.class, 75));

    AbstractSplashNode(SwingGUI.SplashingSpellTypes splashingSpellType, int targetNPCID, boolean splashOnly, Script script){
        this.splashingSpellType = splashingSpellType;
        this.script = script;
        this.targetNPCID = targetNPCID;
        this.splashOnly = splashOnly;
        handleProgressiveSpellSwitch();
    }

    @Override
    public boolean canExecute() {
        boolean canExecute = new ConditionalSleep(2000) {
                @Override
                public boolean condition() {
                    targetNPC = script.getNpcs().closest(targetNPCID);
                    return targetNPC != null;
                }
        }.sleep();
        if(!canExecute){
            script.log("Stopping reason: ran out splash spell casts, or the target NPC was not found");
            script.stop(false);
        }
        return canExecute;
    }

    @Override
    public int executeNode() throws InterruptedException {
        setScriptStatusWrapper();
        handleProgressiveSpellSwitch();
        //after alching, need to wait for tab to auto flip back to magic tab.
        new ConditionalSleep(2000){
            @Override
            public boolean condition() throws InterruptedException {
                return script.getTabs().getOpen() == Tab.MAGIC;
            }
        }.sleep();
        script.getMagic().castSpell(splashingSpell);
        postHoverSpellAction();
        if(script.getMagic().castSpellOnEntity(splashingSpell, targetNPC)){
            /*
            Successor nodes handle sleeping.
            Wanted splashNode/alchNode to handle hovering over respective spell.
            IMO alchNode should not handle hovering over splashing spell, vice versa for splashNode.
            Hovering should happen immediately after successful alch/splash. Therefore wait before next node execute should be 0.
            */
            return 0;


        }
        return (int) Statics.randomNormalDist(1000, 300);
    }

    abstract void setScriptStatusWrapper();

    abstract void postHoverSpellAction() throws InterruptedException;

    @Override
    public List<Edge> getAdjacentNodes() {
        return splashOnly ? SPLASH_ONLY_EDGES : SPLASH_ALCH_EDGES;
    }

    private void handleProgressiveSpellSwitch() { //19, 11, 3, 80, 73, 66
        int mageLvl = script.getSkills().getStatic(Skill.MAGIC);
        if (splashingSpellType == SwingGUI.SplashingSpellTypes.BODY_RUNE_SPELLS) {
            if(mageLvl >= 19)
                splashingSpell = Spells.NormalSpells.CURSE;
            else if(mageLvl >= 11)
                splashingSpell = Spells.NormalSpells.WEAKEN;
            else if(mageLvl >= 3)
                splashingSpell = Spells.NormalSpells.CONFUSE;

        }
        else{
            if(mageLvl >= 80)
                splashingSpell = Spells.NormalSpells.STUN;
            else if(mageLvl >= 73)
                splashingSpell = Spells.NormalSpells.ENFEEBLE;
            else if(mageLvl >= 66)
                splashingSpell = Spells.NormalSpells.VULNERABILITY;
        }
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
