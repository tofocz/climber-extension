package io.hammerhead.climberextension.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.hammerhead.climberextension.R
import io.hammerhead.climberextension.theme.AppTheme

@Composable
fun MainScreen(onConfigureQuadData: (Int) -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
    ) {
        Text(text = stringResource(id = R.string.app_title), color = MaterialTheme.colorScheme.onBackground)
        Button(onClick = { onConfigureQuadData(1) }) {
            Text(text = stringResource(id = R.string.configure_quad_data_1))
        }
        Button(onClick = { onConfigureQuadData(2) }) {
            Text(text = stringResource(id = R.string.configure_quad_data_2))
        }
    }
}

@Preview(
    widthDp = 256,
    heightDp = 426,
)
@Composable
fun DefaultPreview() {
    AppTheme {
        MainScreen()
    }
}
