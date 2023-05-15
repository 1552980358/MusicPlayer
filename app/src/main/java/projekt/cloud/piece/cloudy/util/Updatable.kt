package projekt.cloud.piece.cloudy.util

interface Updatable {

    fun update() = Unit

    fun update(position: Int) = Unit

}