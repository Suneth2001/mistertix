package lk.apexrow.mistertix.model;

public class UpCommingMovies {

private String name;
private String language;
private String genre;
private String duration;
private String image;

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

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public UpCommingMovies(String name, String language, String genre, String duration, String image) {
        this.name = name;
        this.language = language;
        this.genre = genre;
        this.duration = duration;
        this.image = image;
    }
}


