import SwiftUI

struct VendorInvoiceListView: View {
    let vendor: VendorGroup

    private var displayName: String {
        vendor.contractor.businessName.isEmpty ? vendor.contractor.name : vendor.contractor.businessName
    }

    private var categoryDisplay: String {
        TradeCategory(rawValue: vendor.contractor.category)?.displayName ?? vendor.contractor.category.capitalized
    }

    private var locationDisplay: String? {
        let parts = [vendor.contractor.city, vendor.contractor.state].compactMap { $0 }
        return parts.isEmpty ? nil : parts.joined(separator: ", ")
    }

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                // Header
                VStack(alignment: .leading, spacing: 8) {
                    HStack {
                        Image(systemName: TradeCategory(rawValue: vendor.contractor.category)?.icon ?? "hammer.fill")
                            .font(.title2)
                            .foregroundColor(.blue)
                        Text(categoryDisplay)
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                    }

                    if let location = locationDisplay {
                        Label(location, systemImage: "mappin")
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                    }

                    Text("$\(Int(vendor.totalSpent)) total across \(vendor.invoiceCount) \(vendor.invoiceCount == 1 ? "invoice" : "invoices")")
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                }
                .padding()
                .frame(maxWidth: .infinity, alignment: .leading)
                .background(Color(.systemGray6))
                .cornerRadius(12)

                // Invoices
                ForEach(vendor.invoices) { invoice in
                    NavigationLink(destination: InvoiceDetailView(invoice: invoice)) {
                        InvoiceRowView(invoice: invoice)
                    }
                    .buttonStyle(.plain)
                }
            }
            .padding()
        }
        .navigationTitle(displayName)
    }
}

struct InvoiceRowView: View {
    let invoice: DetailedInvoice

    private var formattedDate: String? {
        guard let date = invoice.invoiceDate else { return nil }
        return String(date.prefix(10)).replacingOccurrences(of: "-", with: "/")
    }

    var body: some View {
        HStack(spacing: 12) {
            if let thumbnail = InvoiceImageStore.shared.thumbnail(documentHash: invoice.documentHash) {
                Image(uiImage: thumbnail)
                    .resizable()
                    .aspectRatio(contentMode: .fill)
                    .frame(width: 50, height: 50)
                    .cornerRadius(8)
                    .clipped()
            } else {
                Image(systemName: "doc.text")
                    .font(.title3)
                    .foregroundColor(.secondary)
                    .frame(width: 50, height: 50)
                    .background(Color(.systemGray5))
                    .cornerRadius(8)
            }

            VStack(alignment: .leading, spacing: 4) {
                Text(invoice.description.isEmpty ? "Invoice" : invoice.description)
                    .font(.subheadline)
                    .fontWeight(.medium)
                    .lineLimit(1)
                if let date = formattedDate {
                    Text(date)
                        .font(.caption)
                        .foregroundColor(.secondary)
                }
            }

            Spacer()

            Text("$\(Int(invoice.totalAmount))")
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
