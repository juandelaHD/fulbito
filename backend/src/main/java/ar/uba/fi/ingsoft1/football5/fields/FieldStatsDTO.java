package ar.uba.fi.ingsoft1.football5.fields;

public record FieldStatsDTO(
    double weeklyPercentage,
    double monthlyPercentage,
    double reservedHours,
    double availableHours
) {
    public FieldStatsDTO {
        // Percentage of the field reserved in a week
        weeklyPercentage  = Math.round(weeklyPercentage  * 10) / 10.0;

        // Percentage of the field reserved in a month
        monthlyPercentage = Math.round(monthlyPercentage * 10) / 10.0;

        // How many hours the field is reserved in a week
        reservedHours     = Math.round(reservedHours     * 10) / 10.0;

        // How many hours the field is available in a week
        availableHours    = Math.round(availableHours    * 10) / 10.0;
    }
}
