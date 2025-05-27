package island.model;

import island.model.animals.Animal;
import island.model.plants.Plant;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Cell {
    private final List<Animal> animals = new ArrayList<>();
    private final List<Plant> plants = new ArrayList<>();
    private static final Random random = new Random();
    private static final int MAX_PLANTS_PER_CELL = 200;
    private static final int MIN_INITIAL_PLANTS = 50;
    private static final int MAX_INITIAL_PLANTS_RANGE = 151;
    private final int x;
    private final int y;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
        initializePlants();
    }

    private void initializePlants() {
        int plantCount = random.nextInt(MAX_INITIAL_PLANTS_RANGE) + MIN_INITIAL_PLANTS;
        for (int i = 0; i < plantCount; i++) {
            plants.add(new Plant());
        }
    }

    public void addAnimal(Animal animal) {
        if (canAddAnimal(animal)) {
            animals.add(animal);
        }
    }

    public void removeAnimal(Animal animal) {
        animals.remove(animal);
    }

    public List<Animal> getAnimals() {
        return new ArrayList<>(animals);
    }

    public boolean canAddAnimal(Animal animal) {
        long count = animals.stream().filter(a -> a.getClass() == animal.getClass()).count();
        return count < animal.getMaxAmountPerCell();
    }

    public void addPlant(Plant plant) {
        if (plants.size() < MAX_PLANTS_PER_CELL) {
            plants.add(plant);
        }
    }

    public void removePlant(Plant plant) {
        plants.remove(plant);
    }

    public List<Plant> getPlants() {
        return new ArrayList<>(plants);
    }

    public void removeDeadAnimals() {
        animals.removeIf(animal -> !animal.isAlive());
    }
}