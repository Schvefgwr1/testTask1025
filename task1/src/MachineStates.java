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
        GameState goal = bfs(initialState);
        if (goal == null) {
            System.out.println("\nРешение не найдено.");
            return null;
        }
        System.out.println("\nРешение найдено!");
        return reconstructPath(goal);
    }

    /** Поиск в ширину (BFS) */
    private GameState bfs(GameState start) {
        Queue<GameState> queue = new LinkedList<>();
        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            GameState current = queue.poll();
            //System.out.println(current);

            if (isGoal(current)) {
                return current; // нашли решение
            }

            for (GameState child : current.createChildren()) {
                if (!visited.contains(child)) {
                    visited.add(child);
                    queue.add(child);
                }
            }
        }
        return null; // решения нет
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
