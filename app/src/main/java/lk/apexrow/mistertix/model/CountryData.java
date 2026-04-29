package lk.apexrow.mistertix.model;

public class CountryData {
    private int flagResourceId;
    private String countryname;
    private int countrycode;
    public CountryData(int flagResourceId, String countryname, int countrycode) {
        this.flagResourceId = flagResourceId;
        this.countryname = countryname;
        this.countrycode = countrycode;
    }


    public int getFlagResourceId() {
        return flagResourceId;
    }

    public void setFlagResourceId(int flagResourceId) {
        this.flagResourceId = flagResourceId;
    }



    public String getCountryname() {
        return countryname;
    }

    public int getCountrycode() {
        return countrycode;
    }

    public void setCountrycode(int countrycode) {
        this.countrycode = countrycode;
    }

    public void setCountryname(String countryname) {
        this.countryname = countryname;
    }







}
