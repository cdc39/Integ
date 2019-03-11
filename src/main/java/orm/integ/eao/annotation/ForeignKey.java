package orm.integ.eao.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import orm.integ.eao.model.Entity;

@Retention(RetentionPolicy.RUNTIME)
public @interface ForeignKey {
	
	Class<? extends Entity> masterClass(); 
	
}
