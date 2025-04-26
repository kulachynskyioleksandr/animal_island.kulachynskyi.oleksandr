package island.model.herbivores;

import island.config.AnimalConfig;
import island.model.animals.Animal;

import java.util.List;

public class Horse extends Herbivore {
    private static final AnimalConfig config = loadAnimalConfig("Horse");

    public Horse() {
        super(config.getWeight(), config.getMaxAmountPerCell(), config.getSpeed(), config.getRequiredFood());
    }

    public Horse(double weight, int maxAmountPerCell, int speed, double requiredFood, List<Animal> parents) {
        super(weight, maxAmountPerCell, speed, requiredFood, parents);
    }
}