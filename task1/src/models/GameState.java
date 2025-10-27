package models;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    private final List<Tube> tubes;
    private final GameState parent;

    // откуда и куда было перелито при переходе к этому состоянию
    private final int fromIndex;
    private final int toIndex;

    public GameState(List<Tube> tubes, GameState parent, int fromIndex, int toIndex) {
        this.tubes = tubes;
        this.parent = parent;
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
    }

    public GameState(List<Tube> tubes) {
        this.tubes = tubes;
        this.parent = null;
        this.fromIndex = -1;
        this.toIndex = -1;
    }

    public List<Tube> getTubes() {
        return tubes;
    }

    public GameState getParent() {
        return parent;
    }

    public int getFromIndex() {
        return fromIndex;
    }

    public int getToIndex() {
        return toIndex;
    }

    /** Глубокая копия всех пробирок */
    private List<Tube> copyTubes() {
        List<Tube> result = new ArrayList<>();
        for (Tube t : tubes) {
            result.add(t.copy());
        }
        return result;
    }

    /** Создание всех возможных детей из текущего состояния */
    public List<GameState> createChildren() {
        List<GameState> children = new ArrayList<>();

        for (int i = 0; i < tubes.size(); i++) {
            Tube from = tubes.get(i);
            if (from.isEmpty()) continue;

            for (int j = 0; j < tubes.size(); j++) {
                if (i == j) continue;
                Tube to = tubes.get(j);
                if (to.isFull()) continue;

                Integer colorFrom = from.peekTopColor();
                Integer colorTo = to.peekTopColor();

                // Проверяем базовые условия переливания
                if (to.isEmpty() || colorFrom.equals(colorTo)) {

                    if (parent != null && parent.fromIndex == j && parent.toIndex == i) {
                        continue;
                    }

                    List<Tube> newTubes = copyTubes();
                    Tube newFrom = newTubes.get(i);
                    Tube newTo = newTubes.get(j);

                    if (!newTo.canPourFrom(newFrom)) continue;

                    newTo.pourOneFrom(newFrom);

                    if (newFrom.equals(from) && newTo.equals(to)) continue;

                    // Добавляем новое состояние, указывая действие i→j
                    children.add(new GameState(newTubes, this, i, j));
                }
            }
        }
        return children;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof GameState) {
            GameState other = (GameState) obj;
            if (this.tubes.size() != other.tubes.size()) return false;
            for(int i = 0; i < tubes.size(); i++) {
                if(!tubes.get(i).equals(other.tubes.get(i))) return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 1;
        for (Tube t : tubes) {
            hash = 31 * hash + (t == null ? 0 : t.hashCode());
        }
        return hash;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int index = 0;
        for (Tube t : tubes) {
            sb.append("Tube ").append(index++).append(": ").append(t).append("\n");
        }
        return sb.toString();
    }
}
