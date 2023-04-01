package com.uplus.zip.domain.maps.api

import com.uplus.zip.domain.maps.application.ApartmentService
import com.uplus.zip.domain.maps.dto.MapInfoResponseDto
import com.uplus.zip.domain.maps.dto.MapRequestParameterDto
import com.uplus.zip.domain.member.application.PinInfoDao
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(MapsApiController::class)
@AutoConfigureMockMvc(addFilters = false)
class MapApiControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var apartmentService: ApartmentService

    @DisplayName("핀 조회 성공")
    @Test
    fun getPinsApiSuccess() {
        val request = MapRequestParameterDto(35.0, 128.0, 30.0, 125.0, 10.0, "SALE")
        `when`(apartmentService.getInfoList(request)).thenReturn(
            MapInfoResponseDto(
                listOf(PinInfoDao(9999L, "서울특별시", 40000.0, 0.0, 35.55, 128.33))
            )
        )

        mockMvc.perform(
            get("/maps")
                .param("latNE", request.latNE.toString())
                .param("lngNE", request.lngNE.toString())
                .param("latSW", request.latSW.toString())
                .param("lngSW", request.lngSW.toString())
                .param("zoom", request.zoom.toString())
                .param("type", request.type))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.pins").isNotEmpty)
    }

    @DisplayName("두 위치의 위도 경도 미입력 시 핀 조회 실패")
    @Test
    fun getPinsApiFailWithoutLatLng() {
        mockMvc.perform(
            get("/maps")
                .param("zoom", "11")
                .param("type", "SALE"))
            .andExpect(status().isBadRequest)
    }

    @DisplayName("zoom 사이즈 미입력 시 핀 조회 실패")
    @Test
    fun getPinsApiFailWithoutZoom() {
        mockMvc.perform(
            get("/maps")
                .param("latNE", "35.0")
                .param("lngNE", "126.0")
                .param("latSW", "36.0")
                .param("lngSW", "128.0")
                .param("type", "SALE"))
            .andExpect(status().isBadRequest)
    }

    @DisplayName("type 미입력 시 핀 조회 실패")
    @Test
    fun getPinsApiFailWithoutType() {
        mockMvc.perform(
            get("/maps")
                .param("latNE", "35.0")
                .param("lngNE", "126.0")
                .param("latSW", "36.0")
                .param("lngSW", "128.0")
                .param("zoom", "10.0"))
            .andExpect(status().isInternalServerError)
    }
}