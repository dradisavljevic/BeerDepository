package com.beer.cancatalogue.util

//object to hold helper functions. Currently the only function is not in use
object Helper {

    //Function to clear accents and diacritics from string. Alternative is using java Normalizer class
    fun normalize(accents : String): String {
        //list of accented letters
        val original = arrayOf("ę", "š", "č", "ą", "ć", "ń", "ó", "ś", "ź", "ż", "đ", "ž", "æ", "ø", "å", "á", "é", "ú", "ý", "þ", "ö", "ñ", "ç", "ğ", "ş", "ő", "ü", "ű", "ď", "ě", "ň", "ř", "ů", "Ę", "Š", "Č", "Ą", "Ć", "Ń", "Ó", "Ś", "Ź", "Ż", "Đ", "Ž", "Æ", "Ø", "Å", "Á", "É", "Ú", "Ý", "Þ", "Ö", "Ñ", "Ç", "Ğ", "Ş", "Ő", "Ü", "Ű", "Ď", "Ě", "Ň", "Ř", "Ů")
        //their counterparts without accent
        val normalized =  arrayOf("e", "s", "c", "a", "c", "n", "o", "s", "z", "z", "dj", "z", "ae", "o", "a", "a", "e", "u", "y", "p", "o", "n", "c", "g", "s", "o", "u", "u", "d", "e", "n", "r", "u", "E", "S", "C", "A", "C", "N", "O", "S", "Z", "Z", "Dj", "Z", "AE", "O", "A", "A", "E", "U", "Y", "P", "O", "N", "C", "G", "S", "O", "U", "U", "D", "E", "N", "R", "U")

        return accents.map { it ->
            val index = original.indexOf(it.toString())
            if (index >= 0) normalized[index] else it
        }.joinToString("")
    }

}