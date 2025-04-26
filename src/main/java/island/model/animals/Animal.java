package island.model.animals;

import island.config.AnimalConfig;
import island.config.ConfigLoader;
import island.model.*;

import java.util.*;

public abstract class Animal implements Eatable, Reproducible, Movable {
    protected final double weight;
    protected final int maxAmountPerCell;
    protected final int speed;
    protected final double requiredFood;
    protected double hunger;
    protected boolean isAlive;
    protected boolean hasReproduced;
    protected static final Random random = new Random();
    private static long idCounter = 0;
    private final long id;
    private final Set<Long> relatives;
    private final List<Animal> parents;

    public Animal(double weight, int maxAmountPerCell, int speed, double requiredFood) {
        this(weight, maxAmountPerCell, speed, requiredFood, new ArrayList<>());
    }

    public Animal(double weight, int maxAmountPerCell, int speed, double requiredFood, List<Animal> parents) {
        this.weight = weight;
        this.maxAmountPerCell = maxAmountPerCell;
        this.speed = speed;
        this.requiredFood = requiredFood;
        this.hunger = 0;
        this.isAlive = true;
        this.hasReproduced = false;
        this.id = idCounter++;
        this.relatives = new HashSet<>();
        this.parents = parents;

        for (Animal parent : parents) {
            this.relatives.add(parent.getId());
            this.relatives.addAll(parent.getRelatives());
        }
    }

    public boolean hasReproduced() {
        return hasReproduced;
    }

    public void setHasReproduced(boolean hasReproduced) {
        this.hasReproduced = hasReproduced;
    }

    protected static AnimalConfig loadAnimalConfig(String animalType) {
        return ConfigLoader.loadGlobalConfig("E:\\java projects\\animal_island\\src\\main\\java\\island\\config\\config.json")
                .getAnimals()
                .stream()
                .filter(animal -> animalType.equals(animal.getType()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Config for " + animalType + " not found"));
    }

    public long getId() {
        return id;
    }

    public Set<Long> getRelatives() {
        return relatives;
    }

    public void addRelative(Animal relative) {
        this.relatives.add(relative.getId());
    }

    public boolean isHungry() {
        return hunger < requiredFood * 0.1;
    }

    public boolean isDead() {
        return !isAlive || hunger <= 0;
    }

    public void decreaseHunger() {
        if (requiredFood > 0) {
            hunger -= requiredFood * 0.05;
            if (hunger <= 0) {
                setAlive(false);
            }
        }
    }

    public void eat(double foodAmount) {
        hunger = Math.min(hunger + foodAmount, requiredFood);
    }

    public double getRequiredFood() {
        return requiredFood;
    }

    public double getWeight() {
        return weight;
    }

    public int getSpeed() {
        return speed;
    }

    public int getMaxAmountPerCell() {
        return maxAmountPerCell;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        this.isAlive = alive;
    }

    @Override
    public void reproduce(Island island, int x, int y) {
        if (!isAlive()) {
            island.log(this.getClass().getSimpleName() + " (ID: " + id + ") at (" + x + ", " + y + ") cannot reproduce: dead");
            return;
        }

        Cell cell = island.getCell(x, y);
        Animal partner = findReproductionPartner(cell);
        if (partner == null) {
            island.log(this.getClass().getSimpleName() + " (ID: " + id + ") at (" + x + ", " + y + ") cannot reproduce: no partner available");
            return;
        }

        Animal newAnimal = createOffspring(partner);
        if (newAnimal != null) {
            placeOffspring(cell, partner, newAnimal);
            this.hasReproduced = true;
            partner.setHasReproduced(true);
            island.log(this.getClass().getSimpleName() + " (ID: " + id + ") and " +
                    partner.getClass().getSimpleName() + " (ID: " + partner.getId() + ") reproduced at (" + x + ", " + y + "), offspring ID: " + newAnimal.getId());
        } else {
            island.log(this.getClass().getSimpleName() + " (ID: " + id + ") at (" + x + ", " + y + ") failed to create offspring");
        }
    }

    @Override
    public boolean canReproduce() {
        return isAlive;
    }

    protected Animal findReproductionPartner(Cell cell) {
        List<Animal> sameType = cell.getAnimals().stream()
                .filter(a -> a.getClass() == this.getClass() && a.canReproduce() && !this.relatives.contains(a.getId()))
                .toList();

        if (sameType.size() < 2) return null;
        return sameType.get(random.nextInt(sameType.size()));
    }

    protected Animal createOffspring(Animal partner) {
        try {
            List<Animal> parents = List.of(this, partner);
            Animal newAnimal = this.getClass().getDeclaredConstructor().newInstance();
            return this.getClass().getDeclaredConstructor(double.class, int.class, int.class, double.class, List.class)
                    .newInstance(newAnimal.getWeight(), newAnimal.getMaxAmountPerCell(), newAnimal.getSpeed(), newAnimal.getRequiredFood(), parents);
        } catch (Exception e) {
            throw new RuntimeException("Error reproducing: " + this.getClass().getSimpleName(), e);
        }
    }

    protected void placeOffspring(Cell cell, Animal partner, Animal newAnimal) {
        if (cell.canAddAnimal(newAnimal)) {
            this.addRelative(partner);
            this.addRelative(newAnimal);
            partner.addRelative(this);
            partner.addRelative(newAnimal);

            cell.addAnimal(newAnimal);
        }
    }

    protected int[] getSearchBounds(Island island, int currentX, int currentY, int searchRadius) {
        int minX = Math.max(0, currentX - searchRadius);
        int maxX = Math.min(island.getHeight() - 1, currentX + searchRadius);
        int minY = Math.max(0, currentY - searchRadius);
        int maxY = Math.min(island.getWidth() - 1, currentY + searchRadius);
        return new int[]{minX, maxX, minY, maxY};
    }

    public abstract void eat(Island island, int x, int y);

    @Override
    public abstract void move(Island island, int currentX, int currentY);
}