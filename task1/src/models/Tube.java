package models;

import java.util.*;

public class Tube {
    private final Deque<Integer> elements;
    private final int maxSize;
    private int currentSize;

    public Tube(Deque<Integer> elements, int maxSize) {
        this.maxSize = maxSize;
        this.elements = new ArrayDeque<>(elements);
        this.currentSize = elements.size();
    }

    public boolean isEmpty() { return currentSize == 0; }
    public boolean isFull() { return currentSize >= maxSize; }

    public Deque<Integer> getElements() { return elements; }

    public Integer peekTopColor() {
        return isEmpty() ? null : elements.peekFirst();
    }

    public boolean canNotPourFrom(Tube from) {
        if (this.isFull() || from.isEmpty()) return true;
        Integer colorFrom = from.peekTopColor();
        Integer colorTo = this.peekTopColor();
        return !this.isEmpty() && !colorFrom.equals(colorTo);
    }

    /** Считает количество верхних капель одного цвета */
    public int countTopColorGroup() {
        if (isEmpty()) return 0;
        Integer topColor = peekTopColor();
        int count = 0;
        for (Integer color : elements) {
            if (color.equals(topColor)) {
                count++;
            } else {
                break;
            }
        }
        return count;
    }

    /** Переливает все капли одного цвета (столько, сколько возможно) */
    public void pourAllOfColorFrom(Tube from) {
        if (canNotPourFrom(from)) return;

        int availableToPour = from.countTopColorGroup();
        int spaceAvailable = this.maxSize - this.currentSize;
        int toPour = Math.min(availableToPour, spaceAvailable);
        
        for (int i = 0; i < toPour; i++) {
            Integer color = from.elements.removeFirst();
            from.currentSize--;
            this.elements.addFirst(color);
            this.currentSize++;
        }
    }

    /** Проверяет, что пробирка отсортирована (все капли одного цвета) */
    public boolean isSorted() {
        if (isEmpty()) return true;
        Integer firstColor = peekTopColor();
        for (Integer color : elements) {
            if (!color.equals(firstColor)) return false;
        }
        return true;
    }

    public Tube copy() {
        return new Tube(new ArrayDeque<>(this.elements), this.maxSize);
    }

    @Override
    public String toString() {
        return elements.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Tube)) return false;
        Tube other = (Tube) obj;
        if (this.currentSize != other.currentSize) return false;
        Iterator<Integer> elIt = this.elements.iterator();
        Iterator<Integer> otherIt = other.elements.iterator();
        while (elIt.hasNext() && otherIt.hasNext()) {
            if (!Objects.equals(elIt.next(), otherIt.next())) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 1;
        for (Integer e : elements) {
            hash = 31 * hash + (e == null ? 0 : e.hashCode());
        }
        hash = 31 * hash + currentSize;
        return hash;
    }
}
