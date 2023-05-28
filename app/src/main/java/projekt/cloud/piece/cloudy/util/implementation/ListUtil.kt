package projekt.cloud.piece.cloudy.util.implementation

object ListUtil {

    inline fun <T> mutableList(
        size: Int = 0,
        block: (MutableList<T>) -> Unit
    ): MutableList<T> {
        // public inline fun <T> mutableListOf(): MutableList<T> = ArrayList()
        return ArrayList<T>(size).also(block)
    }

    inline fun <T> mutableListWithIndex(
        size: Int = 0,
        add: (Int) -> T
    ): MutableList<T> {
        return mutableList(size) { mutableList ->
            repeat(size) { index ->
                mutableList += add.invoke(index)
            }
        }
    }

}