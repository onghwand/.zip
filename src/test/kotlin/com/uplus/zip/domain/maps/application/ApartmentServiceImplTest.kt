package com.uplus.zip.domain.maps.application

import com.uplus.zip.domain.maps.application.impl.ApartmentServiceImpl
import com.uplus.zip.domain.maps.dao.ContractRepository
import com.uplus.zip.domain.maps.dao.DongRepository
import com.uplus.zip.domain.maps.dao.GuRepository
import com.uplus.zip.domain.maps.domain.ContractType
import com.uplus.zip.domain.maps.domain.Dong
import com.uplus.zip.domain.maps.domain.Gu
import com.uplus.zip.domain.maps.dto.MapRequestParameterDto
import com.uplus.zip.domain.maps.exception.MapErrorCode
import com.uplus.zip.domain.member.application.PinInfoDao
import com.uplus.zip.global.error.CustomException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDate

class ApartmentServiceImplTest: BehaviorSpec() {

    val contractRepository = mockk<ContractRepository>()
    val guRepository = mockk<GuRepository>()
    val dongRepository = mockk<DongRepository>()
    val apartmentservice = ApartmentServiceImpl(contractRepository, guRepository, dongRepository)

    init {

        Given("zoom값이 10이고 필터값이 주어졌을 때") {
            val request = MapRequestParameterDto(35.0, 128.0, 30.0, 125.0, 10.0, "SALE")

            When("핀 정보를 조회하면") {
                every {
                    contractRepository.findSiList(request.minYear, request.maxYear, request.minArea, request.maxArea, request.minPrice, request.maxPrice, ContractType.SALE, LocalDate.now().minusMonths(1L), LocalDate.now())
                } returns mutableListOf(PinInfoDao(9999L, "서울특별시", 40000.0, 0.0, 35.55, 128.33))
                val response = apartmentservice.getInfoList(request)

                Then("서울특별시 가격 평균 조회 성공") {
                    response.pins shouldNotBe null
                    response.pins shouldHaveSize 1
                    response.pins[0].title shouldBe "서울특별시"
                }
            }
        }

        Given("zoom값이 11이고 필터값이 주어졌을 때") {
            val request = MapRequestParameterDto(35.0, 128.0, 30.0, 125.0, 11.0, "SALE")

            When("핀 정보를 조회하면") {
                every {
                    guRepository.findByLatBetweenAndLngBetween(30.0, 35.0, 125.0, 128.0)
                } returns listOf(Gu("동대문구", 35.0, 125.0, 1L), Gu("중랑구", 35.5, 125.8, 2L))

                every {
                    contractRepository.findGuList(listOf(1L, 2L), request.minYear, request.maxYear, request.minArea, request.maxArea, request.minPrice, request.maxPrice, ContractType.SALE, LocalDate.now().minusMonths(1L), LocalDate.now())
                } returns mutableListOf(PinInfoDao(1L, "동대문구", 30000.0, 0.0, 35.0, 125.0), PinInfoDao(2L, "중랑구", 35000.0, 0.0, 35.5, 125.8))

                val response = apartmentservice.getInfoList(request)
                Then("시군구 단위로 가격 평균 조회 성공") {
                    response.pins shouldNotBe null
                    response.pins shouldHaveSize 2
                }
            }
        }

        Given("zoom값이 13이고 필터값이 주어졌을 때") {
            val request = MapRequestParameterDto(35.0, 128.0, 30.0, 125.0, 13.0, "SALE")

            When("핀 정보를 조회하면") {
                every {
                    dongRepository.findByLatBetweenAndLngBetween(30.0, 35.0, 125.0, 128.0)
                } returns listOf(Dong("휘경동", 35.0, 125.0, Gu("동대문구", 35.0, 125.0, 1L), 1L), Dong("이문동", 35.3, 125.8, Gu("동대문구", 35.0, 125.0, 1L), 2L))

                every {
                    contractRepository.findDongList(listOf(1L, 2L), request.minYear, request.maxYear, request.minArea, request.maxArea, request.minPrice, request.maxPrice, ContractType.SALE, LocalDate.now().minusMonths(1L), LocalDate.now())
                } returns mutableListOf(PinInfoDao(1L, "휘경동", 20000.0, 0.0, 35.0, 125.0), PinInfoDao(2L, "이문동", 29000.0, 0.0, 35.3, 125.8))

                val response = apartmentservice.getInfoList(request)
                Then("읍면동 단위로 가격 평균 조회 성공") {
                    response.pins shouldNotBe null
                    response.pins shouldHaveSize 2
                }
            }
        }

        Given("zoom값이 15이고 필터값이 주어졌을 때") {
            val request = MapRequestParameterDto(35.0, 128.0, 30.0, 125.0, 15.0, "SALE")

            When("핀 정보를 조회하면") {
                every {
                    contractRepository.findApartmentList(request.latSW, request.latNE, request.lngSW, request.lngNE, request.minYear, request.maxYear, request.minArea, request.maxArea, request.minPrice, request.maxPrice, ContractType.SALE.toString(), LocalDate.now().minusMonths(1L), LocalDate.now())
                } returns mutableListOf(PinInfoDao(1L, "SK뷰", 35000.0, 0.0, 35.0, 125.0), PinInfoDao(2L, "레미안", 38000.0, 0.0, 35.7, 125.5), PinInfoDao(3L, "힐스테이트", 47000.0, 0.0, 36.0, 126.0))

                val response = apartmentservice.getInfoList(request)
                Then("아파트 개별 가격 평균 조회 성공") {
                    response.pins shouldNotBe null
                    response.pins shouldHaveSize 3
                }
            }
        }

        Given("매매, 전세, 월세 타입이 아닌 다른 타입을 조회할 경우") {
            val request = MapRequestParameterDto(35.0, 128.0, 30.0, 125.0, 15.0, "DIFF")

            When("핀 정보를 조회하면") {
                Then("조회 실패") {
                    shouldThrow<CustomException> {
                        apartmentservice.getInfoList(request)
                    }.errorCode shouldBe MapErrorCode.INVALID_CONTRACT_TYPE
                }
            }
        }
    }
}