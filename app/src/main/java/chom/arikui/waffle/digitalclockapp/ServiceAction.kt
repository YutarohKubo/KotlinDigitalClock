package chom.arikui.waffle.digitalclockapp

enum class ServiceAction(private val actionName: String) {
    MONITORING_ALARM("monitoring_alarm"),;

    override fun toString(): String {
        return actionName
    }
}