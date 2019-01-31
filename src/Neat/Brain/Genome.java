package Neat.Brain;

import java.util.ArrayList;
import Neat.Neat;
import processing.core.PVector;

public class Genome {

    private Neat main;

    private final ArrayList<ConnectionGene> genes;
    private final ArrayList<Node> nodes;

    private final ArrayList<String> infos;

    private final int inputNodes = 8;
    private final int outputNodes = 4;

    private final float ratioInOutSize = 8;                             //ratio between position in inputs and outputs in drawing

    private float millisMoveFDG, oldMillisMoveFDG;

    public Genome(Neat main) {
        this.main = main;

        genes = new ArrayList<>();
        nodes = new ArrayList<>();
        infos = new ArrayList<>();

        for (int i = 1; i <= inputNodes; i++) {
            nodes.add(new Node(Node.Type.INPUT, nodes.size()));

            PVector pos = nodes.get(i - 1)._drawingPosition;
            pos.x = 1 / ratioInOutSize / 2f;
            pos.y = (float) i / (inputNodes + 1);
        }
        for (int i = 1; i <= outputNodes; i++) {
            nodes.add(new Node(Node.Type.OUTPUT, nodes.size()));

            PVector pos = nodes.get(nodes.size() - 1)._drawingPosition;
            pos.x = 1 / ratioInOutSize * (ratioInOutSize - 1) + 1 / ratioInOutSize / 2f;
            pos.y = (float) i / (outputNodes + 1);
        }
    }

    public void addConnectionMutation() {
        Node rndIn = nodes.get(Math.round(main.random(0, nodes.size() - 1)));
        Node rndOut;

        do {
            rndOut = nodes.get(Math.round(main.random(0, nodes.size() - 1)));
        } while (rndIn.getId() == rndOut.getId());

        for (ConnectionGene c : genes) {
            if (c.inNode() == rndIn.getId() && c.outNode() == rndOut.getId()) {
                return;
            }
        }

        if (rndIn.getType() == rndOut.getType()) {
            addConnectionMutation();
            return;
        }

        if (rndIn.getType() == Node.Type.HIDDEN && rndOut.getType() == Node.Type.INPUT
                || //If the order is reversed (H->I, O->H or O->I), I reverse the nodes
                rndIn.getType() == Node.Type.OUTPUT && rndOut.getType() == Node.Type.HIDDEN
                || rndIn.getType() == Node.Type.OUTPUT && rndOut.getType() == Node.Type.INPUT) {
            int tmp = rndIn.getId();
            rndIn = nodes.get(rndOut.getId());
            rndOut = nodes.get(tmp);
        }

        genes.add(new ConnectionGene(rndIn.getId(), rndOut.getId(), main.random(-1, 1), true, 0));             //DA METTERE INNOVATION NUMBER
    }

    public void addNodeMutation() {
        if (!genes.isEmpty()) {
            ConnectionGene rndConnection = genes.get((int) main.random(genes.size()));

            rndConnection.disable();

            int inNode = rndConnection.inNode();
            int outNode = rndConnection.outNode();

            Node newNode = new Node(Node.Type.HIDDEN, nodes.size());
            newNode._drawingPosition = PVector.add(nodes.get(inNode)._drawingPosition, PVector.mult(PVector.sub(nodes.get(outNode)._drawingPosition, nodes.get(inNode)._drawingPosition), 0.5f));

            nodes.add(newNode);

            ConnectionGene connectionInToNew = new ConnectionGene(rndConnection.inNode(), newNode.getId(), 1f, true, 0);
            ConnectionGene connectionNewToOut = new ConnectionGene(newNode.getId(), rndConnection.outNode(), rndConnection.getWeight(), true, 0);

            genes.add(connectionInToNew);
            genes.add(connectionNewToOut);
        }
    }

    public void DrawNetworkWithForceDirectedGraph(float side) {
        main.fill(0);
        main.rect(0, 0, side, side);

        float sideInputs = side / ratioInOutSize;
        float sideOutputs = side / ratioInOutSize * (ratioInOutSize - 1);

        main.stroke(255);
        main.strokeWeight(3f);

        main.line(sideInputs, 0, sideInputs, side);
        main.line(sideOutputs, 0, sideOutputs, side);

        PVector pos; //temp PVector

        main.stroke(255);
        main.strokeWeight(1);
        float _xIn, _yIn, _xOut, _yOut;
        for (ConnectionGene g : genes) {
            if (g.isExpressed()) {
                _xIn = nodes.get(g.inNode())._drawingPosition.x * side;
                _yIn = nodes.get(g.inNode())._drawingPosition.y * side;
                _xOut = nodes.get(g.outNode())._drawingPosition.x * side;
                _yOut = nodes.get(g.outNode())._drawingPosition.y * side;

                main.line(_xIn, _yIn, _xOut, _yOut);
            }
        }

        main.noStroke();
        main.textAlign(main.CENTER, main.CENTER);
        main.textSize(9);
        
        /////////////Draw the inputs node and the inputs id////////////////////
        main.fill(255, 255, 0);
        for (int i = 1; i <= inputNodes; i++) {
            pos = nodes.get(i - 1)._drawingPosition;

            main.ellipse(pos.x * side, pos.y * side, sideInputs / 2, sideInputs / 2);
        }

        main.fill(0);
        for (int i = 1; i <= inputNodes; i++) {
            pos = nodes.get(i - 1)._drawingPosition;

            main.text(i - 1, pos.x * side, pos.y * side - 2);
        }
        /////////////////////////////////////////////////////////////////////

        /////////////Draw the outputs node and the outputs id////////////////////
        main.fill(0, 0, 255);
        for (int i = 1; i <= outputNodes; i++) {
            pos = nodes.get(inputNodes + i - 1)._drawingPosition;

            main.ellipse(pos.x * side, pos.y * side, sideInputs / 2 * 0.9f, sideInputs / 2 * 0.9f);
        }
        main.fill(255);
        for (int i = 1; i <= outputNodes; i++) {
            pos = nodes.get(inputNodes + i - 1)._drawingPosition;

            main.text(inputNodes + i - 1, pos.x * side, pos.y * side - 2);
        }
        /////////////////////////////////////////////////////////////////////

        millisMoveFDG = main.millis();
        for (ConnectionGene g : genes) {
            if (nodes.get(g.inNode()).getType() == Node.Type.HIDDEN || nodes.get(g.outNode()).getType() == Node.Type.HIDDEN) {
                int in = g.inNode();
                int out = g.outNode();

                PVector distance = PVector.sub(nodes.get(out)._drawingPosition, nodes.get(in)._drawingPosition);

                float distance0 = 0.01f;
                float delta = distance0 - distance.mag();
                float constantSpring = 15;

                PVector force = distance.mult(delta).mult(-constantSpring);

                if (nodes.get(in).getType() == Node.Type.HIDDEN) {
                    nodes.get(in)._drawingVelocity.sub(PVector.mult(force, (oldMillisMoveFDG - millisMoveFDG) / 5000f));
                    nodes.get(in)._drawingVelocity.mult(0.8f);
                    nodes.get(in)._drawingPosition.add(nodes.get(in)._drawingVelocity);
                }
                if (nodes.get(out).getType() == Node.Type.HIDDEN) {
                    nodes.get(out)._drawingVelocity.add(PVector.mult(force, (oldMillisMoveFDG - millisMoveFDG) / 5000f));
                    nodes.get(out)._drawingVelocity.mult(0.8f);
                    nodes.get(out)._drawingPosition.add(nodes.get(out)._drawingVelocity);
                }
            }
        }

        for (int i = 0; i < nodes.size(); i++) {
            for (int j = i + 1; j < nodes.size(); j++) {
                if (i != j) {
                    PVector distance = PVector.sub(nodes.get(j)._drawingPosition, nodes.get(i)._drawingPosition);

                    float constantElectric = 0.05f;

                    PVector force = distance.mult(1f / distance.magSq()).mult(-constantElectric);
                    if (nodes.get(i).getType() == Node.Type.HIDDEN) {
                        nodes.get(i)._drawingVelocity.sub(PVector.mult(force, (oldMillisMoveFDG - millisMoveFDG) / 5000f));
                        nodes.get(i)._drawingVelocity.mult(0.8f);
                        nodes.get(i)._drawingPosition.add(nodes.get(i)._drawingVelocity);
                    }
                    if (nodes.get(j).getType() == Node.Type.HIDDEN) {
                        nodes.get(j)._drawingVelocity.add(PVector.mult(force, (oldMillisMoveFDG - millisMoveFDG) / 5000f));
                        nodes.get(j)._drawingVelocity.mult(0.8f);
                        nodes.get(j)._drawingPosition.add(nodes.get(j)._drawingVelocity);
                    }
                }
            }
        }
        oldMillisMoveFDG = millisMoveFDG;

        /////////////Draw the hidden node and the hidden id////////////////////
        main.fill(0, 255, 0);
        for (int i = 1; i <= (nodes.size() - inputNodes - outputNodes); i++) {
            pos = nodes.get(inputNodes + outputNodes - 1 + i)._drawingPosition;

            main.ellipse(pos.x * side, pos.y * side, sideInputs / 2 * 0.9f, sideInputs / 2 * 0.9f);
        }
        main.fill(255);
        for (int i = 1; i <= (nodes.size() - inputNodes - outputNodes); i++) {
            pos = nodes.get(inputNodes + outputNodes - 1 + i)._drawingPosition;

            main.text(inputNodes + outputNodes + i - 1, pos.x * side, pos.y * side - 2);
        }
        /////////////////////////////////////////////////////////////////////
    }

    public ArrayList<String> getInfos() {
        infos.clear();

        int inputs = 0, outputs = 0, hiddens = 0;
        for (Node n : nodes) {
            if (n.getType() == Node.Type.INPUT) {
                inputs++;
            }
            if (n.getType() == Node.Type.HIDDEN) {
                hiddens++;
            }
            if (n.getType() == Node.Type.OUTPUT) {
                outputs++;
            }
        }

        infos.add("Inputs: " + inputs);
        infos.add("Hidden: " + hiddens);
        infos.add("Output: " + outputs);
        infos.add("Genes: " + genes.size());

        return infos;
    }
}
