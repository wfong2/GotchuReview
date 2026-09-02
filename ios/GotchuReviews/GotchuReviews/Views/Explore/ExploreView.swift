import SwiftUI

struct ExploreView: View {
    @StateObject private var viewModel = ExploreViewModel()
    @State private var showSearch = false

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 20) {
                    // Search bar
                    Button { showSearch = true } label: {
                        HStack {
                            Image(systemName: "magnifyingglass")
                                .foregroundColor(.secondary)
                            Text(NSLocalizedString("explore.searchPlaceholder", comment: ""))
                                .foregroundColor(.secondary)
                            Spacer()
                        }
                        .padding()
                        .background(Color(.systemGray6))
                        .cornerRadius(12)
                    }
                    .padding(.horizontal)

                    // Location
                    HStack {
                        Image(systemName: "location.fill")
                            .foregroundColor(.blue)
                        TextField(
                            NSLocalizedString("explore.zipPlaceholder", comment: ""),
                            text: $viewModel.zipCode
                        )
                        .keyboardType(.numberPad)
                        .onSubmit { Task { await viewModel.search() } }
                    }
                    .padding(.horizontal)

                    // Category grid
                    Text(NSLocalizedString("explore.whatService", comment: ""))
                        .font(.headline)
                        .padding(.horizontal)

                    LazyVGrid(columns: [
                        GridItem(.flexible()),
                        GridItem(.flexible()),
                        GridItem(.flexible()),
                    ], spacing: 12) {
                        ForEach(TradeCategory.allCases) { category in
                            Button {
                                Task { await viewModel.selectCategory(category) }
                            } label: {
                                VStack(spacing: 6) {
                                    Image(systemName: category.icon)
                                        .font(.title2)
                                    Text(category.displayName)
                                        .font(.caption)
                                }
                                .frame(maxWidth: .infinity)
                                .padding(.vertical, 12)
                                .background(
                                    viewModel.selectedCategory == category
                                        ? Color.blue.opacity(0.1)
                                        : Color(.systemGray6)
                                )
                                .cornerRadius(12)
                            }
                            .buttonStyle(.plain)
                        }
                    }
                    .padding(.horizontal)

                    // Results
                    if viewModel.isLoading {
                        HStack {
                            Spacer()
                            ProgressView()
                            Spacer()
                        }
                        .padding(.top, 20)
                    } else if !viewModel.contractors.isEmpty {
                        Text(NSLocalizedString("explore.recentInArea", comment: ""))
                            .font(.headline)
                            .padding(.horizontal)

                        ForEach(viewModel.contractors) { contractor in
                            NavigationLink(value: contractor.id) {
                                ContractorCardView(contractor: contractor)
                            }
                            .buttonStyle(.plain)
                            .padding(.horizontal)
                        }
                    }
                }
                .padding(.vertical)
            }
            .navigationTitle(NSLocalizedString("app.name", comment: ""))
            .navigationDestination(for: String.self) { contractorId in
                ContractorDetailView(contractorId: contractorId)
            }
            .sheet(isPresented: $showSearch) {
                SearchView()
            }
            .task {
                await viewModel.loadNearby()
            }
        }
    }
}
