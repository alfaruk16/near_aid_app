#!/usr/bin/env bash
#
# On-hardware proof for the BLE proximity handoff (:core:proximity).
#
# The emulator and JVM unit tests have no BLE radio, so the handoff can only be proven on real
# phones. This script drives BleProximityHardwareTest#twoDevices_confirmHandoffOverRealBle on TWO
# attached Android phones AT THE SAME TIME — each advertises the same claim's HandoffToken and scans
# for the other; each must resolve ProximityResult.Confirmed. Running both instrumentations in
# parallel is what guarantees their advertise/scan windows overlap.
#
# Usage:
#   scripts/ble-proximity-proof.sh                 # auto-detect exactly two devices
#   scripts/ble-proximity-proof.sh SERIAL_A SERIAL_B
#
# Prereqs on BOTH phones: developer/USB debugging on, Bluetooth ON, in radio range (< a few metres).
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"

SDK="${ANDROID_HOME:-${ANDROID_SDK_ROOT:-$HOME/Library/Android/sdk}}"
ADB="$SDK/platform-tools/adb"
[ -x "$ADB" ] || ADB="$(command -v adb || true)"
[ -n "$ADB" ] && [ -x "$ADB" ] || { echo "ERROR: adb not found (looked in $SDK/platform-tools). Set ANDROID_HOME."; exit 1; }

TEST_PKG="com.nearaid.core.proximity.test"
RUNNER="androidx.test.runner.AndroidJUnitRunner"
TEST_CLASS="com.nearaid.core.proximity.BleProximityHardwareTest#twoDevices_confirmHandoffOverRealBle"
APK="core/proximity/build/outputs/apk/androidTest/debug/proximity-debug-androidTest.apk"

# --- Resolve the two target devices -------------------------------------------------------------
if [ "$#" -eq 2 ]; then
  DEV_A="$1"; DEV_B="$2"
else
  DEVICES=()
  while IFS= read -r line; do
    [ -n "$line" ] && DEVICES+=("$line")
  done < <("$ADB" devices | awk 'NR>1 && $2=="device" {print $1}')
  if [ "${#DEVICES[@]}" -ne 2 ]; then
    echo "ERROR: need exactly 2 authorized devices attached, found ${#DEVICES[@]}:"
    "$ADB" devices
    echo "Attach two phones (or pass serials explicitly) and retry."
    exit 1
  fi
  DEV_A="${DEVICES[0]}"; DEV_B="${DEVICES[1]}"
fi
echo ">> Devices: A=$DEV_A  B=$DEV_B"

# --- Build the self-instrumenting androidTest APK ------------------------------------------------
echo ">> Building instrumented test APK..."
./gradlew :core:proximity:assembleDebugAndroidTest --console=plain -q
[ -f "$APK" ] || { echo "ERROR: test APK not found at $APK"; exit 1; }

check_bt() {
  local serial="$1"
  # 1 == on. Best-effort nudge if off; some OEMs/newer Android refuse `svc bluetooth` without root.
  local state
  state="$("$ADB" -s "$serial" shell settings get global bluetooth_on 2>/dev/null | tr -d '\r')"
  if [ "$state" != "1" ]; then
    echo ">> [$serial] Bluetooth appears OFF — attempting to enable..."
    "$ADB" -s "$serial" shell svc bluetooth enable >/dev/null 2>&1 || true
    sleep 2
    state="$("$ADB" -s "$serial" shell settings get global bluetooth_on 2>/dev/null | tr -d '\r')"
    [ "$state" = "1" ] || echo ">> [$serial] WARNING: could not confirm Bluetooth is ON — enable it manually or the test will fail fast."
  fi
}

install_and_run() {
  local serial="$1" logfile="$2"
  "$ADB" -s "$serial" install -r -t "$APK" >/dev/null
  # -w blocks until the run finishes; the test's own 30s timeout bounds it.
  "$ADB" -s "$serial" shell am instrument -w -r \
    -e class "$TEST_CLASS" \
    "$TEST_PKG/$RUNNER" > "$logfile" 2>&1
}

check_bt "$DEV_A"
check_bt "$DEV_B"

LOG_A="$(mktemp "${TMPDIR:-/tmp}/ble-proof-A.XXXXXX")"
LOG_B="$(mktemp "${TMPDIR:-/tmp}/ble-proof-B.XXXXXX")"
echo ">> Installing + running on both phones in parallel (up to ~40s)..."
install_and_run "$DEV_A" "$LOG_A" &
PID_A=$!
install_and_run "$DEV_B" "$LOG_B" &
PID_B=$!
# Don't let a non-zero instrument exit trip `set -e` before we can read the logs.
RC_A=0; wait "$PID_A" || RC_A=$?
RC_B=0; wait "$PID_B" || RC_B=$?

# am instrument reports pass/fail in its stream, not always via exit code — grep the result.
verdict() {
  local serial="$1" log="$2"
  echo "------------------------------------------------------------"
  echo "Device $serial:"
  # Order matters: an assumption-violated SKIP also prints "OK (1 test)", so check skip FIRST.
  if grep -q "AssumptionViolatedException\|assumption_violated\|No peer confirmed" "$log"; then
    echo "  SKIPPED — no peer confirmed (was the OTHER phone running with BT on, in range?)."
  elif grep -q "OK (1 test)" "$log"; then
    echo "  PASS — handoff Confirmed over real BLE."
  else
    echo "  FAIL — see log below."
  fi
  grep -E "INSTRUMENTATION_STATUS: (stack|stream)=|Confirmed|Timeout|OK \(|FAILURES|Bluetooth" "$log" | sed 's/^/    /' | head -30
  echo "  (full log: $log)"
}

verdict "$DEV_A" "$LOG_A"
verdict "$DEV_B" "$LOG_B"
echo "------------------------------------------------------------"

# A real pass is "OK (1 test)" with NO assumption-violated skip in the same log.
passed() { grep -q "OK (1 test)" "$1" && ! grep -q "AssumptionViolatedException\|No peer confirmed" "$1"; }
if passed "$LOG_A" && passed "$LOG_B"; then
  echo "RESULT: PROVEN — both phones resolved ProximityResult.Confirmed off each other."
  exit 0
fi
echo "RESULT: NOT proven on this run. Check Bluetooth is ON on both phones and they are within a few metres."
echo "        (Exit codes: A=$RC_A B=$RC_B)"
exit 1
