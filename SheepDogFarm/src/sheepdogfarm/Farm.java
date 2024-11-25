package sheepdogfarm;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class Farm {

    private final int rows;
    private final int cols;
    //pehle normal map lenege then isko concurrent hashmpa main badal denge
    private final Map<String, Cell> map;

    //map ka getter
    public Map<String, Cell> getMap() {
        return map;
    }

    public Farm(int rows, int cols) {
        if ((rows - 2) % 3 == 1 || (cols - 2) % 3 == 1 || (rows - 2) % 3 == 2 || (cols - 2) % 3 == 2) {
            throw new IllegalArgumentException("Rows and columns must fit into groups of three plus two.");
        }

        this.rows = rows;
        this.cols = cols;
        this.map = new ConcurrentHashMap<>(rows * cols);
        madeFarm();
    }

    //farm initialization ke liay
    private void madeFarm() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                String key = getKey(i, j);
                Cell cell = new Cell();

                if (i == 0 || i == rows - 1 || j == 0 || j == cols - 1) {
                    map.put(key, cell);
                    cell.setOccupied(new Wall());
                } else {
                    map.put(key, cell);
                }
            }
        }

        fixGates();
    }

    //gates ko place krne liay
    private void fixGates() {
        placeGate(0, ThreadLocalRandom.current().nextInt(1, cols - 1));
        placeGate(rows - 1, ThreadLocalRandom.current().nextInt(1, cols - 1));
        placeGate(ThreadLocalRandom.current().nextInt(1, rows - 1), 0);
        placeGate(ThreadLocalRandom.current().nextInt(1, rows - 1), cols - 1);
    }

    //gates ko place krne liay takay randamom positon pr jaih
    private void placeGate(int row, int col) {
        String key = getKey(row, col);
        Cell cell = map.get(key);
        if (cell != null) {
            cell.setEmpty();
            cell.setAsGate();
        }
    }

    //hashmap kee key lene ke liay method jo ke rows and cols se aygeee
    public String getKey(int row, int col) {
        return row + "," + col;
    }
    
    
//    valid move check krne liay takay wall pr na jaye
    public boolean isMoveValid(FarmObject obj, int newRow, int newCol) {
        if (newRow < 0 || newCol < 0 || newRow >= rows || newCol >= cols) {
            return false;
        }

        String newKey = getKey(newRow, newCol);
        Cell targetCell = map.get(newKey);

        return (targetCell != null && !targetCell.isOccupied());
    }

    //move kee validity check krne ke baad move perform krne ke liay
    public void performMove(FarmObject obj, int newRow, int newCol) {
        if (!isMoveValid(obj, newRow, newCol)) {
            throw new IllegalArgumentException("Invalid move attempted by objects...");
        }

        String recentKey = getKey(obj.getRow(), obj.getCol());
        String targetKey = getKey(newRow, newCol);

        Cell recentCell = map.get(recentKey);
        Cell targetCell = map.get(targetKey);

        synchronized (recentCell) {
            synchronized (targetCell) {
                if (!targetCell.isOccupied()) {
                    recentCell.setEmpty();
                    targetCell.setOccupied(obj);
                    obj.setPosition(newRow, newCol);
                }
            }
        }
    }

//    gate check krne ke liay condition
    public boolean gateCheck(int row, int col) {
        String key = getKey(row, col);
        Cell cell = map.get(key);
        return cell != null && cell.isGate();
    }

    public synchronized void printFarm() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                String key = getKey(i, j);
                Cell cell = map.get(key);
                System.out.print(cell + " ");
            }
            System.out.println();
        }
    }

    public String getZone(int row, int col) {
        if (row == 0 || row == rows - 1 || col == 0 || col == cols - 1) {
            return "Wall";
        }

        int innerRow = row - 1;
        int innerCol = col - 1;
        int zoneRow = innerRow / ((rows - 2) / 3);
        int zoneCol = innerCol / ((cols - 2) / 3);

        return "Zone - " + (zoneRow * 3 + zoneCol + 1);
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }
}
