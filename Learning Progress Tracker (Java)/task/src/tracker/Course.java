package tracker;

import java.util.*;
import java.util.stream.Collectors;
import java.util.Map.Entry;

class Course {
    private final String name;
    int maxPoints;

    private final Map<Student, List<Integer>> students = new HashMap<>();

    private final List<Student> notifiedStudents = new LinkedList<>();

    public String getName() {
        return name;
    }

    public int getMaxPoints() {
        return maxPoints;
    }

    public List<Student> getNotifiedStudents() {
        return notifiedStudents;
    }

    public Course(String name, int maxPoints) {
        this.name = name;
        this.maxPoints = maxPoints;
    }

    public void submitMark(Student st, int mark) {

        if (mark <= 0) return;

        if (this.students.containsKey(st)) {
            students.get(st).add(mark);
        } else {
            List<Integer> newList = new ArrayList<>();
            newList.add(mark);
            students.put(st, newList);
        }
    }

    public int getNumberOfStudents() {
        return students.size();
    }

    public int getNumberOfSubmissions() {
        return (int) students.values().stream()
                .mapToLong(List::size)
                .sum();
    }

    public int getStudentScore(Student st) {
        return students.get(st).stream().reduce(Integer::sum).orElse(0);
    }

    public int getAverageScore() {
        return (int) students.keySet().stream()
                .mapToInt(this::getStudentScore)
                .average().orElse(0);
    }

    public Map<Student, Integer> getStudentsTotalScores() {
        List<Student> st = students.keySet().stream().toList();
        return sortByValue(st.stream().collect(Collectors.toMap(value -> value, this::getStudentScore)));
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Entry.comparingByValue());
        Collections.reverse(list);

        Map<K, V> result = new LinkedHashMap<>();
        for (Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    public void notifyStudent(Student student) {
        System.out.println("To: " + student.getEmail());
        System.out.println("Re: Your Learning Progress");
        System.out.println("Hello, " + student.getFullName() + "! You have accomplished our " + this.name + " course!");
        this.notifiedStudents.add(student);
    }
}
