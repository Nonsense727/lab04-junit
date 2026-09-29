package mn.edu.must.sqat;

/**
 * Calculates course grades from the F.CSA313 assessment structure.
 *
 * <p>Assessment weights: attendance (10), lab + assignments (40),
 * quiz 1 (10), quiz 2 (10), final exam (30). Maximum total is 100.
 */
public class GradeCalculator {

    /**
     * Converts a total score into a letter grade.
     *
     * <p>Boundaries: 90+ -&gt; A, 80-89 -&gt; B, 70-79 -&gt; C,
     * 60-69 -&gt; D, below 60 -&gt; F.
     *
     * @param score total score, must be within 0-100 inclusive
     * @return letter grade A, B, C, D or F
     * @throws IllegalArgumentException if score is outside 0-100 (or NaN)
     */
    public String letterGrade(double score) {
        // Validate input range first so invalid scores never get a grade.
        if (Double.isNaN(score) || score < 0.0 || score > 100.0) {
            throw new IllegalArgumentException(
                    "score must be between 0 and 100, got: " + score);
        }
        // Check boundaries from top down so each band is exact.
        if (score >= 90.0) {
            return "A";
        }
        if (score >= 80.0) {
            return "B";
        }
        if (score >= 70.0) {
            return "C";
        }
        if (score >= 60.0) {
            return "D";
        }
        return "F";
    }

    /**
     * Sums component scores into a course total.
     *
     * @param attendance attendance score, max 10
     * @param lab lab + assignment score, max 40
     * @param quiz1 quiz 1 score, max 10
     * @param quiz2 quiz 2 score, max 10
     * @param exam final exam score, max 30
     * @return sum of all components (0-100)
     * @throws IllegalArgumentException if any component is negative
     *         or exceeds its maximum
     */
    public double totalScore(double attendance, double lab,
                             double quiz1, double quiz2, double exam) {
        // Each component has its own ceiling; reject negatives and overflows.
        checkRange("attendance", attendance, 10.0);
        checkRange("lab", lab, 40.0);
        checkRange("quiz1", quiz1, 10.0);
        checkRange("quiz2", quiz2, 10.0);
        checkRange("exam", exam, 30.0);
        return attendance + lab + quiz1 + quiz2 + exam;
    }

    private void checkRange(String name, double value, double max) {
        if (Double.isNaN(value) || value < 0.0 || value > max) {
            throw new IllegalArgumentException(
                    name + " must be between 0 and " + max + ", got: " + value);
        }
    }
}
