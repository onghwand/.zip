package com.uplus.zip.domain.member.api

import com.uplus.zip.domain.member.application.MemberService
import com.uplus.zip.domain.member.dto.ReissueRequestDto
import com.uplus.zip.domain.member.dto.ReissueResponseDto
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.validation.Valid

@RequestMapping("/users")
@RestController
class MemberApiController(private val memberService: MemberService) {

    @PostMapping("/login")
    fun login(@RequestHeader header: HttpHeaders): ResponseEntity<Any> {
        return ResponseEntity.ok(memberService.login(header))
    }

    @PostMapping("/logout")
    fun logout(@RequestHeader header: HttpHeaders): ResponseEntity<Unit> {
        memberService.logout(header)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/me")
    fun getLoginMemberInfo(): ResponseEntity<Any> {
        return ResponseEntity.ok(memberService.getLoginMemberInfo())
    }

    @PostMapping("/reissue")
    fun reissueAccessToken(@Valid @RequestBody reissueRequestDto: ReissueRequestDto): ResponseEntity<ReissueResponseDto> {
        return ResponseEntity.ok(memberService.reissue(reissueRequestDto))
    }

}