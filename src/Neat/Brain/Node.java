package Neat.Brain;

import processing.core.PVector;

public class Node {

    enum Type {
        INPUT, HIDDEN, OUTPUT;
    }

    private Type type;

    private int id;
    
    public PVector _drawingPosition;
    public PVector _drawingVelocity;

    public Node(Type type, int id) {
        this.type = type;
        this.id = id;
        
        _drawingPosition = new PVector(0, 0);
        _drawingVelocity = new PVector(0, 0);
    }

    public Type getType() {
        return type;
    }

    public int getId() {
        return id;
    }
}
