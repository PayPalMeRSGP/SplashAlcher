package ScriptClasses;

import Nodes.*;
import Nodes.SpinFlax.BankBowStringNode;
import Nodes.SpinFlax.SpinFlaxNode;
import Nodes.StunAlch.*;

import java.util.PriorityQueue;

public class PriorityQueueWrapper {
    private PriorityQueue<ExecutableNode> pq;

    PriorityQueueWrapper(){
        setUpPQForStunAlch();
    }

    private void swapKeysStunAlch(){
        AlchNode alchNodeSingleton = AlchNode.getAlchNodeInstance();
        StunNode stunNodeSingleton = StunNode.getStunNodeInstance();
        int alchNodeKey = alchNodeSingleton.getKey();
        int stunNodeKey = stunNodeSingleton.getKey();
        alchNodeSingleton.setKey(stunNodeKey);
        stunNodeSingleton.setKey(alchNodeKey);

        //pq doesn't update until the nodes with changed keys are removed and re-added
        this.pq.remove(alchNodeSingleton);
        this.pq.remove(stunNodeSingleton);
        this.pq.add(alchNodeSingleton);
        this.pq.add(stunNodeSingleton);
    }

    private void swapKeys(ExecutableNode node1, ExecutableNode node2){

    }

    public int executeTopNode() throws InterruptedException{
        if(pq != null){
            ExecutableNode nextNode = pq.peek();
            swapKeysStunAlch(); //make the next action either a stun if the current action is a stun or vice versa
            StunAlchScriptEntryPoint hostScriptRef = (StunAlchScriptEntryPoint) ConstantsAndStatics.hostScriptReference;

            //if alching, increase the key of a stun error node because a stun is next, vice versa for stuning
            if(nextNode instanceof AlchNode){
                StunErrorNode node = StunErrorNode.getStunErrorNodeInstance();
                node.attemptDecreaseKey();
                pq.remove(node);
                pq.add(node);
            }
            else if(nextNode instanceof StunNode){
                hostScriptRef.incrementSpellCycles();
                AlchErrorNode node = AlchErrorNode.getAlchErrorNodeInstance();
                node.attemptDecreaseKey();
                pq.remove(node);
                pq.add(node);
            }
            else if(nextNode instanceof AlchErrorNode || nextNode instanceof StunErrorNode){
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
            ConstantsAndStatics.hostScriptReference.log(node.toString());
        }
        ConstantsAndStatics.hostScriptReference.log("--------------------------------------------------------");
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
        this.pq.add(StunNode.getStunNodeInstance());
        this.pq.add(AlchErrorNode.getAlchErrorNodeInstance());
        this.pq.add(StunErrorNode.getStunErrorNodeInstance());
    }

    private void setUpPQForSpinFlax(){
        this.pq = new PriorityQueue<>();
        this.pq.add(BankBowStringNode.getBankBowStringNodeInstance());
        this.pq.add(SpinFlaxNode.getSpinFlaxNodeInstnace());
    }

}
