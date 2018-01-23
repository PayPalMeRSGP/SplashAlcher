package ScriptClasses;

import Nodes.ExecutableNode;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;

public class NodesExecutor {
    private class NodeEdge {
        ExecutableNode u; //source node
        ExecutableNode v; //edge to some other node
        int edgeExecutionWeight;

        public NodeEdge(ExecutableNode u, ExecutableNode v, int edgeExecutionWeight) {
            this.u = u;
            this.v = v;
            this.edgeExecutionWeight = edgeExecutionWeight;
        }
    }

    private HashMap<ExecutableNode, LinkedList<NodeEdge>> adjMap;
    private ExecutableNode current;

    public NodesExecutor(ExecutableNode startingNode){
        adjMap = new HashMap<>();
        current = startingNode;
    }

    public void addEdgeToNode(ExecutableNode u, ExecutableNode v, int edgeExecutionWeight){
        if(adjMap.containsKey(u)){ //adjMap has u stored inside
            LinkedList<NodeEdge> edges = adjMap.get(u);
            if(edges == null){ //check if list of edges for u is instantiated, if not do so.
                edges = new LinkedList<>();

            }
            //add new edge
            edges.add(new NodeEdge(u, v, edgeExecutionWeight));
            adjMap.put(u, edges);
        }
        else{
            LinkedList<NodeEdge> edges = new LinkedList<>();
            edges.add(new NodeEdge(u, v, edgeExecutionWeight));
            adjMap.put(u, edges);
        }
    }

    public void deleteEdgeForNode(ExecutableNode u, ExecutableNode v){
        if(adjMap.containsKey(u)){
            LinkedList<NodeEdge> edges = adjMap.get(u);
            edges.forEach(edge -> {
               if(edge.v == v){
                   edges.remove(edge);
               }
            });
        }
    }

    public int executeNodeThenTraverse() throws InterruptedException {
        int onLoopSleepTime = current.executeNodeAction();
        ExecutableNode nextNode;
        return onLoopSleepTime;
    }

    private void traverseToNextNode(){
        if(current != null){
            LinkedList<NodeEdge> edges = adjMap.get(current);
            int combinedWeight = edges.stream().mapToInt(edge -> edge.edgeExecutionWeight).sum();
            int roll = ThreadLocalRandom.current().nextInt(1, combinedWeight + 1);

        }

    }

    public static void main(String[] args){

    }


}
