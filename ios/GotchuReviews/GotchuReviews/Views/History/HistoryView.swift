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
                                            value: "$\(Int(summary.totalSpent))"
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

                        // Grouped by year
                        ForEach(viewModel.sortedYears, id: \.self) { year in
                            VStack(alignment: .leading, spacing: 12) {
                                Text(year)
                                    .font(.title3)
                                    .fontWeight(.bold)
                                    .padding(.horizontal)

                                if let invoices = viewModel.history[year] {
                                    ForEach(invoices) { invoice in
                                        HistoryItemView(invoice: invoice)
                                            .padding(.horizontal)
                                    }
                                }
                            }
                        }

                        if viewModel.history.isEmpty {
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
            .task {
                await viewModel.load()
            }
        }
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

struct HistoryItemView: View {
    let invoice: HistoryInvoice

    var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            Text(invoice.contractor.name)
                .font(.headline)
            HStack {
                Text(invoice.description.isEmpty ? invoice.contractor.category.capitalized : invoice.description)
                    .font(.subheadline)
                    .foregroundColor(.secondary)
                Spacer()
                Text("$\(Int(invoice.totalAmount))")
                    .font(.subheadline)
                    .fontWeight(.medium)
            }
            if let date = invoice.invoiceDate {
                Text(date.prefix(10).replacingOccurrences(of: "-", with: "/"))
                    .font(.caption)
                    .foregroundColor(.secondary)
            }
        }
        .padding()
        .background(Color(.systemBackground))
        .cornerRadius(12)
        .shadow(color: .black.opacity(0.05), radius: 4, y: 2)
    }
}
