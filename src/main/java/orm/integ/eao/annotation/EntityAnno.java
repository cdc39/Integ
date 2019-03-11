package orm.integ.eao.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface EntityAnno {
	
	String classId();
	
	String table();
	
	String schema() default "";
	
	String shortName() default "";
	
	int keyLength() default 12;

	boolean checkIdUseOutsideBeforeDelete() default true;
	
}
