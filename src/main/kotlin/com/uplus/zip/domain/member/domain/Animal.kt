package com.uplus.zip.domain.member.domain

enum class Animal(val animal: String) {

    A1("나무늘보"),
    A2("코비토"),
    A3("스로로리스"),
    A4("카멜레온"),
    A5("미어캣"),
    A6("코알라"),
    A7("펭귄"),
    A8("레서팬더"),
    A9("흰돌고래"),
    A10("크라운피쉬"),
    A11("친칠라"),
    A12("노루"),
    A13("벌새"),
    A14("해달"),
    A15("물개"),
    A16("팬더"),
    A17("원숭이"),
    A18("사막여우"),
    A19("강아지"),
    A20("고양이"),
    A21("토끼"),
    A22("코알라"),
    A23("고슴도치"),
    A24("햄스터"),
    A25("캥거루"),
    A26("다람쥐"),
    A27("나무늘보"),
    A28("거북이"),
    A29("기린"),
    A30("하마")
    ;

    companion object {
        fun getRandomAnimal(): String {
            return Animal.values().random().animal
        }
    }

}