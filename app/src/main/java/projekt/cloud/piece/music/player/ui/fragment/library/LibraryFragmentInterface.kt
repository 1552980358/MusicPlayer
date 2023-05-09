package projekt.cloud.piece.music.player.ui.fragment.library

interface LibraryFragmentInterface {

    fun navigateToArtist(id: String) {
        openPane()
    }

    fun navigateToAlbum(id: String) {
        openPane()
    }

    val canSlide: Boolean
        get() = true

    /**
     * [openPane]
     * Should be override for actual implementing SlidingPaneLayout class
     **/
    fun openPane() = Unit

}