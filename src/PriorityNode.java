import org.osbot.rs07.api.Magic;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.ui.Spells;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.script.Script;

import java.util.Comparator;

public class PriorityNode implements Comparable<PriorityNode> {

    enum Priority {
        ALCH(100), STUN(99), ALCH_ERROR(1), STUN_ERROR(1);

        private int key;
        Priority(int n){
            this.key = n;
        }

        public int getKey() {
            return key;
        }

        public void attemptRankIncrease(){
            ++key;
        }

        public void attemptRankDecrease(){
            --key;
        }

        public void setKey(int key){
            this.key = key;
        }
    }

    Priority p;

    public static PriorityNode getAlchNode(){
        return new PriorityNode(Priority.ALCH);
    }

    public static PriorityNode getStunNode(){
        return new PriorityNode(Priority.STUN);
    }

    public static PriorityNode getAlchErrorNode(){
        return new PriorityNode(Priority.ALCH_ERROR);
    }

    public static PriorityNode getStunErrorNode(){
        return new PriorityNode(Priority.STUN_ERROR);
    }

    private PriorityNode(Priority p){
        this.p = p;
    }

    @Override
    public int compareTo(PriorityNode o) {
        return  o.p.getKey() - this.p.getKey();
    }




}
