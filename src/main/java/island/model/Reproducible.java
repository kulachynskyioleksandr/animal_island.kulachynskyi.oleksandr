package island.model;

public interface Reproducible {
    void reproduce(Island island, int x, int y);
    boolean canReproduce();
}