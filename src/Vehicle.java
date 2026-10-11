import java.math.BigDecimal;

public class Vehicle {
    private final String name;
    private final float speed;
    private final int year;

    public Vehicle(String name, float speed, int year) {
        this.name = name;
        this.speed = speed;
        this.year = year;
    }

    public String getName() {
        return name;
    }

    public float getSpeed() {
        return speed;
    }

    public int getYear() {
        return year;
    }

    public String getSpeedText() {
        return new BigDecimal(Float.toString(speed)).stripTrailingZeros().toPlainString();
    }

    @Override
    public String toString() {
        return name + " (" + getSpeedText() + " km/h, " + year + ")";
    }
}
