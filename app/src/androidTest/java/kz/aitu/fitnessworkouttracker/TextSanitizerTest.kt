package kz.aitu.fitnessworkouttracker

import kz.aitu.fitnessworkouttracker.domain.TextSanitizer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TextSanitizerTest {

    @Test
    fun cleanHtml_removesTags() {
        val input = "<p>Hello</p><b>World</b>"
        val out = TextSanitizer.cleanHtml(input)
        assertEquals("Hello World", out)
    }

    @Test
    fun cleanHtml_collapsesSpaces() {
        val input = "Hello   \n   World"
        val out = TextSanitizer.cleanHtml(input)
        assertEquals("Hello World", out)
    }

    @Test
    fun matchesQuery_trueWhenInName() {
        assertTrue(TextSanitizer.matchesQuery(name = "Biceps curl", desc = "desc", q = "biceps"))
    }

    @Test
    fun matchesQuery_trueWhenInDescription() {
        assertTrue(TextSanitizer.matchesQuery(name = "name", desc = "Works biceps well", q = "biceps"))
    }

    @Test
    fun matchesQuery_falseWhenBlankQuery() {
        assertFalse(TextSanitizer.matchesQuery(name = "name", desc = "desc", q = "   "))
    }
}
