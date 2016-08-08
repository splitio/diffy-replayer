package io.split.diffyreplayer;

/**
 * Used by Unit Tests.
 */
@DiffyReplay(condition = DiffyAnnotationClass.class)
public class WithDiffyReplay {
    public void withoutAnnotation() {

    }

    @DiffyReplay
    public void withAnnotation() {
    }
}