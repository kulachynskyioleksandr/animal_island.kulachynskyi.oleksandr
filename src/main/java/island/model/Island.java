package island.model;

import island.config.ConfigLoader;
import island.config.GlobalConfig;
import island.model.animals.Animal;
import island.model.plants.Plant;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Island {
    private final Cell[][] island;
    private final int width;
    private final int height;
    private final Random random = new Random();
    private FileWriter logWriter;

    public Island() {
        GlobalConfig config = ConfigLoader.loadGlobalConfig("E:\\java projects\\animal_island\\src\\main\\java\\island\\config\\config.json");
        this.width = config.getIsland().getWidth();
        this.height = config.getIsland().getHeight();
        island = new Cell[height][width];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                island[i][j] = new Cell(i, j);
            }
        }

        initializeLogFile();
    }

    private void initializeLogFile() {
        try {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            logWriter = new FileWriter("simulation_log_" + timestamp + ".txt");
            log("Simulation started.");
        } catch (IOException e) {
            System.err.println("Error creating log file: " + e.getMessage());
        }
    }

    public void log(String message) {
        try {
            logWriter.write(message + "\n");
            logWriter.flush();
        } catch (IOException e) {
            System.err.println("Error writing to log file: " + e.getMessage());
        }
    }

    public Cell getCell(int x, int y) {
        if (x < 0 || y < 0 || x >= height || y >= width) return null;
        return island[x][y];
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void randomAddAnimals(Map<Class<? extends Animal>, Integer> animalsToAddMap) {
        List<Map.Entry<Class<? extends Animal>, Integer>> animalEntries = new ArrayList<>(animalsToAddMap.entrySet());
        Collections.shuffle(animalEntries);

        for (Map.Entry<Class<? extends Animal>, Integer> entry : animalEntries) {
            Class<? extends Animal> animalClass = entry.getKey();
            int count = entry.getValue();

            for (int i = 0; i < count; i++) {
                try {
                    placeSingleAnimal(animalClass);
                } catch (Exception e) {
                    throw new RuntimeException("Error creating animal: " + animalClass.getSimpleName(), e);
                }
            }
        }
    }

    private void placeSingleAnimal(Class<? extends Animal> animalClass) throws Exception {
        Animal animal = animalClass.getDeclaredConstructor().newInstance();
        int attempts = 0;
        int maxAttempts = width * height;
        boolean placed = false;

        while (!placed && attempts < maxAttempts) {
            int x = random.nextInt(height);
            int y = random.nextInt(width);
            Cell cell = getCell(x, y);

            if (cell.canAddAnimal(animal)) {
                cell.addAnimal(animal);
                log(animalClass.getSimpleName() + " (ID: " + animal.getId() + ") placed at (" + x + ", " + y + ")");
                placed = true;
            }
            attempts++;
        }

        if (!placed) {
            log("Warning: Could not place " + animalClass.getSimpleName() + " (ID: " + animal.getId() + ") after " + maxAttempts + " attempts.");
        }
    }

    public void simulateDay() {
        resetReproductionStatus();
        processEating();
        processReproduction();
        processMovement();
        decreaseHunger();
        removeDeadAnimals();
        addNewPlants();
    }

    private void resetReproductionStatus() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Cell cell = island[i][j];
                for (Animal animal : cell.getAnimals()) {
                    animal.setHasReproduced(false);
                }
            }
        }
    }

    private void processEating() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Cell cell = island[i][j];
                for (Animal animal : new ArrayList<>(cell.getAnimals())) {
                    animal.eat(this, i, j);
                }
            }
        }
    }

    private void processReproduction() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Cell cell = island[i][j];
                for (Animal animal : new ArrayList<>(cell.getAnimals())) {
                    animal.reproduce(this, i, j);
                }
            }
        }
    }

    private void processMovement() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Cell cell = island[i][j];
                for (Animal animal : new ArrayList<>(cell.getAnimals())) {
                    animal.move(this, i, j);
                }
            }
        }
    }

    private void decreaseHunger() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Cell cell = island[i][j];
                for (Animal animal : cell.getAnimals()) {
                    animal.decreaseHunger();
                }
            }
        }
    }

    private void removeDeadAnimals() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                island[i][j].removeDeadAnimals();
            }
        }
    }

    private void addNewPlants() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Cell cell = island[i][j];
                int newPlants = random.nextInt(5) + 1;
                for (int k = 0; k < newPlants; k++) {
                    cell.addPlant(new Plant());
                }
                log("Added " + newPlants + " new plants to cell (" + i + ", " + j + ")");
            }
        }
    }

    public void simulate(int days) {
        runSimulationDays(days);
        Map<String, Integer> animalCounts = countRemainingAnimals();
        printSimulationResults(days, animalCounts);
        logSimulationResults(days, animalCounts);
        closeLogFile();
    }

    private void runSimulationDays(int days) {
        for (int day = 1; day <= days; day++) {
            log("Day " + day + " started.");
            simulateDay();
            logCaterpillarCount();
            log("Day " + day + " ended.");
        }
    }

    private void logCaterpillarCount() {
        int caterpillarCount = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                List<Animal> animals = island[i][j].getAnimals();
                for (Animal animal : animals) {
                    if (animal.getClass().getSimpleName().equals("Caterpillar")) {
                        caterpillarCount++;
                    }
                }
            }
        }
        log("Caterpillars after day: " + caterpillarCount);
    }

    private Map<String, Integer> countRemainingAnimals() {
        Map<String, Integer> animalCounts = new TreeMap<>();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                List<Animal> animals = island[i][j].getAnimals();
                for (Animal animal : animals) {
                    String animalType = animal.getClass().getSimpleName();
                    animalCounts.put(animalType, animalCounts.getOrDefault(animalType, 0) + 1);
                }
            }
        }
        return animalCounts;
    }

    private void printSimulationResults(int days, Map<String, Integer> animalCounts) {
        System.out.println("Simulation results after " + days + " days:");
        if (animalCounts.isEmpty()) {
            System.out.println("All animals have died.");
        } else {
            for (Map.Entry<String, Integer> entry : animalCounts.entrySet()) {
                String animalType = entry.getKey();
                int count = entry.getValue();
                System.out.println("Remaining " + animalType.toLowerCase() + "s: " + count);
            }
        }
    }

    private void logSimulationResults(int days, Map<String, Integer> animalCounts) {
        log("Simulation results after " + days + " days:");
        if (animalCounts.isEmpty()) {
            log("All animals have died.");
        } else {
            for (Map.Entry<String, Integer> entry : animalCounts.entrySet()) {
                String animalType = entry.getKey();
                int count = entry.getValue();
                log("Remaining " + animalType.toLowerCase() + "s: " + count);
            }
        }
    }

    private void closeLogFile() {
        try {
            logWriter.close();
        } catch (IOException e) {
            System.err.println("Error closing log file: " + e.getMessage());
        }
    }
}