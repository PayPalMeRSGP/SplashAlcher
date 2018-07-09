package MarkovChain;

import java.util.concurrent.ThreadLocalRandom;

public class Edge {
    private Class<? extends ExecutableNode> v;
    private int defaultExecutionWeight;

    public Edge(Class<? extends ExecutableNode> v, int defaultExecutionWeight) {
        this.v = v;
        this.defaultExecutionWeight = defaultExecutionWeight;
    }

    public Class<? extends ExecutableNode> getV() {
        return v;
    }

    public int getCurrentExecutionWeight() {
        return defaultExecutionWeight;
    }
}
