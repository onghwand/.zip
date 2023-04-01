package com.uplus.zip.domain.maps.dao

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldHaveAtLeastSize
import io.kotest.matchers.shouldNotBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class DongRepositoryTest: BehaviorSpec() {
    override fun extensions() = listOf(SpringExtension)

    @Autowired
    private lateinit var dongRepository: DongRepository

    init {
        Given("두 위치의 위도 경도가 주어지고") {
            val fromLat = 33.0
            val toLat = 38.0
            val fromLng = 123.0
            val toLng = 129.0
            When("두 위치 사이에 존재하는 읍면동을 조회하면") {
                val dongList = dongRepository.findByLatBetweenAndLngBetween(fromLat, toLat, fromLng, toLng)

                Then("조회 성공") {
                    dongList shouldNotBe null
                    dongList shouldHaveAtLeastSize 1
                }
            }

        }
    }
}