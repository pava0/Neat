package neat;

import processing.core.PApplet;
import java.util.*;
import processing.core.PVector;

public class Neat extends PApplet {

    final int nOfSquareInInstance = 5;                                              //number of square in single instance
    final int nOfCreaturePerSide = 5;                                               //number of creature per side
    final int windowsWidth = 800;                                                   //width of window
    final int windowsHeight = 600;
    final int leftSide = 20;
    final int upSide = 20;
    final int rightSide = 150;
    final int downSide = 75;
    int windowPlay;

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

    ArrayList<Creature> creatures;
    EvolutionManager evolManager;

    public PApplet main;

    @Override
    public void settings() {
        size(windowsWidth, windowsHeight);
        main = this;
    }

    @Override
    public void setup() {
        //evolManager = new EvolutionManager(creatures);

        counterUpdateCreature = millis();

        float tmpSide1, tmpSide2;
        tmpSide1 = windowsHeight - (upSide + downSide);
        tmpSide2 = windowsWidth - (leftSide + rightSide);

        if (tmpSide1 > windowsWidth) {
            windowPlay = (int) tmpSide2;
        } else {
            windowPlay = (int) tmpSide1;
        }

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

        creatures = new ArrayList<>();

        for (int i = 0; i < nOfCreaturePerSide * nOfCreaturePerSide; i++) {
            creatures.add(new Creature(this));
        }

        _posToDrawCreature = new PVector(0, 0);

        textSize(10);
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
        for (int i = 0; i <= nOfCreaturePerSide; i++) {
            fill(0);

            float actualPos = i * sizeOfInstance;
            line(0, actualPos, windowPlay, actualPos);
            float nextPos = (i + 1) * sizeOfInstance;
            line(actualPos, 0, actualPos, windowPlay);
        }

        ///////////////Draw Creatures/////////////
        creatures.forEach((c) -> {
            pushMatrix();

            translate(_posToDrawCreature.x * (sizeOfInstance), _posToDrawCreature.y * sizeOfInstance/*+ (positionCreaturesOnScreen.y + 1) * offset*/);
            c.Draw();

            _posToDrawCreature.x++;
            if (_posToDrawCreature.x >= nOfCreaturePerSide) {
                _posToDrawCreature.x = 0;
                _posToDrawCreature.y++;
            }

            popMatrix();
        });
        //////////////////////////////////////////

        PVector creatureOnMouse = new PVector((float) Math.floor(getMouseOnWorld().x / sizeOfInstance), (float) Math.floor(getMouseOnWorld().y / sizeOfInstance));
        int indexCreatureMouse = (int) (creatureOnMouse.y * nOfSquareInInstance + creatureOnMouse.x);

        if (indexCreatureMouse >= 0 && indexCreatureMouse < Math.pow(nOfCreaturePerSide, 2)) {
            translate(getMouseOnWorld().x + 20, getMouseOnWorld().y + 20);
            creatures.get(indexCreatureMouse).drawInfoSquare();
        }

        //////////////Draw Brain////////////////////////////////
        ////////////////////////////////////////////////////////
        Update();

        popMatrix();

        lastMs = millis();

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
            creatures.forEach((c) -> {
                c.Move(new PVector(0, -1));
            });

        }
    }
}
