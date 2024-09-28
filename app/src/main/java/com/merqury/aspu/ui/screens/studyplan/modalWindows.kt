package com.merqury.aspu.ui.screens.studyplan

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.merqury.aspu.appContext
import com.merqury.aspu.ui.startTopBarActivity

fun showSelectPlanWindow() {
    appContext.startTopBarActivity {
        StudyPlanScreen(planId = 3677, header = it)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectPlan(onSelect: (String) -> Unit) {
    var query by remember {
        mutableStateOf("")
    }

}

