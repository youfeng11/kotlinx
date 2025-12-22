// ============================================================================
// jsTest/kotlin/Json5JsTest.kt - JavaScript 特定测试
// ============================================================================

package com.youfeng.kotlinx.serialization.json5

import kotlinx.serialization.Serializable
import kotlin.test.*

class Json5JsTest {
    @Test
    fun testJsInterop() {
        @Serializable
        data class JsConfig(val debug: Boolean, val apiUrl: String)
        
        val json5 = """
            {
              debug: true,
              apiUrl: 'https://api.example.com',
            }
        """.trimIndent()
        
        val config = Json5.decodeFromString<JsConfig>(json5)
        
        assertTrue(config.debug)
        assertEquals("https://api.example.com", config.apiUrl)
    }

    @Test
    fun testUnicodeInJs() {
        @Serializable
        data class Message(val text: String)
        
        val json5 = "{ text: '你好世界 🌍' }"
        val message = Json5.decodeFromString<Message>(json5)
        
        assertEquals("你好世界 🌍", message.text)
    }
}