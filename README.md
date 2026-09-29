# Lab 04 — JUnit 5-аар Unit Testing хийх

**Оюутан:** Амарманд Түвшинбаяр

**Оюутны код:** B232270036

**Хичээл:** F.CSA313 Программ хангамжийн чанарын баталгаажуулалт ба тестинг (2026)

## Environment (Орчин)

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

JUnit 5 (Jupiter) 5.10.2, maven-surefire-plugin 3.2.5, `maven.compiler.release` 17. Archetype-ийн default тохиргоонуудыг (`maven.compiler.source/target` 1.7, JUnit 4, surefire 2.22.1, `App.java` / `AppTest.java`) устгаж, шинэчилсэн.

## Project structure (Төслийн бүтэц)

```text
pom.xml
src/main/java/mn/edu/must/sqat/GradeCalculator.java
src/test/java/mn/edu/must/sqat/GradeCalculatorTest.java
results/mvn-test.txt          # green run, full mvn output
results/mvn-test-mutant.txt   # mutant run, full mvn output (BUILD FAILURE)
```

`target/`, `.idea/`, `.DS_Store` зэрэг файлуудыг `.gitignore`-д оруулсан бөгөөд commit хийгдээгүй. Багшийн өгсөн `.docx` зааварчилгаа файл commit хийгдээгүй.

## How to run (Ажиллуулах заавар)

```bash
mkdir -p results && mvn test 2>&1 | tee results/mvn-test.txt
```

Амжилттай ажилласан (green run) үеийн төгсгөлийн гаралт:

```text
Tests run: 30, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## What was tested (Илрүүлэлт ба тестэлсэн зүйлс)

Тест класс: `src/test/java/mn/edu/must/sqat/GradeCalculatorTest.java`.

Тест бүр Arrange-Act-Assert бүтцийг ашигласан ба юуг, яагаад шалгаж байгааг тайлбарласан `@DisplayName` аннотацитай. Тест файлын хэсэг бүр өөрийн зорилгоо тодорхойлсон comment-той (typical values, exact boundaries, invalid inputs).

* Test method-ууд: **16** (`@Test` / `@ParameterizedTest` method-ууд).
* `results/mvn-test.txt` файл дахь тайлан: **Tests run: 30** (Surefire нь `@CsvSource`-ийн мөр бүрийг тусад нь тоолдог: 14 plain test + 12 letterGrade мөр + 4 totalScore мөр = 30). README дээрх тоо файлтай яг таарна.
* `@ParameterizedTest` тоо: **2** — `letterGradeBoundaries` (12 мөр: 100, 95, 90, 89.99, 80, 79.99, 70, 69.99, 60, 59.99, 30, 0) болон `totalScoreValidCases` (төгс 100 болон бүх утга 0 байх тохиолдлуудыг оруулсан 4 мөр).
* Хамарсан Boundary values (хязгаарын утгууд): 90, 89.99, 60, 59.99, 0, 100.
* `assertThrows(IllegalArgumentException.class, ...)` нь хоёр талыг хоёуланг нь хамарна: `letterGrade(-1)`, `letterGrade(101)`, сөрөг ирцтэй `totalScore` (`att = -5`), лаб хэтэрсэн (`lab = 41`), шалгалт хэтэрсэн (`exam = 31`).
* `totalScore` happy path: `(10, 40, 10, 10, 30)` нийлбэр нь яг 100 болно.

## Mutation check (сайн дурын ажиллагааны алдаа)

Mutation: `GradeCalculator.letterGrade` дээр `score >= 90.0` нөхцөлийг `score > 90.0` болгон өөрчилж, дараах тушаалыг ажиллуулсан:

```bash
mvn test 2>&1 | tee results/mvn-test-mutant.txt
# exit code нь 1 байсан (${PIPESTATUS[0]}-оос авсан), мөн файлын төгсгөлд тэмдэглэгдсэн
```

`results/mvn-test-mutant.txt` дахь үр дүн:

```text
Tests run: 30, Failures: 2, Errors: 0, Skipped: 0
BUILD FAILURE
```

Яг 2 тест фэйлдэж, хоёулаа `expected: <A> but was: <B>` гэсэн алдаа заасан: `ninetyIsExactlyA` ба `letterGradeBoundaries[3]` (`90 -> A` мөр). Энэ нь boundary тест заавал `>=` зааглах нөхцөлийг шалгаж чадаж байгааг баталж байна. Хэрэв exact-90 тест байгаагүй бол mutant нь олон тестийг давж үлдэх байсан. Шалгалтын дараа нөхцөлийг буцаан `>= 90.0` болгосон бөгөөд `mvn test` эргэн амжилттай (green) болсон (`results/mvn-test.txt`, `BUILD SUCCESS`).

## Reflection (Миний хийсэн зүйлс болон хамгийн сонирхолтой байсан хэсэг)

Би Maven төслийг `maven-archetype-quickstart`-аас үүсгэн, Java 17 болон JUnit 5-д зориулж POM-ийг зассан. Хичээлийн дүнгийн эзлэх хувьд тохируулан `GradeCalculator`-ийг хэрэгжүүлж, дүнгийн хязгаар ба нийт дүнгийн цуглуулгад зориулсан 2 parameterized test-ийг оролцуулан 16 test method бичсэн. Бүх тест амжилттай болсон файл `results/mvn-test.txt` дээр (30 тест, 0 failure), ажиллах нөхцөлийг `>= 90`-ээс `> 90` болгож өөрчилсөн mutant run нь 2 failure-тайгаар `results/mvn-test-mutant.txt`-д хадгалагдсан. Хамгийн сонирхолтой тест нь `ninetyIsExactlyA` байв, учир нь энэ нь boundary mutant-ийг устгаж чадсан ганц plain тест байсан бөгөөд бүх тест "давж" (pass) байлаа гээд автоматаар сайн тест гэсэн үг биш гэдгийг харуулсан. Мөн parameterized мөрүүд нь Surefire-ийн тоолуурыг нэмэгдүүлж, 16 method нь яг 30 ажилласан тест болдог гэдгийг мэдэж авлаа. `totalScore`-д зориулсан validation тестүүд ч мөн үнэ цэнтэй байсан, учир нь бүрэлдэхүүн хэсэг бүр өөр өөр дээд хязгаартай тул ерөнхий шалгалт нь хэтрэлтийг алгасах аюултай байв.

## Submit checklist (Илгээх шалгах хуудас)

* [x] Шинээр clone хийгээд `mvn test` ажиллуулахад амжилттай болж байгаа (Verification хэсгийг харна уу).
* [x] `results/mvn-test.txt` болон `results/mvn-test-mutant.txt` commit хийгдсэн.
* [x] Байгалийн цагийн интервалтайгаар 5+ commit хийгдсэн (бөөнд нь үүсгээгүй).
* [x] `.gitignore` нь `target/`, `.idea/`, `.DS_Store`-ийг tracking-аас гадуур байлгаж байгаа.
* [x] Repo нь public бөгөөд Teams дээр "Turn in" товчийг дарж холбоосыг илгээсэн.

## Verification (Баталгаажуулалт)

```bash
git clone <repo-url> /tmp/lab04-verify
cd /tmp/lab04-verify
mvn test
```
