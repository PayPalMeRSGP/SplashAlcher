package ScriptClasses;

import Nodes.*;

import java.util.PriorityQueue;

public class PriorityQueueWrapper {
    private PriorityQueue<ExecutableNode> pq;

    PriorityQueueWrapper(){
        setUpPQForStunAlch();
    }

    private void swapKeysStunAlch(){
        AlchNode alchNodeSingleton = AlchNode.getAlchNodeInstance();
        SplashNode splashNodeSingleton = SplashNode.getStunNodeInstance();
        int alchNodeKey = alchNodeSingleton.getKey();
        int stunNodeKey = splashNodeSingleton.getKey();
        alchNodeSingleton.setKey(stunNodeKey);
        splashNodeSingleton.setKey(alchNodeKey);

        //pq doesn't update until the nodes with changed keys are removed and re-added
        this.pq.remove(alchNodeSingleton);
        this.pq.remove(splashNodeSingleton);
        this.pq.add(alchNodeSingleton);
        this.pq.add(splashNodeSingleton);
    }


    public int executeTopNode() throws InterruptedException{
        if(pq != null){
            ExecutableNode nextNode = pq.peek();
            swapKeysStunAlch(); //make the next action either a stun if the current action is a stun or vice versa
            MainScript hostScriptRef = (MainScript) PublicStaticFinalConstants.hostScriptReference;

            //if alching, increase the key of a stun error node because a stun is next, vice versa for stunning
            if(nextNode instanceof AlchNode){
                SplashErrorNode node = SplashErrorNode.getStunErrorNodeInstance();
                node.attemptDecreaseKey();
                pq.remove(node);
                pq.add(node);
            }
            else if(nextNode instanceof SplashNode){
                hostScriptRef.incrementSpellCycles();
                AlchErrorNode node = AlchErrorNode.getAlchErrorNodeInstance();
                node.attemptDecreaseKey();
                pq.remove(node);
                pq.add(node);
            }
            //upon error reset keys of nodes
            else if(nextNode instanceof AlchErrorNode || nextNode instanceof SplashErrorNode){
                resetPQ(); //set keys of all nodes back to default values to resume normal alch->stun->alch... operation
            }
            //debugPQ();
            return nextNode.executeNodeAction(); //returns the sleep time for onLoop()

        }
        return 0;
    }

    private void debugPQ(){
        PriorityQueue<ExecutableNode> pqCopy = new PriorityQueue<>(pq);
        while(!pqCopy.isEmpty()){
            ExecutableNode node = pqCopy.poll();
            PublicStaticFinalConstants.hostScriptReference.log(node.toString());
        }
        PublicStaticFinalConstants.hostScriptReference.log("--------------------------------------------------------");
    }

    private void resetPQ(){
        for(ExecutableNode node: pq){
            node.resetKey();
        }
        setUpPQForStunAlch();

    }

    private void setUpPQForStunAlch(){
        this.pq = new PriorityQueue<>();
        this.pq.add(AlchNode.getAlchNodeInstance());
        this.pq.add(SplashNode.getStunNodeInstance());
        this.pq.add(AlchErrorNode.getAlchErrorNodeInstance());
        this.pq.add(SplashErrorNode.getStunErrorNodeInstance());
    }

}
