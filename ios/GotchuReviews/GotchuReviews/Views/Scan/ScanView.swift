import SwiftUI

struct ScanView: View {
    @EnvironmentObject var authViewModel: AuthViewModel
    @StateObject private var viewModel = ScanViewModel()

    var body: some View {
        NavigationStack {
            Group {
                switch viewModel.flowStep {
                case .camera:
                    CameraView(viewModel: viewModel)
                case .extracting:
                    ExtractingView()
                case .extracted:
                    InvoiceExtractedView(viewModel: viewModel)
                case .review:
                    WriteReviewView(viewModel: viewModel)
                case .submitted:
                    SubmittedView(viewModel: viewModel)
                }
            }
            .navigationTitle(NSLocalizedString("scan.title", comment: ""))
            .navigationBarTitleDisplayMode(.inline)
            .alert(
                NSLocalizedString("common.error", comment: ""),
                isPresented: .init(
                    get: { viewModel.errorMessage != nil },
                    set: { if !$0 { viewModel.errorMessage = nil } }
                )
            ) {
                Button(NSLocalizedString("common.ok", comment: "")) {
                    viewModel.errorMessage = nil
                }
            } message: {
                Text(viewModel.errorMessage ?? "")
            }
        }
    }
}
