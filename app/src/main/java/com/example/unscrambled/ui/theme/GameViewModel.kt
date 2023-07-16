package com.example.unscrambled.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.unscrambled.data.MAX_QUESTIONS
import com.example.unscrambled.data.SCORE_INCREASE
import com.example.unscrambled.data.allWords
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GameViewModel: ViewModel() {
    private val _state = MutableStateFlow(GameUIState())

    val state: StateFlow<GameUIState> = _state.asStateFlow()

    private lateinit var currentWord: String

    private var userWords: MutableSet<String> = mutableSetOf()

    var userGuess by mutableStateOf("")
        private set

    fun updateUserGuess(guessWord: String) {
        userGuess = guessWord
    }
    init {
        reset()
    }
    fun checkUserGuess() {
        if(userGuess.trim().equals(currentWord , ignoreCase = true)) {
            val updateScore = _state.value.score.plus(SCORE_INCREASE)
            updateGameState(updateScore)
        } else {
            _state.update { currentState ->
                currentState.copy( isGuessWrong = true )
            }
        }
        updateUserGuess("")
    }

    private fun updateGameState(updatedScore: Int) {
        if(userWords.size == MAX_QUESTIONS) {
            _state.update { currentState ->
                currentState.copy(
                    isGameOver = true,
                    score = updatedScore,
                    isGuessWrong = false
                )
            }
        } else {
            _state.update { currentState ->
                currentState.copy(
                    isGuessWrong = false,
                    currentScrambledWord = pickRandomAndShuffle(),
                    score = updatedScore,
                    currentWordCount = currentState.currentWordCount.inc()
                )
            }
        }
    }

    private fun pickRandomAndShuffle(): String {
        currentWord = allWords.random()
        return if(userWords.contains(currentWord)) {
            pickRandomAndShuffle()
        } else {
            userWords.add(currentWord)
            shuffleWord(currentWord)
        }
    }

    private fun shuffleWord(word: String): String {
        val charArray = word.toCharArray()

        charArray.shuffle()
        while(String(charArray) == word) {
            charArray.shuffle()
        }
        return String(charArray)
    }

    fun reset() {
        userWords.clear()
        _state.value = GameUIState(
            currentScrambledWord = pickRandomAndShuffle(),
            score = 0,
            currentWordCount = 0
        )
    }

    fun skipWord() {
        updateGameState(_state.value.score)
        updateUserGuess("")
    }
}