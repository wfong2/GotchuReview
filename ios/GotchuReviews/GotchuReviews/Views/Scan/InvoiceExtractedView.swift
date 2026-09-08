import SwiftUI

struct InvoiceExtractedView: View {
    @ObservedObject var viewModel: ScanViewModel

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                // Header
                HStack {
                    Image(systemName: "checkmark.circle.fill")
                        .foregroundColor(.green)
                        .font(.title2)
                    Text(NSLocalizedString("scan.extracted", comment: ""))
                        .font(.title2)
                        .fontWeight(.bold)
                }

                if let result = viewModel.extractionResult {
                    let extracted = result.extracted

                    // Contractor match
                    Text(NSLocalizedString("scan.contractor", comment: ""))
                        .font(.caption)
                        .foregroundColor(.secondary)
                    Text(extracted.contractorName)
                        .font(.headline)

                    if !result.contractorMatches.isEmpty {
                        ContractorMatchCard(
                            matches: result.contractorMatches,
                            selectedId: $viewModel.selectedContractorId,
                            isNewContractor: $viewModel.isNewContractor
                        )
                    } else {
                        Text(NSLocalizedString("scan.newContractor", comment: ""))
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                            .onAppear { viewModel.isNewContractor = true }
                    }

                    Divider()

                    // Extracted fields (read-only)
                    ReadOnlyField(label: NSLocalizedString("scan.total", comment: ""), value: "$\(Int(extracted.totalAmount).formatted())")

                    if let labor = extracted.laborCost {
                        ReadOnlyField(label: NSLocalizedString("scan.labor", comment: ""), value: "$\(Int(labor).formatted())")
                    }
                    if let materials = extracted.materialsCost {
                        ReadOnlyField(label: NSLocalizedString("scan.materials", comment: ""), value: "$\(Int(materials).formatted())")
                    }
                    if let date = extracted.invoiceDate {
                        ReadOnlyField(label: NSLocalizedString("scan.date", comment: ""), value: date)
                    }

                    ReadOnlyField(label: NSLocalizedString("scan.work", comment: ""), value: extracted.description)

                    if result.estimatedBreakdown {
                        Label(
                            NSLocalizedString("scan.estimatedBreakdown", comment: ""),
                            systemImage: "info.circle.fill"
                        )
                        .font(.caption)
                        .foregroundColor(.orange)
                    }

                    Divider()

                    // Re-scan option
                    Button {
                        viewModel.rescan()
                    } label: {
                        Text(NSLocalizedString("scan.rescan", comment: ""))
                            .font(.subheadline)
                            .foregroundColor(.blue)
                    }

                    // Confirm button
                    Button {
                        viewModel.confirmExtraction()
                    } label: {
                        Text(NSLocalizedString("scan.confirm", comment: ""))
                            .font(.headline)
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .padding()
                            .background(Color.blue)
                            .cornerRadius(14)
                    }
                }
            }
            .padding()
        }
    }
}

struct ContractorMatchCard: View {
    let matches: [ContractorMatch]
    @Binding var selectedId: String?
    @Binding var isNewContractor: Bool

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            if let topMatch = matches.first {
                VStack(alignment: .leading, spacing: 4) {
                    Text(NSLocalizedString("scan.matchFound", comment: ""))
                        .font(.subheadline)
                        .fontWeight(.medium)
                        .foregroundColor(.green)

                    Text(topMatch.contractor.businessName.isEmpty ? topMatch.contractor.name : topMatch.contractor.businessName)
                        .font(.headline)

                    Text("\(topMatch.contractor.locationDisplay) · \(topMatch.contractor.reviewCount) \(NSLocalizedString("review.plural", comment: ""))")
                        .font(.caption)
                        .foregroundColor(.secondary)
                }

                HStack(spacing: 12) {
                    Button {
                        selectedId = topMatch.contractor.id
                        isNewContractor = false
                    } label: {
                        Text(NSLocalizedString("scan.yesSame", comment: ""))
                            .font(.subheadline)
                            .fontWeight(.medium)
                            .padding(.horizontal, 16)
                            .padding(.vertical, 8)
                            .background(selectedId == topMatch.contractor.id ? Color.blue : Color(.systemGray5))
                            .foregroundColor(selectedId == topMatch.contractor.id ? .white : .primary)
                            .cornerRadius(8)
                    }

                    Button {
                        selectedId = nil
                        isNewContractor = true
                    } label: {
                        Text(NSLocalizedString("scan.noNew", comment: ""))
                            .font(.subheadline)
                            .fontWeight(.medium)
                            .padding(.horizontal, 16)
                            .padding(.vertical, 8)
                            .background(isNewContractor ? Color.blue : Color(.systemGray5))
                            .foregroundColor(isNewContractor ? .white : .primary)
                            .cornerRadius(8)
                    }
                }
            }
        }
        .padding()
        .background(Color(.systemGray6))
        .cornerRadius(12)
    }
}

struct ReadOnlyField: View {
    let label: String
    let value: String

    var body: some View {
        HStack {
            Text(label)
                .foregroundColor(.secondary)
            Spacer()
            Text(value)
                .fontWeight(.medium)
        }
    }
}
