package ca.rheinmetall.atak.dagger;

import androidx.fragment.app.Fragment;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import dagger.MapKey;

@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@MapKey
public @interface FragmentFactoryKey
{
    Class<? extends Fragment> value();
}
