package com.beer.depository.util

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


    //Function to convert Serbian cyrillic text to serbian latin or english latin
    fun cyrillicDecode(cyrillicText : String, fullDecode : Boolean): String {
        //list containing Serbian cyrillic lower case letters
        var cyrillicSRB = arrayOf("а","б","в","г","д","ђ","е","ж","з","и","ј","к","л","м","н","њ","о","п","р","с","т","ћ","у","ф","х","ц","ч","џ","ш")
        //list containing Serbian latin lower case letters
        var latinSRB =  arrayOf("a","b","v","g","d","đ","e","ž","z","i","j","k","l","m","n","nj","o","p","r","s","t","ć","u","f","h","c","č","dž","š")
        //list containing English latin lower case letters
        var latinENG =  arrayOf("a","b","v","g","d","dj","e","z","z","i","j","k","l","m","n","nj","o","p","r","s","t","c","u","f","h","c","c","dz","s")

        val capitalizeList = arrayListOf<String>()

        //Add capital letters to all three lists

        for (i in 0 until cyrillicSRB.size) {
            capitalizeList.add(cyrillicSRB[i].capitalize())
        }
        cyrillicSRB += capitalizeList

        capitalizeList.removeAll(capitalizeList)

        for (i in 0 until latinSRB.size) {
            capitalizeList.add(latinSRB[i].capitalize())
        }
        latinSRB += capitalizeList

        capitalizeList.removeAll(capitalizeList)

        for (i in 0 until latinENG.size) {
            capitalizeList.add(latinENG[i].capitalize())
        }
        latinENG += capitalizeList

        //Return Serbian latin or English latin conversion
        return if (fullDecode) {
            cyrillicText.map { it ->
                val index = cyrillicSRB.indexOf(it.toString())
                if (index >= 0) latinENG[index] else it
            }.joinToString("")
        } else {
            cyrillicText.map { it ->
                val index = cyrillicSRB.indexOf(it.toString())
                if (index >= 0) latinSRB[index] else it
            }.joinToString("")
        }
    }

}