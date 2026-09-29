package mn.edu.must.sqat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link GradeCalculator}.
 *
 * <p>Each test follows Arrange-Act-Assert: prepare inputs, call the
 * method under test, then assert the outcome. Boundary values
 * (90, 89.99, 60, 59.99, 0, 100) are covered because off-by-one
 * errors hide at grade edges.
 */
class GradeCalculatorTest {

    // Typical values: representative of each grade band.
    @Test
    @DisplayName("95 scores an A (typical A value)")
    void typicalA() {
        // Arrange
        GradeCalculator calc = new GradeCalculator();
        // Act
        String grade = calc.letterGrade(95.0);
        // Assert
        assertEquals("A", grade);
    }

    @Test
    @DisplayName("85 scores a B (typical B value)")
    void typicalB() {
        // Arrange
        GradeCalculator calc = new GradeCalculator();
        // Act
        String grade = calc.letterGrade(85.0);
        // Assert
        assertEquals("B", grade);
    }

    @Test
    @DisplayName("75 scores a C (typical C value)")
    void typicalC() {
        // Arrange
        GradeCalculator calc = new GradeCalculator();
        // Act
        String grade = calc.letterGrade(75.0);
        // Assert
        assertEquals("C", grade);
    }

    @Test
    @DisplayName("65 scores a D (typical D value)")
    void typicalD() {
        // Arrange
        GradeCalculator calc = new GradeCalculator();
        // Act
        String grade = calc.letterGrade(65.0);
        // Assert
        assertEquals("D", grade);
    }

    @Test
    @DisplayName("30 scores an F (typical F value)")
    void typicalF() {
        // Arrange
        GradeCalculator calc = new GradeCalculator();
        // Act
        String grade = calc.letterGrade(30.0);
        // Assert
        assertEquals("F", grade);
    }

    // Exact boundary: 90 must be A, so the >= 90 condition is verified.
    @Test
    @DisplayName("90 is exactly A (boundary kills >= vs > mutant)")
    void ninetyIsExactlyA() {
        // Arrange
        GradeCalculator calc = new GradeCalculator();
        // Act
        String grade = calc.letterGrade(90.0);
        // Assert: fails if implementation uses (score > 90) by mistake.
        assertEquals("A", grade);
    }

    @Test
    @DisplayName("100 is A (upper limit)")
    void hundredIsA() {
        // Arrange
        GradeCalculator calc = new GradeCalculator();
        // Act
        String grade = calc.letterGrade(100.0);
        // Assert
        assertEquals("A", grade);
    }

    @Test
    @DisplayName("0 is F (lower limit)")
    void zeroIsF() {
        // Arrange
        GradeCalculator calc = new GradeCalculator();
        // Act
        String grade = calc.letterGrade(0.0);
        // Assert
        assertEquals("F", grade);
    }

    // Invalid letterGrade inputs must throw.
    @Test
    @DisplayName("letterGrade rejects -1 (below range)")
    void letterGradeRejectsNegative() {
        // Arrange
        GradeCalculator calc = new GradeCalculator();
        // Act + Assert: negative score has no valid grade.
        assertThrows(IllegalArgumentException.class, () -> calc.letterGrade(-1.0));
    }

    @Test
    @DisplayName("letterGrade rejects 101 (above range)")
    void letterGradeRejectsOverHundred() {
        // Arrange
        GradeCalculator calc = new GradeCalculator();
        // Act + Assert: score above 100 has no valid grade.
        assertThrows(IllegalArgumentException.class, () -> calc.letterGrade(101.0));
    }

    // Parameterized boundary sweep for letterGrade (2nd dimension of coverage).
    @ParameterizedTest(name = "score {0} gives grade {1}")
    @DisplayName("letterGrade boundaries via parameterized cases")
    @CsvSource({
            "100, A",
            "95, A",
            "90, A",
            "89.99, B",
            "80, B",
            "79.99, C",
            "70, C",
            "69.99, D",
            "60, D",
            "59.99, F",
            "30, F",
            "0, F"
    })
    void letterGradeBoundaries(double score, String expected) {
        // Arrange
        GradeCalculator calc = new GradeCalculator();
        // Act
        String grade = calc.letterGrade(score);
        // Assert
        assertEquals(expected, grade);
    }

    // totalScore: happy path with full marks.
    @Test
    @DisplayName("totalScore sums perfect components to 100")
    void totalScorePerfectIsHundred() {
        // Arrange
        GradeCalculator calc = new GradeCalculator();
        // Act
        double total = calc.totalScore(10, 40, 10, 10, 30);
        // Assert
        assertEquals(100.0, total, 0.0001);
    }

    @Test
    @DisplayName("totalScore rejects negative attendance (att = -5)")
    void totalScoreRejectsNegativeAttendance() {
        // Arrange
        GradeCalculator calc = new GradeCalculator();
        // Act + Assert: negative component is invalid input.
        assertThrows(IllegalArgumentException.class,
                () -> calc.totalScore(-5, 40, 10, 10, 30));
    }

    @Test
    @DisplayName("totalScore rejects lab above max (lab = 41)")
    void totalScoreRejectsLabOverflow() {
        // Arrange
        GradeCalculator calc = new GradeCalculator();
        // Act + Assert: lab ceiling is 40, so 41 must throw.
        assertThrows(IllegalArgumentException.class,
                () -> calc.totalScore(10, 41, 10, 10, 30));
    }

    @Test
    @DisplayName("totalScore rejects exam above max (exam = 31)")
    void totalScoreRejectsExamOverflow() {
        // Arrange
        GradeCalculator calc = new GradeCalculator();
        // Act + Assert: exam ceiling is 30, so 31 must throw.
        assertThrows(IllegalArgumentException.class,
                () -> calc.totalScore(10, 40, 10, 10, 31));
    }

    // Parameterized valid + edge combos for totalScore.
    @ParameterizedTest(name = "att={0} lab={1} q1={2} q2={3} exam={4} sums to {5}")
    @DisplayName("totalScore valid combinations via parameterized cases")
    @CsvSource({
            "10, 40, 10, 10, 30, 100",
            "0, 0, 0, 0, 0, 0",
            "5, 20, 5, 5, 15, 50",
            "10, 0, 10, 0, 30, 50"
    })
    void totalScoreValidCases(double att, double lab, double quiz1,
                              double quiz2, double exam, double expected) {
        // Arrange
        GradeCalculator calc = new GradeCalculator();
        // Act
        double total = calc.totalScore(att, lab, quiz1, quiz2, exam);
        // Assert
        assertEquals(expected, total, 0.0001);
    }
}
