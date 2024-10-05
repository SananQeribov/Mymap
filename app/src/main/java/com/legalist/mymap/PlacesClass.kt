package com.legalist.mymap

import android.graphics.Bitmap


class PlacesClass private constructor() {
    var image: Bitmap? = null
    var washCar: String? = null
    var area: String? = null
    var service: String? = null

    companion object {
        val instance: PlacesClass? by lazy { PlacesClass() }
    }
}