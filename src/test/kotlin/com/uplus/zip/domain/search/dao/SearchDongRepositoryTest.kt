package com.uplus.zip.domain.search.dao

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.inspectors.forExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(value = [SpringExtension::class])
@SpringBootTest
class SearchDongRepositoryTest(
    private val searchDongRepository: SearchDongRepository
) : BehaviorSpec({

    Given("검색 키워드와 일치하는 행정동이 3개 이상 존재하는 상황에서") {
        val keyword = "종"

        When("findTop3ByNameContains 함수를 실행하면") {
            val result = searchDongRepository.findTop3ByNameContains(keyword)

            Then("검색 키워드가 포함된 행정동 리스트가 조회되어야 한다") {
                result.forExactly(3) { it.name shouldContain keyword }
            }
        }
    }

    Given("검색 키워드와 일치하는 행정동이 존재하지 않는 상황에서") {
        val keyword = "no_keyword"

        When("findTop3ByNameContains 함수를 실행하면") {
            val result = searchDongRepository.findTop3ByNameContains(keyword)

            Then("검색 결과가 0개여야 한다") {
                result shouldHaveSize 0
            }
        }
    }
})