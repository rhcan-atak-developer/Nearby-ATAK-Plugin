package ca.rheinmetall.atak

enum class Severity(val severityCode: Int) {
    LowImpact(1),
    Minor(2),
    Moderate(3),
    Serious(4)
}
