package lk.apexrow.mistertix.model;

public class User {

    private String fname;
    private String lname;
    private String email;

    public User( String fname, String lname, String email, String country, String mobile, String dob, String password, String statusgender) {

        this.fname = fname;
        this.lname = lname;
        this.email = email;
        this.country = country;
        this.mobile = mobile;
        this.dob = dob;
        this.password = password;
        this.statusgender = statusgender;
    }

    private String country;
    private String mobile;



    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatusgender() {
        return statusgender;
    }

    public void setStatusgender(String statusgender) {
        this.statusgender = statusgender;
    }

    private String dob;
    private String password;
    private String statusgender;



}
