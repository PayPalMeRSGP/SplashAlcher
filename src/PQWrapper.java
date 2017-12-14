
import org.osbot.rs07.api.Magic;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.ui.Spells;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.script.Script;

import java.awt.*;
import java.util.PriorityQueue;
import java.util.concurrent.ThreadLocalRandom;

public class PQWrapper {
    private PriorityQueue<PriorityNode> pq;
    private Script hostScriptReference;

    private PriorityNode alchNode;
    private PriorityNode stunNode;

    private static final Point STUN_UPPER_LEFT_BOUND = new Point(613, 417);
    private static final Point STUN_LOWER_RIGHT_BOUND = new Point(626, 431);

    long start;
    long stop;

    private NPC npc;
    PQWrapper(Script hostScriptReference){
        this.hostScriptReference = hostScriptReference;
        pq = new PriorityQueue<>();
        this.alchNode = PriorityNode.getAlchNode();
        this.stunNode = PriorityNode.getStunNode();
        pq.add(alchNode);
        pq.add(stunNode);
        pq.add(PriorityNode.getAlchErrorNode());
        pq.add(PriorityNode.getStunErrorNode());
        npc =  hostScriptReference.getNpcs().closest("Monk of Zamorak");
    }

    public void swapKeysStunAlch(){
        int alchNodeKey = alchNode.p.getKey();
        int stunNodeKey =  stunNode.p.getKey();
        alchNode.p.setKey(stunNodeKey);
        stunNode.p.setKey(alchNodeKey);
        if(pq != null){
            pq.remove(alchNode);
            pq.remove(stunNode);
            pq.add(alchNode);
            pq.add(stunNode);
            hostScriptReference.log("peeking PQ: " + pq.peek().p.name());
        }
    }

    public void increaseKey(PriorityNode node) {
        node.p.attemptRankIncrease();
    }

    public void decreaseKey(PriorityNode node){
        node.p.attemptRankDecrease();
    }

    public int executeNodeAction() throws InterruptedException{
        Magic m = hostScriptReference.getMagic();
        PriorityNode nextNode = this.pq.peek();
        hostScriptReference.log("Executing: " + nextNode.p.name());
        switch(nextNode.p){
            case ALCH:
                if(m.canCast(Spells.NormalSpells.HIGH_LEVEL_ALCHEMY)){
                    m.castSpell(Spells.NormalSpells.HIGH_LEVEL_ALCHEMY);
                    MethodProvider.sleep(ConstantsAndStatics.randomNormalDist(ConstantsAndStatics.BETWEEN_ALCH_MEAN_MS, ConstantsAndStatics.BETWEEN_ALCH_STDDEV_MS));
                    if(m.isSpellSelected()){
                        hostScriptReference.getInventory().interact("Cast","Magic longbow");
                    }
                    MethodProvider.sleep(ConstantsAndStatics.randomNormalDist(ConstantsAndStatics.RS_GAME_TICK_MS, 80));
                    swapKeysStunAlch();
                    if(hoverOverStun()){ //originally was m.hoverSpell(Spells.NormalSpells.STUN)
                        //return (int) ConstantsAndStatics.randomNormalDist(50, 5);
                        return 0; //force onLoop to not sleep to better test
                    }
                }
                hostScriptReference.log("cannot cast alch");
                return 10;


            case STUN:

                if(m.canCast(Spells.NormalSpells.STUN)){
                    if(npc == null){
                        hostScriptReference.log("no NPC creating instance");
                        npc = hostScriptReference.getNpcs().closest("Monk of Zamorak");
                    }
                    stop = System.currentTimeMillis();
                    m.castSpellOnEntity(Spells.NormalSpells.STUN, npc);

                    swapKeysStunAlch();
                    if(m.hoverSpell(Spells.NormalSpells.HIGH_LEVEL_ALCHEMY)){
                        //return (int) ConstantsAndStatics.randomNormalDist(50, 5);
                        return 0; //force onLoop to not sleep to better test
                    }
                }
                hostScriptReference.log("cannot cast stun");
                return 10;
            case ALCH_ERROR: //for implementations of emulating human mistakes
                return 0;
            case STUN_ERROR:
                return 0;
        }
        return 1000;
    }

    private boolean hoverOverStun(){
        int randX = ThreadLocalRandom.current().nextInt(STUN_UPPER_LEFT_BOUND.x, STUN_LOWER_RIGHT_BOUND.x);
        int randY = ThreadLocalRandom.current().nextInt(STUN_UPPER_LEFT_BOUND.y, STUN_LOWER_RIGHT_BOUND.y);
        hostScriptReference.log("hovering stun at: (" + randX + ", " + randY + ")");
        return !hostScriptReference.getMouse().move(randX, randY);
    }
}
