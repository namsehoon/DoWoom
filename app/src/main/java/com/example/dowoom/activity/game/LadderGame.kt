package com.example.dowoom.activity.game

import android.util.Log
import java.util.*
import kotlin.collections.ArrayList

//사다리 게임
class LadderGame {

        // 일자
         val STRAIGHT = 1
        // 오른쪽
         val TURN_RIGHT = 2
        // 왼쪽
         val TURN_LEFT = 3

        //사다리 생성
        fun generateLadder(width: Int, height: Int): Array<IntArray> {
            val random = Random() //랜덤
            val ladder = Array(height) { //세로 //특정 사이즈의 배열을 생성
                IntArray(
                    width //가로
                )
            }
            val pathStack: Stack<Int> =
                Stack<Int>() // The Stack class represents a last-in-first-out
            for (i in 0 until height) {
                for (j in 0 until width) {
                    if (!pathStack.isEmpty()) {
                        ladder[i][j] = TURN_LEFT
                        pathStack.pop()
                    } else {
                        var path: Int = random.nextInt(2) + 1
                        while (!isValid(path, i, j, width, height)) {
                            path = random.nextInt(2) + 1
                        }
                        if (path == TURN_RIGHT) pathStack.push(path)
                        ladder[i][j] = path
                    }
                }
            }
            return ladder
        }

    //사다리 그리기
        fun printLadder(ladder: Array<IntArray>) {
            for (i in ladder.indices) { // 배열의 범위를 출력
                for (j in 0 until ladder[0].size) {
                    print(ladder[i][j].toString() + " ")
                }
                println()
            }
        }

        fun isValid(path: Int, i: Int, j: Int, width: Int, height: Int): Boolean {
            if (path == TURN_RIGHT && j == width - 1) return false
            return if (path == TURN_LEFT && j == 0) false else true
        }

        //사다리 게임 결과
        fun visitLadder(ladder: Array<IntArray>, start: Int): Int {
            var dest = -1
            var cursorX = start
            var cursorY = 0
            if (start < ladder[0].size) {
                while (cursorY < ladder.size) {
                    if (ladder[cursorY][cursorX] == TURN_RIGHT) { // 2 오른쪽
                        cursorX++
                    } else if (ladder[cursorY][cursorX] == TURN_LEFT) { //3 왼쪽
                        cursorX--
                    }
                    cursorY++
                }
                dest = cursorX

            }
            return dest
        }


    fun randomPresent(ladder: Array<IntArray>) : ArrayList<Int> {
        //결과 저장
        val resultList:ArrayList<Int> = ArrayList<Int>()

        for (number in 0..5) {
            val result = visitLadder(ladder, number)
            resultList.add(number,result)
        }
        return resultList

    }


    //width : 가로 줄 개수(사진 개수) , height()
    init {
        // 결과
//        var ladder = generateLadder(6, 6)
//        // 시작
//        val start = 2
//        // 결과
//        randomPresent(ladder)
//
//        printLadder(ladder)
    }
}

// 0 1 2 3 4 시작

// 0 1 2 3 4 끝