package ScriptClasses;

import Nodes.*;

import java.util.PriorityQueue;

public class PriorityQueueWrapper {
    private PriorityQueue<ExecutableNode> pq;

    public PriorityQueueWrapper(){
        setUpPQ();
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
            ExecutableNode nextNode = pq.peek();
            ConstantsAndStatics.hostScriptReference.log(nextNode);

            swapKeysStunAlch();
            if(nextNode instanceof AlchNode){
                //StunErrorNode node = StunErrorNode.getInstance();
            }
            else if(nextNode instanceof StunNode){
                AlchErrorNode node = AlchErrorNode.getAlchErrorNodeInstance();
                node.attemptDecreaseKey();
                pq.remove(node);
                pq.add(node);
            }
            else if(nextNode instanceof AlchErrorNode || nextNode instanceof StunErrorNode){
                resetPQ();
            }
            int onLoopSleepTime = nextNode.executeNodeAction();
            debugPQ();
            return onLoopSleepTime;
        }
        return 0;
    }

    private void debugPQ(){
        PriorityQueue<ExecutableNode> pqCopy = new PriorityQueue<>(pq);
        while(!pqCopy.isEmpty()){
            ExecutableNode node = pqCopy.poll();
            ConstantsAndStatics.hostScriptReference.log(node.toString());
        }
        ConstantsAndStatics.hostScriptReference.log("--------------------------------------------------------");
    }

    private void resetPQ(){
        for(ExecutableNode node: pq){
            node.resetKey();
        }
        setUpPQ();

    }

    private void setUpPQ(){
        this.pq = new PriorityQueue<>();
        this.pq.add(AlchNode.getAlchNodeInstance());
        this.pq.add(StunNode.getStunNodeInstance());
        this.pq.add(AlchErrorNode.getAlchErrorNodeInstance());
        //this.pq.add(StunErrorNode.getStunErrorNodeInstance());
    }

}
