import SwiftUI

struct SubmittedView: View {
    @ObservedObject var viewModel: ScanViewModel

    var body: some View {
        VStack(spacing: 24) {
            Spacer()

            Image(systemName: "checkmark.circle.fill")
                .font(.system(size: 72))
                .foregroundColor(.green)

            Text(NSLocalizedString("submitted.title", comment: ""))
                .font(.title2)
                .fontWeight(.bold)

            Text(NSLocalizedString("submitted.body", comment: ""))
                .font(.subheadline)
                .foregroundColor(.secondary)
                .multilineTextAlignment(.center)
                .padding(.horizontal, 40)

            Text("+5 \(NSLocalizedString("submitted.credits", comment: ""))")
                .font(.title3)
                .fontWeight(.semibold)
                .foregroundColor(.blue)

            Spacer()

            Button {
                viewModel.reset()
            } label: {
                Text(NSLocalizedString("submitted.scanAnother", comment: ""))
                    .font(.headline)
                    .foregroundColor(.white)
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(Color.blue)
                    .cornerRadius(14)
            }
            .padding(.horizontal, 32)
            .padding(.bottom, 40)
        }
    }
}
