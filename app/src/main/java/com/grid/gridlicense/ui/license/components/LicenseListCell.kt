package com.grid.gridlicense.ui.license.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CorporateFare
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.PermDeviceInformation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grid.gridlicense.data.client.Client
import com.grid.gridlicense.data.license.License
import com.grid.gridlicense.model.LicenseModel
import com.grid.gridlicense.model.SettingsModel
import com.grid.gridlicense.ui.theme.GridLicenseTheme
import com.grid.gridlicense.ui.theme.LightGrey
import java.util.Date

@Composable
fun LicenseListCell(
        modifier: Modifier = Modifier,
        licenseModel: LicenseModel,
        onEdit: (Int) -> Unit = {},
        onRemove: (Int) -> Unit = {}
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = LightGrey,
        ),
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 5.dp,
                    top = 5.dp,
                    end = 5.dp
                ),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 5.dp),
                imageVector = Icons.Default.Person,
                contentDescription = "Client",
                tint = SettingsModel.textColor
            )
            Text(
                text = licenseModel.getClientName(),
                modifier = Modifier
                    .weight(1f)
                    .wrapContentHeight(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Start,
                style = TextStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp
                ),
                color = SettingsModel.textColor
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp),
            horizontalArrangement = Arrangement.Center,
            Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 5.dp),
                imageVector = Icons.Default.CorporateFare,
                contentDescription = "Company",
                tint = SettingsModel.textColor
            )
            Text(
                text = licenseModel.getCompany(),
                modifier = Modifier
                    .weight(1f)
                    .wrapContentHeight(),
                maxLines = 1,
                textAlign = TextAlign.Start,
                style = TextStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp
                ),
                color = SettingsModel.textColor
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp),
            horizontalArrangement = Arrangement.Center,
            Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 5.dp),
                imageVector = Icons.Default.PermDeviceInformation,
                contentDescription = "Device ID",
                tint = SettingsModel.textColor
            )
            Text(
                text = licenseModel.getDeviceID(),
                modifier = Modifier
                    .weight(1f)
                    .wrapContentHeight(),
                maxLines = 1,
                textAlign = TextAlign.Start,
                style = TextStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp
                ),
                color = SettingsModel.textColor
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp),
            horizontalArrangement = Arrangement.Center,
            Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 5.dp),
                imageVector = Icons.Rounded.DateRange,
                contentDescription = "Expiry Date",
                tint = SettingsModel.textColor
            )
            Text(
                text = "EX. Date: ${licenseModel.getExpiryDate()}",
                modifier = Modifier
                    .weight(1f)
                    .wrapContentHeight(),
                maxLines = 1,
                textAlign = TextAlign.Start,
                style = TextStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp
                ),
                color = SettingsModel.textColor
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 5.dp,
                    bottom = 5.dp,
                    end = 5.dp
                ),
            horizontalArrangement = Arrangement.Center,
            Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 5.dp),
                imageVector = Icons.Filled.DateRange,
                contentDescription = "Created Date",
                tint = SettingsModel.textColor
            )
            Text(
                text = "CR. Date: ${licenseModel.getCreatedDate()}",
                modifier = Modifier
                    .weight(1f)
                    .wrapContentHeight(),
                maxLines = 1,
                textAlign = TextAlign.Start,
                style = TextStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp
                ),
                color = SettingsModel.textColor
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LicenseListCellPreview() {
    GridLicenseTheme {
        LicenseListCell(
            Modifier,
            LicenseModel(
                License(
                    licenseid = "1",
                    company = "Vianeos",
                    deviseid = "123456",
                    expirydate = Date()
                ),
                Client(
                    clientid = "1",
                    clientName = "Bilal"
                )
            )
        )
    }
}