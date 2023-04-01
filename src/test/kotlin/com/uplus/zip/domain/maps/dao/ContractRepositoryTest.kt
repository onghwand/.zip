package com.uplus.zip.domain.maps.dao

import com.uplus.zip.domain.maps.domain.ContractType
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate

@SpringBootTest
class ContractRepositoryTest: BehaviorSpec() {
    override fun extensions() = listOf(SpringExtension)

    @Autowired
    private lateinit var contractRepository: ContractRepository

    init {
            val minYear = 1900
            val maxYear = 2023
            val minArea = 0.0
            val maxArea = 250.0
            val minPrice = 0
            val maxPrice = 3000000
            val type = ContractType.LEASE
            val startDate = LocalDate.now().minusMonths(1)
            val endDate = LocalDate.now()

        Given("필터값이 주어 졌을 때") {
            When("서울특별시의 가격을 조회하면") {
                val result = contractRepository.findSiList(minYear, maxYear, minArea, maxArea, minPrice, maxPrice, type, startDate, endDate)
                Then("조회 성공") {
                    result shouldNotBe null
                    result shouldHaveSize 1
                    result[0].title shouldBe "서울특별시"
                }
            }

            val guIds = listOf<Long>(1, 2, 3, 4)
            When("해당 시군구에 존재하는 가격을 조회하면") {
                val result = contractRepository.findGuList(guIds, minYear, maxYear, minArea, maxArea, minPrice, maxPrice, type, startDate, endDate)
                Then("조회 성공") {
                    result shouldNotBe null
                    result shouldHaveSize 4
                }
            }

            val dongIds = listOf<Long>(616)
            When("해당 읍면동에 존재하는 가격을 조회하면") {
                val result = contractRepository.findDongList(dongIds, minYear, maxYear, minArea, maxArea, minPrice, maxPrice, type, startDate, endDate)
                Then("조회 성공") {
                    result shouldNotBe null
                    result shouldHaveSize 1
                }
            }

            val minLat = 35.0
            val maxLat = 38.0
            val minLng = 123.0
            val maxLng = 126.0
            When("해당 위치 내에 존재하는 아파트 가격을 조회하면") {
                val result = contractRepository.findApartmentList(minLat, maxLat, minLng, maxLng, minYear, maxYear, minArea, maxArea, minPrice, maxPrice, type.toString(), startDate, endDate)
                Then("조회 성공") {
                    result shouldNotBe null
                }
            }
        }

    }

}