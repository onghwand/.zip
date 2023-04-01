package com.uplus.zip.domain.search.application.impl

import com.uplus.zip.domain.maps.dao.ApartmentRepository
import com.uplus.zip.domain.search.application.SearchService
import com.uplus.zip.domain.search.dao.SearchDongRepository
import com.uplus.zip.domain.search.dto.AutoCompleteResponseDto
import com.uplus.zip.domain.search.dto.SearchResultResponseDto
import com.uplus.zip.domain.search.exception.SearchErrorCode
import com.uplus.zip.global.error.CustomException
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.lang.Math.max
import java.text.Normalizer

@Service
class SearchServiceImpl(
    private val searchDongRepository: SearchDongRepository,
    private val apartmentRepository: ApartmentRepository
) :
    SearchService {
    override fun getAutoCompletes(keyword: String): AutoCompleteResponseDto {
        // 입력받은 keyword 전처리
        val filteredKeyword = keyword.preprocessing()
        // keyword를 포함하고 있는 아파트 최대 7개, 행정동 최대 3개 반환
        val apart = apartmentRepository.find7AutoCompletesByKeyword(filteredKeyword, PageRequest.ofSize(7))
        val dong = searchDongRepository.findTop3ByNameContains(filteredKeyword)
        return AutoCompleteResponseDto(apartments = apart, dongs = dong)
    }

    override fun getSearchResults(keyword: String): SearchResultResponseDto {
        // 입력받은 keyword 전처리
        val filteredKeyword = keyword.preprocessing()
        if (filteredKeyword == "no_keyword") throw CustomException(SearchErrorCode.KEYWORD_IS_REQUIRED)
        // keyword를 포함하고 있는 아파트 최대 10개 반환
        return SearchResultResponseDto(
            apartmentRepository.find10SearchResponseByKeyword(
                filteredKeyword,
                PageRequest.ofSize(10)
            )
        )
    }
}

fun String.preprocessing(): String {
    // 공백 제거
    val trimmedKeyword = this.trim()
    // 마지막 글자가 완성된 상태가 아니거나 한글이 아니면(타이핑중이라고 판단) 마지막 글자 제거
    var filteredKeyword =
        if (isLastCharComplete(trimmedKeyword)) trimmedKeyword
        else trimmedKeyword.substring(0, max(trimmedKeyword.length - 1, 0))
    return filteredKeyword.ifEmpty { "no_keyword" }
}

fun isLastCharComplete(str: String): Boolean {
    // 입력된 문자열을 유니코드 정합형으로 변환
    val normalizedStr = Normalizer.normalize(str, Normalizer.Form.NFC)
    val lastChar = normalizedStr.lastOrNull()
    // 마지막 글자가 완성된 한글자 or 허용된 특수문자이면 완성되었다고 판단
    if (lastChar != null && (lastChar.isHangul() || lastChar.isAllowed())) {
        return true
    }
    // 아니면 오타 혹은 타이핑중이라고 판단하여 해당 글자를 제외하고 검색
    return false
}

// Char 타입이 완성된 한글인지 여부를 반환하는 확장 함수
fun Char.isHangul(): Boolean {
    return this in '\uAC00'..'\uD7AF'
}

// Char 타입이 허용된 문자(숫자 or . or 알파벳 대소문자)인지 여부를 반환하는 확장 함수
fun Char.isAllowed(): Boolean {
    return this.toString().matches("^[a-zA-Z0-9./&()_,ⅡⅠⅢ-]*$".toRegex())
}




