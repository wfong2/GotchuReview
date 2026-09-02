import SwiftUI

struct MainTabView: View {
    @EnvironmentObject var authViewModel: AuthViewModel
    @State private var selectedTab = 0

    var body: some View {
        TabView(selection: $selectedTab) {
            ExploreView()
                .tabItem {
                    Label(
                        NSLocalizedString("tab.explore", comment: "Explore tab"),
                        systemImage: "magnifyingglass"
                    )
                }
                .tag(0)

            ScanView()
                .tabItem {
                    Label(
                        NSLocalizedString("tab.scan", comment: "Scan tab"),
                        systemImage: "camera.fill"
                    )
                }
                .tag(1)

            HistoryView()
                .tabItem {
                    Label(
                        NSLocalizedString("tab.history", comment: "History tab"),
                        systemImage: "clock.fill"
                    )
                }
                .tag(2)
        }
        .tint(.blue)
    }
}
