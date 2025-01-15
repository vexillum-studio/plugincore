package com.vexillum.plugincore.util

fun levenshteinDistance(s1: String, s2: String): Int {
    val dp = Array(s1.length + 1) { IntArray(s2.length + 1) }
    for (i in s1.indices) dp[i][0] = i
    for (j in s2.indices) dp[0][j] = j

    for (i in 1..s1.length) {
        for (j in 1..s2.length) {
            dp[i][j] = if (s1[i - 1] == s2[j - 1]) {
                dp[i - 1][j - 1]
            } else {
                minOf(dp[i - 1][j - 1], dp[i][j - 1], dp[i - 1][j]) + 1
            }
        }
    }
    return dp[s1.length][s2.length]
}

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
