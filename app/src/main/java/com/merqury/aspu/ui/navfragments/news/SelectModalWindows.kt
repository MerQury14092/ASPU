package com.merqury.aspu.ui.navfragments.news

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.merqury.aspu.enums.NewsCategoryEnum
import com.merqury.aspu.ui.showSimpleModalWindow


fun showFacultySelectModalWindow(
    onSelectFaculty: (selectedFaculty: NewsCategoryEnum) -> Unit
) {
    showSimpleModalWindow (
        Modifier.fillMaxWidth(.8f),
    ) {
        NewsCategoryEnum.entries.forEach { entry ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clickable {
                        onSelectFaculty(entry)
                        currentPage.intValue = 1
                        reloadNews()
                        it.value = false
                    }
            ) {
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ){
                    Row{
                        Image(
                            painter = painterResource(id = entry.logo),
                            contentDescription = "",
                            modifier = Modifier.size(35.dp),
                            contentScale = ContentScale.Fit
                        )
                        Text(
                            text = entry.localizedName,
                            textAlign = TextAlign.Center,
                            fontSize = 25.sp,
                            modifier = Modifier
                                .padding(start = 5.dp, end = 5.dp)
                        )
                        Image(
                            painter = painterResource(id = entry.logo),
                            contentDescription = "",
                            modifier = Modifier.size(35.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }
            Divider(modifier = Modifier.fillMaxWidth())
        }
    }
}

fun showPageSelectModalWindow() {
    showSimpleModalWindow(
        Modifier.fillMaxWidth(.8f),
    ) {
        val pageSelectDialogVisible = it
        LazyColumn() {
            items(count = countPages.intValue) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clickable {
                            currentPage.intValue = it + 1
                            pageSelectDialogVisible.value = false
                            reloadNews()
                        }
                ) {
                    Text(
                        text = (it + 1).toString(),
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