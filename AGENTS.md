# Repository Guidelines

## Project Structure & Module Organization
The Java plugin lives in `sisso/LisMapPlugin`, with production sources split into `src/common`, `src/analyzer`, and `src/application`. Unit tests mirror that structure under `sisso/LisMapPlugin/test` and focus on lightweight JUnit 5 assertions. Build assets compile into `sisso/LisMapPlugin/build`, while distributable JARs land in `sisso/LisMapPlugin/dist/lib`. Legacy examples remain in `example/`, `mega_prova/`, and `tests/`; leave them untouched unless you are refreshing those sandboxes.

## Build, Test, and Development Commands
Work from `sisso/LisMapPlugin`. `ant compile` performs a clean compile into `build/classes` using the bundled Ant script. `ant dist` packages a release JAR under `dist/lib/` and refreshes the plugin inside the sibling GiFork application directory when available. Use `ant clean` to remove compiled classes and generated archives, and `ant javadoc` to rebuild API documentation. Pair `ant compile` with an IDE run configuration so you can attach debuggers to the generated classes.

## Coding Style & Naming Conventions
Keep Java sources formatted with tabs for indentation, one class per file, and package strings that mirror the folder tree (`application`, `analyzer`, `common`). Favor descriptive CamelCase for classes and lowerCamelCase for methods and fields. Static constants belong in upper snake case (see `CommonConstants`). The compiler is configured for UTF-8; save files accordingly. Run an auto-formatter that respects these settings, and comment deviations from standard practice.

## Testing Guidelines
Tests should mirror the production package they cover and use the `*Test` suffix (`AnalyzerMapTest` is an example). JUnit 5 is already imported; annotate cases with `@Test` and prefer `assertDoesNotThrow`, `assertTrue`, and `assertEquals` over manual try/catch. Run the suite from your IDE or a console runner that loads compiled classes from `build/classes` alongside the sources in `test`. New features need covering tests unless the change only affects build tooling or documentation.

## Commit & Pull Request Guidelines
Commits should be scoped and message prefixed with a Conventional Commit token such as `feat:`, `fix:`, or `refactor:` (recent history shows this pattern beginning to emerge). Describe the behavioural impact in the body when the change is non-trivial. Pull requests must include: a concise summary, testing notes (command or IDE launcher used), any linked issue IDs, and screenshots or log excerpts when UI or runtime behaviour changes. Flag breaking changes explicitly and outline rollback steps so reviewers can judge the deployment risk quickly.
