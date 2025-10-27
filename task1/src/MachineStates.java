import models.GameState;
import models.Tube;

import java.util.*;

public class MachineStates {
    private final GameState initialState;
    private final Set<GameState> visited = new HashSet<>();

    public MachineStates(GameState initialState) {
        this.initialState = initialState;
    }

    /** Главный метод поиска решения */
    public List<GameState> findSolution() {
        GameState goal = aStar(initialState);
        if (goal == null) {
            System.out.println("\nРешение не найдено.");
            return null;
        }
        System.out.println("\nРешение найдено!");
        return reconstructPath(goal);
    }

    /** Алгоритм A* - поиск с эвристикой */
    private GameState aStar(GameState start) {
        PriorityQueue<GameState> openSet = new PriorityQueue<>(
            Comparator.comparingInt(GameState::getTotalCost)
        );
        
        openSet.add(start);
        visited.add(start);
        
        int statesExplored = 0;
        int lastReported = 0;

        while (!openSet.isEmpty()) {
            GameState current = openSet.poll();
            statesExplored++;
            
            // Периодически выводим прогресс
            if (statesExplored / 10 > lastReported) {
                lastReported = statesExplored / 10;
                System.out.printf("Исследовано состояний: %d, глубина: %d, эвристика: %d, очередь: %d\n", 
                    statesExplored, current.getDepth(), current.calculateHeuristic(), openSet.size());
            }

            if (isGoal(current)) {
                return current;
            }

            for (GameState child : current.createChildren()) {
                if (!visited.contains(child)) {
                    visited.add(child);
                    openSet.add(child);
                }
            }
        }
        return null;
    }

    /** Проверка, достигнуто ли целевое состояние */
    private boolean isGoal(GameState state) {
        Set<Integer> seenColors = new HashSet<>();

        for (Tube tube : state.getTubes()) {
            if (tube.isEmpty()) continue;

            Integer color = tube.peekTopColor();
            // все элементы в пробирке должны быть одного цвета
            for (Integer c : tube.getElements()) {
                if (!Objects.equals(c, color)) return false;
            }

            // один и тот же цвет не должен появляться в нескольких пробирках
            if (seenColors.contains(color)) return false;
            seenColors.add(color);
        }
        return true;
    }

    /** Восстанавливает путь от начального состояния к найденному решению */
    private List<GameState> reconstructPath(GameState goal) {
        List<GameState> path = new ArrayList<>();
        for (GameState s = goal; s != null; s = s.getParent()) {
            path.add(s);
        }
        Collections.reverse(path);
        return path;
    }
}
