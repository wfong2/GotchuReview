import SwiftUI

struct SearchView: View {
    @StateObject private var viewModel = ExploreViewModel()
    @Environment(\.dismiss) private var dismiss

    var body: some View {
        NavigationStack {
            VStack(spacing: 0) {
                // Search + filters
                VStack(spacing: 12) {
                    TextField(
                        NSLocalizedString("search.placeholder", comment: ""),
                        text: $viewModel.searchText
                    )
                    .textFieldStyle(.roundedBorder)
                    .onSubmit { Task { await viewModel.search() } }

                    HStack {
                        Image(systemName: "location.fill")
                            .foregroundColor(.blue)
                            .font(.caption)
                        TextField(
                            NSLocalizedString("explore.zipPlaceholder", comment: ""),
                            text: $viewModel.zipCode
                        )
                        .keyboardType(.numberPad)
                        .font(.subheadline)
                    }
                }
                .padding()

                if viewModel.isLoading {
                    Spacer()
                    ProgressView()
                    Spacer()
                } else {
                    List {
                        if viewModel.totalResults > 0 {
                            Text("\(viewModel.totalResults) \(NSLocalizedString("search.results", comment: ""))")
                                .font(.subheadline)
                                .foregroundColor(.secondary)
                                .listRowSeparator(.hidden)
                        }

                        ForEach(viewModel.contractors) { contractor in
                            NavigationLink(value: contractor.id) {
                                ContractorCardView(contractor: contractor)
                            }
                        }
                        .listRowSeparator(.hidden)
                    }
                    .listStyle(.plain)
                    .navigationDestination(for: String.self) { contractorId in
                        ContractorDetailView(contractorId: contractorId)
                    }
                }
            }
            .navigationTitle(NSLocalizedString("search.title", comment: ""))
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button(NSLocalizedString("common.cancel", comment: "")) {
                        dismiss()
                    }
                }
            }
        }
    }
}
