import SwiftUI

struct StarRatingView: View {
    let rating: Double
    let maxRating: Int = 5
    var starSize: CGFloat = 14
    var color: Color = .yellow

    var body: some View {
        HStack(spacing: 2) {
            ForEach(1...maxRating, id: \.self) { index in
                Image(systemName: starImageName(for: index))
                    .font(.system(size: starSize))
                    .foregroundColor(color)
            }
        }
    }

    private func starImageName(for index: Int) -> String {
        let value = Double(index)
        if rating >= value {
            return "star.fill"
        } else if rating >= value - 0.5 {
            return "star.leadinghalf.filled"
        } else {
            return "star"
        }
    }
}

struct InteractiveStarRating: View {
    @Binding var rating: Int
    var label: String
    var starSize: CGFloat = 28

    var body: some View {
        HStack {
            Text(label)
                .frame(width: 130, alignment: .leading)
            HStack(spacing: 4) {
                ForEach(1...5, id: \.self) { index in
                    Image(systemName: index <= rating ? "star.fill" : "star")
                        .font(.system(size: starSize))
                        .foregroundColor(.yellow)
                        .onTapGesture {
                            rating = index
                        }
                }
            }
        }
    }
}
