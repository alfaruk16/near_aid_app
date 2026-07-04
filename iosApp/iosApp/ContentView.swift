import SwiftUI
import Shared

struct ContentView: View {
    // A model that crosses the KMM boundary from :core:model (commonMain).
    private let tokens = AuthTokens(accessToken: "demo-access-token", refreshToken: "demo-refresh-token")

    var body: some View {
        VStack(spacing: 16) {
            Text("NearAid — KMM walking skeleton")
                .font(.headline)
                .multilineTextAlignment(.center)

            // expect/actual: resolves to the iOS actual in :core:model/iosMain.
            Text("Running on: \(PlatformKt.platform())")

            // Shared @Serializable model instantiated from Swift.
            Text("Shared AuthTokens.accessToken:")
                .font(.subheadline)
            Text(tokens.accessToken)
                .font(.system(.body, design: .monospaced))
                .foregroundColor(.secondary)
        }
        .padding()
    }
}

#Preview {
    ContentView()
}
