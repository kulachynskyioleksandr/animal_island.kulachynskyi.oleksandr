package island.model.predators;

import island.config.AnimalConfig;
import island.model.animals.Animal;

import java.util.List;

public class Bear extends Predator {
    private static final AnimalConfig config = loadAnimalConfig("Bear");

    public Bear() {
        super(config.getWeight(), config.getMaxAmountPerCell(), config.getSpeed(), config.getRequiredFood());
    }

    public Bear(double weight, int maxAmountPerCell, int speed, double requiredFood, List<Animal> parents) {
        super(weight, maxAmountPerCell, speed, requiredFood, parents);
    }
}