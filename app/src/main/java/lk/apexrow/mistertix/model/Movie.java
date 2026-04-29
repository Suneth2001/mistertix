package lk.apexrow.mistertix.model;

public class
Movie {

    private String name;
    private String language;
    private String duration;
    private String genre;
    private String image;
    private String showtime1;
    private String showtime2;
    private String showtime3;
    private String showtime4;
    private String showtime5;
    private String releaseDate;
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getShowtime1() {
        return showtime1;
    }

    public void setShowtime1(String showtime1) {
        this.showtime1 = showtime1;
    }

    public String getShowtime2() {
        return showtime2;
    }

    public void setShowtime2(String showtime2) {
        this.showtime2 = showtime2;
    }

    public String getShowtime3() {
        return showtime3;
    }

    public void setShowtime3(String showtime3) {
        this.showtime3 = showtime3;
    }

    public String getShowtime4() {
        return showtime4;
    }

    public void setShowtime4(String showtime4) {
        this.showtime4 = showtime4;
    }

    public String getShowtime5() {
        return showtime5;
    }

    public void setShowtime5(String showtime5) {
        this.showtime5 = showtime5;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getYoutubeLink() {
        return youtubeLink;
    }

    public void setYoutubeLink(String youtubeLink) {
        this.youtubeLink = youtubeLink;
    }

    private String youtubeLink;

    public Movie(String name, String youtubeLink, String description, String releaseDate, String showtime5, String showtime4, String showtime3, String showtime2, String showtime1, String image, String genre, String duration, String language) {
        this.name = name;
        this.youtubeLink = youtubeLink;
        this.description = description;
        this.releaseDate = releaseDate;
        this.showtime5 = showtime5;
        this.showtime4 = showtime4;
        this.showtime3 = showtime3;
        this.showtime2 = showtime2;
        this.showtime1 = showtime1;
        this.image = image;
        this.genre = genre;
        this.duration = duration;
        this.language = language;
    }
}



