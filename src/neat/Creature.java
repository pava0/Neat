package neat;

import neat.Brain.Brain;
import java.util.ArrayList;
import processing.core.PVector;

public class Creature implements Comparable {

    private final Neat main;

    Brain brain;

    int creatureColor, targetColor;

    private boolean isDead = false;

    private int stepsTaken;
    private int indexFood;

    private float score;
    private float closerDistanceToActualFood;

    private PVector position;
    private PVector actualFood;

    private final ArrayList<String> infos = new ArrayList<>();

    Creature(Neat main) {
        this.main = main;

        Initialize();
    }

    @Override
    public int compareTo(Object _creature) {
        //return ((Creature)_creature).score - score;
        return ((Creature) _creature).score < score ? -1
                : ((Creature) _creature).score > score ? 1
                        : 0;
    }

    @Override
    public String toString() {
        return Float.toString(score);
    }

    final void Initialize() {
        creatureColor = main.color(0, 255, 0);          //Green
        targetColor = main.color(255, 255, 0);          //Yellow
        isDead = false;

        stepsTaken = 0;
        score = 1;
        indexFood = 0;
        closerDistanceToActualFood = Float.MAX_VALUE;

        position = new PVector(0, (int) Math.round(main.nOfCreaturePerSide / 2f));
        actualFood = main.seeds.get(indexFood);

        brain = new Brain();
    }

    void Draw() {
        main.fill(creatureColor);
        main.rect(position.x * main.sizeOfSquareInInstance, position.y * main.sizeOfSquareInInstance, main.sizeOfSquareInInstance, main.sizeOfSquareInInstance);
        main.fill(targetColor);
        main.rect(actualFood.x * main.sizeOfSquareInInstance, actualFood.y * main.sizeOfSquareInInstance, main.sizeOfSquareInInstance, main.sizeOfSquareInInstance);

        main.fill(0);
    }

    void Calculate() {
    }

    void Move(PVector direction) {
        position.add(direction);

        if (position.x >= main.nOfSquareInInstance || position.x < 0 || position.y >= main.nOfSquareInInstance || position.y < 0) {
            isDead = true;
            position.sub(direction);
        }

        stepsTaken++;

        if (stepsTaken >= main.maxNumberOfStepBeforeKill) {         //if it has reached the max number of steps without eating
            isDead = true;
        }

        if (PVector.dist(position, actualFood) == 0) {              //if it's on the food. eat
            indexFood++;
            closerDistanceToActualFood = Float.MAX_VALUE;
            actualFood = main.seeds.get(indexFood);
            stepsTaken = 0;

            if (indexFood >= main.numberOfSeeds - 1) {
                isDead = true;
            }
        }

        float distance = PVector.dist(actualFood, position);
        if (distance < closerDistanceToActualFood) {                //update distance from food(for score)
            closerDistanceToActualFood = distance;
        }

        score = (indexFood * main.pointsPerFood) + main.sizeOfInstance / closerDistanceToActualFood;

        if (isDead) {
            creatureColor = main.color(125, 125, 125);
        }
    }
    
    public Creature Clone() {
        Creature _new = new Creature(main);
        
        return _new;
    }

    ArrayList<String> getInfos() {
        infos.clear();
        infos.add("Score: " + score);

        return infos;
    }

    boolean isDead() {
        return isDead;
    }

    float getScore() {
        return score;
    }

}
