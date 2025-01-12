package svit.beans.naming;

import java.lang.reflect.AnnotatedElement;

public interface BeanNameStrategy {

    boolean supports(AnnotatedElement element);

    String resolve(AnnotatedElement element);

}