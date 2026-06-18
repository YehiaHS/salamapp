package com.yehia.prayertimes.data

data class HajjStep(
    val id: Int,
    val title: String,
    val description: String,
    val detail: String,
    val duaArabic: String = "",
    val duaTranslation: String = ""
)

object HajjData {
    val hajjSteps = listOf(
        HajjStep(
            1,
            "Ihram & Intentions",
            "Prepare at the Miqat boundary",
            "Perform ghusl, put on the two white sheets of Ihram, make intent for Hajj, and chant Talbiyah.",
            "لَبَّيْكَ اللَّهُمَّ حَجًّا. لَبَّيْكَ اللَّهُمَّ لَبَّيْكَ، لَبَّيْكَ لاَ شَرِيكَ لَكَ لَبَّيْكَ",
            "Labbayk Allahumma Hajjan. Labbayk Allahumma labbayk, labbayka la sharika laka labbayk..."
        ),
        HajjStep(
            2,
            "Mina (Day of Tarwiyah)",
            "Begin stay at Mina on 8th Dhul-Hijjah",
            "Travel to Mina and stay there for prayers (Dhuhr, Asr, Maghrib, Isha, and Fajr of 9th). Focus on prayers and remembrance.",
            "",
            ""
        ),
        HajjStep(
            3,
            "Day of Arafah (9th Dhul-Hijjah)",
            "The pinnacle of the Hajj pilgrimage",
            "Travel to Arafah after Sunrise. Remain until Sunset in prayer, supplication, and seeking forgiveness (Wuquf). This is the best day of the year.",
            "لاَ إِلَهَ إِلاَّ اللَّهُ وَحْدَهُ لاَ شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ، وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ",
            "La ilaha illallahu wahdahu la sharika lahu, lahul-mulku wa lahul-hamdu, wa huwa 'ala kulli shay'in qadir."
        ),
        HajjStep(
            4,
            "Muzdalifah (Night of 9th)",
            "Spend the night under the stars",
            "Proceed to Muzdalifah after Sunset. Perform Maghrib and Isha together, rest, and collect pebbles (usually 49 or 70) for stoning.",
            "اللَّهُمَّ إِنَّكَ عَفُوٌّ تُحِبُّ الْعَفْوَ فَاعْفُ عَنِّي",
            "Allahumma innaka 'afuwwun tuhibbul-'afwa fa'fu 'anni."
        ),
        HajjStep(
            5,
            "Ramy al-Jamarat (10th-12th)",
            "Stoning the pillars & Tawaf al-Ifadah",
            "On 10th (Eid), stone Jamarat al-Aqabah (the big pillar), trim/shave hair, sacrifice animal, exit Ihram, and perform Tawaf al-Ifadah in Makkah.",
            "اللَّهُ أَكْبَرُ، اللَّهُ أَكْبَرُ، لاَ إِلَهَ إِلاَّ اللَّهُ، وَاللَّهُ أَكْبَرُ، وَلِلَّهِ الْحَمْدُ",
            "Allahu Akbar, Allahu Akbar, la ilaha illallah, wallahu Akbar, wa lillahil-hamd."
        ),
        HajjStep(
            6,
            "Farewell Tawaf (Tawaf al-Wada')",
            "Last action before exiting Makkah",
            "Before departing home from Makkah, perform the farewell circumambulation around the Kaaba (Tawaf al-Wada') to conclude your Hajj pilgrimage.",
            "",
            ""
        )
    )

    val umrahSteps = listOf(
        HajjStep(
            1,
            "Ihram & Talbiyah",
            "Entering the state of consecration",
            "Purify physically, wear the Ihram sheets, perform two units of voluntary prayer at the Miqat, and continuously recite the Talbiyah.",
            "لَبَّيْكَ اللَّهُمَّ عُمْرَةً. لَبَّيْكَ اللَّهُمَّ لَبَّيْكَ...",
            "Labbayk Allahumma Umrah. Labbayk Allahumma labbayk..."
        ),
        HajjStep(
            2,
            "Tawaf (Circumambulation)",
            "Walk 7 times around the Kaaba",
            "Enter Masjid al-Haram with right foot, begin Tawaf at the Black Stone, making supplications. Conclude with two Rak'ahs behind Maqam Ibrahim.",
            "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ",
            "Rabbana atina fid-dunya hasanatan wa fil-akhirati hasanatan waqina 'adhaban-nar."
        ),
        HajjStep(
            3,
            "Sa'i (Safa and Marwah)",
            "Strive between the hills 7 times",
            "Ascend Mount Safa, face the Kaaba and make supplications. Walk back and forth between Safa and Marwah 7 times (ending at Marwah).",
            "إِنَّ الصَّفَا وَالْمَرْوَةَ مِنْ شَعَائِرِ اللَّهِ...",
            "Innas-Safa wal-Marwata min sha'a'iril-Lah..."
        ),
        HajjStep(
            4,
            "Halq or Taqsir (Shaving/Clipping)",
            "Concluding the Umrah rituals",
            "Men shave their heads (Halq) or trim hair evenly (Taqsir). Women clip a finger-tip's length of their hair. The state of Ihram is now ended.",
            "",
            ""
        )
    )

    val checklistItems = listOf(
        "Ihram clothing sheets (x2 for men)",
        "Comfortable walking sandals",
        "Islamic pocket Dua booklet",
        "Passport, visas, and Hajj permits",
        "Unscented soap, shampoo, and vaseline",
        "Refillable water bottle",
        "Small travel shoulder bag for stones/essentials",
        "Medications and first-aid supplies",
        "Folding prayer mat",
        "Emergency contacts & hotel card details"
    )
}
