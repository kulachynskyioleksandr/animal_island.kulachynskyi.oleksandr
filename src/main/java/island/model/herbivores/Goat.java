package island.model.herbivores;

import island.config.AnimalConfig;
import island.model.animals.Animal;

import java.util.List;

public class Goat extends Herbivore {
    private static final AnimalConfig config = loadAnimalConfig("Goat");

    public Goat() {
        super(config.getWeight(), config.getMaxAmountPerCell(), config.getSpeed(), config.getRequiredFood());
    }

    public Goat(double weight, int maxAmountPerCell, int speed, double requiredFood, List<Animal> parents) {
        super(weight, maxAmountPerCell, speed, requiredFood, parents);
    }
}