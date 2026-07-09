<div align="center">
<<<<<<< HEAD
<img width="1200" height="475" alt="GHBanner" src="https://ai.google.dev/static/site-assets/images/share-ais-513315318.png" />
</div>

# Run and deploy your AI Studio app

This contains everything you need to run your app locally.

View your app in AI Studio: https://ai.studio/apps/71adae47-e08e-44ea-b5cf-2bee6ee7ff5b

## Run Locally

**Prerequisites:**  [Android Studio](https://developer.android.com/studio)


1. Open Android Studio
2. Select **Open** and choose the directory containing this project
3. Allow Android Studio to fix any incompatibilities as it imports the project.
4. Create a file named `.env` in the project directory and set `GEMINI_API_KEY` in that file to your Gemini API key (see `.env.example` for an example)
5. Remove this line from the app's `build.gradle.kts` file: `signingConfig = signingConfigs.getByName("debugConfig")`
6. Run the app on an emulator or physical device
=======

<br/>

[![Typing SVG](https://readme-typing-svg.demolab.com/?font=Fira+Code&weight=600&size=22&pause=1200&color=2FA84F&center=true&vCenter=true&width=650&lines=Ball-by-ball+scoring+in+seconds;Automatic+career+%26+team+statistics;Built+for+street%2C+box+%26+turf+cricket;100%25+Offline+%E2%80%A2+Zero+Ads+%E2%80%A2+Just+Cricket)](https://git.io/typing-svg)

<p><i>A premium, offline-first Android application for recording, managing, and analyzing local cricket matches — built to turn every gully game into a lasting cricket legacy.</i></p>

![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Architecture](https://img.shields.io/badge/Architecture-Clean%20%2B%20MVVM-673AB7?style=for-the-badge)
![Status](https://img.shields.io/badge/Status-Active%20Development-orange?style=for-the-badge)
![License](https://img.shields.io/github/license/Prajwal-koundinya/CricBase-App?style=for-the-badge&color=success)

[![Stars](https://img.shields.io/github/stars/Prajwal-koundinya/CricBase-App?style=social)](https://github.com/Prajwal-koundinya/CricBase-App/stargazers)
[![Forks](https://img.shields.io/github/forks/Prajwal-koundinya/CricBase-App?style=social)](https://github.com/Prajwal-koundinya/CricBase-App/fork)
[![Issues](https://img.shields.io/github/issues/Prajwal-koundinya/CricBase-App?color=blue)](https://github.com/Prajwal-koundinya/CricBase-App/issues)
[![Last Commit](https://img.shields.io/github/last-commit/Prajwal-koundinya/CricBase-App?color=blueviolet)](https://github.com/Prajwal-koundinya/CricBase-App/commits/main)

<img src="https://skillicons.dev/icons?i=kotlin,androidstudio,git,github,gradle,sqlite" alt="Tech Stack" />

</div>

---

## 📑 Table of Contents

- [About](#-about)
- [How It Works](#-how-it-works)
- [Architecture at a Glance](#-architecture-at-a-glance)
- [Key Features](#-key-features)
- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [Match Lifecycle](#-match-lifecycle)
- [Offline First](#-offline-first)
- [Built For](#-built-for)
- [Design Philosophy](#-design-philosophy)
- [Screenshots](#-screenshots)
- [Getting Started](#-getting-started)
- [Gradle Scripts](#-gradle-scripts)
- [Roadmap](#-roadmap)
- [Contributing](#-contributing)
- [Author](#-author)
- [License](#-license)
- [Support & Issues](#-support--issues)

---

## 📖 About

**Legacy XI** (this repository, `CricBase-App`) is a premium **offline-first cricket scoring application** built specifically for local cricket — street cricket, box cricket, turf cricket, society matches, and college tournaments.

Unlike a typical scorekeeping app, Legacy XI doesn't just record a match — it builds a **permanent cricket legacy**. Every completed match automatically rolls up into player careers, team histories, records, and achievements, so a match is never just a forgotten scoresheet — it's a chapter in someone's cricketing story.

The app is designed around one rule above all others: **the next ball is seconds away**. Every scoring interaction is optimized for a single hand, bright sunlight, and zero patience for typing.

---

## 🔬 How It Works

1. **🪙 Set Up** — Pick two teams, run the toss, and choose openers in a few taps.
2. **🏏 Score** — Record every ball with a one-tap grid: runs, extras, and wickets.
3. **⚙️ Auto-Compute** — Strike rotation, run rate, projected score, and bowler figures update instantly, with zero manual math.
4. **🏆 Celebrate** — At the end of the match, awards, confetti, and a full scorecard are generated automatically.
5. **📈 Relive** — Every player's career, every team's record, and every over ever bowled stays queryable, forever, fully offline.

---

## 🧩 Architecture at a Glance

```mermaid
flowchart TD
    subgraph Presentation["🎨 Presentation Layer"]
        A1[Screens]
        A2[Reusable Components]
        A3[ViewModels]
        A4[Navigation Compose]
        A5[Theme]
    end

    subgraph Domain["🧠 Domain Layer"]
        B1[Use Cases]
        B2[Domain Models]
        B3[Repository Interfaces]
        B4[Utils]
    end

    subgraph Data["💾 Data Layer"]
        C1[Room Database]
        C2[DAOs]
        C3[Repository Implementations]
        C4[DataStore Preferences]
    end

    D[[Hilt Dependency Injection]]

    Presentation -->|state & events| Domain
    Domain -->|reactive Flow| Data
    D -.-> Presentation
    D -.-> Domain
    D -.-> Data
```

Clean Architecture keeps every layer independently testable: `domain` never imports Android framework code, `data` owns the single source of truth (Room), and `presentation` only ever reacts to state — never mutates it directly.

---

## ✨ Key Features

### 🏏 Ball-by-Ball Scoring
- One-tap scoring for runs, extras, and wickets
- Automatic strike rotation
- Unlimited Undo / Redo
- Guided wicket flow (how out → fielder → next batter)
- End-of-over workflow with automatic bowler eligibility rules
- Innings transition with live target & required run rate
- Full match-completion flow with confetti and awards

### 👥 Player Management
- Reusable player profiles across every team and match
- Beautiful **color-based avatars** — no photos, no camera permissions, instantly recognizable
- Captain and Wicket-Keeper tagging, per team
- Full career tracking: matches, runs, wickets, catches
- Batting, bowling, and fielding statistics, computed automatically

### 🛡️ Team Management
- Create unlimited reusable teams (Team Sheets)
- Share a single player across multiple teams — no duplication
- Team-level win/loss records and history
- Team statistics that update after every match

### 📊 Advanced Match Analytics
| Analytics | Description |
|---|---|
| Worm Graph | Cumulative run progression, both innings overlaid |
| Run Rate Graph | Over-by-over scoring rate comparison |
| Partnership Analysis | Every partnership, visualized proportionally |
| Over-by-Over Summary | Expandable ball-by-ball history per over |
| Complete Scorecards | Full batting & bowling tables, extras, fall of wickets |
| Match Timeline | Key moments across the innings |
| Awards & Achievements | Auto-generated Player of the Match and more |

### 🏆 Career Statistics — Zero Manual Updates
Every completed match automatically updates:

`Matches Played` · `Runs` · `Batting Average` · `Strike Rate` · `Highest Score` · `Wickets` · `Bowling Economy` · `Best Bowling Figures` · `Catches` · `Wins` · `Awards`

### 📈 Player Leaderboards
Sort the whole squad by Most Runs, Highest Average, Highest Strike Rate, Most Wickets, Best Economy, Most Catches, Most Wins, Matches Played, or Awards Won.

### 🧠 Team-wise Player Analytics — a Legacy XI signature feature
Every player's performance is tracked **separately for every team they've represented**, not just as one blended career number:

| Scope | Matches | Runs | Average |
|---|---|---|---|
| **Career (all teams)** | 52 | 1827 | 44.8 |
| ↳ Royal Tigers | 18 | 724 | 48.2 |
| ↳ Phoenix XI | 16 | 618 | 41.3 |

This lets a player honestly see which team, format, or season they actually performed best in — something a single career average can never show.

### 🎨 Premium UI/UX
Material Design 3 · Jetpack Compose · Dark theme tuned for outdoor sunlight legibility · Smooth, purposeful animation · One-handed operation · Large touch targets · Minimal cognitive load — beautiful, but never in the way of the next ball.

---

## 🚀 Tech Stack

| Technology | Badge | Purpose |
|---|---|---|
| **Kotlin** | ![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white) | Primary application language |
| **Jetpack Compose** | ![Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white) | Declarative UI toolkit |
| **Material Design 3** | ![Material3](https://img.shields.io/badge/Material%20Design%203-757575?style=for-the-badge&logo=materialdesign&logoColor=white) | Design system & theming |
| **Hilt** | ![Hilt](https://img.shields.io/badge/Hilt-DI-4285F4?style=for-the-badge&logo=dagger&logoColor=white) | Dependency injection |
| **Room** | ![Room](https://img.shields.io/badge/Room-Database-3DDC84?style=for-the-badge&logo=sqlite&logoColor=white) | Offline-first local persistence |
| **DataStore** | ![DataStore](https://img.shields.io/badge/DataStore-Preferences-3DDC84?style=for-the-badge) | Lightweight key-value settings |
| **Coroutines / Flow** | ![Coroutines](https://img.shields.io/badge/Coroutines-Flow-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white) | Asynchronous, reactive data streams |
| **Navigation Compose** | ![Nav](https://img.shields.io/badge/Navigation-Compose-4285F4?style=for-the-badge) | In-app screen navigation |
| **Coil** | ![Coil](https://img.shields.io/badge/Coil-Image%20Loading-FF6F00?style=for-the-badge) | Lightweight image loading |
| **Custom Compose Charts** | ![Charts](https://img.shields.io/badge/Charts-Custom%20Compose-2FA84F?style=for-the-badge) | Worm graphs, run-rate graphs, partnerships |
| **JUnit / Espresso** | ![Testing](https://img.shields.io/badge/Testing-JUnit%20%2B%20Espresso-25A162?style=for-the-badge&logo=testinglibrary&logoColor=white) | Unit and instrumentation testing |
| **Git & GitHub** | ![Git](https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white) | Version control |

**Architecture:** Clean Architecture · MVVM · Repository Pattern · Unidirectional Data Flow

---

## 📂 Project Structure

```
CricBase-App/
├── app/
│   ├── presentation/
│   │   ├── screens/          # Composable full screens (Home, Live Scoring, Scorecard...)
│   │   ├── components/       # Reusable UI building blocks (avatars, stat chips, ball pills)
│   │   ├── navigation/       # Navigation Compose graph & routes
│   │   ├── theme/            # Color tokens, typography, motion spec
│   │   └── viewmodels/       # One ViewModel per screen, StateFlow-driven
│   │
│   ├── domain/
│   │   ├── models/           # Pure Kotlin domain models
│   │   ├── repository/       # Repository interfaces (no Android deps)
│   │   ├── usecases/         # RecordBall, UndoLastBall, ComputeAwards, etc.
│   │   └── utils/            # Strike-rotation rules, run-rate math, formatters
│   │
│   ├── data/
│   │   ├── database/         # Room database setup & migrations
│   │   ├── dao/               # DAOs for Player, Team, Match, Ball, Award
│   │   ├── entities/          # Room entities
│   │   ├── repository/        # Repository implementations
│   │   └── datastore/         # DataStore-backed app preferences
│   │
│   ├── di/                    # Hilt modules
│   └── utils/                 # Shared cross-cutting utilities
│
├── tests/                     # Unit + instrumentation tests
├── LICENSE
└── README.md
```

---

## 🔄 Match Lifecycle

```mermaid
flowchart LR
    A[👥 Create Teams & Players] --> B[🪙 Toss]
    B --> C[⚙️ Match Setup]
    C --> D[🏏 Innings 1 — Ball by Ball]
    D --> E[🎯 Innings Break & Target]
    E --> F[🏏 Innings 2 — Ball by Ball]
    F --> G[🏆 Result & Awards]
    G --> H[📈 Career & Team Stats Auto-Updated]
```

Every arrow above is backed by a Room transaction — a ball is written to disk the instant it's confirmed, so the match can survive a crash, a force-close, or a dead battery without losing a single delivery.

---

## 💾 Offline First

Legacy XI is designed to work **entirely offline**, permanently — not just as a fallback mode.

- Local Room database as the single source of truth
- Automatic ball-by-ball saving — nothing is buffered or batched
- Resume any interrupted match exactly where you left off
- Persistent player careers and team history, forever local
- Instant loading, no network round-trip on any core screen
- No internet required, ever

No account creation. No cloud dependency. No advertisements. **Just cricket.**

---

## 📱 Built For

🏏 Street Cricket · 🏏 Box Cricket · 🏏 Turf Cricket · 🏏 College Cricket · 🏏 Society Matches · 🏏 Practice Matches · 🏏 Friendly Matches

---

## 🎯 Design Philosophy

- Speed before decoration.
- Every tap should matter.
- One-handed usability.
- Offline by default.
- Preserve every cricket memory.
- Beautiful, but never distracting.
- Automatic statistics — never manual data entry.
- Zero unnecessary complexity.

---

## 📸 Screenshots

> UI is under active development — real device screenshots will replace this section as screens are finalized. Once available, drop them into `app/assets/screenshots/` and reference them below.

<div align="center">

![Home](https://img.shields.io/badge/Home_Screen-Coming_Soon-1B1D1B?style=for-the-badge)
![Live Scoring](https://img.shields.io/badge/Live_Scoring-Coming_Soon-1E6FD9?style=for-the-badge)
![Scorecard](https://img.shields.io/badge/Scorecard-Coming_Soon-12301F?style=for-the-badge)
![Career Stats](https://img.shields.io/badge/Career_Stats-Coming_Soon-7A3FC4?style=for-the-badge)

</div>

---

## 🚀 Getting Started

### 🔧 Prerequisites
- **Android Studio** — Koala (2024.1) or newer
- **JDK** — 17+
- **Kotlin** — 1.9+ (bundled with recent Android Studio)
- An Android device or emulator running **API 26 (Android 8.0)** or higher

### 📥 Installation

Clone the repository:
```bash
git clone https://github.com/Prajwal-koundinya/CricBase-App.git
cd CricBase-App
```

Open the project in **Android Studio**:
```
File → Open → select the CricBase-App folder
```

Let Gradle sync finish (Android Studio does this automatically on open), then run the app:
```
Run ▶ on any connected device or emulator
```

Or build and install straight from the command line:
```bash
./gradlew installDebug
```

---

## 🧩 Gradle Scripts

| Command | Description |
|---|---|
| `./gradlew assembleDebug` | Build a debug APK |
| `./gradlew assembleRelease` | Build a release APK |
| `./gradlew installDebug` | Install the debug build on a connected device |
| `./gradlew test` | Run unit tests (JUnit) |
| `./gradlew connectedAndroidTest` | Run instrumentation tests (Espresso, Compose UI tests) |
| `./gradlew lint` | Run Android Lint checks |
| `./gradlew clean` | Clean all build outputs |

---

## 🔮 Roadmap

### 🚧 Coming Soon
- [ ] Core match-scoring engine (ball-by-ball, undo/redo, wicket flow)
- [ ] Player & team career statistics engine
- [ ] Match scorecard and analytics screens
- [ ] UI polish pass across all screens
- [ ] Performance optimization for low-end devices

### 🎯 Long-term Vision
- [ ] Tournament management with points tables & Net Run Rate
- [ ] AI-generated match insights and highlights
- [ ] Player performance trend analysis over seasons
- [ ] Head-to-head team comparison tools
- [ ] Tablet-optimized layout
- [ ] Wear OS companion for live score glances
- [ ] Full match replay, ball by ball
- [ ] Exportable match reports (PDF / image)

---

## 🛠 Development Status

The application is currently under **active development**. Current focus areas:

`Core Match Engine` · `Statistics Engine` · `Player Analytics` · `UI Polish` · `Performance Optimization`

---

## 🤝 Contributing

Contributions, suggestions, and feature requests are always welcome.

1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/AmazingFeature`)
3. **Commit** your changes (`git commit -m 'Add: AmazingFeature'`)
4. **Push** to the branch (`git push origin feature/AmazingFeature`)
5. **Open** a Pull Request

### Development Guidelines
- Follow Kotlin coding conventions and idiomatic Compose patterns
- Keep `domain` free of Android framework imports
- Write unit tests for new use cases
- Match the existing dark-theme, sunlight-legible design language

---

## 👨‍💻 Author

**Prajwal Koundinya**
- 🌐 GitHub: [@Prajwal-koundinya](https://github.com/Prajwal-koundinya)
- 💼 LinkedIn: [Connect with me](https://www.linkedin.com/in/prajwal-kowndinya-7506b4268/)
- 📧 Email: prajwalkowndinya@gmail.com
- 🌟 Portfolio: [Portfolio Website](https://mellow-faloodeh-7f6f0f.netlify.app/)

---

## 📜 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

---

## 📞 Support & Issues

1. 🐛 **Bug Reports** — [Open an Issue](https://github.com/Prajwal-koundinya/CricBase-App/issues)
2. 💡 **Feature Requests** — [Start a Discussion](https://github.com/Prajwal-koundinya/CricBase-App/discussions)
3. 📧 **Direct Contact** — prajwalkowndinya@gmail.com

---

<div align="center">

### ⭐ If you like this project, consider giving it a star!

*"Every match tells a story. Legacy XI makes sure it's never forgotten."* 🏏

<img src="https://capsule-render.vercel.app/api?type=waving&color=0:1C8A4B,100:1E6FD9&height=100&section=footer" width="100%"/>

</div>
>>>>>>> 165f926e1670050285736669bab9c859169fa46a
