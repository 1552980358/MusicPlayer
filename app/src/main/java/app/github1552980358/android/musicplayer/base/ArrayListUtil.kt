package app.github1552980358.android.musicplayer.base

/**
 * [ArrayListUtil]
 * @author  : 1552980328
 * @since   : 0.1
 * @date    : 2020/5/31
 * @time    : 10:29
 **/

interface ArrayListUtil {
    
    /**
     * [copy]
     * @param arrayList [ArrayList]<[T]>
     * @return [ArrayList]<[T]>
     * @author 1552980358
     * @since 0.1
     **/
    fun <T> copy(arrayList: ArrayList<T>): ArrayList<T> = ArrayList<T>().apply {
        arrayList.forEach { `object` -> add(`object`) }
    }
    
    /**
     * [copyAndShuffle]
     * @param arrayList [ArrayList]<[T]>
     * @return [ArrayList]<[T]>
     * @author 1552980358
     * @since 0.1
     **/
    fun <T> copyAndShuffle(arrayList: ArrayList<T>) = copy(arrayList).apply { shuffle() }
    
}
