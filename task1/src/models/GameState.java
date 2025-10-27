package models;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    private final List<Tube> tubes;
    private final GameState parent;

    // откуда и куда было перелито при переходе к этому состоянию
    private final int fromIndex;
    private final int toIndex;
    
    // глубина (количество шагов от начального состояния)
    private final int depth;

    public GameState(List<Tube> tubes, GameState parent, int fromIndex, int toIndex) {
        this.tubes = tubes;
        this.parent = parent;
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
        this.depth = parent != null ? parent.depth + 1 : 0;
    }

    public GameState(List<Tube> tubes) {
        this.tubes = tubes;
        this.parent = null;
        this.fromIndex = -1;
        this.toIndex = -1;
        this.depth = 0;
    }
    
    public int getDepth() {
        return depth;
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

            // ФИЛЬТР 1: Не трогаем уже отсортированные полные пробирки
            if (from.isFull() && from.isSorted()) {
                continue;
            }

            for (int j = 0; j < tubes.size(); j++) {
                if (i == j) continue;
                Tube to = tubes.get(j);
                if (to.isFull()) continue;

                Integer colorFrom = from.peekTopColor();
                Integer colorTo = to.peekTopColor();

                // Проверяем базовые условия переливания
                if (to.isEmpty() || colorFrom.equals(colorTo)) {

                    // ФИЛЬТР 2: Не делаем обратный ход
                    if (parent != null && parent.fromIndex == j && parent.toIndex == i) {
                        continue;
                    }

                    // ФИЛЬТР 3: Не переливаем всё содержимое пробирки в пустую
                    if (to.isEmpty() && from.isSorted()) {
                        continue;
                    }

                    // ФИЛЬТР 4: Не переливаем из отсортированной непустой в пустую, если цвет уже присутствует в другой пробирке
                    if (to.isEmpty() && from.isSorted() && !from.isFull()) {
                        boolean colorExistsElsewhere = false;
                        for (int k = 0; k < tubes.size(); k++) {
                            if (k == i || tubes.get(k).isEmpty()) continue;
                            if (colorFrom.equals(tubes.get(k).peekTopColor())) {
                                colorExistsElsewhere = true;
                                break;
                            }
                        }
                        if (colorExistsElsewhere) continue;
                    }

                    List<Tube> newTubes = copyTubes();
                    Tube newFrom = newTubes.get(i);
                    Tube newTo = newTubes.get(j);

                    if (newTo.canNotPourFrom(newFrom)) continue;

                    newTo.pourAllOfColorFrom(newFrom);

                    if (newFrom.equals(from) && newTo.equals(to)) continue;

                    // Добавляем новое состояние, указывая действие i -> j
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
    
    /**
     * Эвристическая функция: оценка "расстояния" до целевого состояния.
     * Чем меньше значение, тем ближе к решению.
     */
    public int calculateHeuristic() {
        int penalty = 0;
        
        for (Tube tube : tubes) {
            if (tube.isEmpty()) {
                // Пустая пробирка - это хорошо (ничего не добавляем)
                continue;
            }
            
            if (tube.isSorted()) {
                // Если не полная - небольшой штраф, чтобы стимулировать заполнение
                if (!tube.isFull()) {
                    penalty += 1;
                }
            } else {
                // Пробирка смешана - считаем количество "переходов" между цветами
                int transitions = countColorTransitions(tube);
                // Каждый переход - это проблема, которую нужно решить
                penalty += transitions * 3;
                
                // Дополнительный штраф за смешанную пробирку
                penalty += 5;
            }
        }
        
        return penalty;
    }
    
    /**
     * Считает количество переходов между разными цветами в пробирке.
     */
    private int countColorTransitions(Tube tube) {
        if (tube.isEmpty()) return 0;
        
        List<Integer> elements = new ArrayList<>(tube.getElements());
        if (elements.size() <= 1) return 0;
        
        int transitions = 0;
        Integer prevColor = elements.get(0);
        for (int i = 1; i < elements.size(); i++) {
            Integer currentColor = elements.get(i);
            if (!currentColor.equals(prevColor)) {
                transitions++;
                prevColor = currentColor;
            }
        }
        return transitions;
    }
    
    /**
     * Общая стоимость для A*
     */
    public int getTotalCost() {
        return depth + calculateHeuristic();
    }
}
