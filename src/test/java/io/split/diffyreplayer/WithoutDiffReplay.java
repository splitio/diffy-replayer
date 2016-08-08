package io.split.diffyreplayer;

/**
 * Used by Unit Tests.
 */
public class WithoutDiffReplay {
    @DiffyReplay(condition = DiffyAnnotationMethod.class)
    public void withAnnotation() {

    }
}
