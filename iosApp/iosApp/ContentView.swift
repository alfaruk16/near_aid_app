import SwiftUI
import Shared

/// Bridges a shared MVI `ViewModel` (Kotlin, `commonMain`) to SwiftUI.
///
/// `PhoneViewModel` is the exact same class the Android app uses. SKIE turns its Kotlin
/// `StateFlow<PhoneState>` into a Swift `AsyncSequence`, so we just `for await` the state and
/// republish it; user actions go back through `onIntent`.
@MainActor
final class PhoneModel: ObservableObject {
    private let viewModel = SharedViewModels.shared.phone()
    @Published private(set) var state: PhoneState

    init() {
        state = viewModel.state.value
        Task { [weak self] in
            guard let self else { return }
            for await next in viewModel.state {
                self.state = next
            }
        }
    }

    // SKIE flattens Kotlin sealed members to top-level Swift types (`PhoneIntentPhoneChanged`,
    // `PhoneIntentSubmit`) — `PhoneIntent` itself is the Swift protocol.
    func phoneChanged(_ value: String) {
        viewModel.onIntent(intent: PhoneIntentPhoneChanged(value: value))
    }

    func submit() {
        viewModel.onIntent(intent: PhoneIntentSubmit())
    }
}

struct ContentView: View {
    @StateObject private var model = PhoneModel()

    var body: some View {
        VStack(spacing: 16) {
            Text("NearAid — shared ViewModel on iOS")
                .font(.headline)
                .multilineTextAlignment(.center)

            Text("PhoneViewModel is Kotlin (commonMain), driven from SwiftUI via Koin + SKIE.")
                .font(.footnote)
                .foregroundColor(.secondary)
                .multilineTextAlignment(.center)

            TextField("01XXXXXXXXX", text: Binding(
                get: { model.state.phone },
                set: { model.phoneChanged($0) }
            ))
            .textFieldStyle(.roundedBorder)
            .keyboardType(.numberPad)

            Button(action: { model.submit() }) {
                if model.state.loading {
                    ProgressView()
                } else {
                    Text("Request OTP")
                }
            }
            .disabled(!model.state.canSubmit)
            .buttonStyle(.borderedProminent)

            if let error = model.state.error {
                Text(error)
                    .foregroundColor(.red)
                    .font(.footnote)
                    .multilineTextAlignment(.center)
            }
        }
        .padding()
    }
}

#Preview {
    ContentView()
}
