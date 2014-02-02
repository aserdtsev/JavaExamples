package com.serdtsev;

import com.google.inject.AbstractModule;

/**
 * User: Andrey Serdtsev
 */
public class AppModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(IDispatcher.class).to(Dispatcher.class);
  }
}
