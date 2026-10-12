package model;

public class Student extends User {
    private String studentId;
    private String displayedName;
    private String program;
    private String yearLevel;
    private String contactNumber;
    private String email;
    private String homeAddress;

    public Student(String id, String username, String passwordHash, boolean temporaryPassword,
                   String studentId, String displayedName, String program, String yearLevel,
                   String contactNumber, String email, String homeAddress) {
        super(id, username, passwordHash, temporaryPassword, Role.STUDENT);
        this.studentId = studentId;
        this.displayedName = displayedName;
        this.program = program;
        this.yearLevel = yearLevel;
        this.contactNumber = contactNumber;
        this.email = email;
        this.homeAddress = homeAddress;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    @Override
    public String getDisplayName() {
        return displayedName;
    }

    public void setDisplayedName(String displayedName) {
        this.displayedName = displayedName;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getYearLevel() {
        return yearLevel;
    }

    public void setYearLevel(String yearLevel) {
        this.yearLevel = yearLevel;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }
}
