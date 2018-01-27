package Nodes;

public class TestNode implements ExecutableNode {

    String testString;
    int testNodeNum;
    static int numInstances = 0;
    public TestNode(){
        this.testNodeNum = ++numInstances;
        testString = "Node " + testNodeNum;
    }

    @Override
    public int executeNodeAction() throws InterruptedException {
        System.out.println("nodeNum: " + testNodeNum);
        return 0;
    }

    @Override
    public String getStatus() {
        return null;
    }
}
