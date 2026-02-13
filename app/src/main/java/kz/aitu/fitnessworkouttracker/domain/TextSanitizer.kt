package kz.aitu.fitnessworkouttracker.domain

object TextSanitizer {
    fun cleanHtml(html: String): String =
        html.replace(Regex("<[^>]*>"), " ")
            .replace("\u00A0", " ")
            .replace(Regex("\\s+"), " ")
            .trim()

    fun matchesQuery(name: String, desc: String, q: String): Boolean {
        val query = q.trim()
        if (query.isBlank()) return false
        return name.contains(query, true) || desc.contains(query, true)
    }
}
