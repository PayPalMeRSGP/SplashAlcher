package Nodes.StunAlch;

import Nodes.ExecutableNode;
import ScriptClasses.PublicStaticFinalConstants;
import org.osbot.rs07.api.Magic;
import org.osbot.rs07.api.Mouse;
import org.osbot.rs07.api.ui.Spells;
import org.osbot.rs07.script.MethodProvider;


import java.util.concurrent.ThreadLocalRandom;


public class AlchErrorNode implements ExecutableNode, Comparable<ExecutableNode> {

    private final int BASE_STARTING_KEY = 27; //odd numbers only, I subtract 2 at a time so key will not every be 0 causing a tie with an alch or stun node.
    private int currentKey = BASE_STARTING_KEY;

    private static AlchErrorNode alchErrorNodeSingleton;

    private AlchErrorNode(){}

    public static AlchErrorNode getAlchErrorNodeInstance() {
        if(alchErrorNodeSingleton == null){
            alchErrorNodeSingleton = new AlchErrorNode();
        }
        return alchErrorNodeSingleton;
    }

    @Override
    public int executeNodeAction() throws InterruptedException {
        Magic m = PublicStaticFinalConstants.hostScriptReference.getMagic();
        int randomNum = ThreadLocalRandom.current().nextInt(0, 2);

        if(randomNum == 0){
            PublicStaticFinalConstants.hostScriptReference.log("committing alchNothing artificial error");
            alchNothing();
        }
        else{
            PublicStaticFinalConstants.hostScriptReference.log("committing misclickEarthBlast artificial error");
            misclickEarthBlast();
        }

        return (int) PublicStaticFinalConstants.randomNormalDist(400, 50);
    }

    private void misclickEarthBlast() throws InterruptedException {
        Mouse mouse = PublicStaticFinalConstants.hostScriptReference.getMouse();
        int randX = ThreadLocalRandom.current().nextInt(PublicStaticFinalConstants.EARTH_BLAST_UPPER_LEFT_BOUND.x, PublicStaticFinalConstants.EARTH_BLAST_LOWER_RIGHT_BOUND.x);
        int randY = ThreadLocalRandom.current().nextInt(PublicStaticFinalConstants.EARTH_BLAST_UPPER_LEFT_BOUND.y, PublicStaticFinalConstants.EARTH_BLAST_LOWER_RIGHT_BOUND.y);
        if(mouse.move(randX, randY)){
            MethodProvider.sleep(PublicStaticFinalConstants.randomNormalDist(30, 5)); //pause before clicking on earth blast
            if(mouse.click(randX, randY, false)){ //select
                if(PublicStaticFinalConstants.hostScriptReference.getMagic().isSpellSelected()){
                    MethodProvider.sleep(PublicStaticFinalConstants.randomNormalDist(400, 50)); //pause to emulate player realizing he has not clicked on alch
                    mouse.click(randX, randY, false); //deselect
                }
            }
        }

    }

    private void alchNothing() throws InterruptedException {
        Mouse mouse = PublicStaticFinalConstants.hostScriptReference.getMouse();
        Magic m = PublicStaticFinalConstants.hostScriptReference.getMagic();
        int randX = ThreadLocalRandom.current().nextInt(PublicStaticFinalConstants.ALCH_NOTHING_UPPER_LEFT_BOUNDS.x, PublicStaticFinalConstants.ALCH_NOTHING_LOWER_RIGHT_BOUNDS.x);
        int randY = ThreadLocalRandom.current().nextInt(PublicStaticFinalConstants.ALCH_NOTHING_UPPER_LEFT_BOUNDS.y, PublicStaticFinalConstants.ALCH_NOTHING_LOWER_RIGHT_BOUNDS.y);
        if(mouse.move(randX, randY)){
            MethodProvider.sleep(PublicStaticFinalConstants.randomNormalDist(30, 5));
            if(m.canCast(Spells.NormalSpells.HIGH_LEVEL_ALCHEMY)){
                m.castSpell(Spells.NormalSpells.HIGH_LEVEL_ALCHEMY);
                MethodProvider.sleep(PublicStaticFinalConstants.randomNormalDist(80, 8));
                mouse.click(randX, randY, false); //deselect
                MethodProvider.sleep(PublicStaticFinalConstants.randomNormalDist(1000, 200)); //pause to emulate player realizing that he just alched nothing
                m.open(); //reopen magic tab
            }
        }
    }

    @Override
    public void increaseKey() {
        this.currentKey++;
    }

    @Override
    public void attemptDecreaseKey() {
        int randNum = ThreadLocalRandom.current().nextInt(0, 10);
        if(randNum >= 7){ //70% change to decrement
            this.currentKey-=2;
        }

    }

    @Override
    public void resetKey() {
        this.currentKey = BASE_STARTING_KEY;
    }

    @Override
    public void setKey(int key) {

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
                return -1;
            }
            else if(o instanceof SplashNode){
                return 1;
            }
        }
        return diff;
        /*if tie, give priority to the error node
          because if a tie exists between an alch error or alch node. We do alch error first then an successful alch to
          emulate a player making a mistake then correcting it.
         */
    }

    @Override
    public String toString(){
        return "Type: AlchError, CurrentKey: " + currentKey;
    }
}
