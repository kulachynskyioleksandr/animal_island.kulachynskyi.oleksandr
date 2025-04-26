package island.model.herbivores;

import island.config.AnimalConfig;
import island.model.Cell;
import island.model.Island;
import island.model.animals.Animal;

import java.util.List;
import java.util.Random;

public class Caterpillar extends Herbivore {
    private static final AnimalConfig config = loadAnimalConfig("Caterpillar");
    private static final Random random = new Random();
    private static final double REPRODUCTION_CHANCE = 0.2;
    private static final int REPRODUCTION_COOLDOWN = 3;
    private int cyclesSinceLastReproduction = 0;

    public Caterpillar() {
        super(config.getWeight(), config.getMaxAmountPerCell(), config.getSpeed(), config.getRequiredFood());
    }

    public Caterpillar(double weight, int maxAmountPerCell, int speed, double requiredFood, List<Animal> parents) {
        super(weight, maxAmountPerCell, speed, requiredFood, parents);
    }

    @Override
    public void reproduce(Island island, int x, int y) {
        if (!isAlive()) {
            island.log(this.getClass().getSimpleName() + " (ID: " + getId() + ") at (" + x + ", " + y + ") cannot reproduce: dead");
            return;
        }

        if (!canAttemptReproduction()) {
            cyclesSinceLastReproduction++;
            return;
        }

        if (hasReproduced()) {
            cyclesSinceLastReproduction++;
            return;
        }

        performReproduction(island, x, y);
    }

    private boolean canAttemptReproduction() {
        if (cyclesSinceLastReproduction < REPRODUCTION_COOLDOWN) {
            return false;
        }
        return random.nextDouble() <= REPRODUCTION_CHANCE;
    }

    private void performReproduction(Island island, int x, int y) {
        Cell cell = island.getCell(x, y);
        Animal partner = findReproductionPartner(cell);
        if (partner == null) {
            island.log(this.getClass().getSimpleName() + " (ID: " + getId() + ") at (" + x + ", " + y + ") cannot reproduce: no partner available");
            cyclesSinceLastReproduction++;
            return;
        }

        Animal newAnimal = createOffspring(partner);
        if (newAnimal != null) {
            placeOffspring(cell, partner, newAnimal);
            this.setHasReproduced(true);
            partner.setHasReproduced(true);
            island.log(this.getClass().getSimpleName() + " (ID: " + getId() + ") and " +
                    partner.getClass().getSimpleName() + " (ID: " + partner.getId() + ") reproduced at (" + x + ", " + y + "), offspring ID: " + newAnimal.getId());
            cyclesSinceLastReproduction = 0;
        } else {
            island.log(this.getClass().getSimpleName() + " (ID: " + getId() + ") at (" + x + ", " + y + ") failed to create offspring");
            cyclesSinceLastReproduction++;
        }
    }
}