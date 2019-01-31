package Neat.Brain;

public class ConnectionGene {

    private float weight;

    private int inNode;
    private int outNode;
    private int innovationNumber;

    private boolean expressed;

    public ConnectionGene(int inNode, int outNode, float weight, boolean expressed, int innovationNumber) {
        this.inNode = inNode;
        this.outNode = outNode;
        this.weight = weight;
        this.expressed = expressed;
        this.innovationNumber = innovationNumber;
    }
    
    public void disable() {
        expressed = false;
    }

    public float getWeight() {
        return weight;
    }
    public int inNode() {
        return inNode;
    }
    public int outNode() {
        return outNode;
    }
    public int innovationNumber() {
        return innovationNumber;
    }
    public boolean isExpressed() {
        return expressed;

    }
}
