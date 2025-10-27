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

    public Integer peekTop() { return elements.peekFirst(); }

    public Deque<Integer> getElements() { return elements; }

    public Integer peekTopColor() { return isEmpty() ? null : peekTop(); }

    public boolean canPourFrom(Tube from) {
        if (this.isFull() || from.isEmpty()) return false;
        Integer colorFrom = from.peekTopColor();
        Integer colorTo = this.peekTopColor();
        return this.isEmpty() || colorFrom.equals(colorTo);
    }

    public void pourOneFrom(Tube from) {
        Integer color = from.elements.removeFirst();
        from.currentSize--;
        this.elements.addFirst(color);
        this.currentSize++;
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
