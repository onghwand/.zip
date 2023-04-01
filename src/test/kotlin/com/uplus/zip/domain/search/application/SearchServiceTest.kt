package com.uplus.zip.domain.search.application

import com.uplus.zip.domain.maps.dao.ApartmentRepository
import com.uplus.zip.domain.search.application.impl.SearchServiceImpl
import com.uplus.zip.domain.search.application.impl.preprocessing
import com.uplus.zip.domain.search.dao.MockAutoComplete
import com.uplus.zip.domain.search.dao.MockSearchResult
import com.uplus.zip.domain.search.dao.SearchDongRepository
import com.uplus.zip.domain.search.exception.SearchErrorCode
import com.uplus.zip.global.error.CustomException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.data.domain.PageRequest

class SearchServiceTest : BehaviorSpec({
    val searchDongRepository = mockk<SearchDongRepository>()
    val apartmentRepository = mockk<ApartmentRepository>()

    val searchService = SearchServiceImpl(searchDongRepository, apartmentRepository)

    val apartmentAutoCompletes = List(7) { MockAutoComplete() }
    val dongAutoCompletes = List(3) { MockAutoComplete() }

    val apartmentSearchResponse = List(10) { MockSearchResult() }

    Given("keyword가 포함된 아파트 7개와 행정동 3개가 있는 상황에서") {
        val keyword = "keyword"

        every { apartmentRepository.find7AutoCompletesByKeyword(any(), any()) } returns apartmentAutoCompletes
        every { searchDongRepository.findTop3ByNameContains(any()) } returns dongAutoCompletes

        When("getAutoCompletes 함수를 실행하면") {
            val response = searchService.getAutoCompletes(keyword)

            Then("find7AutoCompletesByKeyword, findTop3ByNameContains가 1번씩 실행되어야 한다") {
                verify(exactly = 1) { apartmentRepository.find7AutoCompletesByKeyword(keyword, PageRequest.ofSize(7)) }
                verify(exactly = 1) { searchDongRepository.findTop3ByNameContains(keyword) }
            }
        }
    }

    Given("keyword의 마지막 글자가 완성된 한글자도 아니고 허용된 문자도 아닌 상황에서") {
        val keyword = "마ㄱ"

        When("preprocessing 함수를 실행하면") {
            val filteredKeyword = keyword.preprocessing()

            Then("filteredKeyword가 마지막 자음을 제거한 형태여야 한다") {
                filteredKeyword shouldBe keyword.substring(0, keyword.length - 1)
            }
        }
    }

    Given("keyword의 마지막 글자가 완벽한 한글자인 상황에서") {
        val keyword = "마곡"

        When("preprocessing 함수를 실행하면") {
            val filteredKeyword = keyword.preprocessing()

            Then("filteredKeyword가 마지막 한글자를 포함한 형태여야 한다") {
                filteredKeyword shouldBe keyword
            }
        }
    }

    Given("keyword의 마지막 글자가 허용된 문자인 상황에서") {
        val keyword = "마r"

        When("preprocessing 함수를 실행하면") {
            val filteredKeyword = keyword.preprocessing()

            Then("filteredKeyword가 마지막 허용된 문자를 포함한 형태여야 한다") {
                filteredKeyword shouldBe keyword
            }
        }
    }

    Given("keyword의 공백인 상황에서") {
        val keyword = "   "

        When("preprocessing 함수를 실행하면") {
            val filteredKeyword = keyword.preprocessing()

            Then("'no_keyword'여야 한다") {
                filteredKeyword shouldBe "no_keyword"
            }
        }

        When("getSearchResponse 함수를 실행하면") {
            Then("KEYWORD_IS_REQUIRED 예외가 발생해야 한다") {
                shouldThrow<CustomException> {
                    searchService.getSearchResults(keyword)
                }.errorCode shouldBe SearchErrorCode.KEYWORD_IS_REQUIRED
            }
        }
    }

    Given("유효한 keyword가 주어진 상황에서") {
        val keyword = "마곡"

        every { apartmentRepository.find10SearchResponseByKeyword(any(), any()) } returns apartmentSearchResponse

        When("getSearchResponse 함수를 실행하면") {
            val response = searchService.getSearchResults(keyword)

            Then("find10SearchResponseByKeyword가 1번 실행되어야 한다") {
                verify(exactly = 1) {
                    apartmentRepository.find10SearchResponseByKeyword(
                        keyword.preprocessing(),
                        PageRequest.ofSize(10)
                    )
                }
            }
        }
    }
})