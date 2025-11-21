package theater;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

/**
 * This class generates a statement for a given invoice of performances.
 */
public class StatementPrinter {
    private final Invoice invoice;
    private final Map<String, Play> plays;

    public StatementPrinter(Invoice invoice, Map<String, Play> plays) {
        this.invoice = invoice;
        this.plays = plays;
    }

    /**
     * Returns a formatted statement of the invoice associated with this printer.
     *
     * @return the formatted statement
     * @throws RuntimeException if one of the play types is not known
     */
    public String statement() {
        final StatementData data = createStatementData();

        final NumberFormat frmt = NumberFormat.getCurrencyInstance(Locale.US);
        final StringBuilder result =
                new StringBuilder(String.format("Statement for %s%n", data.getCustomer()));

        for (PerformanceData perf : data.getPerformances()) {
            result.append(String.format("  %s: %s (%s seats)%n",
                    perf.getPlayName(),
                    frmt.format(perf.getAmount() / Constants.PERCENT_FACTOR),
                    perf.getAudience()));
        }

        result.append(String.format(
                "Amount owed is %s%n", frmt.format(data.getTotalAmount() / Constants.PERCENT_FACTOR)));
        result.append(String.format(
                "You earned %s credits%n", data.getVolumeCredits()));

        return result.toString();
    }

    private Play getPlay(Performance performance) {
        return plays.get(performance.getPlayID());
    }

    private int getVolumeCredits(Performance performance) {
        final Play currentPlay = getPlay(performance);
        int result = 0;

        result += Math.max(performance.getAudience()
                - Constants.BASE_VOLUME_CREDIT_THRESHOLD, 0);
        if ("comedy".equals(currentPlay.getType())) {
            result += performance.getAudience() / Constants.COMEDY_EXTRA_VOLUME_FACTOR;
        }
        return result;
    }

    private int getAmount(Performance performance) {
        final Play currentPlay = getPlay(performance);
        int result = 0;

        switch (currentPlay.getType()) {
            case "tragedy":
                result = Constants.TRAGEDY_BASE_AMOUNT;
                if (performance.getAudience() > Constants.TRAGEDY_AUDIENCE_THRESHOLD) {
                    result += Constants.TRAGEDY_OVER_BASE_CAPACITY_PER_PERSON
                            * (performance.getAudience()
                            - Constants.TRAGEDY_AUDIENCE_THRESHOLD);
                }
                break;

            case "comedy":
                result = Constants.COMEDY_BASE_AMOUNT;
                if (performance.getAudience() > Constants.COMEDY_AUDIENCE_THRESHOLD) {
                    result += Constants.COMEDY_OVER_BASE_CAPACITY_AMOUNT
                            + (Constants.COMEDY_OVER_BASE_CAPACITY_PER_PERSON
                            * (performance.getAudience()
                            - Constants.COMEDY_AUDIENCE_THRESHOLD));
                }
                result += Constants.COMEDY_AMOUNT_PER_AUDIENCE
                        * performance.getAudience();
                break;

            default:
                throw new RuntimeException(String.format(
                        "unknown type: %s", currentPlay.getType()));
        }

        return result;
    }

    private StatementData createStatementData() {
        final StatementData data = new StatementData();
        data.setCustomer(invoice.getCustomer());

        int totalAmount = 0;
        int volumeCredits = 0;

        for (Performance performance : invoice.getPerformances()) {
            final PerformanceData perfData = new PerformanceData();
            final Play play = getPlay(performance);

            perfData.setPlayName(play.getName());
            perfData.setPlayType(play.getType());
            perfData.setAudience(performance.getAudience());

            final int thisAmount = getAmount(performance);
            perfData.setAmount(thisAmount);

            totalAmount += thisAmount;
            volumeCredits += getVolumeCredits(performance);

            data.getPerformances().add(perfData);
        }

        data.setTotalAmount(totalAmount);
        data.setVolumeCredits(volumeCredits);

        return data;
    }
}
