package island.model.herbivores;

import island.config.AnimalConfig;
import island.model.animals.Animal;

import java.util.List;

public class Deer extends Herbivore {
    private static final AnimalConfig config = loadAnimalConfig("Deer");

    public Deer() {
        super(config.getWeight(), config.getMaxAmountPerCell(), config.getSpeed(), config.getRequiredFood());
    }

    public Deer(double weight, int maxAmountPerCell, int speed, double requiredFood, List<Animal> parents) {
        super(weight, maxAmountPerCell, speed, requiredFood, parents);
    }
}