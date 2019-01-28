package neat;

import processing.core.PApplet;
import java.util.*;
import processing.core.PVector;

public class Neat extends PApplet {

    int nOfSquareInInstance = 5;        //number of square in single instance
    int nOfCreaturePerSide = 5;         //number of creature per side
    int windowsWidth = 600;             //width of window (square window)

    ArrayList<PVector> seeds;           //list of (common) seeds
    int numberOfSeeds = 500;            //number of total seeds

    //###########Draw variables#########
    int sizeOfInstance;                 //size of a single instance
    PVector _posToDrawCreature;         //temp vector for drawing creatures
    float sizeOfSquareInInstance;       //size of a single square in a instance

    //###########Time to update##############
    float timeToUpdateCreature = 300;   //time in ms for updating the creatures
    int counterUpdateCreature = 0;      //counter 
    int lastMs = 0;                     //last ms to use for checking the time differences time millis()

    boolean pause = false;              //pause the game

    int maxNumberOfStepBeforeKill = 30; //max number of steps before a creature is killed

    ArrayList<Creature> creatures;
    EvolutionManager evolManager;
    
    public PApplet main;

    @Override
    public void settings() {
        size(windowsWidth, windowsWidth);
        main = this;
    }

    @Override
    public void setup() {
        //evolManager = new EvolutionManager(creatures);

        counterUpdateCreature = millis();

        sizeOfSquareInInstance = width / (nOfSquareInInstance * nOfCreaturePerSide);
        sizeOfInstance = width / nOfCreaturePerSide;

        seeds = new ArrayList<>();

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
            creatures.add(new Creature());
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
            evolManager.nextGeneration();
        }
    }

    @Override
    public void draw() {
        background(255);

        _posToDrawCreature = new PVector(0, 0);

        //Griglia
        line(0, 0, width, 0);
        for (int i = 0; i < nOfCreaturePerSide; i++) {
            fill(0);

            float xWorld = i * sizeOfInstance;
            line(xWorld, 0, xWorld, height);
            float y_world = (i + 1) * sizeOfInstance;
            line(0, y_world, width, y_world);
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
        
        //////////////Draw Brain////////////////////////////////
        PVector creatureOnMouse = new PVector((float) Math.floor(mouseX / sizeOfInstance), (float) Math.floor(mouseY / sizeOfInstance));
        int indexCreatureMouse = (int) (creatureOnMouse.y * nOfSquareInInstance + creatureOnMouse.x);

        int windowWidth = 50, windowHeight = 50;

        if (width - mouseX < windowWidth) {
            translate(-(width - mouseX), 0);
        }
        if (height - mouseY < windowHeight) {
            translate(0, -(height - mouseY));
        }

        fill(0);
        rect(mouseX, mouseY, windowWidth, windowHeight);
        ////////////////////////////////////////////////////////

        Update();

        lastMs = millis();

    }

    int getNumberOfCreaturesAlive() {
        int nCreatAlive = 0;
        for (Creature c : creatures) {
            if (c.isDead == false) {
                nCreatAlive++;
            }
        }

        return nCreatAlive;
    }
}
