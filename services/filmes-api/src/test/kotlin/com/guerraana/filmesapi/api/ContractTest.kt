package com.guerraana.filmesapi.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.junit.jupiter.api.assertDoesNotThrow
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

/**
 * Contract test: validates that the API DTOs serialize to JSON matching
 * the schemas declared in contracts/openapi.yaml (workspace root).
 *
 * Runs inside the workspace; the contract file is resolved relative to
 * this module (services/filmes-api -> ../../contracts/openapi.yaml).
 */
@Suppress("UNCHECKED_CAST")
class ContractTest {

    private val jsonMapper = ObjectMapper()
        .registerModule(KotlinModule.Builder().build())
        .registerModule(JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    private val contract = loadContract()

    private fun loadContract(): Map<String, Any> {
        val path = Path.of("../../contracts/openapi.yaml").toAbsolutePath().normalize()
        check(Files.exists(path)) { "Contract not found at $path — this test must run inside the workspace" }
        @Suppress("UNCHECKED_CAST")
        return ObjectMapper(YAMLFactory()).readValue(path.toFile(), Map::class.java) as Map<String, Any>
    }

    @Suppress("UNCHECKED_CAST")
    private fun schema(name: String): Map<String, Any> {
        val components = contract["components"] as Map<String, Any>
        val schemas = components["schemas"] as Map<String, Any>
        val raw = schemas[name] as? Map<String, Any> ?: fail("Schema '$name' missing from openapi.yaml")

        // Resolve allOf: merge properties and required from every part
        val mergedProperties = mutableMapOf<String, Map<String, Any>>()
        val mergedRequired = mutableListOf<String>()
        val parts = (raw["allOf"] as? List<Map<String, Any>>)?.map { part ->
            (part["\$ref"] as? String)?.removePrefix("#/components/schemas/")?.let { schema(it) } ?: part
        } ?: listOf(raw)
        for (part in parts) {
            mergedProperties.putAll(part["properties"] as? Map<String, Map<String, Any>> ?: emptyMap())
            mergedRequired.addAll(part["required"] as? List<String> ?: emptyList())
        }
        return mapOf("properties" to mergedProperties, "required" to mergedRequired)
    }

    private fun assertMatchesSchema(serialized: Map<String, Any?>, schemaName: String) {
        val schema = schema(schemaName)
        val properties = schema["properties"] as Map<String, Map<String, Any>>
        val required = schema["required"] as List<String>

        for (field in required) {
            assertTrue(serialized.containsKey(field), "Required field '$field' missing in $schemaName payload")
            assertTrue(serialized[field] != null, "Required field '$field' is null in $schemaName payload")
        }
        for ((key, value) in serialized) {
            val spec = properties[key] ?: fail("Field '$key' not declared in schema $schemaName")
            if (value == null) continue
            when (spec["type"]) {
                "integer" -> assertTrue(value is Int, "$schemaName.$key must be integer, got ${value::class}")
                "string" -> assertTrue(value is String, "$schemaName.$key must be string, got ${value::class}")
                "array" -> assertTrue(value is List<*>, "$schemaName.$key must be array")
                else -> {}
            }
            if (spec["format"] == "date") {
                assertDoesNotThrow("date format") { LocalDate.parse(value as String) }
            }
        }
    }

    @Test
    fun `MovieSummaryResponse matches MovieSummary schema`() {
        val dto = MovieSummaryResponse(id = 1, title = "Interstellar", posterUrl = "http://poster")
        val serialized = jsonMapper.convertValue(dto, Map::class.java) as Map<String, Any?>
        assertMatchesSchema(serialized, "MovieSummary")
    }

    @Test
    fun `MovieDetailResponse matches MovieDetail schema`() {
        val dto = MovieDetailResponse(
            id = 1, title = "Interstellar", posterUrl = "http://poster",
            overview = "overview", releaseDate = LocalDate.of(2014, 11, 6),
        )
        val serialized = jsonMapper.convertValue(dto, Map::class.java) as Map<String, Any?>
        assertMatchesSchema(serialized, "MovieDetail")
        assertEquals("2014-11-06", serialized["releaseDate"])
    }

    @Test
    fun `MovieSummary has no extra fields beyond schema`() {
        val dto = MovieSummaryResponse(id = 1, title = "t", posterUrl = "u")
        val serialized = jsonMapper.convertValue(dto, Map::class.java) as Map<String, Any?>
        assertEquals(setOf("id", "title", "posterUrl"), serialized.keys)
    }
}
