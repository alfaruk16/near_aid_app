import SwiftUI
import Shared

@main
struct iOSApp: App {
    init() {
        // Start the shared Koin graph once, at launch. This is the iOS counterpart of the Android
        // app's `startKoin` — the same commonMain modules (network/data/domain/features), with the
        // backend URLs supplied here (Android reads them from BuildConfig).
        // Point these at your local backend; the Django dev server defaults to :8000.
        KoinKt.doInitKoin(
            baseUrl: "http://localhost:8000/v1/",
            wsUrl: "ws://localhost:8000/ws"
        )
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
