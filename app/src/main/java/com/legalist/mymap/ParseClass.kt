package com.legalist.mymap

import android.app.Application
import com.parse.Parse

class ParseClass():Application() {
    override fun onCreate() {
        super.onCreate()
        Parse.setLogLevel(Parse.LOG_LEVEL_ERROR)
        Parse.initialize(Parse.Configuration.Builder(this)
            .applicationId("JvLyW9mNZU329Xpx13JOdgD4yjbAdenCFtNl7xza")
            .clientKey("ACGuAcNhzGk3OWZob78H49j2zDCIs8xaa4XeA0tk")
            .server("https://parseapi.back4app.com")
            .build()
        )

    }
}