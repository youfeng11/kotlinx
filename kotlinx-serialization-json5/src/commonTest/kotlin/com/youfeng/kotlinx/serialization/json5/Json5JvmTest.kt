package com.youfeng.kotlinx.serialization.json5

import kotlinx.serialization.Serializable
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import java.io.File
import java.nio.file.Files

class Json5JvmTest {
    @Test
    fun `test reading from file`() {
        @Serializable
        data class Config(val host: String, val port: Int)
        
        val tempFile = Files.createTempFile("config", ".json5").toFile()
        tempFile.writeText("""
            {
              host: 'localhost',
              port: 8080,
            }
        """.trimIndent())
        
        val json5String = tempFile.readText()
        val config = Json5.decodeFromString<Config>(json5String)
        
        assertEquals("localhost", config.host)
        assertEquals(8080, config.port)
        
        tempFile.delete()
    }

    @Test
    fun `test large file performance`() {
        @Serializable
        data class Item(val id: Int, val name: String)
        
        @Serializable
        data class ItemList(val items: List<Item>)
        
        // 生成大型 JSON5
        val items = (1..1000).joinToString(",\n") { 
            "    { id: $it, name: 'Item $it' }"
        }
        val json5 = "{\n  items: [\n$items\n  ]\n}"
        
        val startTime = System.currentTimeMillis()
        val result = Json5.decodeFromString<ItemList>(json5)
        val endTime = System.currentTimeMillis()
        
        assertEquals(1000, result.items.size)
        println("Parsed 1000 items in ${endTime - startTime}ms")
    }
}