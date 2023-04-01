package com.uplus.zip.domain.maps.dao

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.inspectors.forExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(value = [SpringExtension::class])
@SpringBootTest
class ApartmentRepositoryTest(
    private val apartmentRepository: ApartmentRepository
) : BehaviorSpec({

    Given("검색 키워드와 일치하는 아파트가 7개 이상 존재하는 상황에서") {
        val keyword = "송"

        When("find7AutoCompletesByKeyword 함수를 실행하면") {
            val result = apartmentRepository.find7AutoCompletesByKeyword(keyword, PageRequest.of(0, 7))

            Then("검색 키워드가 포함된 아파트 리스트가 조회되어야 한다") {
                result.forExactly(7) { it.name shouldContain keyword }
            }
        }
    }

    Given("검색 키워드와 일치하는 아파트가 10개 이상 존재하는 상황에서") {
        val keyword = "아"

        When("find10SearchResponseByKeyword 함수를 실행하면") {
            val result = apartmentRepository.find10SearchResponseByKeyword(keyword, PageRequest.of(0, 10))

            Then("검색 키워드가 포함된 아파트 리스트가 조회되어야 한다") {
                result.forExactly(10) { it.name shouldContain keyword }
            }
        }
    }

    Given("검색 키워드와 일치하는 아파트가 존재하지 않는 상황에서") {
        val keyword = "no_keyword"

        When("find7AutoCompletesByKeyword 함수를 실행하면") {
            val result = apartmentRepository.find7AutoCompletesByKeyword(keyword, PageRequest.of(0, 7))

            Then("검색 결과가 0개여야 한다") {
                result shouldHaveSize 0
            }
        }

        When("find10SearchResponseByKeyword 함수를 실행하면") {
            val result = apartmentRepository.find10SearchResponseByKeyword(keyword, PageRequest.of(0, 10))

            Then("검색 결과가 0개여야 한다") {
                result shouldHaveSize 0
            }
        }
    }
})