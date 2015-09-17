package com.axmexa.gxtapp.client;

import com.google.gwt.junit.client.GWTTestCase;

public class CompileGwtTest extends GWTTestCase {
  
  @Override
  public String getModuleName() {
    return "com.axmexa.gxtapp.Gxtapp";
  }

  public void testSandbox() {
    assertTrue(true);
  }
  
}
