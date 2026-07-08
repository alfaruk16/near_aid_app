import SwiftUI
import Shared

/// Hosts the shared Compose Multiplatform UI. `MainViewController()` (Kotlin, :shared/iosMain)
/// returns a `ComposeUIViewController { App() }` — the exact same Compose tree the Android app runs.
struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        ComposeView()
            .ignoresSafeArea() // Compose manages its own window insets
    }
}
