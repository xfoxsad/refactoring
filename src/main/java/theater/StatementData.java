package theater;

import java.util.ArrayList;
import java.util.List;

public class StatementData {

    private String customer;
    private int totalAmount;
    private int volumeCredits;
    private final List<PerformanceData> performances = new ArrayList<>();

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getVolumeCredits() {
        return volumeCredits;
    }

    public void setVolumeCredits(int volumeCredits) {
        this.volumeCredits = volumeCredits;
    }

    public List<PerformanceData> getPerformances() {
        return performances;
    }
}
