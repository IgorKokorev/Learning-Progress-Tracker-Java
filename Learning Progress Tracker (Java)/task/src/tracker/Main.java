package tracker;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Main {
    static Scanner scanner = new Scanner(System.in);
    static final int NUM_COURSES = 4;
    static Course[] courses = new Course[]{
            new Course("Java", 600),
            new Course("DSA", 400),
            new Course("Databases", 480),
            new Course("Spring", 550)
    };
    static final List<String> coursesNames = Arrays.asList("java", "dsa", "databases", "spring");
    static List<Student> students = new LinkedList<>();

    public static void main(String[] args) {
        menu();
    }

    private static void menu() {
        System.out.println("Learning Progress Tracker");
        while (true) {
            String input = scanner.nextLine().toLowerCase().trim();
            if (input.equals("exit")) break;
            switch (input) {
                case "" -> System.out.println("No input.");
                case "back" -> System.out.println("Enter 'exit' to exit the program");
                case "add students" -> addStudents();
                case "list" -> listStudents();
                case "add points" -> addPoints();
                case "find" -> findStudent();
                case "statistics" -> getStatistics();
                case "notify" -> notifyStudents();
                default -> System.out.println("Error: unknown command!");
            }
        }
        System.out.println("Bye!");
    }

    private static void notifyStudents() {
        Set<Student> notified = new HashSet<>();

        for (Course course: courses) {
            ArrayList<Student> finished = new ArrayList<>(course.getStudentsTotalScores().entrySet().stream()
                    .filter(e -> e.getValue() >= course.getMaxPoints())
                    .map(Map.Entry::getKey)
                    .toList());
            finished.removeAll(course.getNotifiedStudents());

            for (Student student: finished) {
                course.notifyStudent(student);
                notified.add(student);
            }
        }

        System.out.println("Total " + notified.size() + " students have been notified.");
    }

    private static void getStatistics() {
        System.out.println("Type the name of a course to see details or 'back' to quit:");
        printStatistics();

        while (true) {
            String input = scanner.nextLine().toLowerCase().trim();
            if (input.equals("back")) break;

            int index = coursesNames.indexOf(input);

            if (index < 0) {
                System.out.println("Unknown course.");
                continue;
            }

            printCourseInfo(courses[index]);
        }
    }

    private static void printCourseInfo(Course course) {
        System.out.println(course.getName());
        System.out.println("id\tpoints\tcompleted");

        Map<Student, Integer> students =  course.getStudentsTotalScores();
        for (Student st: students.keySet()) {
            System.out.print(st.getId() + "\t");
            System.out.print(students.get(st) + "\t");
            DecimalFormat df = new DecimalFormat("#0.0");
            df.setRoundingMode(RoundingMode.HALF_UP);
            System.out.println(df.format(((double) students.get(st)) * 100 / course.getMaxPoints()) + "%");
        }
    }

    private static void printStatistics() {
        printOneStat("Most popular: ", "Least popular: ", Course::getNumberOfStudents);
        printOneStat("Highest activity: ", "Lowest activity: ", Course::getNumberOfSubmissions);
        printOneStat("Easiest course: ", "Hardest course: ", Course::getAverageScore);
    }

    private static void printOneStat(String lineMax, String lineMin, Function<Course, Integer> fn) {
        int[] evaluation = new int[NUM_COURSES];
        evaluation[0] = fn.apply(courses[0]);
        int max = evaluation[0];
        int min = max;

        for (int i = 1; i < NUM_COURSES; i++) {
            evaluation[i] = fn.apply(courses[i]);
            if (evaluation[i] > max) max = evaluation[i];
            if (evaluation[i] < min) min = evaluation[i];
        }

        String maxNames = "n/a";;
        String minNames = "n/a";
        if (max > 0) maxNames = getCoursesNames(evaluation, max);
        if (max > min) minNames = getCoursesNames(evaluation, min);

        System.out.println(lineMax + maxNames);
        System.out.println(lineMin + minNames);
    }

    private static String getCoursesNames(int[] evaluation, int n) {
        StringBuilder names = new StringBuilder("n/a");
        boolean isNotFirst = false;
        for (int i = 0; i < NUM_COURSES; i++) {
            if (evaluation[i] == n) {
                if (isNotFirst) names.append(", ");
                else names.delete(0, 3);
                isNotFirst = true;
                names.append(courses[i].getName());
            }
        }
        return names.toString();
    }

    private static void findStudent() {
        System.out.println("Enter an id or 'back' to return");

        while (true) {
            String input = scanner.nextLine().toLowerCase().trim();
            if (input.equals("back")) break;

            Student st = getStudent(input);
            if (st == null) continue;

            System.out.println(st);
        }
    }

    private static void addPoints() {
        System.out.println("Enter an id and points or 'back' to return:");

        while (true) {
            String input = scanner.nextLine().toLowerCase().trim();
            if (input.equals("back")) break;
            String[] rawData = input.split("\\s+");

            if (rawData.length != 5) {
                System.out.println("Incorrect points format.");
                continue;
            }

            Student st = getStudent(rawData[0]);
            if (st == null) continue;

            int[] marksToAdd = new int[NUM_COURSES];
            boolean isInputCorrect = true;
            for (int i = 0; i < NUM_COURSES; i++) {
                try {
                    marksToAdd[i] = Integer.parseInt(rawData[i + 1]);
                } catch (NumberFormatException e) {
                    isInputCorrect = false;
                    break;
                }
                if (marksToAdd[i] < 0) {
                    isInputCorrect = false;
                    break;
                }
            }

            if (!isInputCorrect) {
                System.out.println("Incorrect points format.");
                continue;
            }

            submitPoints(st, marksToAdd);
            System.out.println("Points updated.");
        }
    }

    private static void submitPoints(Student st, int[] marksToAdd) {
        for (int i = 0; i < NUM_COURSES; i++) {
            st.getMarks()[i] += marksToAdd[i];
            if (marksToAdd[i] > 0) courses[i].submitMark(st, marksToAdd[i]);
        }
    }

    private static Student getStudent(String idStr) {
        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            System.out.println("No student is found for id=" + idStr + ".");
            return null;
        }

        Student st = getStudentById(id);
        if (st == null) {
            System.out.println("No student is found for id=" + idStr + ".");
            return null;
        }
        return st;
    }

    private static Student getStudentById(int id) {
        for (Student st : students) {
            if (st.id == id) return st;
        }
        return null;
    }

    private static void listStudents() {
        if (students.size() == 0) System.out.println("No students found");
        else {
            System.out.println("Students:");
            students.stream().map(st -> st.id).forEach(System.out::println);
        }
    }

    private static void addStudents() {

        int numStudentsAdded = 0;
        System.out.println("Enter student credentials or 'back' to return:");
        while (true) {
            String input = scanner.nextLine().toLowerCase().trim();
            if (input.equals("back")) break;
            String[] rawData = input.split("\\s+");

            if (rawData.length < 3) {
                System.out.println("Incorrect credentials.");
                continue;
            }

            if (!Student.checkName(rawData[0])) {
                System.out.println("Incorrect first name.");
                continue;
            }

            boolean isCorrectSurname = true;
            StringBuilder surname = new StringBuilder();
            for (int i = 1; i < rawData.length - 1; i++) {
                surname.append(rawData[i]).append(" ");
                if (!Student.checkName(rawData[i])) {
                    isCorrectSurname = false;
                    break;
                }
            }
            if (!isCorrectSurname) {
                System.out.println("Incorrect last name.");
                continue;
            }
            surname.deleteCharAt(surname.length() - 1);

            if (!Student.checkEmail(rawData[rawData.length - 1])) {
                System.out.println("Incorrect email.");
                continue;
            }

            Student newStudent = new Student(rawData[0], surname.toString(), rawData[rawData.length - 1]);

            if (students.contains(newStudent)) {
                System.out.println("This email is already taken.");
                continue;
            }

            if (students.size() == 0) newStudent.setId(1);
            else newStudent.setId(students.get(students.size() - 1).getId() + 1);

            students.add(newStudent);
            numStudentsAdded++;
            System.out.println("The student has been added.");
        }
        System.out.println("Total " + numStudentsAdded + " students have been added.");
    }


}