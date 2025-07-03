package com.vertcdemo.login.ui

import android.content.Context
import android.text.Annotation
import android.text.SpannedString
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vertcdemo.base.KEY_CREDENTIAL
import com.vertcdemo.base.LocalCredential
import com.vertcdemo.base.RouteHome
import com.vertcdemo.base.RouteLogin
import com.vertcdemo.base.StatusBarColor
import com.vertcdemo.base.ui.ProgressDialog
import com.vertcdemo.base.utils.LocalNavController
import com.vertcdemo.login.BuildConfig.APP_VERSION_NAME
import com.vertcdemo.login.BuildConfig.PRIVACY_POLICY_URL
import com.vertcdemo.login.BuildConfig.TERMS_OF_SERVICE_URL
import com.vertcdemo.login.R

private val USER_NAME_REGEX = "^[\\u4e00-\\u9fa5a-zA-Z0-9@_-]+$".toRegex()

@Composable
fun PageLogin(
    modifier: Modifier = Modifier
) {
    StatusBarColor(darkIcons = true)

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val credential = LocalCredential.current

    val viewModel: LoginViewModel = viewModel(
        factory = LoginViewModel.Factory,
        extras = MutableCreationExtras().apply {
            set(KEY_CREDENTIAL, credential)
        }
    )

    val uiState = viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .paint(
                painter = painterResource(R.drawable.login_page_background),
                contentScale = ContentScale.FillBounds,
            )
            .padding(16.dp)
    ) {
        Image(
            modifier = Modifier.padding(top = 88.dp),
            painter = painterResource(R.drawable.app_logo),
            contentDescription = null,
        )
        Text(
            modifier = Modifier.padding(top = 15.dp),
            text = stringResource(R.string.welcome),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            style = TextStyle(
                color = Color(0xFF020814)
            )
        )

        Text(
            modifier = Modifier.padding(top = 24.dp),
            text = stringResource(R.string.username),
            fontSize = 14.sp,
            style = TextStyle(
                color = Color(0xFF1D2129)
            )
        )

        var username by rememberSaveable(stateSaver = TextFieldValue.Saver) {
            mutableStateOf(value = TextFieldValue(text = ""))
        }

        val isUsernameError by remember {
            derivedStateOf {
                username.text.isNotEmpty() && !USER_NAME_REGEX.matches(username.text)
            }
        }

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            singleLine = true,
            textStyle = TextStyle(
                fontSize = 14.sp,
                color = Color(0xFF020814),
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFC9CDD4),
            ),
            supportingText = {
                if (isUsernameError) {
                    Text(
                        text = stringResource(R.string.content_limit, "18"),
                        fontSize = 14.sp,
                        color = Color(0xFFDB373F)
                    )
                }
            },
            isError = isUsernameError,
            placeholder = {
                Text(
                    text = stringResource(R.string.username_hint),
                    fontSize = 14.sp,
                    color = Color(0xFF737578),
                )
            })

        var agreed by remember {
            mutableStateOf(false)
        }

        Row(
            modifier = Modifier.padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = agreed,
                onCheckedChange = {
                    agreed = it
                },
            )

            Text(
                modifier = Modifier.fillMaxWidth(), style = TextStyle(
                    color = Color(0xFF74767B),
                ),

                text = context.readAndAgreeText()
            )
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF346CF9),
                contentColor = Color(0xFFFFFFFF),
                disabledContainerColor = Color(0x66346CF9),
                disabledContentColor = Color(0x66FFFFFF),
            ),
            enabled = agreed && username.text.isNotEmpty() && !isUsernameError,
            onClick = {
                viewModel.login(username.text)
            },
        ) {
            Text(
                text = stringResource(R.string.log_in),
                fontSize = 16.sp,
            )
        }

        Spacer(
            modifier = Modifier
                .fillMaxHeight()
                .weight(weight = 1.0F)
        )

        Text(
            text = stringResource(R.string.login_app_version, APP_VERSION_NAME),
            style = TextStyle(
                color = Color(0xFF74767B),
            ),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth()
        )
    }

    val navController = LocalNavController.current
    if (uiState.value == LoginUiState.LOADING) {
        ProgressDialog(onDismissRequest = {})
    } else if (uiState.value == LoginUiState.SUCCESS) {
        navController.navigate(RouteHome) {
            popUpTo(RouteLogin) {
                inclusive = true
            }
        }
    }
}

fun Context.readAndAgreeText(@StringRes textId: Int = R.string.read_and_agree): AnnotatedString {
    val spanned = getText(textId) as SpannedString
    val linkStyles = TextLinkStyles(style = SpanStyle(color = Color(0xFF346CF9)))

    return buildAnnotatedString {
        append(spanned)

        spanned.getSpans(
            0, spanned.length, Annotation::class.java
        ).forEach { span ->
            when (span.value) {
                "terms_of_service" -> {
                    addLink(
                        url = LinkAnnotation.Url(
                            TERMS_OF_SERVICE_URL, linkStyles
                        ), start = spanned.getSpanStart(span), end = spanned.getSpanEnd(span)
                    )
                }

                "privacy_policy" -> {
                    addLink(
                        url = LinkAnnotation.Url(
                            PRIVACY_POLICY_URL, linkStyles
                        ), start = spanned.getSpanStart(span), end = spanned.getSpanEnd(span)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PageLoginPreview() {
    PageLogin()
}