package com.supernova.integrationtests.controller.withYml.mapper

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.type.TypeFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.sun.istack.logging.Logger
import io.restassured.mapper.ObjectMapper
import io.restassured.mapper.ObjectMapperDeserializationContext
import io.restassured.mapper.ObjectMapperSerializationContext
import com.fasterxml.jackson.databind.ObjectMapper as JacksonObjectMapper

class YMLMapper : ObjectMapper {

    private val objectMapper : JacksonObjectMapper = JacksonObjectMapper(YAMLFactory())
    private val typeFactory : TypeFactory = TypeFactory.defaultInstance()

    private val logger = Logger.getLogger(YMLMapper::class.java)

    init {
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    }

    override fun deserialize(context: ObjectMapperDeserializationContext) : Any? {
        try {
            val dataToSerialize = context.dataToDeserialize.asString()
            val type = context.type as Class<*>
            logger.info("Trying to deserialize object of type $type")
            return objectMapper.readValue(dataToSerialize, typeFactory.constructType(type))
        } catch (e: JsonMappingException) {
            logger.severe("Error deserializing object")
            e.printStackTrace()
        } catch (e: JsonProcessingException) {
            logger.severe("Error deserializing object")
            e.printStackTrace()
        }
        return null
    }

    override fun serialize(context : ObjectMapperSerializationContext) : Any? {
        try {
            logger.info("Trying to serialize object of type ${context.objectToSerialize}")
            return objectMapper.writeValueAsString(context.objectToSerialize)
        } catch (e: JsonMappingException) {
            e.printStackTrace()
        } catch (e: JsonProcessingException) {
            logger.severe("Error serializing object")
            e.printStackTrace()
        }
        return null
    }

}
