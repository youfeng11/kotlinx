package com.youfeng.kotlinx.serialization.json5

import kotlinx.serialization.Serializable
import kotlin.test.*

class Json5IosTest {
    @Test
    fun testIosConfig() {
        @Serializable
        data class IosAppConfig(
            val bundleId: String,
            val version: String,
            val features: List<String>
        )
        
        val json5 = """
            {
              bundleId: 'com.example.app',
              version: '1.0.0',
              features: ['push', 'analytics', 'ads'],
            }
        """.trimIndent()
        
        val config = Json5.decodeFromString<IosAppConfig>(json5)
        
        assertEquals("com.example.app", config.bundleId)
        assertEquals(3, config.features.size)
    }
}