package tj.gfit;

import java.util.Date;

public final class TrackPoint {
    private String dateTime = null;
    private Date date = null;
    private Double distanceMeters = null;
    private Double heartRate = null;
    private Double speed = null;
    private Double latitudeDegrees = null;
    private Double longitudeDegrees = null;

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getDistanceMeters() {
        return distanceMeters;
    }

    public void setDistanceMeters(Double distanceMeters) {
        this.distanceMeters = distanceMeters;
    }

    public Double getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(Double heartRate) {
        this.heartRate = heartRate;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Double getLatitudeDegrees() {
        return latitudeDegrees;
    }

    public void setLatitudeDegrees(Double latitudeDegrees) {
        this.latitudeDegrees = latitudeDegrees;
    }

    public Double getLongitudeDegrees() {
        return longitudeDegrees;
    }

    public void setLongitudeDegrees(Double longitudeDegrees) {
        this.longitudeDegrees = longitudeDegrees;
    }

    @Override
    public String toString() {
        return "TrackPoint{" +
                "dateTime='" + dateTime + '\'' +
                ", date=" + date +
                ", distanceMeters=" + distanceMeters +
                ", heartRate=" + heartRate +
                ", speed=" + speed +
                ", latitudeDegrees=" + latitudeDegrees +
                ", longitudeDegrees=" + longitudeDegrees +
                '}';
    }
}
