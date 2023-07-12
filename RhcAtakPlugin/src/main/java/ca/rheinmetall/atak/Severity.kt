package ca.rheinmetall.atak

enum class Severity(val severityCode: Int) {
    All(0),
    LowImpact(1),
    Minor(2),
    Moderate(3),
    Serious(4);

    companion object{
        fun fromCode(code: Int): Severity {
            for (value in Severity.values()) {
                if (value.severityCode == code) {
                    return value
                }
            }
            return Serious
        }
    }
}
