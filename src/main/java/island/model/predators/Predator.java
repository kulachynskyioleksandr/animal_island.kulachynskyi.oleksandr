package island.model.predators;

import island.model.Cell;
import island.model.Island;
import island.model.animals.Animal;

import java.util.*;

public abstract class Predator extends Animal {
    private static final Random random = new Random();
    private static final Map<String, Map<String, Integer>> predationProbabilities;

    static {
        predationProbabilities = new HashMap<>();
        predationProbabilities.put("Wolf", new HashMap<>(Map.of(
                "Horse", 10, "Deer", 15, "Rabbit", 60, "Mouse", 80,
                "Goat", 60, "Sheep", 70, "Boar", 15, "Buffalo", 10,
                "Duck", 40
        )));
        predationProbabilities.put("Python", new HashMap<>(Map.of(
                "Fox", 15, "Rabbit", 20, "Mouse", 40, "Duck", 10
        )));
        predationProbabilities.put("Fox", new HashMap<>(Map.of(
                "Rabbit", 70, "Mouse", 90, "Duck", 60, "Caterpillar", 10
        )));

        Map<String, Integer> bearProbabilities = new HashMap<>();
        bearProbabilities.put("Python", 80);
        bearProbabilities.put("Horse", 40);
        bearProbabilities.put("Deer", 80);
        bearProbabilities.put("Rabbit", 80);
        bearProbabilities.put("Mouse", 90);
        bearProbabilities.put("Goat", 70);
        bearProbabilities.put("Sheep", 70);
        bearProbabilities.put("Boar", 50);
        bearProbabilities.put("Buffalo", 20);
        bearProbabilities.put("Duck", 10);
        bearProbabilities.put("Caterpillar", 5);
        predationProbabilities.put("Bear", bearProbabilities);

        predationProbabilities.put("Eagle", new HashMap<>(Map.of(
                "Fox", 10, "Rabbit", 90, "Mouse", 90, "Duck", 80, "Caterpillar", 10
        )));
    }

    public Predator(double weight, int maxAmountPerCell, int speed, double requiredFood) {
        super(weight, maxAmountPerCell, speed, requiredFood);
    }

    public Predator(double weight, int maxAmountPerCell, int speed, double requiredFood, List<Animal> parents) {
        super(weight, maxAmountPerCell, speed, requiredFood, parents);
    }

    @Override
    public void eat(Island island, int x, int y) {
        if (!canEat()) {
            island.log(this.getClass().getSimpleName() + " (ID: " + getId() + ") at (" + x + ", " + y + ") cannot eat: " +
                    (isAlive() ? "not hungry" : "dead"));
            return;
        }

        Cell cell = island.getCell(x, y);
        if (tryEatInCurrentCell(cell, island, x, y)) return;

        huntInNearbyCells(island, x, y);
    }

    private boolean canEat() {
        return isAlive() && isHungry();
    }

    private boolean tryEatInCurrentCell(Cell cell, Island island, int x, int y) {
        List<Animal> prey = findPrey(cell);
        if (prey.isEmpty()) {
            island.log(this.getClass().getSimpleName() + " (ID: " + getId() + ") at (" + x + ", " + y + ") cannot eat: no prey in current cell");
            return false;
        }

        Animal victim = prey.get(random.nextInt(prey.size()));
        boolean consumed = consumeVictim(cell, victim);
        if (consumed) {
            island.log(this.getClass().getSimpleName() + " (ID: " + getId() + ") ate " +
                    victim.getClass().getSimpleName() + " (ID: " + victim.getId() + ") at (" + x + ", " + y + ")");
        }
        return consumed;
    }

    private void huntInNearbyCells(Island island, int x, int y) {
        Cell currentCell = island.getCell(x, y);
        int[][] nearbyCoords = getNearbyCoordinates(x, y);

        for (int[] coords : nearbyCoords) {
            int newX = coords[0];
            int newY = coords[1];
            if (isOutOfBounds(island, newX, newY)) continue;

            Cell nearbyCell = island.getCell(newX, newY);
            List<Animal> nearbyPrey = findPrey(nearbyCell);
            if (nearbyPrey.isEmpty()) {
                island.log(this.getClass().getSimpleName() + " (ID: " + getId() + ") found no prey in nearby cell (" + newX + ", " + newY + ")");
                continue;
            }

            if (moveAndHunt(currentCell, nearbyCell, island, x, y, newX, newY)) {
                break;
            }
        }
    }

    private boolean moveAndHunt(Cell currentCell, Cell nearbyCell, Island island, int currentX, int currentY, int newX, int newY) {
        moveToCell(currentCell, nearbyCell);
        island.log(this.getClass().getSimpleName() + " (ID: " + getId() + ") moved from (" + currentX + ", " + currentY + ") to (" + newX + ", " + newY + ") to hunt");

        List<Animal> nearbyPrey = findPrey(nearbyCell);
        return huntInCell(nearbyCell, island, newX, newY, nearbyPrey);
    }

    private int[][] getNearbyCoordinates(int x, int y) {
        int[] dx = {0, 0, -1, 1};
        int[] dy = {-1, 1, 0, 0};
        int[][] nearbyCoords = new int[4][2];
        for (int i = 0; i < 4; i++) {
            nearbyCoords[i][0] = x + dx[i];
            nearbyCoords[i][1] = y + dy[i];
        }
        return nearbyCoords;
    }


    private boolean huntInCell(Cell cell, Island island, int x, int y, List<Animal> prey) {
        Animal victim = prey.get(random.nextInt(prey.size()));
        if (consumeVictim(cell, victim)) {
            island.log(this.getClass().getSimpleName() + " (ID: " + getId() + ") ate " +
                    victim.getClass().getSimpleName() + " (ID: " + victim.getId() + ") at (" + x + ", " + y + ")");
            return true;
        }
        return false;
    }

    private boolean isOutOfBounds(Island island, int x, int y) {
        return x < 0 || x >= island.getHeight() || y < 0 || y >= island.getWidth();
    }

    private List<Animal> findPrey(Cell cell) {
        return cell.getAnimals().stream()
                .filter(a -> a.isAlive() && predationProbabilities.get(this.getClass().getSimpleName())
                        .containsKey(a.getClass().getSimpleName()))
                .toList();
    }

    private boolean consumeVictim(Cell cell, Animal victim) {
        eat(victim.getWeight() * 0.75);
        victim.setAlive(false);
        cell.removeAnimal(victim);
        return true;
    }

    private void moveToCell(Cell fromCell, Cell toCell) {
        fromCell.removeAnimal(this);
        toCell.addAnimal(this);
    }

    @Override
    public void move(Island island, int currentX, int currentY) {
        if (!isAlive() || speed == 0) return;

        if (!hasReproduced && tryMoveToPartner(island, currentX, currentY)) {
            return;
        }

        if (tryMoveToPrey(island, currentX, currentY)) {
            return;
        }

        moveRandomly(island, currentX, currentY);
    }

    private boolean tryMoveToPartner(Island island, int currentX, int currentY) {
        int[] partnerCoords = findPartnerCell(island, currentX, currentY);
        if (partnerCoords != null) {
            moveToCell(island.getCell(currentX, currentY), island.getCell(partnerCoords[0], partnerCoords[1]));
            island.log(this.getClass().getSimpleName() + " (ID: " + getId() + ") moved from (" + currentX + ", " + currentY + ") to (" + partnerCoords[0] + ", " + partnerCoords[1] + ") to find a partner");
            return true;
        }
        return false;
    }

    private boolean tryMoveToPrey(Island island, int currentX, int currentY) {
        int[] preyCoords = findPreyCell(island, currentX, currentY);
        if (preyCoords != null) {
            moveToCell(island.getCell(currentX, currentY), island.getCell(preyCoords[0], preyCoords[1]));
            island.log(this.getClass().getSimpleName() + " (ID: " + getId() + ") moved from (" + currentX + ", " + currentY + ") to (" + preyCoords[0] + ", " + preyCoords[1] + ") to hunt");
            return true;
        }
        return false;
    }

    private void moveRandomly(Island island, int currentX, int currentY) {
        int distance = random.nextInt(speed + 1);
        if (distance == 0) return;

        int[] dx = {0, 0, -1, 1};
        int[] dy = {-1, 1, 0, 0};
        int direction = random.nextInt(4);

        int newX = currentX + dx[direction] * distance;
        int newY = currentY + dy[direction] * distance;

        if (newX < 0 || newX >= island.getHeight() || newY < 0 || newY >= island.getWidth()) {
            return;
        }

        Cell currentCell = island.getCell(currentX, currentY);
        Cell newCell = island.getCell(newX, newY);
        if (newCell.canAddAnimal(this)) {
            moveToCell(currentCell, newCell);
            island.log(this.getClass().getSimpleName() + " (ID: " + getId() + ") randomly moved from (" + currentX + ", " + currentY + ") to (" + newX + ", " + newY + ") in search of prey");
        }
    }

    private int[] findPartnerCell(Island island, int currentX, int currentY) {
        int searchRadius = speed;
        int[] bounds = getSearchBounds(island, currentX, currentY, searchRadius);
        return findClosestPartnerCell(island, currentX, currentY, bounds[0], bounds[1], bounds[2], bounds[3]);
    }

    private int[] findClosestPartnerCell(Island island, int currentX, int currentY, int minX, int maxX, int minY, int maxY) {
        int bestX = -1;
        int bestY = -1;
        int minDistance = Integer.MAX_VALUE;

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                if (x == currentX && y == currentY) continue;

                Cell cell = island.getCell(x, y);
                if (hasPotentialPartner(cell)) {
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

    private boolean hasPotentialPartner(Cell cell) {
        return cell.getAnimals().stream()
                .anyMatch(a -> a.getClass() == this.getClass() && a.canReproduce() && !this.getRelatives().contains(a.getId()));
    }

    private int[] findPreyCell(Island island, int currentX, int currentY) {
        int searchRadius = speed;
        int[] bounds = getSearchBounds(island, currentX, currentY, searchRadius);
        return searchBestPreyCell(island, currentX, currentY, bounds[0], bounds[1], bounds[2], bounds[3]);
    }

    private int[] searchBestPreyCell(Island island, int currentX, int currentY, int minX, int maxX, int minY, int maxY) {
        int bestX = -1;
        int bestY = -1;
        int minDistance = Integer.MAX_VALUE;

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                if (x == currentX && y == currentY) continue;

                Cell cell = island.getCell(x, y);
                if (cell.canAddAnimal(this)) {
                    boolean hasPrey = cell.getAnimals().stream()
                            .anyMatch(a -> a.isAlive() && predationProbabilities.get(this.getClass().getSimpleName())
                                    .containsKey(a.getClass().getSimpleName()));

                    if (hasPrey) {
                        int distance = Math.abs(x - currentX) + Math.abs(y - currentY);
                        if (distance <= speed && distance < minDistance) {
                            minDistance = distance;
                            bestX = x;
                            bestY = y;
                        }
                    }
                }
            }
        }
        return (bestX != -1 && bestY != -1) ? new int[]{bestX, bestY} : null;
    }
}