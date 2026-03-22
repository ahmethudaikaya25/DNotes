package com.duhapp.dnotes.ui.custom_views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duhapp.dnotes.R
import com.duhapp.dnotes.features.base.ui.DialogFragmentState

@Composable
fun ErrorDialogScreen(
    dialogState: DialogFragmentState,
    onDismiss: () -> Unit
) {
    when (dialogState) {
        is DialogFragmentState.OptionDialog -> {
            OptionDialogContent(
                titleRes = dialogState.title,
                messageRes = dialogState.message,
                positiveButtonText = dialogState.positiveButtonText,
                onPositiveClick = {
                    dialogState.positiveButtonAction()
                    onDismiss()
                },
                negativeButtonText = dialogState.negativeButtonText,
                onNegativeClick = {
                    dialogState.negativeButtonAction()
                    onDismiss()
                },
                onDismiss = onDismiss
            )
        }

        is DialogFragmentState.OneButtonDialog -> {
            OneButtonDialogContent(
                titleRes = dialogState.title,
                messageRes = dialogState.message,
                okButtonText = dialogState.okButtonText,
                onOkClick = {
                    dialogState.okButtonAction()
                    onDismiss()
                },
                onDismiss = onDismiss
            )
        }

        is DialogFragmentState.InformativeDialog -> {
            InformativeDialogContent(
                titleRes = dialogState.title,
                messageRes = dialogState.message,
                onDismiss = onDismiss
            )
        }
    }
}

@Composable
fun OptionDialogContent(
    titleRes: Int,
    messageRes: Int,
    positiveButtonText: Int,
    onPositiveClick: () -> Unit,
    negativeButtonText: Int,
    onNegativeClick: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(titleRes),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = stringResource(messageRes),
                fontSize = 14.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onPositiveClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(stringResource(positiveButtonText))
            }
        },
        dismissButton = {
            Button(
                onClick = onNegativeClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(stringResource(negativeButtonText))
            }
        }
    )
}

@Composable
fun OneButtonDialogContent(
    titleRes: Int,
    messageRes: Int,
    okButtonText: Int,
    onOkClick: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(titleRes),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = stringResource(messageRes),
                fontSize = 14.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onOkClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(stringResource(okButtonText))
            }
        }
    )
}

@Composable
fun InformativeDialogContent(
    titleRes: Int,
    messageRes: Int,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(titleRes),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = stringResource(messageRes),
                fontSize = 14.sp
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}
