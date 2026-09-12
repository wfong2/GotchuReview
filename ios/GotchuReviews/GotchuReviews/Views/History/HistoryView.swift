import SwiftUI

struct HistoryView: View {
    @EnvironmentObject var authViewModel: AuthViewModel
    @StateObject private var viewModel = HistoryViewModel()
    @State private var showSettings = false

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 20) {
                    if viewModel.isLoading {
                        HStack {
                            Spacer()
                            ProgressView()
                            Spacer()
                        }
                        .padding(.top, 40)
                    } else {
                        // Summary
                        if let summary = viewModel.summary {
                            VStack(alignment: .leading, spacing: 8) {
                                if summary.totalInvoices > 0 {
                                    HStack {
                                        SummaryCard(
                                            title: NSLocalizedString("history.totalSpent", comment: ""),
                                            value: "$\(Int(summary.totalSpent).formatted())"
                                        )
                                        SummaryCard(
                                            title: NSLocalizedString("history.contractors", comment: ""),
                                            value: "\(summary.contractorCount)"
                                        )
                                        SummaryCard(
                                            title: NSLocalizedString("history.jobs", comment: ""),
                                            value: "\(summary.totalInvoices)"
                                        )
                                    }
                                }
                            }
                            .padding(.horizontal)
                        }

                        // Vendor list
                        ForEach(viewModel.sortedVendors) { vendor in
                            NavigationLink(destination: VendorInvoiceListView(vendor: vendor)) {
                                VendorRowView(vendor: vendor)
                            }
                            .buttonStyle(.plain)
                            .padding(.horizontal)
                        }

                        if viewModel.vendors.isEmpty {
                            VStack(spacing: 12) {
                                Image(systemName: "doc.text")
                                    .font(.system(size: 48))
                                    .foregroundColor(.secondary)
                                Text(NSLocalizedString("history.empty", comment: ""))
                                    .font(.subheadline)
                                    .foregroundColor(.secondary)
                            }
                            .frame(maxWidth: .infinity)
                            .padding(.top, 60)
                        }

                        // Credits
                        HStack {
                            Text(NSLocalizedString("history.credits", comment: ""))
                                .font(.subheadline)
                                .foregroundColor(.secondary)
                            Spacer()
                            Text("\(viewModel.creditBalance)")
                                .font(.title3)
                                .fontWeight(.semibold)
                        }
                        .padding()
                        .background(Color(.systemGray6))
                        .cornerRadius(12)
                        .padding(.horizontal)
                    }
                }
                .padding(.vertical)
            }
            .navigationTitle(NSLocalizedString("history.title", comment: ""))
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Menu {
                        Button {
                            viewModel.sortOption = .recentFirst
                        } label: {
                            Label("Most Recent", systemImage: viewModel.sortOption == .recentFirst ? "checkmark" : "")
                        }
                        Button {
                            viewModel.sortOption = .name
                        } label: {
                            Label("Name", systemImage: viewModel.sortOption == .name ? "checkmark" : "")
                        }
                    } label: {
                        Image(systemName: "arrow.up.arrow.down")
                    }
                }
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button {
                        showSettings = true
                    } label: {
                        Image(systemName: "gearshape")
                    }
                }
            }
            .sheet(isPresented: $showSettings) {
                SettingsView()
            }
            .onAppear {
                Task { await viewModel.load() }
            }
        }
    }
}

struct VendorRowView: View {
    let vendor: VendorGroup

    private var categoryIcon: String {
        TradeCategory(rawValue: vendor.contractor.category)?.icon ?? "hammer.fill"
    }

    private var displayName: String {
        vendor.contractor.businessName.isEmpty ? vendor.contractor.name : vendor.contractor.businessName
    }

    private var formattedDate: String {
        String(vendor.latestInvoiceDate.prefix(10)).replacingOccurrences(of: "-", with: "/")
    }

    var body: some View {
        HStack(spacing: 12) {
            Image(systemName: categoryIcon)
                .font(.title3)
                .foregroundColor(.blue)
                .frame(width: 40, height: 40)
                .background(Color.blue.opacity(0.1))
                .cornerRadius(10)

            VStack(alignment: .leading, spacing: 4) {
                Text(displayName)
                    .font(.headline)
                HStack {
                    Text("\(vendor.invoiceCount) \(vendor.invoiceCount == 1 ? "invoice" : "invoices")")
                        .font(.caption)
                        .foregroundColor(.secondary)
                    Text("·")
                        .foregroundColor(.secondary)
                    Text(formattedDate)
                        .font(.caption)
                        .foregroundColor(.secondary)
                }
            }

            Spacer()

            Text("$\(Int(vendor.totalSpent).formatted())")
                .font(.subheadline)
                .fontWeight(.semibold)

            Image(systemName: "chevron.right")
                .font(.caption)
                .foregroundColor(.secondary)
        }
        .padding()
        .background(Color(.systemBackground))
        .cornerRadius(12)
        .shadow(color: .black.opacity(0.05), radius: 4, y: 2)
    }
}

struct SummaryCard: View {
    let title: String
    let value: String

    var body: some View {
        VStack(spacing: 4) {
            Text(value)
                .font(.title3)
                .fontWeight(.bold)
            Text(title)
                .font(.caption)
                .foregroundColor(.secondary)
        }
        .frame(maxWidth: .infinity)
        .padding(.vertical, 12)
        .background(Color(.systemGray6))
        .cornerRadius(10)
    }
}
