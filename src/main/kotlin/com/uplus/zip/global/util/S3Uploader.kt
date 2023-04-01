package com.uplus.zip.global.util

import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.services.s3.model.DeleteObjectsRequest
import com.amazonaws.services.s3.model.PutObjectRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.*
import javax.imageio.ImageIO

@Component
class S3Uploader(private val amazonS3Client: AmazonS3Client) {
    @Value("\${cloud.aws.s3.bucket}")
    private lateinit var bucket: String

    private val log = LoggerFactory.getLogger(S3Uploader::class.java)
    fun uploadOrigin(uploadFile: MultipartFile): String = originLocalFileSave(uploadFile).let { convertedFile ->
        amazonS3Client.putObject(
            PutObjectRequest(bucket, convertedFile.name, convertedFile)
                .withCannedAcl(CannedAccessControlList.PublicRead)
        )
        convertedFile.delete()              // 로컬에 생성된 이미지 파일 삭제
        convertedFile.name
    }

    private fun createFileName(fileName: String?): String = "${UUID.randomUUID()}.${extractExt(fileName)}"

    private fun extractExt(fileName: String?): String? = fileName?.substringAfterLast(".")

    private fun originLocalFileSave(multipartFile: MultipartFile) =
        Files.write(Path.of(createFileName(multipartFile.originalFilename)), multipartFile.bytes)
            .toFile()      // 로컬에 이미지 파일 생성

    // MultipartFile 에서 BufferedImage 로 변환
    private fun convertBufferedImage(image: MultipartFile): BufferedImage = ImageIO.read(image.inputStream)

    // 최적의 이미지 리사이징을 해줌
    private fun resizingLocalFileSave(image: MultipartFile, targetWidth: Int = 350, targetHeight: Int = 350): File {
        val fileName = createFileName(image.originalFilename)
        val resizedImage = BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB)
        val graphics2D: Graphics2D = resizedImage.createGraphics()
        graphics2D.drawImage(convertBufferedImage(image), 0, 0, targetWidth, targetHeight, null)
        graphics2D.dispose()
        ImageIO.write(resizedImage, "jpg", File(fileName))// 로컬에 이미지 파일 생성
        return File(fileName)
    }

    fun uploadResizing(uploadFile: MultipartFile): String = resizingLocalFileSave(uploadFile).let { resizingFile ->
        amazonS3Client.putObject(
            PutObjectRequest(bucket, resizingFile.name, resizingFile)
                .withCannedAcl(CannedAccessControlList.PublicRead)
        )
        resizingFile.delete()              // 로컬에 생성된 이미지 파일 삭제
        resizingFile.name
    }

    fun deleteImage(fileName: String) {
        try {
            amazonS3Client.deleteObject(bucket, fileName)
        } catch (e: Exception) {
            log.warn("Image Deletion Failed: $fileName")
        }
    }

    fun deleteImage(fileNameList: List<String>) {
        val keys = fileNameList.map { DeleteObjectsRequest.KeyVersion(it) }
        val request = DeleteObjectsRequest(bucket).withKeys(keys).withQuiet(false)
        try {
            amazonS3Client.deleteObjects(request)
        } catch (e: Exception) {
            for (f in fileNameList) {
                log.warn("Image Deletion Failed: $f")
            }
        }
    }

    @Scheduled(cron = "0 0 0 * * 1") // 매주 월요일 00시 00분 00초
    fun deleteImageInS3Scheduled() {
        val basePath = "was-logs"
        var d = Date() //오늘날짜
        val yesterday = SimpleDateFormat("yyyy-MM-dd")
        repeat(7) { i ->
            d = Date(d.time - i * 24 * 60 * 60 * 1000) // 어제 날짜
            var idx = 0
            while (isFileExist(basePath, "warn.${yesterday.format(d)}.$idx.log")) {
                val lines = Files.readAllLines(
                    Paths.get(basePath + "/warn.${yesterday.format(d)}.$idx.log"),
                    StandardCharsets.UTF_8
                )
                val imageList = lines.map { it.split(" ").last() }
                deleteImage(imageList)
                idx++
            }
        }
    }

    fun isFileExist(filePath: String, fileName: String): Boolean {
        val directory = File(filePath)
        if (!directory.isDirectory) {
            // 경로가 디렉토리가 아닌 경우 false 반환
            return false
        }
        val file = File(directory, fileName)
        return file.exists() && file.isFile
    }
}







