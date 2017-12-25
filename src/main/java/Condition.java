/**
 * Created by Simon on 2017-12-25. :)
 */
public class Condition {

    private ConditionType type;
    private double amount;

    public Condition() {
    }

    public ConditionType getType() {
        return type;
    }

    public void setType(ConditionType type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}