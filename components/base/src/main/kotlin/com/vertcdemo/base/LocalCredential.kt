package com.vertcdemo.base

import androidx.compose.runtime.compositionLocalOf

val LocalCredential = compositionLocalOf<CredentialViewModel> {
    error("CompositionLocal LocalCredential not present")
}
