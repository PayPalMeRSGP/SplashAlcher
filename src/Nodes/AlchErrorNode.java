package Nodes;

import ScriptClasses.ConstantsAndStatics;
import org.osbot.rs07.api.Magic;
import org.osbot.rs07.api.Mouse;
import org.osbot.rs07.api.ui.Spells;
import org.osbot.rs07.script.MethodProvider;


import java.util.concurrent.ThreadLocalRandom;


//TODO: Implement
public class AlchErrorNode implements ExecutableNode, Comparable<ExecutableNode> {

    private final int BASE_STARTING_KEY = 51;
    private int currentKey = BASE_STARTING_KEY;

    private static AlchErrorNode alchErrorNodeSingleton;

    private AlchErrorNode(){}

    public static AlchErrorNode getAlchErrorNodeInstance() {
        if(alchErrorNodeSingleton == null){
            //ConstantsAndStatics.hostScriptReference.log("creating new alchErrorNodeSingleton");
            alchErrorNodeSingleton = new AlchErrorNode();
        }
        else{
            //ConstantsAndStatics.hostScriptReference.log("using old alchNodeSingleton");
        }
        return alchErrorNodeSingleton;
    }

    @Override
    public int executeNodeAction() throws InterruptedException {
        Magic m = ConstantsAndStatics.hostScriptReference.getMagic();
        int randomNum = ThreadLocalRandom.current().nextInt(0, 2);
        if(randomNum == 0){
            alchNothing();
        }
        else{
            misclickEarthBlast();
        }

        return (int) ConstantsAndStatics.randomNormalDist(400, 50);
    }

    private boolean misclickEarthBlast() throws InterruptedException {
        Mouse mouse = ConstantsAndStatics.hostScriptReference.getMouse();
        int randX = ThreadLocalRandom.current().nextInt(ConstantsAndStatics.EARTH_BLAST_UPPER_LEFT_BOUND.x, ConstantsAndStatics.EARTH_BLAST_LOWER_RIGHT_BOUND.x);
        int randY = ThreadLocalRandom.current().nextInt(ConstantsAndStatics.EARTH_BLAST_UPPER_LEFT_BOUND.y, ConstantsAndStatics.EARTH_BLAST_LOWER_RIGHT_BOUND.y);
        if(mouse.move(randX, randY)){
            MethodProvider.sleep(ConstantsAndStatics.randomNormalDist(30, 5)); //pause before clicking on earth blast
            if(mouse.click(randX, randY, false)){ //select
                if(ConstantsAndStatics.hostScriptReference.getMagic().isSpellSelected()){
                    MethodProvider.sleep(ConstantsAndStatics.randomNormalDist(400, 50)); //pause to emulate player realizing he has not clicked on alch
                    return mouse.click(randX, randY, false); //deselect
                }
            }
        }
        return false;
    }

    private boolean alchNothing() throws InterruptedException {
        Mouse mouse = ConstantsAndStatics.hostScriptReference.getMouse();
        Magic m = ConstantsAndStatics.hostScriptReference.getMagic();
        int randX = ThreadLocalRandom.current().nextInt(ConstantsAndStatics.ALCH_NOTHING_UPPER_LEFT_BOUNDS.x, ConstantsAndStatics.ALCH_NOTHING_LOWER_RIGHT_BOUNDS.x);
        int randY = ThreadLocalRandom.current().nextInt(ConstantsAndStatics.ALCH_NOTHING_UPPER_LEFT_BOUNDS.y, ConstantsAndStatics.ALCH_NOTHING_LOWER_RIGHT_BOUNDS.y);
        if(mouse.move(randX, randY)){
            MethodProvider.sleep(ConstantsAndStatics.randomNormalDist(30, 5));
            if(m.canCast(Spells.NormalSpells.HIGH_LEVEL_ALCHEMY)){
                m.castSpell(Spells.NormalSpells.HIGH_LEVEL_ALCHEMY);
                MethodProvider.sleep(ConstantsAndStatics.randomNormalDist(80, 8));
                mouse.click(randX, randY, false); //deselect
                MethodProvider.sleep(ConstantsAndStatics.randomNormalDist(1000, 200));
                return m.open(); //reopen magic tab
            }
        }
        return false;
    }

    @Override
    public void increaseKey() {
        this.currentKey++;
    }

    @Override
    public void attemptDecreaseKey() {
        int randNum = ThreadLocalRandom.current().nextInt(0, 10);
        if(randNum >= 7){
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
            else if(o instanceof StunNode){
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
