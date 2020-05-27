package com.risingpark.risingvoiceindicator

import java.util.*

class Utils {
    companion object {
        /**
         * get Random Number
         */
        @JvmStatic
        fun getRandomNumber(min :Int, max :Int) :Int {
            return Random().nextInt(max - min + 1) + min
        }

    }
}