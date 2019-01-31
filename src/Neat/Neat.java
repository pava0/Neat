package Neat;

import processing.core.PApplet;
import java.util.*;
import processing.core.PVector;

public class Neat extends PApplet {

    final int nOfSquareInInstance = 10;                                              //number of square in single instance
    final int nOfCreaturePerSide = 10;                                               //number of creature per side
    final int windowsWidth = 1300;                                                   //width of window
    final int windowsHeight = 900;
    final int leftSide = 20;
    final int upSide = 20;
    final int rightSide = 250;
    final int downSide = 20;
    final int borderFromNetwork = 20;
    int sideDrawNetwork;
    int windowPlay;
    int menuWidth;

    final ArrayList<PVector> seeds = new ArrayList<>();                             //list of (common) seeds
    final int numberOfSeeds = 500;                                                  //number of total seeds

    //###########Draw variables#########
    int sizeOfInstance;                                                             //size of a single instance
    PVector _posToDrawCreature;                                                     //temp vector for drawing creatures
    float sizeOfSquareInInstance;                                                   //size of a single square in a instance

    //###########Time to update##############
    float timeToUpdateCreature = 300;                                               //time in ms for updating the creatures
    int counterUpdateCreature = 0;                                                  //counter 
    int lastMs = 0;                                                                 //last ms to use for checking the time differences time millis()

    boolean pause = false;                                                          //pause the game

    int maxNumberOfStepBeforeKill = 30;                                             //max number of steps before a creature is killed
    int pointsPerFood = 10;                                                         //number of points taken from eating

    final ArrayList<String> infos = new ArrayList<>();
    final ArrayList<Creature> creatures = new ArrayList<>();

    final EvolutionManager evolManager = new EvolutionManager();
    Creature selected;

    @Override
    public void settings() {
        size(windowsWidth, windowsHeight);
    }

    @Override
    public void setup() {
        counterUpdateCreature = millis();

        float tmpSide1, tmpSide2;
        tmpSide1 = windowsHeight - (upSide + downSide);
        tmpSide2 = windowsWidth - (leftSide + rightSide);

        if (tmpSide1 > windowsWidth) {
            windowPlay = (int) tmpSide2;
        } else {
            windowPlay = (int) tmpSide1;
        }

        menuWidth = windowsWidth - windowPlay - leftSide;

        sideDrawNetwork = menuWidth - 2 * borderFromNetwork;

        sizeOfSquareInInstance = windowPlay / (nOfSquareInInstance * nOfCreaturePerSide);
        sizeOfInstance = Math.round(windowPlay / nOfCreaturePerSide);

        PVector _temp;
        for (int i = 0; i < numberOfSeeds; i++) {
            _temp = new PVector((int) Math.floor(random(0, nOfSquareInInstance)), (int) Math.floor(random(0, nOfSquareInInstance)));
            if (seeds.isEmpty() || PVector.dist(seeds.get(seeds.size() - 1), _temp) != 0) {
                seeds.add(_temp);
            } else {
                i--;
            }
        }

        for (int i = 0; i < nOfCreaturePerSide * nOfCreaturePerSide; i++) {
            creatures.add(new Creature(this));
        }

        _posToDrawCreature = new PVector(0, 0);

        textSize(12);
        textAlign(LEFT, TOP);
    }

    void Update() {
        if (lastMs - counterUpdateCreature > timeToUpdateCreature && pause == false) {
            creatures.forEach((c) -> {
                c.Calculate();

            });
            counterUpdateCreature = millis();
        }

        if (getNumberOfCreaturesAlive() == 0) {
            //evolManager.nextGeneration();
        }
    }

    @Override
    public void draw() {
        background(255);

        _posToDrawCreature = new PVector(0, 0);

        pushMatrix();

        translate(leftSide, upSide);

        //Griglia
        stroke(0);
        strokeWeight(1f);
        for (int i = 0; i <= nOfCreaturePerSide; i++) {
            float actualPos = i * sizeOfInstance;
            line(0, actualPos, windowPlay, actualPos);
            float nextPos = (i + 1) * sizeOfInstance;
            line(actualPos, 0, actualPos, windowPlay);
        }

        ///////////////Draw Creatures/////////////
        creatures.forEach((c) -> {
            pushMatrix();

            translate(_posToDrawCreature.x * sizeOfInstance, _posToDrawCreature.y * sizeOfInstance);
            c.Draw();

            _posToDrawCreature.x++;
            if (_posToDrawCreature.x >= nOfCreaturePerSide) {
                _posToDrawCreature.x = 0;
                _posToDrawCreature.y++;
            }

            popMatrix();
        });
        //////////////////////////////////////////

        popMatrix();

        ////////////Draw infos/////////////////////
        pushMatrix();
        translate(windowsWidth - menuWidth, upSide);

        infos.clear();
        if (selected != null) {
            addInfos("Selected: " + creatures.indexOf(selected));
            infos.addAll(selected.getInfos());
        } else {
            addInfos("Selected: false");
        }

        textAlign(LEFT, TOP);
        textSize(12);
        for (int i = 0; i < infos.size(); i++) {
            text(infos.get(i), 10, i * 15);
        }

        popMatrix();
        ///////////////////////////////////////////

        pushMatrix();

        translate(width - (sideDrawNetwork + borderFromNetwork), height - (sideDrawNetwork + borderFromNetwork));
        if (selected != null) {
            selected.genome.DrawNetworkWithForceDirectedGraph(sideDrawNetwork);
        }

        popMatrix();

        Update();

        lastMs = millis();

    }

    public void addInfos(String s) {
        infos.add(s);
    }

    PVector getMouseOnWorld() {
        return new PVector(mouseX - leftSide, mouseY - upSide);
    }

    int getNumberOfCreaturesAlive() {
        int nCreatAlive = 0;
        for (Creature c : creatures) {
            if (c.isDead() == false) {
                nCreatAlive++;
            }
        }

        return nCreatAlive;
    }

    @Override
    public void mouseClicked() {
        if (mouseButton == LEFT) {

            int xPositon = (int) Math.floor(getMouseOnWorld().x / sizeOfInstance);
            int yPositon = (int) Math.floor(getMouseOnWorld().y / sizeOfInstance);

            if (xPositon < 0 || xPositon >= nOfCreaturePerSide || yPositon < 0 || yPositon >= nOfCreaturePerSide) {
                selected = null;
            } else {
                int indexCreatureMouse = (int) (yPositon * nOfSquareInInstance + xPositon);

                selected = creatures.get(indexCreatureMouse);
            }

        }
    }
    @Override
    public void keyPressed() {
        if(key == 'h') {
            if(selected != null) {
                selected.genome.addConnectionMutation();
            }
        }else if(key == 'k') {
            if(selected != null) {
                selected.genome.addNodeMutation();
            }
        }
    }
}
