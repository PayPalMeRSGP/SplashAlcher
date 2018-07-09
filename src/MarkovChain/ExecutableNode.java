package MarkovChain;

import java.util.List;

public interface ExecutableNode {
    boolean canExecute() throws InterruptedException;

    int executeNode() throws InterruptedException;

    List<Edge> getAdjacentNodes();


    /*
    Markov chain classes are copied from other project.
    Below methods are not used but cannot be deleted as MarkovNodeExecutor uses them and I do not want to modify these classes.
     */
    boolean isJumping();                    //used by MarkovNodeExecutor to indicate whether a special node traversal is requested

    Class<? extends ExecutableNode> setJumpTarget();  //if isJumping() specify the class jump target

    void logNode();
}
