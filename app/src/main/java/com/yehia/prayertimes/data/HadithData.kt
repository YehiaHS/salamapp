package com.yehia.prayertimes.data

data class Hadith(
    val id: Int,
    val arabic: String,
    val english: String,
    val narrator: String,
    val source: String
)

object HadithData {
    val hadiths: List<Hadith> = listOf(
        Hadith(
            id = 1,
            arabic = "إِنَّمَا الأَعْمَالُ بِالنِّيَّاتِ، وَإِنَّمَا لِكُلِّ امْرِئٍ مَا نَوَى",
            english = "Actions are but by intentions, and every person shall have only that which he intended.",
            narrator = "Umar ibn al-Khattab",
            source = "Sahih al-Bukhari & Sahih Muslim"
        ),
        Hadith(
            id = 2,
            arabic = "لاَ يُؤْمِنُ أَحَدُكُمْ حَتَّى يُحِبَّ لأَخِيهِ مَا يُحِبُّ لِنَفْسِهِ",
            english = "None of you [truly] believes until he loves for his brother what he loves for himself.",
            narrator = "Anas ibn Malik",
            source = "Sahih al-Bukhari & Sahih Muslim"
        ),
        Hadith(
            id = 3,
            arabic = "مَنْ كَانَ يُؤْمِنُ بِاللَّهِ وَالْيَوْمِ الآخِرِ فَلْيَقُلْ خَيْرًا أَوْ لِيَصْمُتْ",
            english = "Whosoever believes in Allah and the Last Day, let him say good or remain silent.",
            narrator = "Abu Hurayrah",
            source = "Sahih al-Bukhari & Sahih Muslim"
        ),
        Hadith(
            id = 4,
            arabic = "خَيْرُكُمْ مَنْ تَعَلَّمَ الْقُرْآنَ وَعَلَّمَهُ",
            english = "The best of you are those who learn the Quran and teach it.",
            narrator = "Uthman ibn Affan",
            source = "Sahih al-Bukhari"
        ),
        Hadith(
            id = 5,
            arabic = "الطهُورُ شَطْرُ الإِيمَانِ",
            english = "Cleanliness is half of faith.",
            narrator = "Abu Malik al-Ash'ari",
            source = "Sahih Muslim"
        ),
        Hadith(
            id = 6,
            arabic = "الدِّينُ النَّصِيحَةُ",
            english = "The religion is sincere advice.",
            narrator = "Tamim ad-Dari",
            source = "Sahih Muslim"
        ),
        Hadith(
            id = 7,
            arabic = "اتَّقِ اللَّهَ حَيْثُمَا كُنْتَ، وَأَتْبِعِ السَّيِّئَةَ الْحَسَنَةَ تَمْحُهَا",
            english = "Fear Allah wherever you are, and follow up a bad deed with a good deed which will wipe it out.",
            narrator = "Abu Dharr al-Ghifari",
            source = "Sunan at-Tirmidhi"
        ),
        Hadith(
            id = 8,
            arabic = "احْفَظِ اللَّهَ يَحْفَظْكَ، احْفَظِ اللَّهَ تَجِدْهُ تُجَاهَكَ",
            english = "Be mindful of Allah and Allah will protect you. Be mindful of Allah and you will find Him in front of you.",
            narrator = "Abdullah ibn Abbas",
            source = "Sunan at-Tirmidhi"
        ),
        Hadith(
            id = 9,
            arabic = "لاَ يَغْضَبْ أَحَدُكُمْ إِذَا غَضِبَ فَلْيَسْكُتْ",
            english = "A strong man is not one who defeats others in wrestling, but one who controls his anger.",
            narrator = "Abu Hurayrah",
            source = "Sahih al-Bukhari"
        ),
        Hadith(
            id = 10,
            arabic = "كُلُّ مَعْرُوفٍ صَدَقَةٌ",
            english = "Every good deed is a charity.",
            narrator = "Jabir ibn Abdullah",
            source = "Sahih al-Bukhari"
        ),
        Hadith(
            id = 11,
            arabic = "الدُّعَاءُ هُوَ الْعِبَادَةُ",
            english = "Supplication (Dua) is the essence of worship.",
            narrator = "An-Nu'man ibn Bashir",
            source = "Sunan at-Tirmidhi"
        ),
        Hadith(
            id = 12,
            arabic = "التَّائِبُ مِنَ الذَّنْبِ كَمَنْ لاَ ذَنْبَ لَهُ",
            english = "The one who repents from sin is like one who has no sin.",
            narrator = "Abdullah ibn Mas'ud",
            source = "Sunan Ibn Majah"
        ),
        Hadith(
            id = 13,
            arabic = "مَنْ سَلَكَ طَرِيقًا يَلْتَمِسُ فِيهِ عِلْمًا سَهَّلَ اللَّهُ لَهُ بِهِ طَرِيقًا إِلَى الْجَنَّةِ",
            english = "Whoever takes a path upon which he seeks knowledge, Allah will make easy for him a path to Paradise.",
            narrator = "Abu Hurayrah",
            source = "Sahih Muslim"
        ),
        Hadith(
            id = 14,
            arabic = "الْمُسْلِمُ مَنْ سَلِمَ الْمُسْلِمُونَ مِنْ لِسَانِهِ وَيَدِهِ",
            english = "A Muslim is the one from whose tongue and hands the Muslims are safe.",
            narrator = "Abdullah ibn Amr",
            source = "Sahih al-Bukhari"
        ),
        Hadith(
            id = 15,
            arabic = "لاَ يَدْخُلُ الْجَنَّةَ قَاطِعٌ",
            english = "The severer of family relationships will not enter Paradise.",
            narrator = "Jubayr ibn Mut'im",
            source = "Sahih al-Bukhari"
        ),
        Hadith(
            id = 16,
            arabic = "يَسِّرُوا وَلاَ تُعَسِّرُوا، وَبَشِّرُوا وَلاَ تُنَفِّرُوا",
            english = "Make things easy for people and do not make them difficult, and give good tidings and do not repel them.",
            narrator = "Anas ibn Malik",
            source = "Sahih al-Bukhari"
        ),
        Hadith(
            id = 17,
            arabic = "مَنْ لاَ يَرْحَمِ النَّاسَ لاَ يَرْحَمْهُ اللَّهُ",
            english = "Whoever does not show mercy to people, Allah will not show mercy to him.",
            narrator = "Jarir ibn Abdullah",
            source = "Sahih Muslim"
        ),
        Hadith(
            id = 18,
            arabic = "الْيَدُ الْعُلْيَا خَيYNٌ مِنَ الْيَدِ السُّفْلَى",
            english = "The upper hand (giving) is better than the lower hand (taking).",
            narrator = "Hakim ibn Hizam",
            source = "Sahih al-Bukhari"
        ),
        Hadith(
            id = 19,
            arabic = "مَنْ غَشَّنَا فَلَيْسَ مِنَّا",
            english = "Whoever cheats us is not one of us.",
            narrator = "Abu Hurayrah",
            source = "Sahih Muslim"
        ),
        Hadith(
            id = 20,
            arabic = "آيَةُ الْمُنَافِقِ ثَلاَثٌ إِذَا حَدَّثَ كَذَبَ وَإِذَا وَعَدَ أَخْلَفَ وَإِذَا اؤْتُمِنَ خَانَ",
            english = "The signs of a hypocrite are three: whenever he speaks he lies, whenever he promises he breaks it, and whenever he is trusted he betrays it.",
            narrator = "Abu Hurayrah",
            source = "Sahih al-Bukhari"
        ),
        Hadith(
            id = 21,
            arabic = "إِنَّ اللَّهَ جَمِيلٌ يُحِبُّ الْجَمَال",
            english = "Verily, Allah is beautiful and He loves beauty.",
            narrator = "Abdullah ibn Mas'ud",
            source = "Sahih Muslim"
        ),
        Hadith(
            id = 22,
            arabic = "الْمُؤْمِنُ الْقَوِيُّ خَيْرٌ وَأَحَبُّ إِلَى اللَّهِ مِنَ الْمُؤْمِنِ الضَّعِيفِ",
            english = "A strong believer is better and more beloved to Allah than a weak believer.",
            narrator = "Abu Hurayrah",
            source = "Sahih Muslim"
        ),
        Hadith(
            id = 23,
            arabic = "بُنِيَ الإِسْلاَمُ عَلَى خَمْسٍ",
            english = "Islam is built upon five pillars.",
            narrator = "Abdullah ibn Umar",
            source = "Sahih al-Bukhari"
        ),
        Hadith(
            id = 24,
            arabic = "تَرْكُ مَا لاَ يَعْنِيهِ",
            english = "Part of the perfection of one's Islam is his leaving alone that which does not concern him.",
            narrator = "Abu Hurayrah",
            source = "Sunan at-Tirmidhi"
        ),
        Hadith(
            id = 25,
            arabic = "أَقْرَبُ مَا يَكُونُ الْعَبْدُ مِنْ رَبِّهِ وَهُوَ سَاجِدٌ",
            english = "The nearest a servant comes to his Lord is when he is prostrating.",
            narrator = "Abu Hurayrah",
            source = "Sahih Muslim"
        ),
        Hadith(
            id = 26,
            arabic = "لَيْسَ الْغِنَى عَنْ كَثْرَةِ الْعَرَضِ وَلَكِنَّ الْغِنَى غِنَى النَّفْسِ",
            english = "Richness does not lie in abundance of worldly goods, but richness is the richness of the soul.",
            narrator = "Abu Hurayrah",
            source = "Sahih al-Bukhari"
        ),
        Hadith(
            id = 27,
            arabic = "صِلَةُ الرَّحِمِ تَزِيدُ فِي الْعُمُرِ",
            english = "Keeping relations with family relations increases one's lifespan.",
            narrator = "Anas ibn Malik",
            source = "Sahih al-Bukhari"
        ),
        Hadith(
            id = 28,
            arabic = "سَبْعَةٌ يُظِلُّهُمُ اللَّهُ فِي ظِلِّهِ",
            english = "There are seven whom Allah will shade in His Shade on the Day when there is no shade except His Shade.",
            narrator = "Abu Hurayrah",
            source = "Sahih al-Bukhari"
        ),
        Hadith(
            id = 29,
            arabic = "مَا نَقَصَتْ صَدَقَةٌ مِنْ مَالٍ",
            english = "Charity does not decrease wealth.",
            narrator = "Abu Hurayrah",
            source = "Sahih Muslim"
        ),
        Hadith(
            id = 30,
            arabic = "إِنَّ الصِّدْقَ يَهْدِي إِلَى الْبِرِّ وَإِنَّ الْبِرَّ يَهْدِي إِلَى الْجَنَّةِ",
            english = "Truthfulness leads to righteousness, and righteousness leads to Paradise.",
            narrator = "Abdullah ibn Mas'ud",
            source = "Sahih al-Bukhari"
        ),
        Hadith(
            id = 31,
            arabic = "إِنَّ اللَّهَ كَتَبَ الْإِحْسَانَ عَلَى كُلِّ شَيْءٍ",
            english = "Verily Allah has prescribed proficiency (Ihsan) in all things.",
            narrator = "Shaddad ibn Aws",
            source = "Sahih Muslim"
        ),
        Hadith(
            id = 32,
            arabic = "لا ضَرَرَ وَلا ضِرَارَ",
            english = "There should be neither harming nor reciprocating harm.",
            narrator = "Abu Sa'id al-Khudri",
            source = "Sunan Ibn Majah"
        ),
        Hadith(
            id = 33,
            arabic = "الْبَيِّنَةُ عَلَى الْمُدَّعِي، وَالْيَمِينُ عَلَى مَنْ أَنْكَرَ",
            english = "The onus of proof is on the claimant, and the taking of an oath is on the denier.",
            narrator = "Abdullah ibn Abbas",
            source = "Al-Baihaqi"
        ),
        Hadith(
            id = 34,
            arabic = "مَنْ رَأَى مِنْكُمْ مُنْكَرًا فَلْيُغَيِّرْهُ بِيَدِهِ",
            english = "Whosoever of you sees an evil, let him change it with his hand.",
            narrator = "Abu Sa'id al-Khudri",
            source = "Sahih Muslim"
        ),
        Hadith(
            id = 35,
            arabic = "الْحَلالُ بَيِّنٌ وَالْحَرَامُ بَيِّنٌ",
            english = "The halal is clear and the haram is clear, and between them are matters that are ambiguous.",
            narrator = "Al-Nu'man ibn Bashir",
            source = "Sahih al-Bukhari & Sahih Muslim"
        ),
        Hadith(
            id = 36,
            arabic = "إِنَّ اللَّهَ طَيِّبٌ لا يَقْبَلُ إِلا طَيِّبًا",
            english = "Allah the Almighty is Pure and accepts only that which is pure.",
            narrator = "Abu Hurayrah",
            source = "Sahih Muslim"
        ),
        Hadith(
            id = 37,
            arabic = "ازْهَدْ فِي الدُّنْيَا يُحِبَّك اللَّهُ",
            english = "Renounce the world and Allah will love you, and renounce what people possess and people will love you.",
            narrator = "Sahl ibn Sa'd",
            source = "Sunan Ibn Majah"
        ),
        Hadith(
            id = 38,
            arabic = "كُلُّ سُلامَى مِنَ النَّاسِ عَلَيْهِ صَدَقَةٌ",
            english = "Every joint of a person must perform a charity each day the sun rises.",
            narrator = "Abu Hurayrah",
            source = "Sahih al-Bukhari & Sahih Muslim"
        ),
        Hadith(
            id = 39,
            arabic = "الْبِرُّ حُسْنُ الْخُلُقِ وَالْإِثْمُ مَا حَاكَ فِي صَدْرِك",
            english = "Righteousness is good character, and sin is that which wavers in your chest.",
            narrator = "Al-Nawas ibn Sam'an",
            source = "Sahih Muslim"
        ),
        Hadith(
            id = 40,
            arabic = "كُنْ فِي الدُّنْيَا كَأَنَّك غَرِيبٌ أَوْ عَابِرُ سَبِيلٍ",
            english = "Be in this world as if you were a stranger or a traveler.",
            narrator = "Abdullah ibn Umar",
            source = "Sahih al-Bukhari"
        ),
        Hadith(
            id = 41,
            arabic = "مَا نَهَيْتُكُمْ عَنْهُ فَاجْتَنِبُوهُ وَمَا أَمَرْتُكُمْ بِهِ فَأْتُوا مِنْهُ مَا اسْتَطَعْتُمْ",
            english = "What I have forbidden you, avoid; and what I have ordered you, do as much of it as you can.",
            narrator = "Abu Hurayrah",
            source = "Sahih al-Bukhari & Sahih Muslim"
        ),
        Hadith(
            id = 42,
            arabic = "إِنَّ اللَّهَ تَجَاوَزَ لِي عَنْ أُمَّتِي الْخَطَأَ وَالنِّسْيَانَ وَمَا اسْتُكْرِهُوا عَلَيْهِ",
            english = "Verily, Allah has pardoned for my nation their mistakes, their forgetfulness, and what they are coerced into doing.",
            narrator = "Abdullah ibn Abbas",
            source = "Sunan Ibn Majah"
        )
    )
}
