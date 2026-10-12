package model;

public class Semester {
    private String term;
    private String academicYear;

    public Semester(String term, String academicYear) {
        this.term = term;
        this.academicYear = academicYear;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public String label() {
        return term + ", " + academicYear;
    }
}
