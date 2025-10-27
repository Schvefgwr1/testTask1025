import models.*;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("Введите количество пробирок (n):");
        int n = sc.nextInt();
        System.out.println("Введите объем каждой пробирки (v):");
        int v = sc.nextInt();

        List<Tube> tubes = new ArrayList<>();

        System.out.println("Введите содержимое пробирок (по строкам, сверху вниз).");

        for (int i = 0; i < n; i++) {
            Deque<Integer> stack = new ArrayDeque<>();
            List<Integer> colors = new ArrayList<>();
            for (int j = 0; j < v; j++) {
                colors.add(sc.nextInt());
            }
            // Добавляем сверху вниз (по порядку ввода)
            for (int j = colors.size() - 1; j >= 0; j--) {
                int color = colors.get(j);
                if (color != 0) stack.addFirst(color);
            }
            tubes.add(new Tube(stack, v));
        }

        GameState initialState = new GameState(tubes);
        MachineStates machine = new MachineStates(initialState);

        List<GameState> solution = machine.findSolution();

        if (solution == null) {
            System.out.println("Решение не найдено.");
        } else {
            System.out.println("\nНайдено решение! Последовательность переливаний:");
            printSolutionPath(solution);
        }
    }

    /** Печатает путь (последовательность переливаний) от начального состояния к решению */
    private static void printSolutionPath(List<GameState> path) {
        for (int i = 1; i < path.size(); i++) {
            GameState step = path.get(i);
            System.out.printf("(%d, %d)\n", step.getFromIndex() + 1, step.getToIndex() + 1);
        }
    }
}
