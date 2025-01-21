package com.vexillum.plugincore.util

fun levenshteinDistance(s1: String, s2: String): Int {
    val m = s1.length
    val n = s2.length
    val dp = Array(m + 1) { IntArray(n + 1) }
    for (i in 0..m) {
        dp[i][0] = i
    }
    for (j in 0..n) {
        dp[0][j] = j
    }
    for (i in 1..m) {
        for (j in 1..n) {
            val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
            dp[i][j] = minOf(dp[i - 1][j] + 1, dp[i][j - 1] + 1, dp[i - 1][j - 1] + cost)
        }
    }
    return dp[m][n]
}

fun Collection<String>.bestLevenshtein(input: String): Pair<Int, String>? =
    map { levenshteinDistance(input, it) to it }.minByOrNull { it.first }

fun Collection<String>.sortByLevenshtein(input: String): List<String> =
    sortedBy { levenshteinDistance(input, it) }

fun Sequence<String>.sortByLevenshtein(input: String): Sequence<String> =
    sortedBy { levenshteinDistance(input, it) }

fun <T> Sequence<T>.sortByLevenshtein(
    input: String,
    selector: T.() -> String
): Sequence<T> =
    sortedBy {
        levenshteinDistance(input, it.selector())
    }
