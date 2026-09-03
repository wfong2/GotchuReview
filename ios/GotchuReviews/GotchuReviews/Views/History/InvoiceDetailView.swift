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

    @State private var scale: CGFloat = 1.0
    @State private var lastScale: CGFloat = 1.0
    @State private var offset: CGSize = .zero
    @State private var lastOffset: CGSize = .zero
    @State private var showSavedAlert = false

    var body: some View {
        ZStack {
            Color.black.ignoresSafeArea()

            if let image = InvoiceImageStore.shared.loadImage(documentHash: documentHash) {
                Image(uiImage: image)
                    .resizable()
                    .aspectRatio(contentMode: .fit)
                    .scaleEffect(scale)
                    .offset(offset)
                    .gesture(
                        MagnifyGesture()
                            .onChanged { value in
                                scale = lastScale * value.magnification
                            }
                            .onEnded { value in
                                scale = max(1.0, lastScale * value.magnification)
                                lastScale = scale
                                if scale == 1.0 {
                                    offset = .zero
                                    lastOffset = .zero
                                }
                            }
                            .simultaneously(with:
                                DragGesture()
                                    .onChanged { value in
                                        if scale > 1.0 {
                                            offset = CGSize(
                                                width: lastOffset.width + value.translation.width,
                                                height: lastOffset.height + value.translation.height
                                            )
                                        }
                                    }
                                    .onEnded { _ in
                                        lastOffset = offset
                                    }
                            )
                    )
                    .onTapGesture(count: 2) {
                        withAnimation(.easeInOut(duration: 0.25)) {
                            if scale > 1.0 {
                                scale = 1.0
                                lastScale = 1.0
                                offset = .zero
                                lastOffset = .zero
                            } else {
                                scale = 3.0
                                lastScale = 3.0
                            }
                        }
                    }
            }

            VStack {
                HStack {
                    Button {
                        if let image = InvoiceImageStore.shared.loadImage(documentHash: documentHash) {
                            UIImageWriteToSavedPhotosAlbum(image, nil, nil, nil)
                            showSavedAlert = true
                        }
                    } label: {
                        Image(systemName: "square.and.arrow.down")
                            .font(.title2)
                            .foregroundColor(.white)
                    }
                    .padding()

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
        .alert("Saved to Photos", isPresented: $showSavedAlert) {
            Button("OK", role: .cancel) {}
        }
    }
}
