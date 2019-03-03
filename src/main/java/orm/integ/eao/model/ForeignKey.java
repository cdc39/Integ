package orm.integ.eao.model;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ForeignKey {
	
	Class<? extends Entity> masterClass();
	
}
