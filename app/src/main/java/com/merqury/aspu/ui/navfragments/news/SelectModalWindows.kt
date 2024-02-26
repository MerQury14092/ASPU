package com.merqury.aspu.ui.navfragments.news

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.merqury.aspu.enums.NewsCategoryEnum
import com.merqury.aspu.ui.ModalWindow


@Composable
fun FacultySelectModalWindow(
    facultySelectDialogVisible: MutableState<Boolean>,
    selectedFaculty: MutableState<NewsCategoryEnum>
) {
    ModalWindow(
        Modifier.fillMaxWidth(),
        onDismiss = {
            facultySelectDialogVisible.value = false
        }
    ) {
        NewsCategoryEnum.entries.forEach { entry ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clickable {
                        selectedFaculty.value = entry
                        currentPage.intValue = 1
                    }
            ) {
                Text(
                    text = entry.localizedName,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 25.sp
                )
            }
            Divider(modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
fun PageSelectModalWindow(
    pageSelectDialogVisible: MutableState<Boolean>
){
    ModalWindow(
        Modifier.fillMaxWidth(),
        onDismiss = {
            pageSelectDialogVisible.value = false
        }
    ) {
        LazyColumn(){
            items(count = countPages.intValue){
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clickable {
                            currentPage.intValue = it+1
                            pageSelectDialogVisible.value = false
                        }
                ) {
                    Text(
                        text = (it+1).toString(),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 25.sp
                    )
                }
                Divider(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}