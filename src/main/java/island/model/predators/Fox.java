package island.model.predators;

import island.config.AnimalConfig;
import island.model.animals.Animal;

import java.util.List;

public class Fox extends Predator {
    private static final AnimalConfig config = loadAnimalConfig("Fox");

    public Fox() {
        super(config.getWeight(), config.getMaxAmountPerCell(), config.getSpeed(), config.getRequiredFood());
    }

    public Fox(double weight, int maxAmountPerCell, int speed, double requiredFood, List<Animal> parents) {
        super(weight, maxAmountPerCell, speed, requiredFood, parents);
    }
}