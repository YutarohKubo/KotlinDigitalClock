package chom.arikui.waffle.digitalclockapp

enum class BackgroundMode(val mode: String) {
    COLOR("color"), PICTURE("picture");

    companion object {
        fun modeOf(mode: String): BackgroundMode {
            return values().find { it.mode == mode } ?: COLOR
        }
    }

}