package com.supernova.services

import com.supernova.config.FileStorageConfig
import com.supernova.exceptions.FileStorageException
import com.supernova.exceptions.MyFileNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption


@Service
class FileStorageService @Autowired constructor(fileStorageConfig : FileStorageConfig){

    private val fileStorageLocation: Path

    init {
        fileStorageLocation = Paths.get(fileStorageConfig.uploadDir).toAbsolutePath().normalize()
        try {
            Files.createDirectories(fileStorageLocation)
        } catch (e: Exception) {
            throw FileStorageException("Cannot create directory where the files will be stored", e)
        }
    }

    fun storeFile(file : MultipartFile): String {
        val fileName = StringUtils.cleanPath(file.originalFilename!!)
        return try {
            if(fileName.contains(".."))
                throw FileStorageException("File properties invalid on file $fileName. Please review and try again...")
            val targetLocation = fileStorageLocation.resolve(fileName)
            Files.copy(file.inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING)
            fileName
        } catch (e: Exception) {
            throw FileStorageException("Cannot store the file $fileName. Please try again.", e)
        }
    }

    fun loadFileAsResource(fileName: String): Resource {
        return try{
            val filePath = fileStorageLocation.resolve(fileName).normalize()
            val resource: Resource = UrlResource(filePath.toUri())
            if (resource.exists()) resource
            else throw MyFileNotFoundException("File path not found: $fileName")
        } catch (e: java.lang.Exception) {
            throw MyFileNotFoundException("File path not found: $fileName", e)
        }
    }
}
