package island.model.herbivores;

import island.model.Cell;
import island.model.Island;
import island.model.animals.Animal;
import island.model.plants.Plant;

import java.util.*;

public abstract class Herbivore extends Animal {
    private static final Random random = new Random();

    public Herbivore(double weight, int maxAmountPerCell, int speed, double requiredFood) {
        super(weight, maxAmountPerCell, speed, requiredFood);
    }

    public Herbivore(double weight, int maxAmountPerCell, int speed, double requiredFood, List<Animal> parents) {
        super(weight, maxAmountPerCell, speed, requiredFood, parents);
    }

    @Override
    public void eat(Island island, int x, int y) {
        if (!isAlive() || !isHungry()) {
            island.log(this.getClass().getSimpleName() + " (ID: " + getId() + ") at (" + x + ", " + y + ") cannot eat: " +
                    (isAlive() ? "not hungry" : "dead"));
            return;
        }

        Cell cell = island.getCell(x, y);
        List<Plant> plants = cell.getPlants();

        if (plants.isEmpty()) {
            island.log(this.getClass().getSimpleName() + " (ID: " + getId() + ") at (" + x + ", " + y + ") cannot eat: no plants available");
            return;
        }

        Plant plant = plants.get(random.nextInt(plants.size()));
        eat(plant.getWeight());
        cell.removePlant(plant);
        island.log(this.getClass().getSimpleName() + " (ID: " + getId() + ") ate a plant at (" + x + ", " + y + ")");
    }

    @Override
    public void move(Island island, int currentX, int currentY) {
        if (!canMove()) return;

        if (!hasReproduced) {
            int[] partnerCoords = findPartnerCell(island, currentX, currentY);
            if (partnerCoords != null) {
                moveToCell(island, currentX, currentY, partnerCoords[0], partnerCoords[1]);
                island.log(this.getClass().getSimpleName() + " (ID: " + getId() + ") moved from (" + currentX + ", " + currentY + ") to (" + partnerCoords[0] + ", " + partnerCoords[1] + ") to find a partner");
                return;
            }
        }

        int[] bestCoords = findBestCell(island, currentX, currentY);
        if (bestCoords != null) {
            moveToCell(island, currentX, currentY, bestCoords[0], bestCoords[1]);
            island.log(this.getClass().getSimpleName() + " (ID: " + getId() + ") moved from (" + currentX + ", " + currentY + ") to (" + bestCoords[0] + ", " + bestCoords[1] + ") to find more plants");
        }
    }

    private boolean canMove() {
        return isAlive() && speed > 0;
    }

    private int[] findPartnerCell(Island island, int currentX, int currentY) {
        int searchRadius = speed;
        int bestX = -1;
        int bestY = -1;
        int minDistance = Integer.MAX_VALUE;

        for (int x = Math.max(0, currentX - searchRadius); x <= Math.min(island.getHeight() - 1, currentX + searchRadius); x++) {
            for (int y = Math.max(0, currentY - searchRadius); y <= Math.min(island.getWidth() - 1, currentY + searchRadius); y++) {
                if (x == currentX && y == currentY) continue;

                Cell cell = island.getCell(x, y);
                boolean hasPartner = cell.getAnimals().stream()
                        .anyMatch(a -> a.getClass() == this.getClass() && a.canReproduce() && !this.getRelatives().contains(a.getId()));

                if (hasPartner) {
                    int distance = Math.abs(x - currentX) + Math.abs(y - currentY);
                    if (distance <= speed && distance < minDistance) {
                        minDistance = distance;
                        bestX = x;
                        bestY = y;
                    }
                }
            }
        }

        return (bestX != -1 && bestY != -1) ? new int[]{bestX, bestY} : null;
    }

    private int[] findBestCell(Island island, int currentX, int currentY) {
        int searchRadius = speed;
        int[] bounds = getSearchBounds(island, currentX, currentY, searchRadius);
        return searchBestPlantCell(island, currentX, currentY, bounds[0], bounds[1], bounds[2], bounds[3]);
    }

    private int[] searchBestPlantCell(Island island, int currentX, int currentY, int minX, int maxX, int minY, int maxY) {
        int bestPlantCount = island.getCell(currentX, currentY).getPlants().size();
        int bestX = currentX;
        int bestY = currentY;
        boolean foundBetterCell = false;

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                if (x == currentX && y == currentY) continue;

                Cell cell = island.getCell(x, y);
                if (cell.canAddAnimal(this)) {
                    int plantCount = cell.getPlants().size();
                    int distance = Math.abs(x - currentX) + Math.abs(y - currentY);
                    if (plantCount > bestPlantCount && distance <= speed) {
                        bestPlantCount = plantCount;
                        bestX = x;
                        bestY = y;
                        foundBetterCell = true;
                    }
                }
            }
        }

        return foundBetterCell ? new int[]{bestX, bestY} : null;
    }

    private void moveToCell(Island island, int currentX, int currentY, int newX, int newY) {
        Cell currentCell = island.getCell(currentX, currentY);
        Cell newCell = island.getCell(newX, newY);
        currentCell.removeAnimal(this);
        newCell.addAnimal(this);
    }
}