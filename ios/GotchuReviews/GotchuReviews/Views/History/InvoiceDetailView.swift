import SwiftUI

struct InvoiceDetailView: View {
    let invoice: DetailedInvoice
    @State private var showFullImage = false

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                // Invoice image
                if let image = InvoiceImageStore.shared.loadImage(documentHash: invoice.documentHash) {
                    Image(uiImage: image)
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                        .frame(maxHeight: 250)
                        .cornerRadius(12)
                        .onTapGesture { showFullImage = true }
                } else {
                    HStack {
                        Spacer()
                        VStack(spacing: 8) {
                            Image(systemName: "photo")
                                .font(.system(size: 36))
                                .foregroundColor(.secondary)
                            Text("Image not available")
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }
                        Spacer()
                    }
                    .frame(height: 150)
                    .background(Color(.systemGray6))
                    .cornerRadius(12)
                }

                // Extracted data
                VStack(alignment: .leading, spacing: 12) {
                    ReadOnlyField(
                        label: NSLocalizedString("scan.total", comment: ""),
                        value: "$\(Int(invoice.totalAmount))"
                    )

                    if let labor = invoice.laborCost {
                        ReadOnlyField(
                            label: NSLocalizedString("scan.labor", comment: ""),
                            value: "$\(Int(labor))"
                        )
                    }

                    if let materials = invoice.materialsCost {
                        ReadOnlyField(
                            label: NSLocalizedString("scan.materials", comment: ""),
                            value: "$\(Int(materials))"
                        )
                    }

                    if let rate = invoice.hourlyRate {
                        ReadOnlyField(label: "Hourly Rate", value: "$\(Int(rate))/hr")
                    }

                    if let duration = invoice.projectDuration {
                        ReadOnlyField(label: "Duration", value: duration)
                    }

                    if let date = invoice.invoiceDate {
                        ReadOnlyField(
                            label: NSLocalizedString("scan.date", comment: ""),
                            value: String(date.prefix(10)).replacingOccurrences(of: "-", with: "/")
                        )
                    }

                    if !invoice.description.isEmpty {
                        ReadOnlyField(
                            label: NSLocalizedString("scan.work", comment: ""),
                            value: invoice.description
                        )
                    }

                    ReadOnlyField(label: "Currency", value: invoice.currency)
                }
                .padding()
                .background(Color(.systemGray6))
                .cornerRadius(12)

                // Line items
                if !invoice.lineItems.isEmpty {
                    VStack(alignment: .leading, spacing: 8) {
                        Text("Line Items")
                            .font(.headline)

                        ForEach(invoice.lineItems, id: \.description) { item in
                            HStack {
                                VStack(alignment: .leading, spacing: 2) {
                                    Text(item.description)
                                        .font(.subheadline)
                                    Text(item.category)
                                        .font(.caption)
                                        .foregroundColor(.secondary)
                                }
                                Spacer()
                                Text("$\(Int(item.amount))")
                                    .font(.subheadline)
                                    .fontWeight(.medium)
                            }
                            .padding(.vertical, 4)
                            Divider()
                        }
                    }
                    .padding()
                    .background(Color(.systemGray6))
                    .cornerRadius(12)
                }

                // Review status
                if let review = invoice.review {
                    VStack(alignment: .leading, spacing: 8) {
                        Text("Review")
                            .font(.headline)
                        HStack {
                            StarRatingView(rating: review.overallRating)
                            Text(review.title)
                                .font(.subheadline)
                                .foregroundColor(.secondary)
                        }
                    }
                    .padding()
                    .background(Color(.systemGray6))
                    .cornerRadius(12)
                }
            }
            .padding()
        }
        .navigationTitle("Invoice")
        .navigationBarTitleDisplayMode(.inline)
        .fullScreenCover(isPresented: $showFullImage) {
            FullImageView(documentHash: invoice.documentHash, isPresented: $showFullImage)
        }
    }
}

struct FullImageView: View {
    let documentHash: String
    @Binding var isPresented: Bool

    var body: some View {
        ZStack {
            Color.black.ignoresSafeArea()

            if let image = InvoiceImageStore.shared.loadImage(documentHash: documentHash) {
                Image(uiImage: image)
                    .resizable()
                    .aspectRatio(contentMode: .fit)
            }

            VStack {
                HStack {
                    Spacer()
                    Button {
                        isPresented = false
                    } label: {
                        Image(systemName: "xmark.circle.fill")
                            .font(.title)
                            .foregroundColor(.white)
                    }
                    .padding()
                }
                Spacer()
            }
        }
    }
}
