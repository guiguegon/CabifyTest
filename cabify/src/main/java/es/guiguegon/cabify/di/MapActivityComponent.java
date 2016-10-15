package es.guiguegon.cabify.di;

import dagger.Component;
import es.guiguegon.cabify.MapActivity;

/**
 * Created by Guille on 15/10/2016.
 */

@Component(modules = MapActivityModule.class)
public interface MapActivityComponent {

    void inject(MapActivity mapActivity);
}
