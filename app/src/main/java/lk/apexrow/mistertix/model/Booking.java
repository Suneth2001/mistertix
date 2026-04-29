package lk.apexrow.mistertix.ui.seat_Booking;

import java.util.List;

public class Booking {
    private String movie;
    private String showtime;
    private List<String> seats;
    private String date;

    // **Empty constructor for Firestore**
    public Booking() {}

    // **Constructor**
    public Booking(String movie, String showtime, List<String> seats, String date) {
        this.movie = movie;
        this.showtime = showtime;
        this.seats = seats;
        this.date = date;
    }

    // **Getters and Setters** (Needed for Firestore)
    public String getMovie() {
        return movie;
    }

    public void setMovie(String movie) {
        this.movie = movie;
    }

    public String getShowtime() {
        return showtime;
    }

    public void setShowtime(String showtime) {
        this.showtime = showtime;
    }

    public List<String> getSeats() {
        return seats;
    }

    public void setSeats(List<String> seats) {
        this.seats = seats;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
