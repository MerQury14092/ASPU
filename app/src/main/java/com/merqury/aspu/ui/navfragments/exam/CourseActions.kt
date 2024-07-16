package com.merqury.aspu.ui.navfragments.exam

import com.merqury.aspu.appContext
import com.merqury.aspu.services.exam.getQuizzes
import com.merqury.aspu.services.exam.logoutCourseById
import com.merqury.aspu.services.exam.models.ExamCourse
import com.merqury.aspu.services.exam.models.ExamQuiz
import com.merqury.aspu.services.quiz.getResults
import com.merqury.aspu.ui.makeToast
import com.merqury.aspu.ui.navfragments.quiz.QuizScreen
import com.merqury.aspu.ui.navfragments.quiz.showResults
import com.merqury.aspu.ui.showSelectListDialog
import com.merqury.aspu.ui.showSelectListDialogWithClickAnimation
import com.merqury.aspu.ui.startTopBarActivityWithActivityLink

fun showCourseActionsModalWindow(
    course: ExamCourse
) {
    showSelectListDialogWithClickAnimation(mapOf(
        "Открыть экзамен" to {
            getQuizzes(
                course.id,
                {
                    appContext!!.makeToast("Error")
                }
            ){ quizzes ->
                it.value = true
                showSelectQuizModalWindow(quizzes)
            }
        },
        "Участники" to {

        },
        "Результаты" to {
            getQuizzes(
                course.id,
                {
                    appContext!!.makeToast("Error")
                }
            ){ quizzes ->
                it.value = true
                showSelectQuizResultsModalWindows(quizzes)
            }
        },
        "Удалить из моих" to {
            logoutCourseById(
                course.id,
                {}
            ) {
                it.value = true
                myCourses = null
            }
        }
    ))
}

fun showSelectQuizModalWindow(
    quizzes: List<ExamQuiz>
) {
    showSelectListDialog(
        quizzes.associate {
            it.name to {
                openQuiz(it.id)
            }
        }
    )
}

fun showSelectQuizResultsModalWindows(
    quizzes: List<ExamQuiz>
){
    showSelectListDialogWithClickAnimation(
        quizzes.associate { quiz ->
            quiz.name to { loaded ->
                getResults(
                    quiz.id,
                    {},
                    {
                        loaded.value = true
                        showResults(it)
                    }
                )
            }
        }
    )
}

fun openQuiz(
    quizId: Int
) {
    appContext!!.startTopBarActivityWithActivityLink { header, activity ->
        QuizScreen(header = header, quizId = quizId){
            activity!!.finish()
        }
    }
}