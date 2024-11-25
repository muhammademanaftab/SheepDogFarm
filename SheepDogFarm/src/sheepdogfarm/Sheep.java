package sheepdogfarm;

import java.util.concurrent.ThreadLocalRandom;

public class Sheep extends FarmObject implements Runnable {

    private final String name;
    private static final ThreadLocalRandom randgen = ThreadLocalRandom.current();

    public Sheep(String name, Farm farm) {
        this.name = name;
        putSheep(farm);
    }
// similar to dog placement but only in zone 5
    private void putSheep(Farm farm) {
        
        //zoen calculation
        int innerRows = farm.getRows() - 2;
        int innerCols = farm.getCols() - 2;

        int zoneStartRow = innerRows / 3 + 1;
        int zoneEndRow = 2 * (innerRows / 3);
        int zoneStartCol = innerCols / 3 + 1;
        int zoneEndCol = 2 * (innerCols / 3);

        int startRow, startCol;
        do {
            startRow = randgen.nextInt(zoneStartRow, zoneEndRow + 1);
            startCol = randgen.nextInt(zoneStartCol, zoneEndCol + 1);

            String key = farm.getKey(startRow, startCol);
            Cell cell = farm.getMap().get(key);

            if (cell != null && cell.setOccupied(this)) {
                setPosition(startRow, startCol);
                break;
            }
        } while (true); 
    }

    @Override
    public void run() {
        while (FarmSimulation.isSimulationRunning()) {
            try {
                Thread.sleep(200);
                move(FarmSimulation.farm);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    @Override
    public void move(Farm farm) {
        int currentRow = getRow();
        int currentCol = getCol();
        int[] movement = findMovement(farm, currentRow, currentCol);

        int newRow = currentRow + movement[0];
        int newCol = currentCol + movement[1];

        String recentKey = farm.getKey(currentRow, currentCol);
        String targetKey = farm.getKey(newRow, newCol);

        Cell recentCell = farm.getMap().get(recentKey);
        Cell targetCell = farm.getMap().get(targetKey);

        if (targetCell == null || recentCell == null) {
            return; 
        }

        synchronized (recentCell) {
            synchronized (targetCell) {
                if (targetCell.setOccupied(this)) {
                    recentCell.setEmpty();
                    setPosition(newRow, newCol);

                    if (farm.gateCheck(newRow, newCol)) {
                        FarmSimulation.stopSimulation();
                        System.out.println("Sheep " + name + " has escaped at (" + newRow + ", " + newCol + ")!");
                    }
                }
            }
        }
    }

    //movement check krne ke liay takay dog se dur rhay
    private int[] findMovement(Farm farm, int currentRow, int currentCol) {
        boolean dogUp = DogSurrounding(farm, currentRow - 1, currentCol);
        boolean dogDown = DogSurrounding(farm, currentRow + 1, currentCol);
        boolean dogLeft = DogSurrounding(farm, currentRow, currentCol - 1);
        boolean dogRight = DogSurrounding(farm, currentRow, currentCol + 1);

        int change_x = 0;
        int change_y = 0;

        if (dogUp) {
            change_y = 1;
        } else if (dogDown) {
            change_y = -1;
        }
        if (dogLeft) {
            change_x = 1;
        } else if (dogRight) {
            change_x = -1;
        }

        if (change_x == 0 && change_y == 0) {
            change_x = randgen.nextInt(-1, 2);
            change_y = randgen.nextInt(-1, 2);
        }

        while (change_x == 0 && change_y == 0) {
            change_x = randgen.nextInt(-1, 2);
            change_y = randgen.nextInt(-1, 2);
        }

        return new int[]{change_y, change_x};
    }

    private boolean DogSurrounding(Farm farm, int row, int col) {
        String key = farm.getKey(row, col);
        Cell cell = farm.getMap().get(key);
        return cell != null && cell.getOccupant() instanceof Dog;
    }

    @Override
    public String toString() {
        return name;
    }
}
