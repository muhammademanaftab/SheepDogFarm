package sheepdogfarm;

import java.util.concurrent.ThreadLocalRandom;

public class Dog extends FarmObject implements Runnable {

    private static final ThreadLocalRandom randgen = ThreadLocalRandom.current();
    private final String name;

    public Dog(String name, Farm farm) {
        this.name = name;
        putDog(farm);
    }
    //dog ko farm ke zone main place krne ke liay method
    private void putDog(Farm farm) {
        int innerRows = farm.getRows() - 2;
        int innerCols = farm.getCols() - 2;

        int startRow, startCol;

        do {
            int zone = randgen.nextInt(1, 10); 
            while (zone == 5) { 
                zone = randgen.nextInt(1,10);
            }

            if (zone <= 3) { 
                startRow = randgen.nextInt(1,innerRows/3 + 1);
                startCol = randgen.nextInt((zone - 1)* (innerCols/ 3) + 1, zone *(innerCols/ 3) + 1);
            } else if (zone <= 6) { 
                startRow = randgen.nextInt(innerRows /3 + 1, 2* (innerRows / 3) + 1);
                if (zone == 4) { 
                    startCol = randgen.nextInt(1, innerCols / 3+ 1);
                } else { 
                    startCol = randgen.nextInt(2 * (innerCols /3) + 1, innerCols +1);
                }
            } else { 
                startRow = randgen.nextInt(2*(innerRows / 3) +1, innerRows + 1);
                startCol = randgen.nextInt((zone - 7) * (innerCols / 3) + 1,(zone -6)*(innerCols/3) + 1);
            }

            String key = farm.getKey(startRow,startCol);
            Cell cell = farm.getMap().get(key);

            if (cell != null && cell.setOccupied(this)) {
                setPosition(startRow, startCol);
                break;
            }
        } while (true); 
    }

    
    //run metohd ko thread ke liay override krne ke liay
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
    
    
//    thread ka move method override jo farmobject se arha ha 
    @Override
    public void move(Farm farm) {
        int currentRow = getRow();
        int currentCol = getCol();
        int[] movement = checkMovement();

        int newRow = currentRow + movement[0];
        int newCol = currentCol + movement[1];

        String recentKey = farm.getKey(currentRow, currentCol);
        String targetKey = farm.getKey(newRow, newCol);

        Cell recentCell = farm.getMap().get(recentKey);
        Cell targetCell = farm.getMap().get(targetKey);

        if (recentCell == null || targetCell == null) {
            return; 
        }

        synchronized (recentCell) {
            synchronized (targetCell) {
                if ( moveValid(farm, newRow, newCol) && targetCell.setOccupied(this)) {
                    recentCell.setEmpty();
                    setPosition(newRow, newCol);
                }
            }
        }
    }

    //movement valid check krne ke liay takay zone 5 main jaye 
    private boolean moveValid(Farm farm, int newRow, int newCol) {
        return newRow > 0 && newRow < farm.getRows() - 1 && newCol > 0 && newCol < farm.getCols() - 1 && !(newRow >= farm.getRows() / 3 && newRow < 2 * farm.getRows() / 3 && newCol >= farm.getCols() / 3 && newCol < 2 * farm.getCols() / 3);

    }

    //move krwanay ke liay random moves lene ke liay
    private int[] checkMovement() {
        int dx, dy;
        do {
            dx = randgen.nextInt(-1, 2); 
            dy = randgen.nextInt(-1, 2);
        } while (dx == 0 && dy == 0);

        return new int[]{dy, dx};
    }

    @Override
    public String toString() {
        return name; 
    }
}
