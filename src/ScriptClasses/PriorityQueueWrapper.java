package ScriptClasses;

import Nodes.*;

import java.util.PriorityQueue;

public class PriorityQueueWrapper {
    private PriorityQueue<ExecutableNode> pq;

    public PriorityQueueWrapper(){
        this.pq = new PriorityQueue<>();
        this.pq.add(AlchNode.getAlchNodeInstance());
        this.pq.add(StunNode.getStunNodeInstance());
        //this.pq.add(AlchErrorNode.getAlchErrorNodeInstance());
        //this.pq.add(StunErrorNode.getStunErrorNodeInstance());
    }

    private void swapKeysStunAlch(){
        AlchNode alchNodeSingleton = AlchNode.getAlchNodeInstance();
        StunNode stunNodeSingleton = StunNode.getStunNodeInstance();
        int alchNodeKey = alchNodeSingleton.getKey();
        int stunNodeKey = stunNodeSingleton.getKey();
        alchNodeSingleton.setKey(stunNodeKey);
        stunNodeSingleton.setKey(alchNodeKey);

        this.pq.remove(alchNodeSingleton);
        this.pq.remove(stunNodeSingleton);
        this.pq.add(alchNodeSingleton);
        this.pq.add(stunNodeSingleton);
    }

    public int executeTopNode() throws InterruptedException{
        if(pq != null){
            ExecutableNode nextNode = pq.poll();
            ConstantsAndStatics.hostScriptReference.log(nextNode);
            int onLoopSleepTime = nextNode.executeNodeAction();
            swapKeysStunAlch();
            return onLoopSleepTime;
        }
        return 0;
    }

}
