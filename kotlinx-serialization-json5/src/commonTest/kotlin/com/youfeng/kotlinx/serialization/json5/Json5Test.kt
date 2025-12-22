package com.youfeng.kotlinx.serialization.json5

import kotlinx.serialization.Serializable
import kotlin.test.*

@Serializable
data class TestUser(
    val name: String,
    val age: Int,
    val email: String? = null
)

class Json5Test {
    @Test
    fun testBasicDecoding() {
        val json5 = """
            {
              name: 'Alice',
              age: 30,
            }
        """.trimIndent()
        
        val user = Json5.decodeFromString<TestUser>(json5)
        
        assertEquals("Alice", user.name)
        assertEquals(30, user.age)
        assertNull(user.email)
    }

    @Test
    fun testSingleQuoteStrings() {
        val json5 = "{ name: 'Bob', age: 25 }"
        val user = Json5.decodeFromString<TestUser>(json5)
        
        assertEquals("Bob", user.name)
    }

    @Test
    fun testComments() {
        val json5 = """
            {
              // This is a comment
              name: 'Charlie',
              age: 35, /* inline comment */
            }
        """.trimIndent()
        
        val user = Json5.decodeFromString<TestUser>(json5)
        assertEquals("Charlie", user.name)
    }

    @Test
    fun testHexadecimalNumbers() {
        @Serializable
        data class HexData(val value: Long)
        
        val json5 = "{ value: 0xDECAF }"
        val data = Json5.decodeFromString<HexData>(json5)
        
        assertEquals(912559L, data.value)
    }

    @Test
    fun testTrailingCommas() {
        val json5 = """
            {
              name: 'Dave',
              age: 40,
            }
        """.trimIndent()
        
        val user = Json5.decodeFromString<TestUser>(json5)
        assertEquals("Dave", user.name)
    }

    @Test
    fun testLeadingDecimalPoint() {
        @Serializable
        data class DecimalData(val value: Double)
        
        val json5 = "{ value: .5 }"
        val data = Json5.decodeFromString<DecimalData>(json5)
        
        assertEquals(0.5, data.value)
    }

    @Test
    fun testEncoding() {
        val user = TestUser("Eve", 28, "eve@example.com")
        val json = Json5.encodeToString(user)
        
        assertTrue(json.contains("\"name\":\"Eve\""))
        assertTrue(json.contains("\"age\":28"))
    }

    @Test
    fun testCustomConfiguration() {
        val json5 = Json5 {
            prettyPrint = true
            ignoreUnknownKeys = true
        }
        
        val json5String = """
            {
              name: 'Frank',
              age: 45,
              unknownField: 'ignored',
            }
        """.trimIndent()
        
        val user = json5.decodeFromString<TestUser>(json5String)
        assertEquals("Frank", user.name)
    }

    @Test
    fun testNestedObjects() {
        @Serializable
        data class Address(val city: String)
        
        @Serializable
        data class Person(val name: String, val address: Address)
        
        val json5 = """
            {
              name: 'Grace',
              address: {
                city: 'New York',
              }
            }
        """.trimIndent()
        
        val person = Json5.decodeFromString<Person>(json5)
        assertEquals("Grace", person.name)
        assertEquals("New York", person.address.city)
    }

    @Test
    fun testArrays() {
        @Serializable
        data class Team(val members: List<String>)
        
        val json5 = """
            {
              members: [
                'Alice',
                'Bob',
                'Charlie',
              ]
            }
        """.trimIndent()
        
        val team = Json5.decodeFromString<Team>(json5)
        assertEquals(3, team.members.size)
        assertEquals("Alice", team.members[0])
    }
}