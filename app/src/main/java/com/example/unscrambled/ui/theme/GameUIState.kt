package com.example.unscrambled.ui.theme

data class GameUIState (
    val currentScrambledWord: String = "",
    val isGuessWrong: Boolean = false,
    val score: Int = 0,
    val currentWordCount: Int = 0,
    val isGameOver: Boolean = false
)
