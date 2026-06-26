# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Build debug AAR
./gradlew :paymentSDK:assembleDebug

# Build release AAR
./gradlew :paymentSDK:assembleRelease

# Publish locally (generates AAR with POM)
./gradlew :paymentSDK:publishToMavenLocal

# Run unit tests
./gradlew :paymentSDK:test

# Run instrumented tests (requires connected device/emulator)
./gradlew :paymentSDK:connectedAndroidTest

# Build the demo app
./gradlew :app:assembleDebug

# Clean build
./gradlew clean
```

The SDK is distributed via JitPack. The version is set in two places: `versionName` in `paymentSDK/build.gradle` (currently `1.9.15`) and the `version` in the `publishing` block (currently `1.0.31`) — these should be kept in sync when cutting a release.

## Architecture Overview

This is an Android SDK library (`paymentSDK` module) with a demo app (`app` module).

### Entry Points — Two Initiation Modes

**1. Config-based initiation** (`GQPaymentSDK.initiate`):
- Caller passes a `config` JSONObject (auth credentials, student_id, env, customization, etc.) and an optional `prefill` JSONObject
- If `customer_number` is present in config, the SDK calls `POST v1/customer/create-customer` to look up or create the customer, then launches `GQWebActivity`
- If no `customer_number`, it skips the API call and launches `GQWebActivity` directly with `user=new`

**2. Token-based initiation** (`GQPaymentSDK.tokenCheckout`):
- Caller passes a Bearer token and environment string
- SDK calls `GET v1/pp/get-session-data` to exchange the token for a `session_code`
- Launches `GQWebActivity` with the session code, which constructs a simpler URL (`?_code=<session_code>&s=asdk&_v=<VERSION>`)

### Core Flow

`GQPaymentSDK` (static singleton-style class) → validates inputs → API call if needed → starts `GQWebActivity`

`GQWebActivity` → builds a URL for the GrayQuest web SDK → loads it in a `WebView` → registers a `JavascriptInterface` (`GQPaymentSDKInterface` exposed as `"Gqsdk"`) so the web page can call back into native code

The web page calls JavaScript functions on the interface:
- `Gqsdk.sdkSuccess(data)` → `GQWebActivity.sdkSuccess()` → `GQPaymentSDK.successSDK()` → `GQPaymentSDKListener.onSuccess()`
- `Gqsdk.sdkError(data)` → `GQPaymentSDKListener.onFailed()`
- `Gqsdk.sdkCancel(data)` → `GQPaymentSDKListener.onCancel()`
- `Gqsdk.sendADOptions(data)` → triggers Razorpay/auto-debit native checkout
- `Gqsdk.sendPGOptions(data)` → routes to one of three PG SDKs based on `name` field

### Payment Gateway Routing (in `GQWebActivity.PGOptions`)

The web SDK sends a `name` field to select the payment gateway:
- `CASHFREE` → `doDropCheckoutPayment()` using the Cashfree SDK (web checkout)
- `UNIPG` → `ADOptions()` → Razorpay checkout
- `EASEBUZZ` → `ezPGCheckout()` using `PWECouponsActivity`
- Anything else → opens a payment link URL in `GQWebActivity_Sec` (a simple WebView wrapper)

After payment, results are sent back to the WebView via `webSdk.evaluateJavascript()`:
- `javascript:sendADPaymentResponse(...)` — for Razorpay/AD payments
- `javascript:sendPGPaymentResponse(...)` — for Cashfree/Easebuzz payments

### Environment Configuration (`Environment.java`)

The `env` string passed by the caller determines all URLs:
- `"test"` (default) → UAT (`uat.graydev.in`)
- `"stage"` → Stage (`graydev.tech/stage`)
- `"preprod"` → Pre-prod (`graydev.tech/preprod`)
- `"live"` → Production (`grayquest.com`)

`Environment.VERSION` is the SDK version string appended as `_v` to all web URLs.

### Key Classes

| Class | Role |
|---|---|
| `GQPaymentSDK` | Static entry point; validates config, manages customer creation/session, holds static listener reference |
| `GQWebActivity` | Main activity; WebView host; handles all PG callbacks and JS bridge |
| `GQPaymentSDKInterface` | `@JavascriptInterface` bridge — web → native |
| `GQPaymentSDKListener` | Interface the host app implements (`onSuccess`, `onFailed`, `onCancel`) |
| `Environment` | Manages URL configuration per environment |
| `API_Client` | Retrofit instance builder (base URL pulled from `Environment.BASE_URL`) |
| `ApiInterface` | Retrofit interface: `createCustomer` and `sessionCode` endpoints |
| `GQWebActivity_Sec` | Secondary WebView for external payment links |
| `ADPayment` | Legacy Razorpay activity (appears superseded by inline handling in `GQWebActivity`) |

### Important Implementation Notes

- `GQPaymentSDK.gqPaymentSDKListener` is set by casting the `Context` passed to `initiate()`/`tokenCheckout()` — the calling `Activity` **must** implement `GQPaymentSDKListener`
- Auth is Base64-encoded as `client_id:client_secret_key` and passed as the `abase` URL query parameter (not an Authorization header) to the web SDK
- The `GQPaymentSDKInterface` casts its `Context` to `GQWebActivity` — it only works when instantiated with a `GQWebActivity` context
- `GQWebActivity_New` is declared in the manifest but appears to be an experimental/unused activity
