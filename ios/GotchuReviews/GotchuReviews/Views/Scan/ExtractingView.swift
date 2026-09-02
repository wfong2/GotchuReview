import SwiftUI

struct ExtractingView: View {
    var body: some View {
        VStack(spacing: 24) {
            Spacer()

            ProgressView()
                .scaleEffect(1.5)

            Text(NSLocalizedString("scan.extracting", comment: ""))
                .font(.title3)
                .fontWeight(.medium)

            Text(NSLocalizedString("scan.extractingDesc", comment: ""))
                .font(.subheadline)
                .foregroundColor(.secondary)
                .multilineTextAlignment(.center)
                .padding(.horizontal, 40)

            Spacer()
        }
    }
}
