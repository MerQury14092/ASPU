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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.merqury.aspu.ui.theme.SurfaceTheme
import com.merqury.aspu.ui.theme.theme


fun showFacultySelectModalWindow(
    onSelectFaculty: (selectedFaculty: NewsCategoryEnum) -> Unit
) {
    showSimpleModalWindow (
        Modifier.fillMaxWidth(.8f),
        containerColor = theme.value[SurfaceTheme.background]!!
    ) {
        NewsCategoryEnum.entries.forEach { entry ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .clickable {
                        onSelectFaculty(entry)
                        currentPage.intValue = 1
                        reloadNews()
                        it.value = false
                    }
            ) {
                Card (
                    colors = CardDefaults.cardColors(
                        containerColor = theme.value[SurfaceTheme.foreground]!!
                    )
                ){
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ){
                        Row(
                            modifier = Modifier.padding(5.dp)
                        ){
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
                                    .padding(horizontal = 5.dp),
                                color = theme.value[SurfaceTheme.text]!!
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
            }
        }
    }
}

fun showPageSelectModalWindow() {
    showSimpleModalWindow(
        Modifier.fillMaxWidth(.8f),
        containerColor = theme.value[SurfaceTheme.background]!!
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
                        fontSize = 25.sp,
                        color = theme.value[SurfaceTheme.text]!!
                    )
                }
                Divider(modifier = Modifier.fillMaxWidth(), color = theme.value[SurfaceTheme.divider]!!)
            }
        }
    }
}