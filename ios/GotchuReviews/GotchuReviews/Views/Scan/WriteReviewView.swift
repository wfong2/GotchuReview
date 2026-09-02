import SwiftUI

struct WriteReviewView: View {
    @ObservedObject var viewModel: ScanViewModel
    @EnvironmentObject var authViewModel: AuthViewModel
    @State private var isSubmitting = false

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 20) {
                // Header
                if let result = viewModel.extractionResult {
                    VStack(alignment: .leading, spacing: 4) {
                        Text("\(NSLocalizedString("review.reviewing", comment: "")) \(result.extracted.contractorName)")
                            .font(.headline)
                        Text("\(NSLocalizedString("review.work", comment: "")) \(result.extracted.description)")
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                    }
                }

                Divider()

                // Star ratings
                Text(NSLocalizedString("review.rateExperience", comment: ""))
                    .font(.headline)

                InteractiveStarRating(
                    rating: $viewModel.ratingQuality,
                    label: NSLocalizedString("rating.quality", comment: "")
                )
                InteractiveStarRating(
                    rating: $viewModel.ratingCommunication,
                    label: NSLocalizedString("rating.communication", comment: "")
                )
                InteractiveStarRating(
                    rating: $viewModel.ratingTimeliness,
                    label: NSLocalizedString("rating.timeliness", comment: "")
                )
                InteractiveStarRating(
                    rating: $viewModel.ratingValue,
                    label: NSLocalizedString("rating.value", comment: "")
                )

                Divider()

                // Title
                Text(NSLocalizedString("review.titleLabel", comment: ""))
                    .font(.subheadline)
                    .foregroundColor(.secondary)
                TextField(
                    NSLocalizedString("review.titlePlaceholder", comment: ""),
                    text: $viewModel.reviewTitle
                )
                .textFieldStyle(.roundedBorder)

                // Body
                Text(NSLocalizedString("review.bodyLabel", comment: ""))
                    .font(.subheadline)
                    .foregroundColor(.secondary)
                TextEditor(text: $viewModel.reviewBody)
                    .frame(minHeight: 120)
                    .overlay(
                        RoundedRectangle(cornerRadius: 8)
                            .stroke(Color(.systemGray4), lineWidth: 1)
                    )

                // Verified badge
                HStack {
                    Image(systemName: "checkmark.seal.fill")
                        .foregroundColor(.green)
                    Text(NSLocalizedString("review.invoiceVerified", comment: ""))
                        .font(.subheadline)
                        .foregroundColor(.green)
                }

                // Submit
                Button {
                    isSubmitting = true
                    Task {
                        if let newBalance = await viewModel.submitReview() {
                            authViewModel.currentUser = try? await APIClient.shared.getCurrentUser()
                        }
                        isSubmitting = false
                    }
                } label: {
                    if isSubmitting {
                        ProgressView()
                            .frame(maxWidth: .infinity)
                            .padding()
                    } else {
                        Text(NSLocalizedString("review.submit", comment: ""))
                            .font(.headline)
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .padding()
                    }
                }
                .background(viewModel.canSubmitReview ? Color.blue : Color.gray)
                .cornerRadius(14)
                .disabled(!viewModel.canSubmitReview || isSubmitting)
            }
            .padding()
        }
    }
}
