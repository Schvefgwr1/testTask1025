import models.GameState;
import models.Tube;

import java.util.*;

/**
 * Тесты игровой логики "Вода и колбочки" на чистой Java.
 * Запуск через Main: java -cp out Main, затем ввести 2.
 */
public class GameTests {
    private int passed;
    private int failed;
    private int testNumber;

    /** Запуск всех тестов (вызывается из Main при выборе режима 2) */
    public void runTests() {
        passed = 0;
        failed = 0;
        testNumber = 0;
        System.out.println("\n=== Запуск тестов ===\n");

        testSimpleTwoColorGame();
        testThreeColorGame();
        testEmptyTubesHandling();
        testAlreadySolvedGame();
        testSingleTubeFullySorted();
        testTubePourOperation();
        testTubeSortedCheck();
        testGameStateHeuristic();
        testSolutionStepsValid();
        testComplexFourColorGame();

        System.out.println("\n=== Результаты ===");
        System.out.println("Пройдено: " + passed + ", Провалено: " + failed);
        System.out.println(failed == 0 ? "Все тесты пройдены!" : "Есть проваленные тесты.");
    }

    private void assertTrue(boolean condition, String testName, String message) {
        if (condition) {
            passed++;
            System.out.println("└─ PASS: " + message);
        } else {
            failed++;
            System.out.println("└─ FAIL: " + message);
        }
        System.out.println();
    }

    /** Создаёт пробирку из списка цветов (сверху вниз), 0 = пусто */
    private Tube createTube(List<Integer> colors, int maxSize) {
        Deque<Integer> stack = new ArrayDeque<>();
        for (int j = colors.size() - 1; j >= 0; j--) {
            int color = colors.get(j);
            if (color != 0) stack.addFirst(color);
        }
        return new Tube(stack, maxSize);
    }

    /** Создаёт начальное состояние из конфигурации пробирок */
    private GameState createGameState(int maxSize, List<List<Integer>> tubesConfig) {
        List<Tube> tubes = new ArrayList<>();
        for (List<Integer> config : tubesConfig) {
            tubes.add(createTube(config, maxSize));
        }
        return new GameState(tubes);
    }

    /** Печатает заголовок теста */
    private void printTestHeader(String testName) {
        testNumber++;
        System.out.println("┌─ Тест " + testNumber + ": " + testName);
    }

    /** Печатает входные данные: объём пробирок и конфигурация */
    private void printInput(String testName, int maxSize, List<List<Integer>> config) {
        printTestHeader(testName);
        System.out.println("│  Вход: объём=" + maxSize + ", пробирки: " + config);
    }

    /** Печатает входные данные для теста с одной пробиркой */
    private void printInput(String testName, List<Integer> tubeColors, int maxSize) {
        printTestHeader(testName);
        System.out.println("│  Вход: пробирка " + tubeColors + ", maxSize=" + maxSize);
    }

    /** Печатает входные данные для теста с двумя пробирками */
    private void printInput(String testName, List<Integer> from, List<Integer> to, int maxSize) {
        printTestHeader(testName);
        System.out.println("│  Вход: from=" + from + ", to=" + to + ", maxSize=" + maxSize);
    }

    /** Печатает заголовок и вход для теста с двумя конфигурациями игр */
    private void printInputTwoConfigs(String testName, List<List<Integer>> config1, List<List<Integer>> config2, int maxSize) {
        printTestHeader(testName);
        System.out.println("│  Вход: solved=" + config1 + ", mixed=" + config2 + ", maxSize=" + maxSize);
    }

    /** Проверяет, что состояние является целевым */
    private boolean isGoalState(GameState state) {
        Set<Integer> seenColors = new HashSet<>();
        for (Tube tube : state.getTubes()) {
            if (tube.isEmpty()) continue;
            if (!tube.isSorted()) return false;
            Integer color = tube.peekTopColor();
            if (seenColors.contains(color)) return false;
            seenColors.add(color);
        }
        return true;
    }

    private void testSimpleTwoColorGame() {
        String name = "testSimpleTwoColorGame";
        List<List<Integer>> config = Arrays.asList(
                Arrays.asList(1, 2),
                Arrays.asList(1, 2),
                Arrays.asList(0, 0)
        );
        printInput(name, 2, config);
        GameState initial = createGameState(2, config);
        List<GameState> solution = new MachineStates(initial).findSolution();
        boolean ok = solution != null && !solution.isEmpty() && isGoalState(solution.get(solution.size() - 1));
        assertTrue(ok, name, ok ? "Решение найдено за " + (solution.size() - 1) + " шагов" : "Решение не найдено");
    }

    private void testThreeColorGame() {
        String name = "testThreeColorGame";
        List<List<Integer>> config = Arrays.asList(
                Arrays.asList(1, 2, 3),
                Arrays.asList(1, 2, 3),
                Arrays.asList(1, 2, 3),
                Arrays.asList(0, 0, 0),
                Arrays.asList(0, 0, 0)
        );
        printInput(name, 3, config);
        GameState initial = createGameState(3, config);
        List<GameState> solution = new MachineStates(initial).findSolution();
        boolean ok = solution != null && !solution.isEmpty() && isGoalState(solution.get(solution.size() - 1));
        assertTrue(ok, name, ok ? "Решение найдено за " + (solution.size() - 1) + " шагов" : "Решение не найдено");
    }

    private void testEmptyTubesHandling() {
        String name = "testEmptyTubesHandling";
        List<List<Integer>> config = Arrays.asList(
                Arrays.asList(1, 1),
                Arrays.asList(0, 0),
                Arrays.asList(0, 0)
        );
        printInput(name, 2, config);
        GameState initial = createGameState(2, config);
        List<GameState> solution = new MachineStates(initial).findSolution();
        boolean ok = solution != null && !solution.isEmpty() && isGoalState(solution.get(solution.size() - 1));
        assertTrue(ok, name, ok ? "Корректная работа с пустыми пробирками" : "Ошибка при пустых пробирках");
    }

    private void testAlreadySolvedGame() {
        String name = "testAlreadySolvedGame";
        List<List<Integer>> config = Arrays.asList(
                Arrays.asList(1, 1),
                Arrays.asList(2, 2),
                Arrays.asList(0, 0)
        );
        printInput(name, 2, config);
        GameState initial = createGameState(2, config);
        List<GameState> solution = new MachineStates(initial).findSolution();
        boolean ok = solution != null && solution.size() == 1; // Только начальное состояние, ходов нет
        assertTrue(ok, name, ok ? "Уже решённая игра: 0 ходов" : "Неверный результат для решённой игры");
    }

    private void testSingleTubeFullySorted() {
        String name = "testSingleTubeFullySorted";
        printInput(name, Arrays.asList(1, 1, 1), 3);
        Tube tube = createTube(Arrays.asList(1, 1, 1), 3);
        boolean ok = tube.isSorted() && tube.isFull();
        assertTrue(ok, name, ok ? "Полная отсортированная пробирка распознана" : "Ошибка isSorted/isFull");
    }

    private void testTubePourOperation() {
        String name = "testTubePourOperation";
        printInput(name, Arrays.asList(2, 2), Arrays.asList(0, 0, 0), 3);
        Tube from = createTube(Arrays.asList(2, 2), 3);
        Tube to = createTube(Arrays.asList(0, 0, 0), 3);
        to.pourAllOfColorFrom(from);
        boolean ok = from.isEmpty() && to.getElements().size() == 2;
        assertTrue(ok, name, ok ? "Переливание работает" : "Ошибка переливания");
    }

    private void testTubeSortedCheck() {
        String name = "testTubeSortedCheck";
        printTestHeader(name);
        System.out.println("│  Вход: sorted=" + Arrays.asList(2, 2, 2) + ", mixed=" + Arrays.asList(1, 2, 1) + ", maxSize=3");
        Tube sorted = createTube(Arrays.asList(2, 2, 2), 3);
        Tube mixed = createTube(Arrays.asList(1, 2, 1), 3);
        boolean ok = sorted.isSorted() && !mixed.isSorted();
        assertTrue(ok, name, ok ? "isSorted() работает корректно" : "Ошибка isSorted()");
    }

    private void testGameStateHeuristic() {
        String name = "testGameStateHeuristic";
        List<List<Integer>> configSolved = Arrays.asList(
                Arrays.asList(1, 1),
                Arrays.asList(2, 2),
                Arrays.asList(0, 0)
        );
        List<List<Integer>> configMixed = Arrays.asList(
                Arrays.asList(1, 2),
                Arrays.asList(1, 2),
                Arrays.asList(0, 0)
        );
        printInputTwoConfigs(name, configSolved, configMixed, 2);
        GameState solved = createGameState(2, configSolved);
        GameState mixed = createGameState(2, configMixed);
        int hSolved = solved.calculateHeuristic();
        int hMixed = mixed.calculateHeuristic();
        boolean ok = hSolved < hMixed; // Решённое состояние должно иметь меньшую эвристику
        assertTrue(ok, name, ok ? "Эвристика: решённое=" + hSolved + ", смешанное=" + hMixed : "Эвристика некорректна");
    }

    private void testSolutionStepsValid() {
        String name = "testSolutionStepsValid";
        List<List<Integer>> config = Arrays.asList(
                Arrays.asList(1, 2),
                Arrays.asList(1, 2),
                Arrays.asList(0, 0)
        );
        printInput(name, 2, config);
        GameState initial = createGameState(2, config);
        List<GameState> solution = new MachineStates(initial).findSolution();
        if (solution == null || solution.size() < 2) {
            assertTrue(false, name, "Решение не найдено или слишком короткое");
            return;
        }
        boolean valid = true;
        for (int i = 1; i < solution.size(); i++) {
            GameState prev = solution.get(i - 1);
            GameState curr = solution.get(i);
            if (curr.getFromIndex() < 0 || curr.getToIndex() < 0) {
                valid = false;
                break;
            }
        }
        assertTrue(valid, name, valid ? "Все шаги решения валидны" : "Найден невалидный шаг");
    }

    private void testComplexFourColorGame() {
        String name = "testComplexFourColorGame";
        List<List<Integer>> config = Arrays.asList(
                Arrays.asList(1, 2, 3, 4),
                Arrays.asList(1, 2, 3, 4),
                Arrays.asList(1, 2, 3, 4),
                Arrays.asList(1, 2, 3, 4),
                Arrays.asList(0, 0, 0, 0),
                Arrays.asList(0, 0, 0, 0)
        );
        printInput(name, 4, config);
        GameState initial = createGameState(4, config);
        List<GameState> solution = new MachineStates(initial).findSolution();
        boolean ok = solution != null && !solution.isEmpty() && isGoalState(solution.get(solution.size() - 1));
        assertTrue(ok, name, ok ? "Сложная игра решена за " + (solution.size() - 1) + " шагов" : "Решение не найдено");
    }
}
