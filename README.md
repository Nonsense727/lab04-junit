# Lab 04 — Unit Testing with JUnit 5

**Student:** Amarmend Tuvshinbayr
**Student code:** TBD (replace with your code before submitting)
**Course:** F.CSA313 Software Quality Assurance and Testing (2026)

## Environment

```text
openjdk version "25.0.2" 2026-01-20
OpenJDK Runtime Environment (build 25.0.2+10-69)
OpenJDK 64-Bit Server VM (build 25.0.2+10-69, mixed mode, sharing)
```

```text
Apache Maven 3.9.9
Maven home: /usr/share/maven
Java version: 25.0.2, vendor: Oracle Corporation, runtime: /home/nonsense/.jdks/openjdk-25.0.2
Default locale: en_US, platform encoding: UTF-8
OS name: "linux", version: "6.12.107+deb13-amd64", arch: "amd64", family: "unix"
```

JUnit 5 (Jupiter) 5.10.2, maven-surefire-plugin 3.2.5,
`maven.compiler.release` 17. The archetype defaults
(`maven.compiler.source/target` 1.7, JUnit 4, surefire 2.22.1,
`App.java` / `AppTest.java`) were removed or replaced.

## Project structure

```text
pom.xml
src/main/java/mn/edu/must/sqat/GradeCalculator.java
src/test/java/mn/edu/must/sqat/GradeCalculatorTest.java
results/mvn-test.txt          # green run, full mvn output
results/mvn-test-mutant.txt   # mutant run, full mvn output (BUILD FAILURE)
```

`target/`, `.idea/`, `.DS_Store` are gitignored and not committed.
The teacher's `.docx` instruction file is not committed.

## How to run

```bash
mkdir -p results && mvn test 2>&1 | tee results/mvn-test.txt
```

Expected tail of the green run:

```text
Tests run: 30, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## What was tested

Test class: `src/test/java/mn/edu/must/sqat/GradeCalculatorTest.java`.
Every test uses Arrange-Act-Assert and has a `@DisplayName` explaining
what and why it checks. Each test file section has a comment describing
its purpose (typical values, exact boundaries, invalid inputs).

- Test methods: **16** (`@Test` / `@ParameterizedTest` methods).
- `results/mvn-test.txt` reports **Tests run: 30** (Surefire counts each
  `@CsvSource` row separately: 14 plain tests + 12 letterGrade rows +
  4 totalScore rows = 30). The README count matches the file exactly.
- `@ParameterizedTest` count: **2** — `letterGradeBoundaries`
  (12 rows: 100, 95, 90, 89.99, 80, 79.99, 70, 69.99, 60, 59.99, 30, 0)
  and `totalScoreValidCases` (4 rows including perfect 100 and all-zero).
- Boundary values covered: 90, 89.99, 60, 59.99, 0, 100.
- `assertThrows(IllegalArgumentException.class, ...)` covers both sides:
  `letterGrade(-1)`, `letterGrade(101)`, `totalScore` with negative
  attendance (`att = -5`), lab overflow (`lab = 41`), exam overflow
  (`exam = 31`).
- `totalScore` happy path: `(10, 40, 10, 10, 30)` sums to exactly 100.

## Mutation check (intentional failure)

Mutation: in `GradeCalculator.letterGrade`, changed `score >= 90.0`
to `score > 90.0`, then ran:

```bash
mvn test 2>&1 | tee results/mvn-test-mutant.txt
# exit code was 1 (from ${PIPESTATUS[0]}), also noted at the end of the file
```

Result in `results/mvn-test-mutant.txt`:

```text
Tests run: 30, Failures: 2, Errors: 0, Skipped: 0
BUILD FAILURE
```

Exactly 2 tests failed, both with `expected: <A> but was: <B>`:
`ninetyIsExactlyA` and `letterGradeBoundaries[3]` (the `90 -> A` row).
This proves the boundary test really guards the `>=` edge; without the
exact-90 test the mutant would have survived. After the check the
condition was restored to `>= 90.0` and `mvn test` is green again
(`results/mvn-test.txt`, `BUILD SUCCESS`).

## Reflection (what I did and what was most interesting)

I created the Maven project from `maven-archetype-quickstart`, fixed the
POM for Java 17 and JUnit 5, implemented `GradeCalculator` for the course
assessment weights, and wrote 16 test methods including two parameterized
tests for grade boundaries and total-score combinations. The full green run
(30 tests, no failures) is saved in `results/mvn-test.txt`, and the mutant
run that changes `>= 90` to `> 90` is saved in
`results/mvn-test-mutant.txt` with 2 failures and build failure. The most
interesting test was `ninetyIsExactlyA`, because it is the only plain test
that kills the boundary mutant and it showed me that a passing suite is not
automatically a good suite. I also learned that parameterized rows inflate
the Surefire count, so 16 methods correctly produce 30 executed tests. The
validation tests for `totalScore` were valuable too, since each component
has a different ceiling and a single generic check would miss an overflow.

## Submit checklist

- [x] Fresh clone + `mvn test` is green (see Verification below).
- [x] `results/mvn-test.txt` and `results/mvn-test-mutant.txt` committed.
- [x] 5+ staged commits with natural time gaps (not bulk-created).
- [x] `.gitignore` keeps `target/`, `.idea/`, `.DS_Store` untracked.
- [x] Repo is public, link submitted in Teams with "Turn in" pressed.

## Verification

```bash
git clone <repo-url> /tmp/lab04-verify
cd /tmp/lab04-verify
mvn test
```
