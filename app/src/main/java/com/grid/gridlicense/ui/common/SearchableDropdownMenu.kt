package com.grid.gridlicense.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grid.gridlicense.R
import com.grid.gridlicense.data.DataModel
import com.grid.gridlicense.model.SettingsModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchableDropdownMenu(
        modifier: Modifier = Modifier,
        items: MutableList<DataModel> = mutableListOf(),
        label: String = "",
        selectedId: String? = null,
        showSelected: Boolean = true,
        enableSearch: Boolean = true,
        color: Color = SettingsModel.backgroundColor,
        leadingIcon: @Composable ((Modifier) -> Unit)? = null,
        onLeadingIconClick: () -> Unit = {},
        onSearch: (String) -> Unit = {},
        onSelectionChange: (DataModel) -> Unit = {},
) {
    var expandedState by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    var selectedItemState by remember { mutableStateOf(label) }
    LaunchedEffect(
        selectedId,
        items
    ) {
        if (showSelected && !selectedId.isNullOrEmpty()) {
            items.forEach {
                if (it.getId().equals(
                        selectedId,
                        ignoreCase = true
                    )
                ) {
                    selectedItemState = it.getName()
                }
            }
        } else {
            selectedItemState = label
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(color = color)
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .border(
                1.dp,
                Color.Black,
                RoundedCornerShape(10.dp)
            )
            .clickable {
                expandedState = !expandedState
            }) {
            leadingIcon?.invoke(
                Modifier
                    .padding(
                        start = 10.dp,
                        top = 10.dp,
                        bottom = 10.dp
                    )
                    .align(Alignment.CenterVertically)
                    .clickable {
                        onLeadingIconClick.invoke()
                        expandedState = false
                    })
            Text(
                modifier = Modifier
                    .padding(
                        start = 10.dp,
                        top = 10.dp,
                        bottom = 10.dp
                    )
                    .align(Alignment.CenterVertically),
                text = selectedItemState,
                style = TextStyle(
                    textDecoration = TextDecoration.None,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ),
                color = Color.Black
            )

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                Icons.Filled.ArrowDropDown,
                null,
                Modifier
                    .padding(
                        top = 10.dp,
                        bottom = 10.dp,
                        end = 10.dp
                    )
                    .align(Alignment.CenterVertically)
                    .rotate(if (expandedState) 180f else 0f),
                tint = Color.Black
            )
        }

        if (expandedState) {
            Surface(
                shadowElevation = 5.dp,
                modifier = Modifier.background(color = color)
            ) {
                Column {
                    if (enableSearch) {
                        OutlinedTextField(modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp),
                            value = searchText,
                            onValueChange = {
                                searchText = it
                            },
                            label = {
                                Text(
                                    "Search",
                                    color = SettingsModel.textColor
                                )
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Search
                            ),
                            keyboardActions = KeyboardActions(
                                onSearch = {
                                    onSearch.invoke(searchText)
                                },
                            ),
                            trailingIcon = {
                                IconButton(onClick = {
                                    if (searchText.isNotEmpty()) {
                                        searchText = ""
                                        onSearch.invoke(searchText)
                                    }
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "clear",
                                        tint = SettingsModel.buttonColor
                                    )
                                }
                            })
                    }

                    if (items.isNotEmpty()) {
                        LazyColumn(
                            modifier = modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .heightIn(
                                    min = 40.dp,
                                    max = 160.dp
                                ),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            items.forEach { dataObj ->
                                item {
                                    Text(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(40.dp)
                                            .clickable {
                                                onSelectionChange(dataObj)
                                                if (showSelected) selectedItemState = dataObj.getName()
                                                expandedState = false
                                            },
                                        text = dataObj.getName(),
                                        maxLines = 2,
                                        color = SettingsModel.textColor,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .padding(10.dp)
                                .background(color = Color.Transparent)
                                .clickable {
                                    onSearch.invoke("")
                                },
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Image(
                                modifier = Modifier.size(100.dp),
                                painter = painterResource(R.drawable.empty_result),
                                contentDescription = "clear"
                            )
                        }
                    }
                }
            }
        }
    }
}
