package com.connorjensen.griddlers.model;

import java.util.UUID;

public class Griddler {
  private UUID id;

  public Griddler() {
    this.id = UUID.randomUUID();
  }
}
