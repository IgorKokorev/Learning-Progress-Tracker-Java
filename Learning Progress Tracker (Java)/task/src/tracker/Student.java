package tracker;

import java.util.Objects;

class Student {
    int id;

    public void setId(int id) {
        this.id = id;
    }

    private String name;
    private String surname;
    private String email;
    private int[] marks = new int[] {0, 0, 0, 0};

    public static boolean checkName(String name) {
        if (name.length() < 2) return false;
        String pattern = "([a-zA-Z]+)([-']([a-zA-Z]+))*";
        return name.matches(pattern);
    }

    public static boolean checkEmail(String email) {
        String pattern = "([a-zA-Z0-9]+)([-.]([a-zA-Z0-9]+))*@([a-zA-Z0-9]+)([-.]([a-zA-Z0-9]+))*\\.[a-zA-Z0-9]+";
        return email.matches(pattern);
    }
    public Student(String name, String surname, String email) {
        this.name = name;
        this.surname = surname;
        this.email = email;
    }

    public int getId() {
        return id;
    }

//    public String getName() {
//        return name;
//    }
    public String getFullName() {
        return name + " " + surname;
    }

//    public String getSurname() {
//        return surname;
//    }


    public String getEmail() {
        return email;
    }

    public int[] getMarks() {
        return marks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(email, student.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public String toString(){
        return id + " points: Java=" + marks[0] + "; DSA=" + marks[1] + "; Databases=" + marks[2] + "; Spring=" + marks[3];
    }

}
