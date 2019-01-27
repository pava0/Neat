package neat;

import processing.core.PApplet;

public class Neat extends PApplet {

    @Override
    public void settings() {
        size(300, 300);
    }

    @Override
    public void setup() {
        noStroke();
    }

    @Override
    public void draw() {
        ellipse(50, 50, 10, 10);

    }
}
