package io.split.diffyreplayer;

import io.split.diffyreplayer.condition.DiffyReplayerCondition;
import io.split.diffyreplayer.condition.LowRateCondition;

import javax.ws.rs.NameBinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Endpoints that are annotated with this are going to be replayed by Diffy.
 *
 * Both classes and methods can be annotated with this. Annotations on Methods
 * take precedence over annotations on Classes for defining which condition applies.
 */
@NameBinding
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface DiffyReplay {
    // Header used so we do not replay a query that is already a replay query.
    String HEADER = "replayer";

    // Condition for replaying or not a request.
    // By default it will accept what is defined in low rate.
    Class<? extends DiffyReplayerCondition> condition() default LowRateCondition.class;
}
