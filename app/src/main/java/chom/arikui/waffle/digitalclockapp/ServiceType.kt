package chom.arikui.waffle.digitalclockapp

enum class ServiceType(private val serviceName: String) {
    ACTIVITY_RUNNING("activity_running"),
    ACTIVITY_DESTROYED("activity_destroyed"),;

    override fun toString(): String {
        return serviceName
    }
}