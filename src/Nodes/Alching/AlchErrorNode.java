package Nodes.Alching;

import MarkovChain.Edge;
import MarkovChain.ExecutableNode;
import Nodes.Splashing.AbstractSplashNode;
import Nodes.Splashing.HoverSplashingNPC;
import Nodes.Splashing.RegularSplash;
import ScriptClasses.MainScript;
import ScriptClasses.Statics;
import org.osbot.rs07.api.ui.Spells;
import org.osbot.rs07.input.mouse.RectangleDestination;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.script.Script;


import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class AlchErrorNode implements ExecutableNode {

    private final static String NODE_STATUS = "Artificial Alching Error";
    private Script script;
    private static final List<Edge> EDGES = Arrays.asList(new Edge(RegularSplash.class, 1), new Edge(HoverSplashingNPC.class, 1), new Edge(AlchNode.class, 1));
    private RectangleDestination deadAlch; //area where hitbox of item to alch is not directly under the hitbox for the high alch spell.

    public AlchErrorNode(Script script){
        this.script = script;
        deadAlch = new RectangleDestination(script.bot, new Rectangle(721, 319, 4, 18));
    }

    @Override
    public boolean canExecute() {
        return true;
    }

    @Override
    public int executeNode() throws InterruptedException {
        if(script instanceof MainScript){
            ((MainScript) script).setScriptStatus(NODE_STATUS);
        }
        if(ThreadLocalRandom.current().nextBoolean())
            alchNothing();
        else
            misclickEarthBlast();
        return (int) Statics.randomNormalDist(1000, 500);
    }

    private void misclickEarthBlast() throws InterruptedException {
        if(script.getMagic().castSpell(Spells.NormalSpells.EARTH_BLAST)){
            if(script.getMagic().isSpellSelected()){
                MethodProvider.sleep(Statics.randomNormalDist(1500, 500));
                script.getMagic().deselectSpell();
            }
        }
    }

    private void alchNothing() {
        if(script.getMouse().click(deadAlch)){
            if(script.getMagic().isSpellSelected()){
                script.getMouse().click(false);
            }
        }
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
