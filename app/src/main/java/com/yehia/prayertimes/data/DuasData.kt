package com.yehia.prayertimes.data

data class Dua(
    val arabic: String,
    val transliteration: String,
    val translation: String,
    val reference: String
)

data class DuaCategory(
    val name: String,
    val icon: String,
    val duas: List<Dua>
)

object DuasData {

    val categories: List<DuaCategory> = listOf(
        DuaCategory(
            name = "Morning Adhkar · أذكار الصباح",
            icon = "WbSunny",
            duas = listOf(
                Dua(
                    arabic = "أَصْبَحْنَا وَأَصْبَحَ الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لَا إِلَٰهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ",
                    transliteration = "Asbahna wa asbahal-mulku lillah, walhamdu lillah, la ilaha illallahu wahdahu la shareeka lah, lahul-mulku wa lahul-hamdu wa huwa 'ala kulli shay'in qadeer.",
                    translation = "We have reached the morning and at this very time the whole kingdom belongs to Allah. All praise is due to Allah. None has the right to be worshipped except Allah alone, having no partner. To Him belongs the dominion and to Him belongs all praise, and He is over all things capable.",
                    reference = "Abu Dawud 4:317"
                ),
                Dua(
                    arabic = "اللَّهُمَّ بِكَ أَصْبَحْنَا، وَبِكَ أَمْسَيْنَا، وَبِكَ نَحْيَا، وَبِكَ نَمُوتُ، وَإِلَيْكَ النُّشُورُ",
                    transliteration = "Allahumma bika asbahna, wa bika amsayna, wa bika nahya, wa bika namootu, wa ilaykan-nushoor.",
                    translation = "O Allah, by Your leave we have reached the morning, and by Your leave we have reached the evening. By Your leave we live and die, and unto You is the resurrection.",
                    reference = "Tirmidhi 5:466"
                ),
                Dua(
                    arabic = "اللَّهُمَّ إِنِّي أَسْأَلُكَ الْعَافِيَةَ فِي الدُّنْيَا وَالْآخِرَةِ، اللَّهُمَّ إِنِّي أَسْأَلُكَ الْعَفْوَ وَالْعَافِيَةَ فِي دِينِي وَدُنْيَايَ وَأَهْلِي وَمَالِي",
                    transliteration = "Allahumma inni as'alukal-'afiyata fid-dunya wal-akhirah. Allahumma inni as'alukal-'afwa wal-'afiyata fi deeni wa dunyaya wa ahli wa mali.",
                    translation = "O Allah, I ask You for well-being in this world and the Hereafter. O Allah, I ask You for pardon and well-being in my religion, my worldly affairs, my family and my wealth.",
                    reference = "Abu Dawud 4:324, Ibn Majah"
                )
            )
        ),
        DuaCategory(
            name = "Evening Adhkar · أذكار المساء",
            icon = "NightsStay",
            duas = listOf(
                Dua(
                    arabic = "أَمْسَيْنَا وَأَمْسَى الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ، لَا إِلَٰهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ",
                    transliteration = "Amsayna wa amsal-mulku lillah, walhamdu lillah, la ilaha illallahu wahdahu la shareeka lah, lahul-mulku wa lahul-hamdu wa huwa 'ala kulli shay'in qadeer.",
                    translation = "We have reached the evening and at this very time the whole kingdom belongs to Allah. All praise is due to Allah. None has the right to be worshipped except Allah alone, having no partner. To Him belongs the dominion and to Him belongs all praise, and He is over all things capable.",
                    reference = "Abu Dawud 4:317"
                ),
                Dua(
                    arabic = "اللَّهُمَّ بِكَ أَمْسَيْنَا، وَبِكَ أَصْبَحْنَا، وَبِكَ نَحْيَا، وَبِكَ نَمُوتُ، وَإِلَيْكَ الْمَصِيرُ",
                    transliteration = "Allahumma bika amsayna, wa bika asbahna, wa bika nahya, wa bika namootu, wa ilaykal-maseer.",
                    translation = "O Allah, by Your leave we have reached the evening, and by Your leave we have reached the morning. By Your leave we live and die, and unto You is the return.",
                    reference = "Tirmidhi 5:466"
                ),
                Dua(
                    arabic = "أَعُوذُ بِكَلِمَاتِ اللَّهِ التَّامَّاتِ مِنْ شَرِّ مَا خَلَقَ",
                    transliteration = "A'udhu bikalimatillahit-tammati min sharri ma khalaq.",
                    translation = "I seek refuge in the perfect words of Allah from the evil of what He has created.",
                    reference = "Muslim 4:2080"
                )
            )
        ),
        DuaCategory(
            name = "Before Sleep · أذكار النوم",
            icon = "Bedtime",
            duas = listOf(
                Dua(
                    arabic = "بِاسْمِكَ اللَّهُمَّ أَمُوتُ وَأَحْيَا",
                    transliteration = "Bismika Allahumma amootu wa ahya.",
                    translation = "In Your name, O Allah, I die and I live.",
                    reference = "Sahih al-Bukhari 6324"
                ),
                Dua(
                    arabic = "اللَّهُمَّ قِنِي عَذَابَكَ يَوْمَ تَبْعَثُ عِبَادَكَ",
                    transliteration = "Allahumma qini 'adhabaka yawma tab'athu 'ibadak.",
                    translation = "O Allah, protect me from Your punishment on the Day You resurrect Your servants.",
                    reference = "Abu Dawud 4:311"
                ),
                Dua(
                    arabic = "اللَّهُمَّ بِاسْمِكَ أَحْيَا وَأَمُوتُ، سُبْحَانَكَ اللَّهُمَّ وَبِحَمْدِكَ، أَسْتَغْفِرُكَ وَأَتُوبُ إِلَيْكَ",
                    transliteration = "Allahumma bismika ahya wa amootu. Subhanaka Allahumma wa bihamdika, astaghfiruka wa atoobu ilayk.",
                    translation = "O Allah, in Your name I live and die. Glory be to You, O Allah, and praise. I seek Your forgiveness and repent to You.",
                    reference = "Sahih al-Bukhari 6312, Tirmidhi"
                )
            )
        ),
        DuaCategory(
            name = "After Prayer · أذكار بعد الصلاة",
            icon = "Mosque",
            duas = listOf(
                Dua(
                    arabic = "أَسْتَغْفِرُ اللَّهَ، أَسْتَغْفِرُ اللَّهَ، أَسْتَغْفِرُ اللَّهَ. اللَّهُمَّ أَنْتَ السَّلَامُ وَمِنْكَ السَّلَامُ، تَبَارَكْتَ يَا ذَا الْجَلَالِ وَالْإِكْرَامِ",
                    transliteration = "Astaghfirullah, Astaghfirullah, Astaghfirullah. Allahumma antas-salam wa minkas-salam, tabarakta ya dhal-jalali wal-ikram.",
                    translation = "I seek the forgiveness of Allah (three times). O Allah, You are Peace and from You comes peace. Blessed are You, O Owner of majesty and honour.",
                    reference = "Muslim 1:414"
                ),
                Dua(
                    arabic = "لَا إِلَٰهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ. اللَّهُمَّ لَا مَانِعَ لِمَا أَعْطَيْتَ، وَلَا مُعْطِيَ لِمَا مَنَعْتَ، وَلَا يَنْفَعُ ذَا الْجَدِّ مِنْكَ الْجَدُّ",
                    transliteration = "La ilaha illallahu wahdahu la shareeka lah, lahul-mulku wa lahul-hamdu wa huwa 'ala kulli shay'in qadeer. Allahumma la mani'a lima a'tayt, wa la mu'tiya lima mana't, wa la yanfa'u dhal-jaddi minkal-jadd.",
                    translation = "None has the right to be worshipped except Allah alone, having no partner. To Him belongs the dominion and all praise, and He is over all things capable. O Allah, none can withhold what You give, and none can give what You withhold, and the greatness of the great will be of no avail against You.",
                    reference = "Sahih al-Bukhari 844, Muslim 593"
                )
            )
        ),
        DuaCategory(
            name = "Food & Drink · أذكار الطعام",
            icon = "Restaurant",
            duas = listOf(
                Dua(
                    arabic = "بِسْمِ اللَّهِ وَعَلَى بَرَكَةِ اللَّهِ",
                    transliteration = "Bismillahi wa 'ala barakatillah.",
                    translation = "In the name of Allah and with the blessings of Allah.",
                    reference = "Abu Dawud 3:347, Tirmidhi 4:288"
                ),
                Dua(
                    arabic = "الْحَمْدُ لِلَّهِ الَّذِي أَطْعَمَنِي هَٰذَا، وَرَزَقَنِيهِ مِنْ غَيْرِ حَوْلٍ مِنِّي وَلَا قُوَّةٍ",
                    transliteration = "Alhamdu lillahil-ladhi at'amani hadha, wa razaqaneehi min ghayri hawlin minni wa la quwwah.",
                    translation = "All praise is due to Allah Who has given me this food and sustained me with it through no might or power of my own.",
                    reference = "Tirmidhi 5:507, Abu Dawud"
                ),
                Dua(
                    arabic = "اللَّهُمَّ بَارِكْ لَنَا فِيهِ وَأَطْعِمْنَا خَيْرًا مِنْهُ",
                    transliteration = "Allahumma barik lana fihi wa at'imna khayran minhu.",
                    translation = "O Allah, bless it for us and provide us with something better than it.",
                    reference = "Tirmidhi 5:506"
                )
            )
        ),
        DuaCategory(
            name = "Travel · أذكار السفر",
            icon = "Flight",
            duas = listOf(
                Dua(
                    arabic = "سُبْحَانَ الَّذِي سَخَّرَ لَنَا هَٰذَا وَمَا كُنَّا لَهُ مُقْرِنِينَ وَإِنَّا إِلَىٰ رَبِّنَا لَمُنْقَلِبُونَ",
                    transliteration = "Subhanal-ladhi sakhkhara lana hadha wa ma kunna lahu muqrinin. Wa inna ila Rabbina lamunqaliboon.",
                    translation = "Glory be to Him Who has subjected this to us, and we were not capable of that, and indeed to our Lord we will surely return.",
                    reference = "Muslim 2:978, Quran 43:13-14"
                ),
                Dua(
                    arabic = "اللَّهُمَّ إِنَّا نَسْأَلُكَ فِي سَفَرِنَا هَٰذَا الْبِرَّ وَالتَّقْوَىٰ، وَمِنَ الْعَمَلِ مَا تَرْضَىٰ",
                    transliteration = "Allahumma inna nas'aluka fi safarina hadhal-birra wat-taqwa, wa minal-'amali ma tarda.",
                    translation = "O Allah, we ask You in this journey of ours for righteousness, piety, and deeds that are pleasing to You.",
                    reference = "Muslim 2:978"
                )
            )
        ),
        DuaCategory(
            name = "General Supplications · أدعية عامة",
            icon = "AutoAwesome",
            duas = listOf(
                Dua(
                    arabic = "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الْآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ",
                    transliteration = "Rabbana atina fid-dunya hasanatan wa fil-akhirati hasanatan wa qina 'adhaban-nar.",
                    translation = "Our Lord, give us in this world that which is good and in the Hereafter that which is good and protect us from the punishment of the Fire.",
                    reference = "Quran 2:201, Sahih al-Bukhari 4522"
                ),
                Dua(
                    arabic = "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنَ الْهَمِّ وَالْحَزَنِ، وَأَعُوذُ بِكَ مِنَ الْعَجْزِ وَالْكَسَلِ، وَأَعُوذُ بِكَ مِنَ الْجُبْنِ وَالْبُخْلِ، وَأَعُوذُ بِكَ مِنْ غَلَبَةِ الدَّيْنِ وَقَهْرِ الرِّجَالِ",
                    transliteration = "Allahumma inni a'udhu bika minal-hammi wal-hazan, wa a'udhu bika minal-'ajzi wal-kasal, wa a'udhu bika minal-jubni wal-bukhl, wa a'udhu bika min ghalabatid-dayni wa qahrir-rijal.",
                    translation = "O Allah, I seek refuge in You from anxiety and sorrow, and I seek refuge in You from helplessness and laziness, and I seek refuge in You from cowardice and miserliness, and I seek refuge in You from the burden of debt and being overpowered by men.",
                    reference = "Sahih al-Bukhari 6369"
                ),
                Dua(
                    arabic = "اللَّهُمَّ أَصْلِحْ لِي دِينِي الَّذِي هُوَ عِصْمَةُ أَمْرِي، وَأَصْلِحْ لِي دُنْيَايَ الَّتِي فِيهَا مَعَاشِي، وَأَصْلِحْ لِي آخِرَتِي الَّتِي فِيهَا مَعَادِي، وَاجْعَلِ الْحَيَاةَ زِيَادَةً لِي فِي كُلِّ خَيْرٍ، وَاجْعَلِ الْمَوْتَ رَاحَةً لِي مِنْ كُلِّ شَرٍّ",
                    transliteration = "Allahumma aslih li deeni alladhi huwa 'ismatu amri, wa aslih li dunyaya allati fiha ma'ashi, wa aslih li akhirati allati fiha ma'adi, waj'alil-hayata ziyadatan li fi kulli khayr, waj'alil-mawta rahatan li min kulli sharr.",
                    translation = "O Allah, set right my religion which is the safeguard of my affairs. And set right for me my worldly affairs wherein is my living. And set right for me my Hereafter to which is my return. And make life for me an increase in every good, and make death a relief for me from every evil.",
                    reference = "Muslim 2720"
                )
            )
        )
    )
}
