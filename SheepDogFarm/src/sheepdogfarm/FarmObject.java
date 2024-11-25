package sheepdogfarm;

import java.util.concurrent.atomic.AtomicInteger;

//farm object jo ke parent hoga dusri classes kaa like wall,sheeep , dog
public abstract class FarmObject {

    private final AtomicInteger row = new AtomicInteger();
    private final AtomicInteger col = new AtomicInteger();

    public int getRow() {
        return row.get();
    }

    public int getCol() {
        return col.get();
    }

    public boolean setPosition(int newRow, int newCol) {
        int oldRow = row.get();
        int oldCol = col.get();
        return compareAndSetPosition(oldRow, oldCol, newRow, newCol);
    }

    public boolean compareAndSetPosition(int expectedRow, int expectedCol, int newRow, int newCol) {
        return (row.compareAndSet(expectedRow, newRow) && col.compareAndSet(expectedCol, newCol));
    }

    public abstract void move(Farm farm);
}
