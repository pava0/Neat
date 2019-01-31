package Neat;

import java.util.ArrayList;
import java.util.Collections;

public class EvolutionManager {
    
    private Neat main;
    
    private float magnitudeForBreeding = 2f;            //power to which each score is raised to sum up for the total score

    private int generation = 0;
    
    private ArrayList<Creature> creatures;
    
    void nextGeneration(Neat main, ArrayList<Creature> creatures) {
        this.creatures = creatures;
        this.main = main;
    }
    
    public void Sort() {
        Collections.sort(creatures);
        generation++;
    }
    
    private void KillWorstNumber(int numberOfRemaining) {
        for (int i = 0; i < creatures.size() - numberOfRemaining; i++) {
            creatures.remove(creatures.size() - 1);
        }
    }
    
    private void KillWorstPercent(float percentOfRemaining) {
        for (int i = 0; i < creatures.size() - Math.round(creatures.size() * creatures.size()); i++) {
            creatures.remove(creatures.size() - 1);
        }
    }
    
    private void BreedBest() {
        
        ArrayList<Float> probabilities = new ArrayList<>();
        float totalScore = 0;
        
        for (Creature c : creatures) {
            probabilities.add(c.getScore());
            totalScore += c.getScore();
        }
        
        float rndNumber = 0;
        int index = 0;
        while (creatures.size() < Math.pow(main.nOfCreaturePerSide, 2)) {
            rndNumber = main.random(0, totalScore);
            
            while (rndNumber > 0) {
                if (rndNumber <= probabilities.get(index)) {
                    creatures.add(creatures.get(index).Clone());
                    break;
                } else {
                    rndNumber -= probabilities.get(index);
                    index++;
                }
            }
        }
    }
    
    private void Mutate() {
        
    }
    
    public void NextGeneration() {
        Sort();
        KillWorstPercent(0.1f);
        BreedBest();
        Mutate();
        
        creatures.forEach((c) -> {
            c.Initialize();
        });
    }
}
