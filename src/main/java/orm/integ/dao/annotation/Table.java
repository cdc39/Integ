package orm.integ.dao.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Table {
	
	String name();
	
	String schema() default "";
	
	String keyColumn() default "";
	
	String createTimeColumn() default "";
	
	int keyType() default KeyTypes.RANDOM; 
	
	String keyPrefix() default "";
	
	int keyLength() default 10;

	boolean cacheAll() default false;
	
	int loadOrder() default 9;
	
}
