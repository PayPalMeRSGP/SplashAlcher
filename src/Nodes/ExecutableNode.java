package Nodes;

public interface ExecutableNode {
    int executeNodeAction() throws InterruptedException;
    void increaseKey();
    void attemptDecreaseKey();
    void resetKey();
    void setKey(int key);
    int getKey();
    String getStatus();
    String toString();

}
